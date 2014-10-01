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
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CitySet implements Iterable<City> {

	private final Map<Integer, City> cities = new HashMap<Integer, City>();

	public void addCity(City c) {
		cities.put(c.ID, c);
	}

	public City getCity(int id) {
		return cities.get(id);
	}

	public boolean containsCity(int id) {
		return cities.containsKey(id);
	}

	public int size(){
		return cities.size();
	}
	
	@Override
	public Iterator<City> iterator() {
		return cities.values().iterator();
	}

	public void render(Graphics g, Rectangle r) {

		// Find the bounds
		Bounds b = getBounds();
		Bounds target = new Bounds();
		target.xMax = r.getMaxX();
		target.xMin = r.getMinX();
		target.yMax = r.getMaxY();
		target.yMin = r.getMinY();
		
		g.setColor(Color.red);

		AbstractPointTranslator trans = new PointTranslator(b, target, null);
		trans = new MirrorXYPointTranslator(target, trans);
		for (City c : this) {
			Point p = trans.getDestPoint(new Point(c.X, c.Y));
			g.fillOval((int)p.x - 2, (int)p.y - 2, 4, 4);
		}
	}

	public Bounds getBounds() {
		Bounds b = new Bounds();
		b.xMin = Double.MAX_VALUE;
		b.xMax = Double.MIN_VALUE;
		b.yMin = Double.MAX_VALUE;
		b.yMax = Double.MIN_VALUE;

		for (City c : this) {
			b.xMin = Math.min(b.xMin, c.X);
			b.xMax = Math.max(b.xMax, c.X);
			b.yMin = Math.min(b.yMin, c.Y);
			b.yMax = Math.max(b.yMax, c.Y);
		}
		return b;
	}
	
	public void saveCities(OutputStream output) throws IOException{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
		for(City c : this.cities.values()){
			bw.write(String.format("%d %f %f%n", c.ID, c.Y, c.X));			
		}
		bw.close();
	}

	public static CitySet GenerateRandom(int count) {

		CitySet set = new CitySet();
		for (int i = 0; i < count; i++) {
			City c = new City(i, Math.random() * 500, Math.random() * 250 * 75);
			set.addCity(c);
		}
		return set;
	}

	public static CitySet LoadFromUrl(String location) {
		CitySet set = new CitySet();
		try{
			URL url = new URL(location);
			InputStream in = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String line;
			while((line = br.readLine()) != null){
				// break into 3 parts
				String[] parts = line.split("\\s");
				try{
					int id = Integer.parseInt(parts[0]);
					// Everything seems to make more sense if we interpret the first coordinate as Y
					double y = Double.parseDouble(parts[1]);
					double x = Double.parseDouble(parts[2]);
					set.addCity(new City(id, x, y));
				} catch (Exception ex){
					// just continue
					//System.err.println("There was problem loading the data");
					//ex.printStackTrace();
				}
			}
		} catch(Exception ex){
			// Report this as a problem
			ex.printStackTrace();
		}
		return set;
	}

}
