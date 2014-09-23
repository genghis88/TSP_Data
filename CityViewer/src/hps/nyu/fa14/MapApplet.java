package hps.nyu.fa14;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Double buffering concept from Ken Perlin's Computer Graphics class
 * 
 * @author ck1456@nyu.edu
 */
public class MapApplet extends Applet implements Runnable, MouseListener {

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
	
	
	CitySet set;
	Tour tour;
	
	private void setup(){
		//set = CitySet.GenerateRandom(500000);
		//set = CitySet.LoadFromUrl("https://files.nyu.edu/ck1456/public/hps/tsp/usa115475.tsp");
		set = CitySet.LoadFromUrl("https://files.nyu.edu/ck1456/public/hps/tsp/data/sample_1.tsp");
		tour = Tour.GenerateRandom(set);
		
		System.out.println(String.format("Loaded %d cities", set.size()));
		addMouseListener(this);
	}
	
	/**
	 * The main render routine
	 * @param g
	 */
	private void render(Graphics g){
		
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		

		if(set != null){
			set.render(g, new Rectangle(width, height));
		}
		if(tour != null){
			tour.render(g, new Rectangle(width, height));
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		tour = Tour.GenerateRandom(set);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	

}
