package hps.nyu.fa14;

public class Bounds {

	public double xMin;
	public double xMax;
	public double yMin;
	public double yMax;
	
	public double xDiff(){
		return xMax - xMin;
	}
	
	public double yDiff(){
		return yMax - yMin;
	}
	
	public Point center(){
		Point c = new Point();
		c.x = xMin + (xDiff() / 2.0);
		c.y = yMin + (yDiff() / 2.0);
		return c;
	}
	
	@Override
	public String toString(){
		return String.format("X = %f:%f Y = %f:%f", xMin, xMax, yMin, yMax);
	}
}

