package hps.nyu.fa14;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tour {

	private final List<City> tour = new ArrayList<City>();

	private final CitySet cities;
	
	public Tour(CitySet cities){
		this.cities = cities;
	}
	
	public void addCity(int id) {
		tour.add(cities.getCity(id));
	}

	public boolean isValidPermutation() {

		return false;
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

		PointTranslator trans = new PointTranslator(b, target, null);
		for (int i = 0; i < tour.size(); i++) {
			City c1 = tour.get(i);
			City c2 = tour.get((i + 1) % tour.size()); // wrap around to one
			Point p1 = trans.getDestPoint(new Point(c1.X, c1.Y));
			Point p2 = trans.getDestPoint(new Point(c2.X, c2.Y));
			g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
		}
	}
	
	public static Tour GenerateRandom(CitySet cities){
		
		Tour newTour = new Tour(cities);
	
		Integer[] cityIds = new Integer[cities.size()];
		int index = 0;
		for(City c : cities){
			cityIds[index++] = c.ID;
		}
		permute(cityIds);
		for(int id : cityIds){
			newTour.addCity(id);
		}
		
		return newTour;
	}
	
    // Implements Fisher-Yates: http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
	private static <T> void permute(T[] input){
		Random rand = new Random();
		for(int i = input.length - 1; i > 1; i--){
			int swapIndex = rand.nextInt(i + 1);
			T swapValue = input[swapIndex];
			input[swapIndex] = input[i];
			input[i] = swapValue;
		}
	}
	
	
}
