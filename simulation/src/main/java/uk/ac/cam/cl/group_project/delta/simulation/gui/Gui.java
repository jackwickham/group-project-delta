package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uk.ac.cam.cl.group_project.delta.simulation.*;

import javax.swing.text.html.StyleSheet;
import java.io.IOException;

/**
 * GUI for interacting with a simulated environment
 */
public class Gui extends Application {

	/**
	 * FXML definition of the interface.
	 */
	private static final String UI_FXML = "gui.fxml";

	/**
	 * Main method for environments that do not explicitly support JavaFX
	 * applications.
	 * @param args    Command line arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Run the application.
	 * @param primaryStage    Stage in which to display the app contents.
	 * @throws IOException    Thrown if `UI_FXML` cannot be found.
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(UI_FXML));
		primaryStage.setScene(new Scene(root));
		primaryStage.setResizable(true);
		primaryStage.setTitle("Simulation");
		primaryStage.show();
	}

}
