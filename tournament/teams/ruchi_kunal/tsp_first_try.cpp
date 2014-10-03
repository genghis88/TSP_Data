#include <iostream>
#include <fstream>
#include <string>
#include <cstring>
#include <stdlib.h>
#include <cfloat>
#include <queue>
#include <vector>
#include <cmath>
#include <iomanip>
#include <unordered_set>
#include <time.h>
//#include <algorithm>

using namespace std;

typedef struct city
{
	int id;
	double x,y;
} city;
city cities[2500];
double distances[2500][2500];
int num_candidates = 15;
double cl[2500][15];
int count = 1;
std::vector<int> path;
std::vector<int> fwd;
std::vector<int> bkd;
string opfile;
void genMatrix(string inFile)
{
	FILE* ifile=fopen(inFile.c_str(),"r");
	if(NULL==ifile)
	{
		cout<<"error opening file"<<endl;
		exit;
	}
	else
	{
			do
			{
				int nItemsRead = fscanf(ifile,"%d %lf %lf\n", &cities[count].id, &cities[count].x, &cities[count].y);
				if(nItemsRead!=3)
					break;
				count++;
			}
			while (1);
	}
	/*for(int x=1;x<count;x++)
		{
			//cout << setprecision(6);
			//cout << cities[x].id << " " <<cities[x].x << " " <<cities[x].y << endl;
			printf("%d %6f %6f\n",cities[x].id,cities[x].x,cities[x].y);
		}*/
		std::cout << "Count = " << count << std::endl;

		for(int i=1; i<count; i++)
		{
			for(int j=1; j<count; j++)
			{
				if(i == j)
				{
					distances[i][j] = FLT_MAX;
				}
				else
				{
					//cout <<"indide"<<endl;
					distances[i][j] = sqrt(pow((cities[i].x-cities[j].x),2) + pow((cities[i].y-cities[j].y),2));
					distances[j][i] = distances[i][j];
					//printf("%6f ",distances[i][j]);

				}
			}
		}
		/*std::cout << "Count = " << count << std::endl;
		for(int i=1; i<count; i++)
		{
			for(int j=1; j<count; j++)
			{
				//cout << distances[i][j] << " ";
				printf("%6f ",distances[i][j]);
			}
			//cout << endl;
			printf("\n");
		}
		std::cout << "Count = " << count << std::endl;*/

	
}

typedef struct cl_distance
{
	int id;
	double distance;
} cl_distance;

struct compare
{
	bool operator()(const cl_distance& a, const cl_distance& b)
	{
		return a.distance < b.distance;
	}
};

void generate_candidate_list()
{
	
	//cout<<"some candidate thing";
	for(int c=1;c<count;c++)
	{
		std::priority_queue<cl_distance, std::vector<cl_distance>, compare > minHeap;
		for(int other=1;other<count;other++)
		{
			if(minHeap.size() < num_candidates)
			{
				cl_distance c1;
				c1.id = other;
				c1.distance = distances[c][other];
				minHeap.push(c1);
			}
			else
			{
				if(distances[c][other] <= minHeap.top().distance)
				{
					minHeap.pop();
					cl_distance c1;
					c1.id = other;
					c1.distance = distances[c][other];
					minHeap.push(c1);
				}
			}
		}
		for(int j=num_candidates;j>0;j--)
		{
			cl[c][j] = minHeap.top().id;
			minHeap.pop();
		}
	}
}

void greedy_path()
{
	//cout << "Inside funciton ";
	std::unordered_set<int> s;
	while (s.size() < 3)
	{ 
		//cout << "In while";
		srand(time(NULL));
		s.insert(rand()%count);///or count-1?????
	}
	
	//int path[num_cities+2]; 
	int k=0;
	
	//std::vector<int>::iterator k;
	//k = path.begin();
	std::unordered_set<int>::iterator it;
	for(it=s.begin();it!=s.end();++it)
	{
		//path[k]=*it;
		//cout << *it << " ";
		path.push_back(*it);
		k++;
		//path.emplace(k,*it);
		//k++;
	}

	
	it=s.begin(); double d1,d2,d3; int m;double cost,minCost;
	path.push_back(*it);
	k++;

	//auto iit=path.begin();
	//while(iit != path.end())
	//{
	//	cout << *iit << " ";
	//	++iit;

	//}
	//cout << endl;

//	cout << "HI" << endl;

	//path.emplace(k++,*it);  /*from here!!
	
	for(int i=1;i<count;i++)
	{
		//cout << "Inside loop" << " ";
		bool found = false;
		for(auto k1=path.begin();k1!=path.end();k1++)
		{
			if(*k1 == i)
			{
				found = true;
				break;
			}
		}
		if(found == false)
		{
		//	cout << "Inside false if ";
			m=0;
			d1=distances[path[m]][i];
			d2=distances[path[m+1]][i];
			d3=distances[path[m]][path[m+1]];
			minCost = ( d1 + d2 ) - d3;
			for(int l=1;l<k-1;l++)
			{
				d1=distances[path[l]][i];
				d2=distances[path[l+1]][i];
				d3=distances[path[l]][path[l+1]];
				cost=(d1+d2)-d3;
				if(cost<minCost)
				{
					minCost=cost;
					m=l;
				}

			}
			std::vector<int>::iterator myit = path.begin();
			myit+=(m+1);
			//if(m == 0)
			//{
			//	cout << "\nWHOA!\n";
			//}
			path.emplace(myit,i);
			k++;
		}

	}
	

	//for(int x=0;x<k;x++)
	//	cout << path[x] << " ";
	//cout << endl;

	fwd.push_back(0);
	bkd.push_back(0);
	
	std::vector<int>::iterator i1,i2;
	for(int i=1;i<count;i++)
	{
		//cout << "Inside for";
		//fwd[i]=0;
		//bkd[i]=0;
		/*std::vector<int>::iterator i3,i4;
		
		i3 = fwd.emplace(i3,0);
		i4 = bkd.emplace(i4,0);*/
		fwd.push_back(0);
		bkd.push_back(0);
	}
	i1 = fwd.begin();
	i2 = bkd.begin();
	for(int i=1;i<count;i++)
	{
		
		fwd[path[i]] = path[i-1];         //////i-1 doesnot exist for 0
		bkd[path[i-1]] = path[i];
	}

}
double length(std::vector<int> path)
{
	double length=0.0;int i=1;
	for(auto it=path.begin();it!=path.end();it++)
	{
		length+=distances[i][path[i]];
		i++;
	}
	return length;
}
void twist( int i,int j)
{
	int j_next = fwd[j];
	int idx = fwd[i];
	while(idx != j_next)
	{
		int temp = fwd[idx];
		fwd[idx] = bkd[idx];
		bkd[idx] = temp;
		idx = bkd[idx];
	}
	bkd[j_next] = fwd[i];
	fwd[fwd[i]] = j_next;
	fwd[i] = j;
	bkd[j] = i;
}

typedef struct struct_k_tuple
{
	int i,j;
	double d3;
} struct_k_tuple;

struct_k_tuple k_opt(int p_length)///////rRETURN TYPE!!!!???
{
	srand(time(NULL));
	int i = rand()%(count-1);///or count-1???
	int tp = rand()%(num_candidates-1);
	int j = cl[i][tp];
	double d1 = distances[i][fwd[i]] + distances[j][fwd[j]];
	double d2 = distances[i][j] + distances[fwd[i]][fwd[j]];
	double d3 = (p_length - d1) + d2;
	//return???
	struct_k_tuple k_tuple;
	k_tuple.i = i;
	k_tuple.j = j;
	k_tuple.d3 = d3;
	return k_tuple;
}

double P(double prev_score, double next_score, double temperature)
{
	if(next_score < prev_score)
	{
		return 1.0;
	}
	else
	{
		if(temperature == 0.0)
		{
			return 0.0;
		}
		double result = exp( -1 * abs(next_score - prev_score) / temperature);
		return result;
	}
}



void anneal(double start_temp,double alpha,time_t limit)
{
	time_t starttimer;
	double next_score;
	int i,j;
	starttimer=time(NULL);
	std::vector<int> best=path;
	double current_score=length(fwd);
	double best_score=current_score;
	bool done=false;
	double temp=start_temp;
	int num_evals=0;
	struct_k_tuple k_tuple;
	vector<int> best_fwd=fwd;
	vector<int> best_bkd=bkd; 
	while(temp)
	{
		time_t currtime=time(NULL);
		if(currtime-starttimer>=limit)
		{
			done=true;
			break;
		}
		num_evals+=1;
		k_tuple = k_opt(current_score);
		i = k_tuple.i;
		j = k_tuple.j;
		next_score = k_tuple.d3;

		double p = P(current_score,next_score,temp);
		if(rand() < p)
		{
			twist(i,j);
			current_score = next_score;
			if(current_score < best_score)
			{
				best_fwd = fwd ;
				best_bkd = bkd;
				best_score = current_score;
			}
		}
		temp=temp*alpha;

	}

	//print best, best_score and temperature
	cout<<best_score;
	ofstream outf;
	cout << opfile << endl;
	outf.open(opfile);
	if(!outf.is_open())
		cout << "Op file not opened!" << endl;
	
	for(auto it=best_fwd.begin()+1;it!=best_fwd.end();it++)
	{
		outf << cities[(*it)].id;
		outf << "\n";
		//cout<<cities[(*it)].id<<endl;
	}
	outf.close();
	cout<<length(best_fwd);
	//seconds=difftime(timer,);


}

int main(int argc, char*argv[])
{
	time_t nowt=time(NULL);
	opfile = argv[2];
	genMatrix(argv[1]);
	generate_candidate_list();
	/*for(int i=0;i<count;i++)
	{
		for(int k=1;k<=15;k++)
		{
			cout << cl[i][k] << " ";
		}
		cout << endl;
	}*/
	
	
	greedy_path();
	double start_temp=270;
	double alpha=0.9999998;
	time_t time_left=115-(time(NULL)-nowt);
	anneal(start_temp,alpha,time_left);
	return 0;
}