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

class ControlFrame {

	private final JFrame mainFrame;
	private final ProblemModel model;
	private final JTextArea solutionText;
	private final JLabel lengthValueLabel;

	ControlFrame(ProblemModel problemModel) {
		this.model = problemModel;

		mainFrame = new JFrame("Controls");

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		mainPanel.add(new JLabel("Specify cities:"));
		JPanel citiesPanel = new JPanel();
		{
			citiesPanel.setLayout(new BoxLayout(citiesPanel, BoxLayout.X_AXIS));
			citiesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JButton saveCitiesButton = new JButton("Save Cities");
			{
				saveCitiesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						saveCitiesOutputFile();
					}
				});
				saveCitiesButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				citiesPanel.add(saveCitiesButton);
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
				citiesPanel.add(loadCitiesButton);
			}

			JButton allCitiesButton = new JButton("All Cities");
			{
				allCitiesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						model.currentCities = model.AllCities;
					}
				});
				allCitiesButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				citiesPanel.add(allCitiesButton);
			}
		}
		mainPanel.add(citiesPanel);

		mainPanel.add(new JLabel("Specify tour:"));
		
		solutionText = new JTextArea();
		solutionText.setAlignmentX(Component.LEFT_ALIGNMENT);
		solutionText.setSize(100, 100);
		mainPanel.add(new JScrollPane(solutionText));
		
		JPanel tourPanel = new JPanel();
		{
			tourPanel.setLayout(new BoxLayout(tourPanel, BoxLayout.X_AXIS));
			tourPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JButton saveTourButton = new JButton("Save Tour");
			{
				saveTourButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						saveTourFile();
					}
				});
				saveTourButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				tourPanel.add(saveTourButton);
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
				tourPanel.add(loadTourButton);
			}
			
			JButton clearTourButton = new JButton("Clear Tour");
			{
				clearTourButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						clearTour();
					}
				});
				clearTourButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				tourPanel.add(clearTourButton);
			}
			
			JButton randomTourButton = new JButton("Generate Random Tour");
			{
				randomTourButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						generateRandomTour();
					}
				});
				randomTourButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				tourPanel.add(randomTourButton);
			}
		}
		mainPanel.add(tourPanel);

		JPanel evaluatePanel = new JPanel();
		{
			evaluatePanel.setLayout(new BoxLayout(evaluatePanel, BoxLayout.X_AXIS));
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
			
			JLabel lengthLabel = new JLabel("Tour Length:");
			evaluatePanel.add(lengthLabel);

			lengthValueLabel = new JLabel("Not Set");
			evaluatePanel.add(lengthValueLabel);
		}
		mainPanel.add(evaluatePanel);
		
		// Setup complete, show it
		mainFrame.add(mainPanel);

		mainFrame.setSize(500, 400);
		mainFrame.setVisible(true);
	}

	private void saveCitiesOutputFile() {
		CitySet set = model.currentCities;
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
			model.currentCities = CitySet.LoadFromUrl(file.toURI().toString());
		}
	}

	private void evaluateSolution(String text) {
		// System.out.println("evaluate: " + text);

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				solutionText.getText().getBytes());
		try {
			Tour t = Tour.parseTour(inputStream, model.currentCities);
			model.currentTour = t;
			model.currentTourLength = t.evaluate();
			lengthValueLabel.setText(String.format("%f",
					model.currentTourLength));
		} catch (Exception ex) {
			System.out.println("Cannot evaluate solution: " + ex.getMessage());
		}

	}

	private void saveTourFile(){
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

	private void loadTourFile(){
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try{
			model.currentTour = Tour.parseTour(new FileInputStream(file), model.currentCities);
			} catch(Exception ex){
				System.out.println("Cannot load tour");
				ex.printStackTrace();
			}
		}
	}
	
	private void clearTour(){
		solutionText.setText("");
		model.currentTour = null;
		model.currentTourLength = Double.NaN;
		lengthValueLabel.setText("Not Set");
	}
	
	private void generateRandomTour() {
		CitySet set = model.currentCities;
		if (set != null) {
			Tour randomTour = Tour.GenerateRandom(model.currentCities);
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

}
