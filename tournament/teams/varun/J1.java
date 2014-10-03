import java.util.*;
import java.lang.*;
import java.io.*;

class J1
{
 private boolean unsettled[];
 private boolean settled[];
 private int numberofvertices;
 private double adjacencyMatrix[][];
 private double d[][];
 private double key[];
 public static final double INFINITE = 999999999;
 private int parent[];
 private List<Integer> tour;
 public J1(int numberofvertices)
 {
  this.numberofvertices = numberofvertices;
  unsettled = new boolean[numberofvertices + 1];
  settled = new boolean[numberofvertices + 1];
  adjacencyMatrix = new double[numberofvertices + 1][numberofvertices + 1];
  key = new double[numberofvertices + 1];
  parent = new int[numberofvertices + 1];
  d = new double[numberofvertices + 1][numberofvertices + 1];
  tour= new ArrayList<Integer>();
 }
 
 public int getUnsettledCount(boolean unsettled[])
 {
  int count=0;
  for (int index=0;index<unsettled.length;index++)
  {
   if (unsettled[index]){count++;}        
  }
  return count;
 }

 public void primsAlgorithm(double adjacencyMatrix[][])
 {
  int evaluationVertex;
  for(int s=1; s<=numberofvertices;s++)
  {
   for(int d=1;d<=numberofvertices;d++)
   {
    this.adjacencyMatrix[s][d]=adjacencyMatrix[s][d];
   }
  }
  for(int index=1;index<=numberofvertices;index++)
  {
    key[index]=INFINITE;
  }
  key[1]= 0;
  unsettled[1] = true;
  parent[1] = 1;
  while (getUnsettledCount(unsettled) != 0)
  {
   evaluationVertex = getMimumKeyVertexFromUnsettled(unsettled);
   unsettled[evaluationVertex] = false;
   settled[evaluationVertex] = true;
   evaluateNeighbours(evaluationVertex);
  }
 } 

 private int getMimumKeyVertexFromUnsettled(boolean[] unsettled2)
 {
  double min = Double.MAX_VALUE;
  int node = 0;
  for (int vertex = 1; vertex <= numberofvertices; vertex++)
  {
   if (unsettled[vertex] == true && key[vertex] < min)
   {
    node = vertex;
    min = key[vertex];
   }
  }
  return node;
 }
 
 public void evaluateNeighbours(int evaluationVertex)
 {
  for (int d= 1; d <= numberofvertices; d++)
  {
   if (settled[d] == false)
   {
    if (adjacencyMatrix[evaluationVertex][d] != INFINITE)
    {
     if (adjacencyMatrix[evaluationVertex][d] < key[d])
     {
      key[d] = adjacencyMatrix[evaluationVertex][d];
      parent[d] = evaluationVertex;
     }
     unsettled[d] = true;
    }
   }
  }
 }

 void dfs(int cnode, int par)
 {
  tour.add(cnode);
  for (int i = 1; i <=numberofvertices; i++) 
  {
   if(d[cnode][i]>0 && i != par)          
   dfs(i, cnode);
  }
 }
 public void printMST(float[] v , String s) throws Exception
 {
  for (int i=2;i<=numberofvertices;i++)
  for (int j=2;j<=numberofvertices;j++)
  d[i][j]=0;
  for (int vertex = 2; vertex <= numberofvertices; vertex++)
  {
   d[parent[vertex]][vertex]=adjacencyMatrix[parent[vertex]][vertex];
   d[vertex][parent[vertex]]=d[parent[vertex]][vertex];
  }
  int random = (int)(Math.random()*numberofvertices+1);
  try
  { 
   PrintWriter writer = new PrintWriter(s, "UTF-8");
   dfs(random,-1);
   for(int i11=0;i11<tour.size();i11++)
   { 
    System.out.println(v[i11]);
    writer.println((int)v[tour.get(i11)-1]);
   }
   writer.close();
  }
  catch(Exception e)
  {
  }
 }

 public static void main (String[] args) throws Exception
 {
  float [] x = new float [1000];
  float [] y = new float [1000];
  float [] v1= new float [1000];
  int k=0,j=0,l=0;
  try
  {
   Scanner scan = new Scanner(new File(args[0]));
   String line="";
   String[] split;
   while (scan.hasNextLine())
   {
    line = scan.nextLine();
    if(!line.equals("aaa"))
    {
     split=line.split("\\s+",3); 
     int i=0;
     while(!split[i].isEmpty())
     { 	
      float z=Float.parseFloat(split[i]);
      if(i%3==0)
      {
       v1[l]=z;
       l++; 
      }
      if(i%3==1)
      {
       x[j]=z;
       j++;
      }
      if(i%3==2)
      { 
       y[k]=z;
       k++;
      } 
      i++;
      if(i==3) break;
     }
    }  
   }
   double x1=0,x2=0,y1=0,y2=0,xDiff,yDiff;
   double ySqr,output;
   double xSqr;
   int i1=0,j1=0,n1=0;
   double [][] dist = new double [500][500]; 
   n1=k;
   //System.out.println(n1);
   for(i1=1;i1<=n1;i1++)
   {
    x1=x[i1-1];y1=y[i1-1];
    for(j1=1;j1<=n1;j1++)
    {	
     x2=x[j1-1];y2=y[j1-1];		
     xDiff=x1-x2;
     xSqr=Math.pow(xDiff, 2);
     yDiff=y1-y2;
     ySqr=Math.pow(yDiff, 2);
     output=Math.sqrt(xSqr + ySqr);
     //System.out.println(output);
     dist[i1][j1]=output;
     if(i1==j1) dist[i1][j1]=INFINITE;
    }
   }
   J1 jc = new J1(n1);
   jc.primsAlgorithm(dist);
   jc.printMST(v1,args[1]);
  }
  catch(Exception e)
  {
  e.printStackTrace(); 
  //System.out.println(e.getMessage());
  } 
 }
} 
