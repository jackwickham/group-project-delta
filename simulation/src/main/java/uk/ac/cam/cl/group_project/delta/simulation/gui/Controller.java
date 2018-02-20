package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX GUI controller.
 */
public class Controller {

	/**
	 * Thread running this application's simulation.
	 */
	private SimulationThread simulation;

	/**
	 * List of nodes representing objects in the simulated world.
	 */
	private List<SimulatedBodyNode> simulatedNodes;

	/**
	 * GUI element containing the current scene.
	 */
	@FXML
	private Pane viewPane;

	/**
	 * GUI element containing the hierarchical information for a selected
	 * object.
	 */
	@FXML
	private TreeView<String> propertiesView;

	/**
	 * The JavaFX GUI updater - an "animation".
	 */
	private Timeline timeline;

	/**
	 * Construct the application's simulation thread.
	 */
	public Controller() {
		simulation = new SimulationThread();
		simulatedNodes = new ArrayList<>();
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(
			Duration.seconds(1.0 / 40.0),
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

	/**
	 * Display the object in the information pane.
	 * @param obj    The object to display.
	 */
	private void showProperties(Treeable obj) {
		TreeItem<String> root = obj.toTree();
		root.setExpanded(true);
		propertiesView.setRoot(root);
	}

	/**
	 * Handle mouse click in the main simulation view area.
	 * @param event     Mouse click event.
	 */
	@FXML
	public void onViewPaneMouseClick(MouseEvent event) {

		if (event.getButton().equals(MouseButton.SECONDARY)) {
			System.out.println("Hey");
			ContextMenu menu = new ContextMenu(
				new MenuItem("Add object")
			);
			menu.show(viewPane, event.getScreenX(), event.getScreenY());
		}

		/*SimulatedCar car = simulation.createCar(0.15);

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

		viewPane.getChildren().add(node);*/

	}

}
