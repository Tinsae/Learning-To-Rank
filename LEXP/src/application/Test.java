package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ciir.umass.edu.eval.Evaluator;
import ciir.umass.edu.learning.RANKER_TYPE;
import ciir.umass.edu.metric.ERRScorer;
import ciir.umass.edu.utilities.SimpleMath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Test extends VBox implements Initializable {

	// required UI controls
	@FXML
	ListView<String> lvSimpleModels;
	@FXML
	ListView<String> lvKFoldModels;

	@FXML
	private RadioButton rdTest;
	@FXML
	private RadioButton rdRank;
	@FXML
	private TextField txtTestingFile;
	@FXML
	ComboBox<String> cmbMetric;
	@FXML
	Spinner<Integer> spMetric;
	@FXML
	Spinner<Integer> spGmax;
	@FXML
	RadioButton rdNormalizeYes;
	@FXML
	RadioButton rdNormalizeNo;
	@FXML
	private TextField txtResultFileName;
	@FXML
	private Button btnLoadSelected;
	
	// load models
	String savedModelFile = "";
	List<String> savedModelFiles = new ArrayList<String>();

	// data to be tested(with -test)
	private String testFile;
	List<String> testFiles = new ArrayList<String>();
	private File testFileBytes;
	
	// results of testing (with -idv)
	String prpFile = "";

	// file to be ranked( with -rank)
	String rankFile = "";
	// storage location for the result of ranking(with -score)
	String scoreFile = "";
	
	private File defaultSimpleModelsDir;
	private File defaultKFoldModelsDir;
	private File defaultOutputDir;
	private File defaultDatasetDirectory;

	
	// metric types
	String[] mType;

	private Evaluator eval;

	
	RANKER_TYPE[] rType2;
	int rankerType;
	String trainMetric;
	String testMetric;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		rankerType = 4;
		trainMetric = "ERR@10";
		testMetric = "ERR@10";

		rType2 = new RANKER_TYPE[] { RANKER_TYPE.MART, RANKER_TYPE.RANKNET, RANKER_TYPE.RANKBOOST, RANKER_TYPE.ADARANK,
				RANKER_TYPE.COOR_ASCENT, RANKER_TYPE.LAMBDARANK, RANKER_TYPE.LAMBDAMART, RANKER_TYPE.LISTNET,
				RANKER_TYPE.RANDOM_FOREST, RANKER_TYPE.LINEAR_REGRESSION };
		mType = new String[] { "MAP", "NDCG", "DCG", "P", "RR", "BEST", "ERR" };
		cmbMetric.getItems().clear();
		cmbMetric.getItems().addAll(mType);

		spMetric.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30));
		// setting a default value
		spMetric.getValueFactory().setValue(10);

		spMetric.valueProperty().addListener(
				(obs, oldValue, newValue) -> testMetric = mType[cmbMetric.getSelectionModel().getSelectedIndex()] + "@"
						+ spMetric.getValue());

		
		spGmax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10));
		spGmax.valueProperty().addListener(
				(obs, oldValue, newValue) -> testMetric = mType[cmbMetric.getSelectionModel().getSelectedIndex()] + "@"
						+ spMetric.getValue());
		
		if(spGmax.getValue() > 0)
		{
			ERRScorer.MAX = Math.pow(2, Double.parseDouble(spGmax.getValue().toString()));			
		}
		String homeDirectory = System.getProperty("user.home");
		
		File simpleModels = new File(homeDirectory + "\\LEXP\\simplemodels");
		defaultSimpleModelsDir = simpleModels;
		
		File kFoldModels = new File(homeDirectory + "\\LEXP\\kfoldmodels");
		defaultKFoldModelsDir = kFoldModels;

		File dataSetsDir = new File(homeDirectory + "\\LEXP\\datasets");
		defaultDatasetDirectory = dataSetsDir;

		File outputDir = new File(homeDirectory +"\\LEXP\\output");
		defaultOutputDir=outputDir;
		
		if(!outputDir.exists())
		{
			outputDir.mkdirs();
		}
		
		
		if (!defaultSimpleModelsDir.exists() || defaultSimpleModelsDir.isFile()) {
			System.out.println("no simple models in the default directory");
		}

		else {
			refershSimpleModels();
			lvSimpleModels.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

				public void changed(ObservableValue<? extends String> ov,

						final String oldvalue, final String newvalue) {
					// load selected model

					String prefix = defaultSimpleModelsDir + "\\";
					savedModelFile = prefix + lvSimpleModels.getSelectionModel().getSelectedItem();
				}
			});

		}
		
		if (!defaultKFoldModelsDir.exists() || defaultKFoldModelsDir.isFile()) {
			System.out.println("no kfold models in the default directory");
		}

		else {
			refreshKFoldModels();
		
		}	
	}
	
	public void refershSimpleModels()
	{
		lvSimpleModels.getItems().clear();
		File[] listOfFiles = defaultSimpleModelsDir.listFiles();

		for (int c = 0; c < listOfFiles.length; c++) {
			if(!listOfFiles[c].isDirectory())
			{
				lvSimpleModels.getItems().add(listOfFiles[c].getName());
			}
		}
		
	}
	public void refreshKFoldModels()
{
	lvKFoldModels.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	lvKFoldModels.getItems().clear();
	File[] listOfFiles = defaultKFoldModelsDir.listFiles();

	for (int c = 0; c < listOfFiles.length; c++) {
		if(!listOfFiles[c].isDirectory())
		{
		lvKFoldModels.getItems().add(listOfFiles[c].getName());
		}
	}
	}
	public void loadSelectedClick(ActionEvent e)
	{
		String prefix = defaultKFoldModelsDir + "\\";

		for(String s : lvKFoldModels.getSelectionModel().getSelectedItems())
		{
			savedModelFile = prefix + s;
			savedModelFiles.add(savedModelFile);
		}	
		System.out.println("Loaded");
	}
	public void generateClick(ActionEvent e) {
		// I passed three arguments but only testMetric was required
		// it is like this because Evaluator has no overload that 
		// takes only testMetric
		eval = new Evaluator(rType2[rankerType], trainMetric, testMetric);

		String theFileName = txtResultFileName.getText();

		if (rdRank.isSelected()) {
			scoreFile = defaultOutputDir + "\\" + theFileName;
			
				if(savedModelFiles.size() > 1)//models trained via cross-validation
					eval.score(savedModelFiles, rankFile, scoreFile);
				else //a single model
					eval.score(savedModelFile, rankFile, scoreFile);
							

		} else if (rdTest.isSelected()) {
			//testFiles.add(testFile); COMMENTED FOR GOOD
			System.out.println("test is selected");
			prpFile = defaultOutputDir + "\\" + theFileName;
			System.out.println("Test metric:\t" + testMetric);
			if(testMetric.startsWith("ERR"))
				System.out.println("Highest relevance label (to compute ERR): " + (int)SimpleMath.logBase2(ERRScorer.MAX));
			
			
			// only if kfold models are not inside
			if(savedModelFiles.size() == 0)
			{
				savedModelFiles.add(savedModelFile);
			}
			if(savedModelFile.compareTo("") != 0)
			{
			
				System.out.println("found the model file " + savedModelFiles.size());
				if(savedModelFiles.size() > 1)//models trained via cross-validation
				{
					System.out.println("place 0");

					// TGA EXCULUDED THIS BY NOT USING THE COLLECTION
					if(testFiles.size() > 1)
					{
						System.out.println("place 1");
						eval.test(savedModelFiles, testFiles, prpFile);
					}
					else
					{
						eval.test(savedModelFiles, testFile, prpFile);
						System.out.println("place 2 = BIG MODEL ");
					}
				}
				else if(savedModelFiles.size() == 1) // a single model
				{
					System.out.println("place 3 = SINGLE MODEL");
					eval.test(savedModelFile, testFile, prpFile);
				}
			}
			else if(scoreFile.compareTo("") != 0)
			{
				System.out.println("place 4");
				eval.testWithScoreFile(testFile, scoreFile);
			}
			//It will evaluate the input ranking (without being re-ranked by any model) using any measure specified via metric2T
			else
			{
				System.out.println("place 6");
				eval.test(testFile, prpFile);
			}
		}
		else {
			System.out.print("place 7");

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Problem");
			alert.setHeaderText("File Not Given ");
			alert.setContentText("Please provide a file name that can be used" + "to save the results of the "
					+ (rdTest.isSelected() ? "testing" : "ranking"));
			alert.showAndWait();
		}
	}
	public void wantToNormalize(ActionEvent e) {

		if (e.getSource().equals(rdNormalizeYes)) {
			Evaluator.normalize = true;
		} else {
			Evaluator.normalize = false;
		}

	}

	public void metricSelected(ActionEvent e) {
		// when train metric is selected
		int selIndex = cmbMetric.getSelectionModel().getSelectedIndex();
		testMetric = mType[selIndex] + "@" + spMetric.getValue();

	}
	public void browsingData(ActionEvent e)
	{
		try {
			
			if(!defaultDatasetDirectory.exists())
			{
				defaultDatasetDirectory = new File("C:");
			}
				FileChooser fc = new FileChooser();
				fc.setTitle("Choose File");
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
				fc.getExtensionFilters().add(filter);
				fc.setInitialDirectory(defaultDatasetDirectory);
				testFileBytes = fc.showOpenDialog(Main.theStage);
				if (testFileBytes!= null) {
					testFile =rankFile= testFileBytes.getCanonicalPath().toString();
					txtTestingFile.setText(testFile);
				} else {
					txtTestingFile.setText(defaultDatasetDirectory + "\\test.txt");
					testFile = rankFile = defaultDatasetDirectory + "\\test.txt";
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Notice");
					alert.setHeaderText("File Not Given ");
					alert.setContentText("A default file is used: test.txt");
					alert.showAndWait();
				}
		
			}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}

	}
}
