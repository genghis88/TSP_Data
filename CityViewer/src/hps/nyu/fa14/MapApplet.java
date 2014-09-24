package hps.nyu.fa14;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Double buffering concept from Ken Perlin's Computer Graphics class
 * 
 * @author ck1456@nyu.edu
 */
public class MapApplet extends Applet implements Runnable {

	private static final long serialVersionUID = 4901830364284199595L;

	private Thread t;
	private Graphics buffer;
	private Image image;
	private int width = 0;
	private int height = 0;

	@Override
	public void update(Graphics g) {
		if (width != getWidth() || height != getHeight()) {
			width = getWidth();
			height = getHeight();
			image = createImage(width, height);
			buffer = image.getGraphics();
		}
		render(buffer);
		g.drawImage(image, 0, 0, this);
	}

	@Override
	public void start() {
		setup();
		if (t == null) {
			(t = new Thread(this)).start();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				repaint();
				Thread.sleep(30);
			}
		} catch (Exception ex) {
		}
	}
	
	private final ProblemModel model;
	
	private final ControlFrame frame;
	
	private CityBounder bounder;
	
	public MapApplet(){
//		model = new ProblemModel(CitySet.LoadFromUrl("https://files.nyu.edu/ck1456/public/hps/tsp/usa115475.tsp"));
		model = new ProblemModel(CitySet.LoadFromUrl("https://files.nyu.edu/ck1456/public/hps/tsp/data/sample_5.tsp"));
		model.currentCities = model.AllCities;
		System.out.println(String.format("Loaded %d cities", model.AllCities.size()));
		//model.currentTour = Tour.GenerateRandom(model.AllCities);
		frame = new ControlFrame(model);
		bounder = new CityBounder(model);
	}
	
	private void setup(){
		
		addMouseListener(bounder);
		addMouseMotionListener(bounder);
		
		
	}
	
	/**
	 * The main render routine
	 * @param g
	 */
	private void render(Graphics g){
		
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		CitySet set = model.currentCities;
		if(set != null){
			set.render(g, new Rectangle(width, height));
		}
		Tour tour = model.currentTour;
		if(tour != null){
			tour.render(g, new Rectangle(width, height));
		}
		bounder.render(g, new Rectangle(width, height));
		
	}

}
