package hps.nyu.fa14;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tour {

	private final List<City> tour = new ArrayList<City>();

	private final CitySet cities;

	public Tour(CitySet cities) {
		this.cities = cities;
	}

	public void addCity(int id) {
		tour.add(cities.getCity(id));
	}

	public boolean isValidPermutation() {

		return false;
	}

	public double evaluate(){
		double length = 0;
		if(tour.size() <= 1){
			return length;  // Nowhere to go
		}
		City lastCity = null;
		
		for(City c : tour){
			if(lastCity != null){
				length += lastCity.distance(c);
			}
			lastCity = c;
		}
		// finish the tour and return to the start city - we know the tour must have length > 1
		length += lastCity.distance(tour.get(0));
		return length;
	}
	
	
	public void render(Graphics g, Rectangle r) {

		// Find the bounds
		Bounds b = cities.getBounds();
		Bounds target = new Bounds();
		target.xMax = r.getMaxX();
		target.xMin = r.getMinX();
		target.yMax = r.getMaxY();
		target.yMin = r.getMinY();

		g.setColor(Color.blue);

		AbstractPointTranslator trans = new PointTranslator(b, target, null);
		trans = new MirrorXYPointTranslator(target, trans);
		for (int i = 0; i < tour.size(); i++) {
			City c1 = tour.get(i);
			City c2 = tour.get((i + 1) % tour.size()); // wrap around to one
			Point p1 = trans.getDestPoint(new Point(c1.X, c1.Y));
			Point p2 = trans.getDestPoint(new Point(c2.X, c2.Y));
			g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
		}
	}

	public static Tour GenerateRandom(CitySet cities) {

		Tour newTour = new Tour(cities);

		Integer[] cityIds = new Integer[cities.size()];
		int index = 0;
		for (City c : cities) {
			cityIds[index++] = c.ID;
		}
		permute(cityIds);
		for (int id : cityIds) {
			newTour.addCity(id);
		}

		return newTour;
	}

	/**
	 * Parses a newline-separated list of integer city ids into a well-formed
	 * tour
	 * 
	 * @param tour
	 * @param cities
	 * @return
	 */
	public static Tour parseTour(InputStream tour, CitySet cities)
			throws IOException {

		Tour t = new Tour(cities);
		BufferedReader br = new BufferedReader(new InputStreamReader(tour));

		String line;
		while ((line = br.readLine()) != null) {
			int cityId = Integer.parseInt(line);
			t.addCity(cityId);
		}
		return t;
	}
	
	public void saveTour(OutputStream output) throws IOException{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
		for(City c : this.tour){
			bw.write(String.format("%d%n", c.ID));			
		}
		bw.close();
	}

	// Implements Fisher-Yates:
	// http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
	private static <T> void permute(T[] input) {
		Random rand = new Random();
		for (int i = input.length - 1; i > 1; i--) {
			int swapIndex = rand.nextInt(i + 1);
			T swapValue = input[swapIndex];
			input[swapIndex] = input[i];
			input[i] = swapValue;
		}
	}

}
