package hps.nyu.fa14;

class ProblemModel {

	CitySet AllCities;
	CitySet currentCities;
	Tour currentTour = null;
	
	double currentTourLength = Double.NaN;
	
	ProblemModel(CitySet allCities){
		AllCities = allCities;
	}
	
	
}
