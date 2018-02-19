package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;
import uk.ac.cam.cl.group_project.delta.simulation.Vector2D;

import java.util.Random;

public class Controller {

	/**
	 * Thread running this application's simulation.
	 */
	private SimulationThread simulation;

	@FXML
	private Pane viewPane;

	@FXML
	private TreeView<String> propertiesView;

	/**
	 * Construct the application's simulation thread.
	 */
	public Controller() {
		simulation = new SimulationThread();
	}

	/**
	 * Start the simulation.
	 */
	@FXML
	public void initialize() {
		simulation.start();
	}

	private void showProperties(Treeable obj) {
		TreeItem<String> root = obj.toTree();
		root.setExpanded(true);
		propertiesView.setRoot(root);
	}

	@FXML
	public void createNewSimulationObject(Event event) {

		SimulatedCar car = simulation.createCar(0.15);

		Random random = new Random();
		car.setPosition(
			new Vector2D(
				random.nextDouble() * 1000,
				random.nextDouble() * 1000
			)
		);
		car.setVelocity(
			new Vector2D(
				random.nextDouble() * 1000,
				random.nextDouble() * 1000
			)
		);

		SimulatedBodyNode node = new SimulatedBodyNode(car);
		node.addEventFilter(
			MouseEvent.MOUSE_CLICKED,
			e -> {
				showProperties(node);
				e.consume();
			}
		);

		viewPane.getChildren().add(node);

	}

}
