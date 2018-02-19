package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;
import uk.ac.cam.cl.group_project.delta.simulation.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {

	/**
	 * Thread running this application's simulation.
	 */
	private SimulationThread simulation;

	private List<SimulatedBodyNode> simulatedNodes;

	@FXML
	private Pane viewPane;

	@FXML
	private TreeView<String> propertiesView;

	Timeline timeline;

	/**
	 * Construct the application's simulation thread.
	 */
	public Controller() {
		simulation = new SimulationThread();
		simulatedNodes = new ArrayList<>();
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(
			Duration.millis(1),
			e -> this.update()
		));
	}

	/**
	 * Start the simulation and update Timeline.
	 */
	@FXML
	public void initialize() {
		simulation.start();
		timeline.play();
	}

	/**
	 * Update positions of all displayed nodes.
	 */
	public void update() {
		for (SimulatedBodyNode node : simulatedNodes) {
			node.update();
		}
	}

	private void showProperties(Treeable obj) {
		TreeItem<String> root = obj.toTree();
		root.setExpanded(true);
		propertiesView.setRoot(root);
	}

	@FXML
	public void onViewPaneMouseClick(MouseEvent event) {

		SimulatedCar car = simulation.createCar(0.15);

		car.setPosition(
			new Vector2D(
				event.getX(),
				event.getY()
			)
		);

		Random random = new Random();
		car.setVelocity(
			new Vector2D(
				random.nextDouble() * 10 - 5,
				random.nextDouble() * 10 - 5
			)
		);

		SimulatedBodyNode node = new SimulatedBodyNode(car);
		simulatedNodes.add(node);
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
