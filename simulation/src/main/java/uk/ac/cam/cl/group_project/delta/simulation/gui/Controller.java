package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;
import uk.ac.cam.cl.group_project.delta.simulation.Vector2D;

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
	 * The simulated world containing nodes representing the simulated objects.
	 */
	@FXML
	private Group scene;

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
	 * Context menu displayed when the user right-clicks the main view pane.
	 */
	private ContextMenu sceneContextMenu;

	/**
	 * The last recorded mouse position.
	 */
	private Vector2D cursorPosition;

	/**
	 * Construct the application's simulation thread.
	 */
	public Controller() {

		simulation = new SimulationThread();
		simulatedNodes = new ArrayList<>();
		cursorPosition = new Vector2D();

		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(
			Duration.seconds(1.0 / 40.0),
			e -> this.update()
		));

		MenuItem item = new MenuItem("Add object");
		item.setOnAction(this::addObject);
		sceneContextMenu = new ContextMenu(item);

	}

	/**
	 * Start the simulation and update Timeline.
	 */
	@FXML
	public void initialize() {

		// Start background tasks
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

	public void onGenericMouseEvent(double x, double y) {
		sceneContextMenu.hide();
		cursorPosition.setX(x);
		cursorPosition.setY(y);
	}

	/**
	 * Handle mouse click in the main simulation view area.
	 * @param event     Mouse click event.
	 */
	@FXML
	public void onViewPaneMouseClick(MouseEvent event) {

		if (event.getButton().equals(MouseButton.SECONDARY)) {
			sceneContextMenu.show(viewPane, event.getScreenX(), event.getScreenY());
		}

	}

	@FXML
	public void onViewPaneMousePressed(MouseEvent event) {
		onGenericMouseEvent(event.getX(), event.getY());
	}

	@FXML
	public void onViewPaneMouseDragged(MouseEvent event) {

		// Calculate relative drag
		scene.setTranslateX(scene.getTranslateX() + event.getX() - cursorPosition.getX());
		scene.setTranslateY(scene.getTranslateY() + event.getY() - cursorPosition.getY());

		onGenericMouseEvent(event.getX(), event.getY());

	}

	private void addObject(ActionEvent event) {

		SimulatedCarFormDialog dialog = new SimulatedCarFormDialog(
			cursorPosition.getX() - scene.getTranslateX(),
			cursorPosition.getY() - scene.getTranslateY(),
			(wheelBase, posX, posY) -> {
				SimulatedCar car = simulation.createCar(wheelBase);
				synchronized (car) {
					car.getPosition().setX(posX);
					car.getPosition().setY(posY);
				}
				SimulatedBodyNode node = new SimulatedBodyNode(car);
				node.addEventFilter(
					MouseEvent.MOUSE_CLICKED,
					e -> {
						showProperties(node);
						e.consume();
					}
				);
				scene.getChildren().add(node);
				simulatedNodes.add(node);
			}
		);
		dialog.show();

	}

}
