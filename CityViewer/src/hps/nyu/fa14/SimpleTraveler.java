package hps.nyu.fa14;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SimpleTraveler {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// Parse the first argument as the input file and the second as the output file
		if(args.length != 2){
			usage();
			System.exit(1);
		}
		CitySet cities = CitySet.LoadFromUrl(new File(args[0]).toURI().toString());
		Tour.GenerateRandom(cities).saveTour(new FileOutputStream(new File(args[1])));
	}
	
	private static void usage(){
		// How to use it
		System.out.println("java -jar SimpleTraveler <input> <output>");
	}

}
