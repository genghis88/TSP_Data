package hps.nyu.fa14;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class SolutionEvaluator {
	
	private ArrayList<City> cityList = null;
	
	public SolutionEvaluator() {
		cityList = new ArrayList<City>();
	}
	
	private void readCityList() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("usa115475.tsp"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    try {
      String line = br.readLine();
      while (line != null) {
      	String[] comps = line.split(" ");
      	int cityId = Integer.parseInt(comps[0]);
      	double xCoord = Double.parseDouble(comps[1]);
      	double yCoord = Double.parseDouble(comps[2]);
      	cityList.add(new City(cityId,xCoord,yCoord));
        line = br.readLine();
      }
    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    finally {
      try {
			  br.close();
			} catch (IOException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			}
    }
	}
	
	private HashMap<Integer,Boolean> readSample(String sampleName) {
		BufferedReader br = null;
		HashMap<Integer,Boolean> sample = new HashMap<Integer,Boolean>();
		try {
			br = new BufferedReader(new FileReader(sampleName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    try {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
      	String[] comps = line.split(" ");
      	int cityId = Integer.parseInt(comps[0]);
      	sample.put(cityId,false);
        sb.append(line);
        line = br.readLine();
      }
      String everything = sb.toString();
    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    finally {
      try {
			  br.close();
			} catch (IOException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			}
    }
    return sample;
	}
	
	private double evaluateSolution(HashMap<Integer,Boolean> sample, ArrayList<Integer> cityIdList) {
		double length = 0;
		boolean firstCity = true;
		double prevXCoord = 0;
		double prevYCoord = 0;
		for(int cityId:cityIdList) {
			City city = cityList.get(cityId - 1);
			if(!firstCity) {
				length = length + Math.sqrt((city.X - prevXCoord)*(city.X - prevXCoord) 
						+ (city.Y - prevYCoord)*(city.Y - prevYCoord));
			}
			else {
				firstCity = false;
			}
			prevXCoord = city.X;
			prevYCoord = city.Y;
			if(!sample.containsKey(cityId)) {
				return -1;
			}
			else {
				sample.put(cityId,true);
			}
		}
		for(int cityId:sample.keySet()) {
			if(!sample.get(cityId)) {
				return -1;
			}
		}
		return length;
	}
	
	private ArrayList<Integer> readSolutionFile(String solutionFile) {
		ArrayList<Integer> solution = new ArrayList<Integer>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(solutionFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    try {
      String line = br.readLine();
      while (line != null) {
      	solution.add(Integer.parseInt(line));
        line = br.readLine();
      }
    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    finally {
      try {
			  br.close();
			} catch (IOException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			}
    }
    return solution;
	}
	
  public static void main(String[] args) {
  	String sampleFile = args[0];
  	String solutionFile = args[1];
  	
  	SolutionEvaluator solEval = new SolutionEvaluator();
  	solEval.readCityList();
  	HashMap<Integer,Boolean> sample = solEval.readSample(sampleFile);
  	ArrayList<Integer> solution = solEval.readSolutionFile(solutionFile);
  	System.out.println(solEval.evaluateSolution(sample,solution));
  }
}
