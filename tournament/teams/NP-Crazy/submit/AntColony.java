//package tsp1;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

class AntColony {

	private static final double exploitationProbability = 0.7;

	private static double distanceWeight = 2.0;

	private static final double pheromoneAffinity = 0.1;

	private static long startTime = Calendar.getInstance().getTimeInMillis();
	
	private static final int timeConstant = 116000;

	// Return the euclidian distance between two cities
	public static double getDistance(Coordinates a, Coordinates b) {
		return Math.sqrt(Math.pow(Math.abs(a.x - b.x), 2) +
				Math.pow(Math.abs(a.y - b.y), 2));
	}

	@SuppressWarnings("unchecked")
	public static double nearestNeighborHeuristic(ArrayList<Integer> greedyTour,
			HashSet<Integer> cities,
			HashMap<Integer, Coordinates> coordinates, 
			Integer start) {
		HashSet<Integer> toVisit = (HashSet<Integer>) cities.clone();
		greedyTour.add(start);
		toVisit.remove(start);
		// System.out.println(start);
		Integer currentNode = start;
		Coordinates currentCoordinates = coordinates.get(currentNode);
		double tourWeight = 0.0;
		do {
			double bestDistance = Double.MAX_VALUE;
			Integer bestNeighbor = -1;
			for (Integer city : toVisit) {
				double candidateDistance = getDistance(currentCoordinates, 
						coordinates.get(city));
				if (candidateDistance < bestDistance) {
					bestDistance = candidateDistance;
					bestNeighbor = city;
				}
			}
			tourWeight += bestDistance;
			toVisit.remove(bestNeighbor);
			greedyTour.add(bestNeighbor);
			currentNode = bestNeighbor;
			currentCoordinates = coordinates.get(bestNeighbor);
			// System.out.println(bestNeighbor);
		} while (toVisit.size() > 0);
		greedyTour.add(greedyTour.get(0));
		// get distance from last city to start city
		tourWeight += getDistance(currentCoordinates, coordinates.get(start));
		return tourWeight;
	}

	public static HashMap<String, Double> getInitialPheromoneTable(HashSet<Integer> cities, double bound) {
		HashMap<String, Double> pheromoneTable = new HashMap<String, Double>();
		for (Integer a : cities) {
			for (Integer b : cities) {
				if (a < b) {
					pheromoneTable.put(a + "," + b, bound);
				}
			}
		}
		return pheromoneTable;
	}


	public static double getPheromone(HashMap<String, Double> pheromoneTable, Integer a, Integer b) {
		String key = (a < b) ? (a + "," + b) : (b + "," + a); 
		return pheromoneTable.get(key);
	}

	public static void addPheromone(HashMap<String, Double> pheromoneTable, Integer a, Integer b, 
			double affinity, double basePheromone) {
		double current = getPheromone(pheromoneTable, a, b);
		double updated = ((1 - affinity) * current) +  // some of the existing pheromone "evaporates"
				(affinity * basePheromone);  // new pheromone is deposited since this edge was visited
		String key = (a < b) ? (a + "," + b) : (b + "," + a); 
		pheromoneTable.put(key, updated);
	}

	public static void addBestTourPheromone(HashMap<String, Double> pheromoneTable, ArrayList<Integer> bestTour, double bestTourCost) {
		// Step 1: scale down each key by 1 - pheromoneAffinity.
		for (String key : pheromoneTable.keySet()) {
			pheromoneTable.put(key, ((1 - pheromoneAffinity) * pheromoneTable.get(key)));
		}
		// Step 2: add pheromone for edges in the best tour.
		for (int i = 0; i < bestTour.size() - 1; ++i) {
			Integer a = bestTour.get(i);
			Integer b = bestTour.get(i + 1);
			addPheromone(pheromoneTable, a, b, 
					1.0, (1.0 / bestTourCost));
		}
		Integer last = bestTour.get(bestTour.size() - 1);
		Integer first = bestTour.get(0);
		addPheromone(pheromoneTable, last, first, 
				1.0, (1.0 / bestTourCost));
	}

	public static Double getCostMeasure(HashMap<Integer, Coordinates> coordinates,
			HashMap<String, Double> pheromoneTable,
			Integer from, Integer to) {
		double distance = getDistance(coordinates.get(from), coordinates.get(to));
		double pheromone = getPheromone(pheromoneTable, from, to);
		return pheromone * Math.pow((1.0 / distance), distanceWeight);	
	}



	public static ArrayList<Double> pickNextCity(Integer from,
			HashSet<Integer> toVisit,
			HashMap<Integer, Coordinates> coordinates,
			HashMap<String, Double> pheromoneTable,
			double basePheromone) {
		Random rand = new Random();
		double nextCity = -1;
		double prob = -1;
		if (rand.nextDouble() <= exploitationProbability) {
			Double bestDistance = Double.MIN_VALUE;
			Integer bestNeighbor = -1;
			for (Integer city : toVisit) {
				double costMeasure = getCostMeasure(coordinates, pheromoneTable, from, city);
				if (costMeasure > bestDistance) {
					bestDistance = costMeasure;
					bestNeighbor = city;
				}
			}
			prob = bestDistance;
			nextCity = bestNeighbor;
		} else {
			ArrayList<Double> probDistribution = new ArrayList<Double>(toVisit.size());
			// pass 1: calculate the sum
			double sum = 0.0;
			for (Integer city : toVisit) {
				double costMeasure = getCostMeasure(coordinates, pheromoneTable, from, city);
				probDistribution.add(costMeasure);
				sum += costMeasure;
			}
			// pass 2: pick the city based on a random number
			Integer lastCity = -1;
			int i = 0;
			double drawingTally = 0.0;
			double randPick = rand.nextDouble();
			for (Integer city : toVisit) {
				drawingTally += (probDistribution.get(i) / sum);
				if (randPick <= drawingTally) {
					lastCity = city;
					prob = drawingTally;
					break;
				}
				// get the last one in case of a floating point issue
				if (++i == toVisit.size()) {
					prob = drawingTally;
					lastCity = city;
				}
			}
			nextCity =  lastCity;
		}
		ArrayList<Double> probCity = new ArrayList<Double>();
		probCity.add(nextCity);
		probCity.add(prob);
		return probCity;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Integer> antColonyTour(HashSet<Integer> cities,
			HashMap<Integer, Coordinates> coordinates,
			HashMap<String, Double> pheromoneTable,
			double basePheromone,
			int numAnts,
			int numIterations) {
		Random rand = new Random();
		double bestTourCost = Double.MAX_VALUE;
		ArrayList<Integer> cityList = new ArrayList<Integer>(cities);
		double eMax = Math.log(cityList.size()*(cityList.size()-1)/2);
		ArrayList<Integer> bestTour = null;
		// ArrayList<Integer> startPoints = new ArrayList<Integer>(numAnts);
		ArrayList<ArrayList<Double>> probEdge = null;

		double xThresh = 0.85, yThresh = 0.65, zThresh = 0.5;
		int bTourIndex = -1;
		long runTime = 0, currTime = 0;

		for (int iter = 0; iter < numIterations; ++iter) {
			// check running time
			currTime = Calendar.getInstance().getTimeInMillis();
			runTime = currTime - startTime;
			if(runTime > timeConstant){
				return null;
			}
			probEdge = new ArrayList<ArrayList<Double>>();
			for(int nAnts=0; nAnts < numAnts; nAnts++){
				probEdge.add(new ArrayList<Double>());
			}
			// Initialisation
			ArrayList<HashSet<Integer>> antWaypoints = new ArrayList<HashSet<Integer>>(numAnts);
			// ArrayList<Integer> antLocations = new ArrayList<Integer>(numAnts);
			ArrayList<ArrayList<Integer>> antTours = new ArrayList<ArrayList<Integer>>(numAnts);
			ArrayList<Double> tourCosts = new ArrayList<Double>(numAnts);
			for (int i = 0; i < numAnts; ++i) {
				HashSet<Integer> toVisit = (HashSet<Integer>) cities.clone();
				Integer startPoint = // (iter == 0) ?
						cityList.get(rand.nextInt(cityList.size()));
				//    startPoints.get(i);
				// if (iter == 0) {
				// startPoints.add(startPoint);
				// }
				toVisit.remove(startPoint);
				antWaypoints.add(toVisit); 
				ArrayList<Integer> tour = new ArrayList<Integer>(cities.size());
				tour.add(startPoint);
				antTours.add(tour);
				tourCosts.add(0.0);
			}
			// This is the phase where ants build their tours. pick next city
			//			ArrayList<Integer> ignoreAnts = new ArrayList<Integer>();
			boolean  flag = true;
			double eCurr = 0;

			for (int i = 1; i < cities.size(); ++i) {
				for (int j = 0; j < numAnts; ++j) {
					/*for(int antID : ignoreAnts){
						if (j == antID){
							flag = false;
							break;
						}
					}*/
					if(flag){
						Integer currentCity = antTours.get(j).get(i-1);
						ArrayList<Double> probCity = pickNextCity(currentCity, 
								antWaypoints.get(j),
								coordinates,
								pheromoneTable,
								basePheromone);
						Integer nextCity = (int)probCity.get(0).doubleValue();
						Double prob = probCity.get(1);
						// don't visit this city anymore
						antWaypoints.get(j).remove(nextCity);
						// mark this city's place within the tour
						antTours.get(j).add(nextCity);
						// update the cost of the tour
						probEdge.get(j).add(prob);
						tourCosts.set(j, tourCosts.get(j) + getDistance(coordinates.get(currentCity), 
								coordinates.get(nextCity)));

						// Maintain a list of ant tours which need not be checked further as the tour cost is already higher than the best Tour cost.

						if (tourCosts.get(j) > bestTourCost) {
							// add to ignore Ant Tour
							//							ignoreAnts.add(j);
						}

						// last city, add cost from last city to the beginning city
						if (i == cities.size() - 1) {
							Integer firstCity = antTours.get(j).get(0);
							tourCosts.set(j, tourCosts.get(j) + getDistance(coordinates.get(nextCity),
									coordinates.get(firstCity)));
							if (tourCosts.get(j) < bestTourCost) {
								bestTourCost = tourCosts.get(j);
								bestTour = antTours.get(j);
								bTourIndex = j;
							}
						}
					}
				}
				for (int j = 0; j < numAnts; ++j) {
					// update pheromone
					addPheromone(pheromoneTable, antTours.get(j).get(i), antTours.get(j).get(i-1), pheromoneAffinity, basePheromone);
				}
			}
			for (int j = 0; j < numAnts; ++j) {
				// update pheromone for the last to first edge
				addPheromone(pheromoneTable, antTours.get(j).get(cities.size() - 1), antTours.get(j).get(0), pheromoneAffinity, basePheromone);
			}
			addBestTourPheromone(pheromoneTable, bestTour, bestTourCost);
			// update heuristic param
			for(int it = 0; it < probEdge.size(); it++){
				double r = probEdge.get(bTourIndex).get(it);
				eCurr = eCurr + (1/r)*(Math.log(r));
			}
			double ePrime = 1 - ((eMax - eCurr)/eMax);
			if(ePrime > xThresh){
				distanceWeight = 5;
			}
			else if(ePrime > yThresh && ePrime < xThresh){
				distanceWeight = 4;
			}
			else if(ePrime > zThresh && ePrime < yThresh){
				distanceWeight = 3;
			}
			else if(ePrime < zThresh){
				distanceWeight = 2;
			}
		}
		//		System.out.println(bestTourCost);
		return bestTour;
	}

	public static ArrayList<Integer> twoOptSwap(ArrayList<Integer> tour, int i, int j) {
		ArrayList<Integer> swapTour = new ArrayList<Integer>(tour.size());
		for (int loc = 0; loc <= i - 1; ++loc) {
			swapTour.add(tour.get(loc));
		}
		for (int loc = j; loc >= i; --loc) {
			swapTour.add(tour.get(loc));
		}
		for (int loc = j + 1; loc < tour.size(); ++loc) {
			swapTour.add(tour.get(loc));
		}
		return swapTour;
	}

	public static double getTourScore(HashMap<Integer, Coordinates> coordinates,
			ArrayList<Integer> tour) {
		double score = 0.0;
		for (int i = 0; i < tour.size() - 1; ++i) {
			score += getDistance(coordinates.get(tour.get(i)), coordinates.get(tour.get(i+1)));
		}
		return score;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Integer> twoOpt(HashMap<Integer, Coordinates> coordinates,
			ArrayList<Integer> tour) {
		ArrayList<Integer> modTour = (ArrayList<Integer>) tour.clone();
		modTour.add(modTour.get(0));
		boolean modified = false;
		double bestScore  = getTourScore(coordinates, modTour);
		long currTime = 0, runTime = 0;
		do {
			modified = false;
			for (int i = 1; i < modTour.size() - 2; ++i) {
				for (int j = i + 1; j < modTour.size() - 1; ++j) {
					// check running time
					currTime = Calendar.getInstance().getTimeInMillis();
					runTime = currTime - startTime;
					if(runTime > timeConstant){
						return null;
					}
					ArrayList<Integer> candidate = twoOptSwap(modTour, i, j);
					double score = getTourScore(coordinates, candidate);
					if (score < bestScore) {
						bestScore = score;
						modTour = candidate;
						modified = true;
						continue;
					}
				}
			}
		} while (modified);
		return modTour;
	} 

	public static void main(String[] args) {
		/*if (args.length != 2) {
			System.out.println("Unexpected number of arguments!");
			System.exit(1);
		}*/
		
		//String filePrefix = "/media/venu/Data/Workspace/eclipse/HPS/src/tsp1/";
		/*String filePrefix = "D:\\Workspace\\eclipse\\HPS\\src\\tsp1\\";
		args = new String[2];
		args[0] = filePrefix + "input\\" + "sample_5.tsp";
		args[1] = filePrefix + "output\\" + "sample_5_out.tsp";*/
		Date date = null;
		try {
			HashSet<Integer> cities = new HashSet<Integer>();
			HashMap<Integer, Coordinates> coordinates = new HashMap<Integer, Coordinates>();
			Integer someId = null;
			Scanner scanner = new Scanner(new File(args[0]));
			while (scanner.hasNextLine()) {
				StringTokenizer tokenizer = new StringTokenizer(scanner.nextLine());
				if (tokenizer.countTokens() != 3) {
					System.out.println("Unexpected number of tokens!");
					System.exit(1);
				}
				Integer nodeId = Integer.parseInt(tokenizer.nextToken());
				someId = nodeId;
				Double x = Double.parseDouble(tokenizer.nextToken());
				Double y = Double.parseDouble(tokenizer.nextToken());
				cities.add(nodeId);
				coordinates.put(nodeId, new Coordinates(x, y));
			}
			scanner.close();
			date = new Date();
			//System.out.println(date.toString());
			// TODO (Venu): try putting the greedy tour directly into twoOpt.
			ArrayList<Integer> greedyTour = new ArrayList<Integer>(cities.size());
			startTime = Calendar.getInstance().getTimeInMillis();
			double greedyTourCost = nearestNeighborHeuristic(greedyTour, cities, coordinates, someId);
			double basePheromone = (1.0 / greedyTourCost);
			//			System.out.println(basePheromone);
			//System.out.println("NN=" + greedyTourCost);
			HashMap<String, Double> pheromoneTable = getInitialPheromoneTable(cities, basePheromone);
			ArrayList<Integer> bestTour = new ArrayList<Integer>();
			bestTour = twoOpt(coordinates, greedyTour);
			if(bestTour!=null){
				//System.out.println("NN+2-OPT=" + getTourScore(coordinates, bestTour));
				greedyTour = bestTour;
			}
			else{
				bestTour = greedyTour;
			}
			if(cities.size() < 500){
				bestTour = antColonyTour(cities,
						coordinates,
						pheromoneTable,
						basePheromone,
						10,
						100);
				if(bestTour!=null){
					//System.out.println("ACO=" + getTourScore(coordinates, bestTour));
					greedyTour = bestTour;
				}
				else{
					bestTour = greedyTour;
				}
				bestTour = twoOpt(coordinates, bestTour);
				if(bestTour!=null){
					//System.out.println("ACO+2-Opt=" + getTourScore(coordinates, bestTour));
				}
				else{
					bestTour = greedyTour;
				}
			}
			bestTour.remove(bestTour.size() - 1);
			PrintWriter writer = new PrintWriter(args[1], "UTF-8");
			date = new Date();
			//System.out.println(date.toString());
			for (Integer city : bestTour) {
				writer.println(city);
			}
			writer.close();
		} catch (Exception e) {
			System.out.println("File read/write exception : " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
