package hps.nyu.fa14.tournament;

import hps.nyu.fa14.CitySet;
import hps.nyu.fa14.IModelViewer;
import hps.nyu.fa14.Tour;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TournamentModel {

	public final List<Team> teams = new ArrayList<Team>();

	public final List<CitySet> tests = new ArrayList<CitySet>();
	public final List<String> testFileNames = new ArrayList<String>();

	private static final File tournamentDir = new File("../tournament");
	private static final File teamDir = new File(tournamentDir, "teams");
	private static final File testDir = new File(tournamentDir, "tests");

	private int currentRound = 0;
	public void setCurrentRound(int round){
		currentRound = round;
		updateViewers();
	}
	
	public int getCurrentRound(){
		return currentRound;
	}

	public void render(Graphics g, Rectangle rectangle) {

		CitySet citiesToRender = null;
		String testFileName = null;
		if (currentRound > 0 && currentRound <= tests.size()) {
			citiesToRender = tests.get(currentRound - 1);
			testFileName = testFileNames.get(currentRound - 1);
		}
		// Divide the height by the number of teams and render each tour
		int frameHeight = rectangle.height / teams.size();
		for (int i = 0; i < teams.size(); i++) {
			Rectangle r = new Rectangle(rectangle.x, rectangle.y
					+ (i * frameHeight), rectangle.width, frameHeight);

			Team currentTeam = teams.get(i);
			int margin = 8;
			Rectangle inset = new Rectangle(r.x + margin, r.y + margin, r.width
					- (2 * margin), r.height - (2 * margin));
			r = inset;
			if (citiesToRender != null) {
				citiesToRender.render(g, r);
			}
			
			currentTeam.render(testFileName, g, r);

			// Draw a border
			g.setColor(Color.white);
			g.drawRect(r.x, r.y, r.width, r.height);
		}
	}

	public void refreshTests() {
		tests.clear();
		testFileNames.clear();
		// Get all files in the test directory and load them in order
		File[] testFiles = testDir.listFiles();
		Arrays.sort(testFiles);
		for (File f : testFiles) {
			System.out.println("Loading " + f.toURI());
			tests.add(CitySet.LoadFromUrl(f.toURI().toString()));
			testFileNames.add(f.getName());
		}
	}

	public void refreshTeams() {
		teams.clear();
		// Get all files in the test directory and load them in order
		File[] teamDirs = teamDir.listFiles();
		for (File f : teamDirs) {
			if (f.isDirectory()) {
				System.out.println("Loading " + f.toURI());
				Team newTeam = new Team(f.getName());

				for (int i = 0; i < tests.size(); i++) {
					String testFileName = testFileNames.get(i);
					CitySet cities = tests.get(i);
					String tourFileName = testFileName.replaceAll(".tsp",
							".out");
					try {
						// replace extension
						File tourFile = new File(f, tourFileName);
						Tour t = Tour.parseTour(new FileInputStream(tourFile),
								cities);
						newTeam.tours.put(testFileName, t);
						newTeam.results.put(testFileName, t.evaluate());
					} catch (Exception ex) {
						System.err.println(String.format(
								"Cannot load tour (%s) for team %s",
								tourFileName, newTeam.name));
						ex.printStackTrace();
					}
				}
				teams.add(newTeam);
			}
		}
	}

	public void scoreTeams() {

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
