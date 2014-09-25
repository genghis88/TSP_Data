package hps.nyu.fa14;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class CityBounder implements MouseListener, MouseMotionListener {

	private final ProblemModel model;

	CityBounder(ProblemModel model) {
		this.model = model;
	}

	java.awt.Point startPoint;
	java.awt.Point currentPoint;
	private boolean isDown;

	private AbstractPointTranslator trans;

	public void render(Graphics g, Rectangle r) {

		// Find the bounds
		Bounds b = model.getCurrentCities().getBounds();
		Bounds target = new Bounds();
		target.xMax = r.getMaxX();
		target.xMin = r.getMinX();
		target.yMax = r.getMaxY();
		target.yMin = r.getMinY();

		g.setColor(Color.green);

		trans = new PointTranslator(b, target, null);
		trans = new MirrorXYPointTranslator(target, trans);

		if (startPoint != null && currentPoint != null) {
			int x = Math.min(startPoint.x, currentPoint.x);
			int y = Math.min(startPoint.y, currentPoint.y);
			int w = Math.abs(currentPoint.x - startPoint.x);
			int h = Math.abs(currentPoint.y - startPoint.y);
			g.drawRect(x, y, w, h);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isDown = true;
		startPoint = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isDown = false;
		// Find all the points in this region
		model.setCurrentCities(getCurrentCapturedCities());
		model.currentTour = null;
		currentPoint = null;
		startPoint = null;
	}

	private CitySet getCurrentCapturedCities() {
		System.out
				.println("Current Bounds: " + model.getCurrentCities().getBounds());

		Point p1 = trans.getSourcePoint(new Point(currentPoint.x,
				currentPoint.y));
		Point p2 = trans.getSourcePoint(new Point(startPoint.x, startPoint.y));

		Bounds b = new Bounds();
		b.xMin = Math.min(p1.x, p2.x);
		b.xMax = Math.max(p1.x, p2.x);
		b.yMin = Math.min(p1.y, p2.y);
		b.yMax = Math.max(p1.y, p2.y);

		System.out.println("Captured Bounds: " + b);
		CitySet newSet = new CitySet();
		for (City c : model.getCurrentCities()) {
			if (c.X > b.xMin && c.X < b.xMax && c.Y > b.yMin && c.Y < b.yMax) {
				newSet.addCity(c);
			}
		}
		System.out.println(String.format("Captured %d cities", newSet.size()));
		return newSet;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isDown) {
			currentPoint = e.getPoint();
			// getCurrentCapturedCities();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

}
