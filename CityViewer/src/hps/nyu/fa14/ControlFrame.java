package hps.nyu.fa14;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class ControlFrame implements IModelViewer {

	private final JFrame mainFrame;
	private final ProblemModel model;
	private final JTextArea solutionText;
	private final JTextArea cityText;
	private final JLabel lengthValueLabel;

	ControlFrame(ProblemModel problemModel) {
		this.model = problemModel;

		mainFrame = new JFrame("Controls");

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		cityText = new JTextArea();
		cityText.setAlignmentX(Component.LEFT_ALIGNMENT);
		cityText.setSize(100, 100);
		
		mainPanel.add(getCityPanel());

		solutionText = new JTextArea();
		solutionText.setAlignmentX(Component.LEFT_ALIGNMENT);
		solutionText.setSize(100, 100);

		
		
		lengthValueLabel = new JLabel("Not Set");
		
		mainPanel.add(getTourPanel());

		

		// Setup complete, show it
		mainFrame.add(mainPanel);

		mainFrame.setSize(400, 600);
		mainFrame.setVisible(true);
	}

	private JPanel getCityPanel() {

		JPanel cityPanel = new JPanel();
		{
			cityPanel.setLayout(new BoxLayout(cityPanel, BoxLayout.Y_AXIS));
			cityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			cityPanel.setAlignmentY(Component.TOP_ALIGNMENT);

			cityPanel.add(new JLabel("Specify Cities:"));
			cityPanel
					.add(new JLabel(
							"You can copy this data into your own file"));
			JPanel sampleButtonsPanel = new JPanel();
			{
				sampleButtonsPanel.setLayout(new BoxLayout(sampleButtonsPanel,
						BoxLayout.X_AXIS));
				sampleButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

				sampleButtonsPanel.add(new JLabel("Sample "));
				for (int i = 1; i <= 5; i++) {
					final int sampleID = i;
					JButton sampleButton = new JButton(String.format(
							"%d", sampleID));
					{
						sampleButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								model.setCurrentCities(CitySet.LoadFromUrl(String
										.format("https://files.nyu.edu/ck1456/public/hps/tsp/data/sample_%d.tsp",
												sampleID)));
								clearTour();
							}
						});
						sampleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
						sampleButtonsPanel.add(sampleButton);
					}
				}
				
				JButton allCitiesButton = new JButton("All Cities");
				{
					allCitiesButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							model.setCurrentCities(model.AllCities);
							clearTour();
						}
					});
					allCitiesButton.setAlignmentX(Component.LEFT_ALIGNMENT);
					sampleButtonsPanel.add(allCitiesButton);
				}
			}
			cityPanel.add(sampleButtonsPanel);

			cityPanel.add(new JScrollPane(cityText));
			
			JPanel cityButtonsPanel = new JPanel();
			{
				cityButtonsPanel.setLayout(new BoxLayout(cityButtonsPanel,
						BoxLayout.X_AXIS));
				cityButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

				JButton saveCitiesButton = new JButton("Save Cities");
				{
					saveCitiesButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							saveCitiesOutputFile();
						}
					});
					saveCitiesButton.setAlignmentX(Component.LEFT_ALIGNMENT);
					cityButtonsPanel.add(saveCitiesButton);
				}

				JButton loadCitiesButton = new JButton("Load Cities");
				{
					loadCitiesButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							loadCitiesFile();
						}
					});
					loadCitiesButton.setAlignmentX(Component.LEFT_ALIGNMENT);
					cityButtonsPanel.add(loadCitiesButton);
				}
				
			}
			//cityPanel.add(cityButtonsPanel);
		}

		return cityPanel;
	}

	private JPanel getTourPanel() {

		JPanel tourPanel = new JPanel();
		{
			tourPanel.setLayout(new BoxLayout(tourPanel,
					BoxLayout.Y_AXIS));
			tourPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			tourPanel.add(new JLabel("Specify tour:"));

			tourPanel
			.add(new JLabel(
					"You can paste your own solution here"));
			tourPanel
			.add(new JLabel(
					"Then click Evaluate to calculate the length"));
			
			tourPanel.add(new JScrollPane(solutionText));
			
			JPanel tourButtonPanel = new JPanel();
			{
				tourButtonPanel.setLayout(new BoxLayout(tourButtonPanel,
						BoxLayout.X_AXIS));
				tourButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

				JButton clearTourButton = new JButton("Clear Tour");
				{
					clearTourButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							clearTour();
						}
					});
					clearTourButton.setAlignmentX(Component.LEFT_ALIGNMENT);
					tourButtonPanel.add(clearTourButton);
				}

				JButton randomTourButton = new JButton("Generate Random Tour");
				{
					randomTourButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							generateRandomTour();
							evaluateSolution(solutionText.getText());
						}
					});
					randomTourButton.setAlignmentX(Component.LEFT_ALIGNMENT);
					tourButtonPanel.add(randomTourButton);
				}
			}
			tourPanel.add(tourButtonPanel);
			
			JPanel saveButtonPanel = new JPanel();
			{
				saveButtonPanel.setLayout(new BoxLayout(saveButtonPanel,
						BoxLayout.X_AXIS));
				saveButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

				JButton saveTourButton = new JButton("Save Tour");
				{
					saveTourButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							saveTourFile();
						}
					});
					saveTourButton.setAlignmentX(Component.LEFT_ALIGNMENT);
					saveButtonPanel.add(saveTourButton);
				}

				JButton loadTourButton = new JButton("Load Tour");
				{
					loadTourButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							loadTourFile();
						}
					});
					loadTourButton.setAlignmentX(Component.LEFT_ALIGNMENT);
					saveButtonPanel.add(loadTourButton);
				}
			}
			//tourPanel.add(saveButtonPanel);
			
			JPanel evaluatePanel = new JPanel();
			{
				evaluatePanel.setLayout(new BoxLayout(evaluatePanel,
						BoxLayout.X_AXIS));
				evaluatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				JButton evaluateButton = new JButton("Evaluate");
				{
					evaluateButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							evaluateSolution(solutionText.getText());
						}
					});
					evaluateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
					evaluatePanel.add(evaluateButton);
				}

				JLabel lengthLabel = new JLabel("Tour Length: ");
				evaluatePanel.add(lengthLabel);
				
				evaluatePanel.add(lengthValueLabel);
			}
			tourPanel.add(evaluatePanel);
		}
		return tourPanel;

	}

	private void saveCitiesOutputFile() {
		CitySet set = model.getCurrentCities();
		if (set == null) {
			System.out.println("No cities to save");
			return;
		}

		// prompt for a location to save the file
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			// Output the cities
			try {
				set.saveCities(new FileOutputStream(file));
			} catch (Exception ex) {
				System.out.println("The cities file could not be saved");
			}
		}
	}

	private void loadCitiesFile() {
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			model.setCurrentCities(CitySet.LoadFromUrl(file.toURI().toString()));
		}
	}

	private void evaluateSolution(String text) {
		// System.out.println("evaluate: " + text);

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				solutionText.getText().getBytes());
		try {
			Tour t = Tour.parseTour(inputStream, model.getCurrentCities());
			model.currentTour = t;
			model.currentTourLength = t.evaluate();
			lengthValueLabel.setText(String.format("%f",
					model.currentTourLength));
		} catch (Exception ex) {
			System.out.println("Cannot evaluate solution: " + ex.getMessage());
		}

	}

	private void saveTourFile() {
		Tour t = model.currentTour;
		if (t == null) {
			System.out.println("No Tour to save");
			return;
		}

		// prompt for a location to save the file
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			// Output the cities
			try {
				t.saveTour(new FileOutputStream(file));
			} catch (Exception ex) {
				System.out.println("The tour file could not be saved");
			}
		}
	}

	private void loadTourFile() {
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				model.currentTour = Tour.parseTour(new FileInputStream(file),
						model.getCurrentCities());
			} catch (Exception ex) {
				System.out.println("Cannot load tour");
				ex.printStackTrace();
			}
		}
	}

	private void clearTour() {
		solutionText.setText("");
		model.currentTour = null;
		model.currentTourLength = Double.NaN;
		lengthValueLabel.setText("Not Set");
	}

	private void generateRandomTour() {
		CitySet set = model.getCurrentCities();
		if (set != null) {
			Tour randomTour = Tour.GenerateRandom(model.getCurrentCities());
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			try {
				randomTour.saveTour(byteStream);
			} catch (Exception ex) {
				System.out.println("Cannot display tour");
				ex.printStackTrace();
			}
			solutionText.setText(byteStream.toString());
			model.currentTour = randomTour;
			model.currentTourLength = randomTour.evaluate();
		}
	}

	@Override
	public void update() {
		ByteArrayOutputStream cityBytes = new ByteArrayOutputStream();
		try{
		model.getCurrentCities().saveCities(cityBytes);
		} catch(Exception ex){
			System.out.println("Cannot update cities");
			ex.printStackTrace();
		}
		cityText.setText(cityBytes.toString());
		
	}

}
