package hps.nyu.fa14.tournament;

import hps.nyu.fa14.Tour;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class Team {

	public final String name;
	
	public final Map<String, Tour> tours = new HashMap<String, Tour>();
	public final Map<String, Double> results = new HashMap<String, Double>();
	public final Map<String, Double> rank = new HashMap<String, Double>();
	
	public Team(String name){
		this.name = name;
	}
	
	
	public void render(String testFileName, Graphics g, Rectangle r) {

		// render team name, results, rank
		g.setColor(Color.yellow);
		g.drawString(name, r.x, r.y);
		
		if(tours.containsKey(testFileName)){
			tours.get(testFileName).render(g, r);
		}
		
		if(results.containsKey(testFileName)){
			g.setColor(Color.yellow);
			g.drawString(String.format("%f", results.get(testFileName)), r.x, r.y + r.height - 2);
		}
		
	}
	
	public Double getRank(String testFileName){
		if(rank.containsKey(testFileName)){
			return rank.get(testFileName);
		}
		// else return default
		return 1.5;
	}
	
	public Double getResults(String testFileName){
		if(results.containsKey(testFileName)){
			return results.get(testFileName);
		}
		// else return default
		return Double.NaN;
	}
	
	public String roundDetail(String testFileName){
		return String.format("%f   %s   %f", getRank(testFileName), name, getResults(testFileName));
	}
}
