package hps.nyu.fa14;

public class City {

	public final int ID;
	public final double X;
	public final double Y;
	
	public City(int id, double x, double y){
		ID = id;
		X = x;
		Y = y;
	}
	
	public double distance(City other){
		return Math.sqrt(((this.X - other.X)*(this.X - other.X)) + ((this.Y - other.Y)*(this.Y - other.Y))); 
	}
}
