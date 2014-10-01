package hps.nyu.fa14.tournament;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * Double buffering concept from Ken Perlin's Computer Graphics class
 * 
 * @author ck1456@nyu.edu
 */
public class TournamentApplet extends Applet implements Runnable {

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
	
	private final TournamentControl frame;
	private final TournamentModel model;
	
	public TournamentApplet(){
		model = new TournamentModel();
		model.refreshTests();
		frame = new TournamentControl(model);
	}
	
	private void setup(){
		model.refreshTeams();
	}
	
	/**
	 * The main render routine
	 * @param g
	 */
	private void render(Graphics g){
		
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		model.render(g, new Rectangle(width, height));
	}

}
