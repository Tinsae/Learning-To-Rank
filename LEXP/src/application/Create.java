package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ciir.umass.edu.eval.Evaluator;
import ciir.umass.edu.features.LinearNormalizer;
import ciir.umass.edu.features.SumNormalizor;
import ciir.umass.edu.features.ZScoreNormalizor;
import ciir.umass.edu.learning.RANKER_TYPE;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.learning.RankerFactory;
import ciir.umass.edu.metric.ERRScorer;
import ciir.umass.edu.utilities.SimpleMath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class Create extends VBox implements Initializable {

	// UI controls
	@FXML
	Accordion fullAccordion;
	@FXML
	TitledPane firstTitledPane;

	@FXML
	ComboBox<String> cbAlgorithm;
	@FXML
	TextField txtTraining;

	@FXML
	RadioButton rdSparseYes;
	@FXML
	RadioButton rdSparseNo;
	@FXML
	RadioButton rdNormYes;
	@FXML
	RadioButton rdNormNo;

	@FXML
	ComboBox<String> cmbNormalizers;

	@FXML
	TextField txtFeatDesc;

	@FXML
	RadioButton rdCrossYes;
	@FXML
	RadioButton rdCrossNo;
	@FXML
	TextField txtNFold;
	@FXML
	Slider sliderTT;
	@FXML
	Label lblTT;
	@FXML
	Slider sliderTV;
	@FXML
	Label lblTV;
	@FXML
	TextField txtTestingFile;
	@FXML
	TextField txtValidationFile;
	@FXML
	TextField txtKCVDirectory;
	@FXML
	TextField txtModelPrefix;
	@FXML
	CheckBox chkSave;
	@FXML
	TextField txtModelFileName;

	@FXML
	Button btnBrowseTrain;
	@FXML
	Button btnBrowseFeat;
	@FXML
	Button btnBrowseTest;
	@FXML
	Button btnBrowseVal;
	@FXML
	Button btnBrowseModel;

	@FXML
	ComboBox<String> cmbTrainMetric;
	@FXML
	ComboBox<String> cmbTestMetric;
	@FXML
	Spinner<Integer> spinnerTrainMetric;
	@FXML
	Spinner<Integer> spinnerTestMetric;
	@FXML
	RadioButton rdSilentYes;
	@FXML
	RadioButton rdSilentNo;

	@FXML
	TextField txtKCVName;
	
	
	
	// declaring all required variables
	String[] rType;
	RANKER_TYPE[] rType2;
	// !! Not in original code(storing all metric types)
	String[] mType;
	// !! Not in orignal code(stroing all normalizer types)
	String[] nType;
	// !! Not in original code
	// I need it to get the actual file contents
	File trainFileBytes;
	String trainFile;
	// !! Not in original code
	// I need it to get the actual file contents
	File featureDescBytes;
	String featureDescriptionFile;
	// train-test split
	float ttSplit;
	// train-validation split
	float tvSplit;
	int foldCV;
	// !! Not in original code
	// I need it to get the actual file contents
	File valiFileBytes;
	String validationFile;
	// !! Not in original code
	// I need it to get the actual file contents
	File testFileBytes;
	String testFile;
	List<String> testFiles = new ArrayList<String>();
	int rankerType;
	String trainMetric;
	String testMetric;
	String savedModelFile;
	List<String> savedModelFiles = new ArrayList<String>();
	// !! not in original code
	File kcvModelDirR;
	// where to save the models(folder url)
	String kcvModelDir;
	// prefix for model names
	String kcvModelFile;
	// individual query evaluation result (query by query)
	// for example NDCG for each query
	// !! Not in original code
	int nThread; // nThread = #cpu-cores

	// !! myOwn 3
	// for datasets
	private File defaultDatasetDirectory;
	// to save kfold models
	private File defaultKFoldDirectory;
	// to save normal models
	private File defaultSimpleModelsDirectory;
	public Evaluator eval;

	@FXML
	VBox myVBox;
	
	public void whatHappens(ActionEvent ee)
	{
		
		try
		{
		AnchorPane ancPaneChild = (AnchorPane) myVBox.getParent();
		ancPaneChild.getChildren().clear();
		
		Pane testPane = (Pane) FXMLLoader.load(getClass().getResource("Test.fxml"));
		ancPaneChild.getChildren().clear();
	
		ancPaneChild.getChildren().add(testPane);
		}
		catch(Exception eee)
		{
			eee.printStackTrace();
		}		
	}
	
	
	
	
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		String homeDirectory = System.getProperty("user.home");
		rType = new String[] { "MART", "RankNet", "RankBoost", "AdaRank", "Coordinate Ascent", "LambdaRank",
				"LambdaMART", "ListNet", "Random Forests", "Linear Regression" };
		rType2 = new RANKER_TYPE[] { RANKER_TYPE.MART, RANKER_TYPE.RANKNET, RANKER_TYPE.RANKBOOST, RANKER_TYPE.ADARANK,
				RANKER_TYPE.COOR_ASCENT, RANKER_TYPE.LAMBDARANK, RANKER_TYPE.LAMBDAMART, RANKER_TYPE.LISTNET,
				RANKER_TYPE.RANDOM_FOREST, RANKER_TYPE.LINEAR_REGRESSION };
		mType = new String[] { "MAP", "NDCG", "DCG", "P", "RR", "BEST", "ERR" };
		nType = new String[] { "Sum", "ZScore", "Linear" };
		trainFile = "";
		featureDescriptionFile = "";
		ttSplit = 0;
		tvSplit = 0;
		foldCV = -1;
		validationFile = "";
		testFile = "";
		testFiles = new ArrayList<String>();
		rankerType = 4;
		trainMetric = "ERR@10";
		testMetric = "ERR@10";
		Evaluator.normalize = false;

		savedModelFile = "";
		savedModelFiles = new ArrayList<String>();

		kcvModelDir = "";
		kcvModelFile = "";
		nThread = -1; // nThread = #cpu-cores
		// initialize UI elements
		cbAlgorithm.getItems().clear();
		cbAlgorithm.getItems().addAll(rType);
		cbAlgorithm.getSelectionModel().select(1);
		rankerType = 1;

		cmbNormalizers.getItems().clear();
		cmbNormalizers.getItems().addAll(nType);

		cmbNormalizers.getSelectionModel().select(0);

		cmbTrainMetric.getItems().clear();
		cmbTrainMetric.getItems().addAll(mType);
		// setting a default metric
		cmbTrainMetric.getSelectionModel().select(6);

		cmbTestMetric.getItems().clear();
		cmbTestMetric.getItems().addAll(mType);
		// setting a default metric
		cmbTestMetric.getSelectionModel().select(6);

		spinnerTrainMetric.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30));
		// setting a default value
		spinnerTrainMetric.getValueFactory().setValue(10);

		spinnerTestMetric.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30));
		// setting a default value
		spinnerTestMetric.getValueFactory().setValue(10);

		/// some special event handlers

		sliderTT.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				String theVal = String.format("%.2f", (Double) new_val / 100.);
				lblTT.setText(theVal);
				ttSplit = Float.parseFloat(theVal);
			}
		});

		sliderTV.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				String theVal = String.format("%.2f", (Double) new_val / 100.);
				lblTV.setText(String.format("%.2f", (Double) new_val / 100.));
				tvSplit = Float.parseFloat(theVal);

			}
		});

		File dataSetsDir = new File(homeDirectory + "\\LEXP\\datasets");
		// the last directory name is used as a name for the kfold model
		File KFoldDir = new File(homeDirectory + "\\LEXP\\kfoldmodels\\untitled");
		File simpleModelsDir = new File(homeDirectory + "\\LEXP\\simplemodels");
		if (!dataSetsDir.exists()) {
			dataSetsDir.mkdirs();
		}
		if (!KFoldDir.exists()) {
			KFoldDir.mkdirs();
		}
		if (!simpleModelsDir.exists()) {
			simpleModelsDir.mkdirs();
		}

		defaultDatasetDirectory = dataSetsDir;
		defaultKFoldDirectory = KFoldDir;
		defaultSimpleModelsDirectory = simpleModelsDir;

		// !! own
		// put default files and urls

		txtTraining.setText(defaultDatasetDirectory.getAbsolutePath() + "\\train.txt");
		trainFile = defaultDatasetDirectory.getAbsolutePath() + "\\train.txt";
		trainFileBytes = new File(trainFile);

		txtTestingFile.setText(defaultDatasetDirectory.getAbsolutePath() + "\\test.txt");
		testFile = defaultDatasetDirectory.getAbsolutePath() + "\\test.txt";
		testFileBytes = new File(testFile);

		txtValidationFile.setText(defaultDatasetDirectory.getAbsolutePath() + "\\vali.txt");

		validationFile = defaultDatasetDirectory.getAbsolutePath() + "\\vali.txt";
		valiFileBytes = new File(validationFile);

		String ori = defaultKFoldDirectory.getAbsolutePath();
		txtKCVDirectory.setText(ori.substring(0,ori.lastIndexOf("\\") + 1));
		txtKCVName.setText(ori.substring(ori.lastIndexOf("\\") + 1, ori.length()));
		kcvModelDir = defaultKFoldDirectory.getAbsolutePath();
		kcvModelFile = "txt";
		kcvModelDirR = new File(kcvModelDir);

		fullAccordion.setExpandedPane(firstTitledPane);

		// In case user changed the spinners value
		spinnerTrainMetric.valueProperty().addListener(
				(obs, oldValue, newValue) -> testMetric = mType[cmbTrainMetric.getSelectionModel().getSelectedIndex()]
						+ "@" + spinnerTrainMetric.getValue());

		spinnerTestMetric.valueProperty().addListener(
				(obs, oldValue, newValue) -> testMetric = mType[cmbTestMetric.getSelectionModel().getSelectedIndex()]
						+ "@" + spinnerTestMetric.getValue());

		txtNFold.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {

				try {
					if (!txtNFold.getText().equals("")) {
						if (Integer.parseInt(txtNFold.getText()) >= 2) {
							foldCV = Integer.parseInt(txtNFold.getText());
						} else {
							txtNFold.setText("2");
						}

					}
				}

				catch (NumberFormatException nf) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Invalid Input");
					alert.setHeaderText("Number Format Exception");
					alert.setContentText(nf.getMessage());
					alert.show();

				}
			}
		});

	}

	public void keepQuiet(ActionEvent e) {
		if (e.getSource().equals(rdSilentYes)) {
			Ranker.verbose = false;

		} else if (e.getSource().equals(rdSilentNo)) {
			Ranker.verbose = true;
		}

	}

	public void metricSelected(ActionEvent e) {
		if (e.getSource().equals(cmbTrainMetric)) {
			// when train metric is selected
			// change the test metric to be the same
			int selIndex = cmbTrainMetric.getSelectionModel().getSelectedIndex();
			trainMetric = mType[selIndex] + "@" + spinnerTrainMetric.getValue();
			cmbTestMetric.getSelectionModel().select(selIndex);
			testMetric = mType[selIndex] + "@" + spinnerTrainMetric.getValue();
		} else if (e.getSource().equals(cmbTestMetric)) {
			int selIndex = cmbTestMetric.getSelectionModel().getSelectedIndex();
			testMetric = mType[selIndex] + "@" + spinnerTestMetric.getValue();
		}
	}

	public void normalizeFeatures(ActionEvent e) {
		if (e.getSource().equals(rdNormYes)) {
			Evaluator.normalize = true;
			cmbNormalizers.setVisible(true);
		} else if (e.getSource().equals(rdNormNo)) {
			Evaluator.normalize = false;
			cmbNormalizers.setVisible(false);
		}
	}

	public void normalizerSelected(ActionEvent e) {
		Integer selItem = cmbNormalizers.getSelectionModel().getSelectedIndex();
		if (selItem == 0)
			Evaluator.nml = new SumNormalizor();
		else if (selItem == 1)
			Evaluator.nml = new ZScoreNormalizor();
		else if (selItem == 2)
			Evaluator.nml = new LinearNormalizer();

	}

	public void useSparseRepresentation(ActionEvent e) {
		if (e.getSource().equals(rdSparseYes)) {
			Evaluator.useSparseRepresentation = true;
		} else if (e.getSource().equals(rdSparseNo)) {
			Evaluator.useSparseRepresentation = false;
		}

	}

	public void wantToDoCV(ActionEvent e) {

		if (e.getSource().equals(rdCrossYes)) {
			// in case user didn't give number of folds; it defaults to 5
			foldCV = txtNFold.getText() != "" ? Integer.parseInt(txtNFold.getText()) : 5;
			if (tvSplit == 0f) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Split Point");
				alert.setHeaderText("Reminder");
				alert.setContentText("Don't forget to give train validation split point");
				alert.showAndWait();

			}
		} else if (e.getSource().equals(rdCrossNo)) {
			foldCV = -1;
			txtNFold.setText("-1");

		}

	}

	public void createModelClick(ActionEvent event) {
		kcvModelFile = txtModelPrefix.getText();
		kcvModelDir = txtKCVDirectory.getText() + txtKCVName.getText();

		// TBD on other fxml
		// prpFile = defaultPath +"\\"+ txtIDVFileName.getText();
		eval = new Evaluator(rType2[rankerType], trainMetric, testMetric);

		// !! not in original
		// set settings to evaluator instance

		// Main settings

		Evaluator.mustHaveRelDoc = false;
		// Evaluator.useSparseRepresentation = false;
		// Evaluator.normalize = false;
		// Evaluator.nml = new SumNormalizor();

		if (chkSave.isSelected()) {
			if (txtModelFileName.getText().trim().equals("")) {
				// if user wants to save but file name is not provided
				String defaultName = "sample.txt";
				txtModelFileName.setText(defaultName);
				Evaluator.modelFile = defaultSimpleModelsDirectory.getAbsolutePath() + "\\" + defaultName;
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Default File Name");
				alert.setHeaderText("Your model will be saved");
				alert.setContentText(
						"System will use the default model name " + "because you didn't give file name" + "sample.txt");
				alert.showAndWait();

			}
			Evaluator.modelFile = defaultSimpleModelsDirectory.getAbsolutePath() + "\\" + txtModelFileName.getText();

		} else {
			Evaluator.modelFile = "";
		}

		Evaluator.qrelFile = "";
		Evaluator.newFeatureFile = "";
		Evaluator.keepOrigFeatures = false;
		Evaluator.topNew = 2000;

		System.out.println("Training data:\t" + trainFile);

		if (foldCV != -1) {
			System.out.println("Cross validation: " + foldCV + " folds.");
			if (tvSplit > 0) {
				System.out.println("Train-Validation split: " + tvSplit);
			}
		} else {
			if (testFile.compareTo("") != 0) {
				System.out.println("Test data:\t" + testFile);
			} else if (ttSplit > 0) {
				// choose to split training data into train and test
				System.out.println("Train-Test split: " + ttSplit);
			}
			// the user has specified the validation set
			if (validationFile.compareTo("") != 0)
				System.out.println("Validation data:\t" + validationFile);
			else if (ttSplit <= 0 && tvSplit > 0)
				System.out.println("Train-Validation split: " + tvSplit);
		}

		System.out.println(
				"Feature vector representation: " + ((Evaluator.useSparseRepresentation) ? "Sparse" : "Dense") + ".");
		System.out.println("Ranking method:\t" + rType[rankerType]);

		if (featureDescriptionFile.compareTo("") != 0)
			System.out.println("Feature description file:\t" + featureDescriptionFile);
		else
			System.out.println("Feature description file:\tUnspecified. All features will be used.");

		System.out.println("Train metric:\t" + trainMetric);
		System.out.println("Test metric:\t" + testMetric);
		if (trainMetric.toUpperCase().startsWith("ERR") || testMetric.toUpperCase().startsWith("ERR"))
			System.out.println("Highest relevance label (to compute ERR): " + (int) SimpleMath.logBase2(ERRScorer.MAX));
		if (Evaluator.qrelFile.compareTo("") != 0)
			System.out.println(
					"TREC-format relevance judgment (only affects MAP and NDCG scores): " + Evaluator.qrelFile);
		System.out.println("Feature normalization: " + ((Evaluator.normalize) ? Evaluator.nml.name() : "No"));
		if (kcvModelDir.compareTo("") != 0)
			System.out.println("Models directory: " + kcvModelDir);
		if (kcvModelFile.compareTo("") != 0)
			System.out.println("Models' name: " + kcvModelFile);
		if (Evaluator.modelFile.compareTo("") != 0)
			System.out.println("Model file: " + Evaluator.modelFile);
		// System.out.println("#threads:\t" + nThread);

		System.out.println("");
		System.out.println("[+] " + rType[rankerType] + "'s Parameters:");
		RankerFactory rf = new RankerFactory();

		rf.createRanker(rType2[rankerType]).printParameters();
		System.out.println("");

		// starting to do some work

		if (foldCV != -1) {
			if (kcvModelDir.compareTo("") != 0 && kcvModelFile.compareTo("") == 0) {
				kcvModelFile = "default";
			}
			if (tvSplit == 0) {
				// if tvsplit is not given but user opted for cv
				sliderTV.setValue(80.0);
				tvSplit = 0.8f;
			}

			System.out.println("KFold Directory " + kcvModelDir);
			eval.evaluate(trainFile, featureDescriptionFile, foldCV, tvSplit, kcvModelDir, kcvModelFile);// models
			// won't be saved if kcvModelDir=""
		}
		// no kfold cv
		else {
			// we should use a held-out portion of the
			// training data for testing?
			// missing testfile
			if (ttSplit > 0.0) {
				eval.evaluate(trainFile, validationFile, featureDescriptionFile, ttSplit);
			}
			// no validation will be done if validationFile=""
			// should we use a portion of the training data for validation?
			// missing validation file
			else if (tvSplit > 0.0) {
				eval.evaluate(trainFile, tvSplit, testFile, featureDescriptionFile);
			}
			// all are given
			else {
				eval.evaluate(trainFile, validationFile, testFile, featureDescriptionFile);
			}
		}

	}

	public void algSelected(ActionEvent e) {
		// String selItem =
		// cbAlgorithm.getSelectionModel().getSelectedItem().toString();
		// lblLeftStatus.setText(selItem + " is selected");
		rankerType = cbAlgorithm.getSelectionModel().getSelectedIndex();

	}

	public void browseClick(ActionEvent e)

	{
		try {
			FileChooser fc = new FileChooser();
			fc.setTitle("Choose File");
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
			fc.getExtensionFilters().add(filter);
			fc.setInitialDirectory(defaultDatasetDirectory);
			if (e.getSource().equals(btnBrowseTrain)) {
				trainFileBytes = fc.showOpenDialog(Main.theStage);
				if (trainFileBytes != null) {
					trainFile = trainFileBytes.getCanonicalPath().toString();
					txtTraining.setText(trainFile);
				} else {
					txtTraining.setText(defaultDatasetDirectory + "\\train.txt");
				}
			} else if (e.getSource().equals(btnBrowseFeat)) {
				featureDescBytes = fc.showOpenDialog(Main.theStage);
				if (featureDescBytes != null) {
					featureDescriptionFile = featureDescBytes.getCanonicalPath().toString();
					txtFeatDesc.setText(featureDescriptionFile);
				} else {
					txtFeatDesc.setText(defaultDatasetDirectory + "\\feature.txt");
				}
			} else if (e.getSource().equals(btnBrowseTest)) {
				testFileBytes = fc.showOpenDialog(Main.theStage);
				if (testFileBytes != null) {
					testFile = testFileBytes.getCanonicalPath().toString();
					txtTestingFile.setText(testFile);
				} else {
					txtTestingFile.setText(defaultDatasetDirectory + "\\test.txt");

				}
			} else if (e.getSource().equals(btnBrowseVal)) {

				valiFileBytes = fc.showOpenDialog(Main.theStage);
				if (valiFileBytes != null) {
					validationFile = valiFileBytes.getCanonicalPath().toString();
					txtValidationFile.setText(validationFile);
				} else {
					txtValidationFile.setText(defaultDatasetDirectory + "\\vali.txt");

				}
			} else if (e.getSource().equals(btnBrowseModel)) {
				DirectoryChooser dc = new DirectoryChooser();
				dc.setInitialDirectory(defaultKFoldDirectory);
				dc.setTitle("Choose Folder");
				kcvModelDirR = dc.showDialog(Main.theStage);
				if (kcvModelDirR != null) {
					kcvModelDir = kcvModelDirR.getCanonicalPath().toString();
					txtKCVDirectory.setText(kcvModelDir);
				} else {
					txtKCVDirectory.setText(defaultKFoldDirectory.getAbsolutePath());

				}

			}
		} catch (Exception ee) {
			System.out.println("some error occurred");
		}

	}

}