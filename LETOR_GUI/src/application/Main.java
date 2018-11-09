package application;

import java.net.URL;
import java.util.ResourceBundle;


/*	
Application: Is a single instance which creates the environment for you. 
It creates a primaryStage and launches the javafx ui thread.

Stage: Is a window. You can have as many Stages as you want. 
Application provides you with a Stage in the start method, which has some special properties, compared to manually created Stages.

Scene: Every Stage can hold exactly one Scene at a time. 
Scenes can be swapped out, but is discouraged to do so. 
It is better to just swap out the root of the Scene.

Parent: A simple Node that can hold other Nodes as children. 
Every Scene needs exactly one Parent as the root.
 * */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application implements Initializable {

	@FXML
	Button btnCreate;
	@FXML
	Button btnTest;
	@FXML
	Button btnButton;
	@FXML
	AnchorPane ancPaneChild;
	private Pane createPane;
	private Pane testPane;
	private Test testLoader;
	//private Create createLoader;
	public static Stage theStage;

	
	@Override
	public void start(Stage primaryStage) {
		try {

			//theStage = primaryStage;
			//primaryStage.getIcons().add(new Image(getClass().getResource("wheat.png").toExternalForm()));
			Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Letor Experimenter");
			primaryStage.setMaxWidth(1200);
			primaryStage.setMaxHeight(600);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	
	
	public void initialize(URL location, ResourceBundle resources) {
		try {
			FXMLLoader cl = new FXMLLoader(getClass().getResource("Create.fxml"));
		//	createLoader = cl.getController();
			createPane =(Pane) cl.load();
			FXMLLoader tl = new FXMLLoader(getClass().getResource("Test.fxml"));
			testLoader = tl.getController();
			testPane = (Pane) tl.load();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		
	}

	public void openCreate(ActionEvent event) {
		try {
			
			ancPaneChild.getChildren().clear();
			ancPaneChild.getChildren().add(createPane);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void openTest(ActionEvent event) {
		try {
			ancPaneChild.getChildren().clear();

			if(testLoader != null)
			{
				System.out.println("Now refershing");
			testLoader.refershSimpleModels();
			testLoader.refreshKFoldModels();
			}
			ancPaneChild.getChildren().add(testPane);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
