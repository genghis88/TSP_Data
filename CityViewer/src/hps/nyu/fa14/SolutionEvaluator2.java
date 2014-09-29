package hps.nyu.fa14;

import java.io.File;
import java.io.FileInputStream;

public class SolutionEvaluator2 {

	public static void main(String[] args) {
		String sampleFile = args[0];
		String solutionFile = args[1];

		// Parse the first argument as the city input file and the second as the
		// tour solution file
		if (args.length != 2) {
			usage();
			System.exit(-1);
		}
		CitySet cities = CitySet.LoadFromUrl(new File(sampleFile).toURI()
				.toString());
		try {
			Tour t = Tour.parseTour(
					new FileInputStream(new File(solutionFile)), cities);
			// Print sentinel value if this is not a valid permutation
			if(!t.isValidPermutation()){
				System.err.println("Not a valid permutation of cities");
				System.out.println(-1);
				return;
			}
			// In the error-free case, only print the value we care about
			System.out.println(t.evaluate());
		} catch (Exception ex) {
			System.err.println("Cannot evaluate solution: " + ex.getMessage());
			System.out.println(-1);
		}
	}

	private static void usage() {
		// How to use it
		System.out.println("java -jar tsp.jar <city_file> <tour_file>");
	}
}
