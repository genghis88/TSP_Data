package hps.nyu.fa14.tournament;

import hps.nyu.fa14.IModelViewer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class TournamentControl implements IModelViewer {

	private final TournamentModel model;
	private final JFrame mainFrame;
	private final JPanel leaderboard;

	TournamentControl(TournamentModel model) {
		this.model = model;
		model.addViewer(this);

		mainFrame = new JFrame("Tournament");

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		mainPanel.add(getTestPanel());

		leaderboard = new JPanel();
		mainPanel.add(leaderboard);

		setupLeaderboard();

		// Setup complete, show it
		mainFrame.add(mainPanel);

		mainFrame.setSize(400, 600);
		mainFrame.setVisible(true);
	}

	private JPanel getTestPanel() {

		JPanel testPanel = new JPanel();
		{
			testPanel.setLayout(new BoxLayout(testPanel, BoxLayout.Y_AXIS));
			testPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			testPanel.setAlignmentY(Component.TOP_ALIGNMENT);

			testPanel.add(new JLabel("Round:"));
			JPanel testButtonsPanel = new JPanel();
			{
				testButtonsPanel.setLayout(new BoxLayout(testButtonsPanel,
						BoxLayout.Y_AXIS));
				testButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

				for (int i = 1; i <= model.tests.size(); i++) {
					final int testID = i;
					JButton testButton = new JButton(String.format("%d - %s",
							testID, model.testFileNames.get(testID - 1)));
					{
						testButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								model.setCurrentRound(testID);
							}
						});
						testButton.setAlignmentX(Component.LEFT_ALIGNMENT);
						testButtonsPanel.add(testButton);
					}
				}
			}
			testPanel.add(testButtonsPanel);
		}
		return testPanel;
	}

	private void setupLeaderboard() {

		leaderboard.removeAll();
		leaderboard.setLayout(new BoxLayout(leaderboard, BoxLayout.Y_AXIS));
		leaderboard.setAlignmentX(Component.LEFT_ALIGNMENT);
		leaderboard.setAlignmentY(Component.TOP_ALIGNMENT);

		leaderboard.add(new JLabel("Leaderboard:"));

		// for the current round, sort teams by rank and then
		if (!(model.getCurrentRound() > 0 && model.getCurrentRound() <= model.testFileNames
				.size())) {
			return;
		}
		final String currentTestFile = model.testFileNames
				.get(model.getCurrentRound() - 1);
		List<Team> teams = new ArrayList<Team>(model.teams);
		Collections.sort(teams, new Comparator<Team>() {
			@Override
			public int compare(Team t1, Team t2) {
				return t1.getRank(currentTestFile).compareTo(
						t2.getRank(currentTestFile));
			}
		});
		
		for(Team t: teams){
			leaderboard.add(new JLabel(t.roundDetail(currentTestFile)));
		}
		mainFrame.getContentPane().validate();
		mainFrame.getContentPane().repaint();
	}

	@Override
	public void update() {
		setupLeaderboard();
	}

}
