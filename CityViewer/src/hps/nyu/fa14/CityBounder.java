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

	public void render(Graphics g, Rectangle r) {

		// Find the bounds
		// Bounds b = model.currentCities.getBounds();
		// Bounds target = new Bounds();
		// target.xMax = r.getMaxX();
		// target.xMin = r.getMinX();
		// target.yMax = r.getMaxY();
		// target.yMin = r.getMinY();

		g.setColor(Color.green);

		// AbstractPointTranslator trans = new PointTranslator(b, target, null);
		// trans = new MirrorXYPointTranslator(target, trans);

		if (startPoint != null && currentPoint != null) {
			g.drawRect(startPoint.x, startPoint.y, currentPoint.x
					- startPoint.x, currentPoint.y - startPoint.y);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//model.currentTour = Tour.GenerateRandom(model.currentCities);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
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
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if (isDown) {
			currentPoint = e.getPoint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

}
