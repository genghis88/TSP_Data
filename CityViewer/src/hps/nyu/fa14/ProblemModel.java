package hps.nyu.fa14;

import java.util.ArrayList;
import java.util.List;

class ProblemModel {

	CitySet AllCities;
	Tour currentTour = null;
	private CitySet currentCities;
	
	
	double currentTourLength = Double.NaN;
	
	ProblemModel(CitySet allCities){
		AllCities = allCities;
	}
	
	public void setCurrentCities(CitySet set){
		currentCities = set;
		updateViewers();
	}
	
	public CitySet getCurrentCities(){
		return currentCities;
	}
	
	private final List<IModelViewer> viewers = new ArrayList<IModelViewer>();
	public void addViewer(IModelViewer viewer){
		viewers.add(viewer);
	}
	
	private void updateViewers(){
		for(IModelViewer v : viewers){
			v.update();
		}
	}
	
}
