package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
	 * Scaling factor to convert from mouse scroll units (number of pixels to
	 * scroll) to a zoom factor, where the actual scaling is given by raising 2
	 * to the power of the zoom factor.
	 */
	public static final double MOUSE_SCROLL_SENSITIVITY = 0.0005;
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

	/**
	 * Translate an x-coordinate from relative to the bottom-right
	 * of the view-pane in pixels to relative to the displayed world origin.
	 * @param x    X-coordinate, in view-pane space.
	 * @return     X-coordinate, in world space.
	 */
	private double fromViewPaneToWorldSpaceX(double x) {
		return (x - scene.getTranslateX()) / scene.getScaleX();
	}

	/**
	 * Translate a y-coordinate from relative to the bottom-right
	 * of the view-pane in pixels to relative to the displayed world origin.
	 * @param y    Y-coordinate, in view-pane space.
	 * @return     Y-coordinate, in world space.
	 */
	private double fromViewPaneToWorldSpaceY(double y) {
		return (y - scene.getTranslateY()) / scene.getScaleY();
	}

	/**
	 * Translate an x-coordinate from relative to the displayed world origin to
	 * relative to the bottom-right of the view-pane in pixels.
	 * @param x    X-coordinate, in world space.
	 * @return     X-coordinate, in view-pane space.
	 */
	private double fromWorldToViewPaneSpaceX(double x) {
		return x * scene.getScaleX() + scene.getTranslateX();
	}

	/**
	 * Translate a y-coordinate from relative to the displayed world origin to
	 * relative to the bottom-right of the view-pane in pixels.
	 * @param y    Y-coordinate, in world space.
	 * @return     Y-coordinate, in view-pane space.
	 */
	private double fromWorldToViewPaneSpaceY(double y) {
		return y * scene.getScaleY() + scene.getTranslateY();
	}

	/**
	 * Update the stored cursor position.
	 * @param x    X-position of cursor.
	 * @param y    Y-position of cursor.
	 */
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

	/**
	 * Handle mouse button down. This is the first event fired so we record the
	 * cursor position for later use, i.e. in the context menu handler and when
	 * calculating the relative drag vector.
	 * @param event    Mouse down event.
	 */
	@FXML
	public void onViewPaneMousePressed(MouseEvent event) {
		onGenericMouseEvent(event.getX(), event.getY());
	}

	/**
	 * Handle dragging of the mouse.
	 * @param event    Mouse drag event.
	 */
	@FXML
	public void onViewPaneMouseDragged(MouseEvent event) {

		// Calculate relative drag
		scene.setTranslateX(scene.getTranslateX() + event.getX() - cursorPosition.getX());
		scene.setTranslateY(scene.getTranslateY() + event.getY() - cursorPosition.getY());

		onGenericMouseEvent(event.getX(), event.getY());

	}

	/**
	 * Handle scrolling in the view pane, which is translated into scaling of
	 * the scene.
	 * @param event    Mouse scroll event.
	 */
	@FXML
	public void onViewPaneScroll(ScrollEvent event) {

		double mouseWorldX = fromViewPaneToWorldSpaceX(event.getX());
		double mouseWorldY = fromViewPaneToWorldSpaceY(event.getY());

		double scaling = Math.pow(2, event.getDeltaY() * MOUSE_SCROLL_SENSITIVITY);
		scene.setScaleX(scene.getScaleX() * scaling);
		scene.setScaleY(scene.getScaleY() * scaling);

		scene.setTranslateX(event.getX() - mouseWorldX * scene.getScaleX());
		scene.setTranslateY(event.getY() - mouseWorldY * scene.getScaleY());

	}

	/**
	 * Handle the user directive to add an object at the cursor - given via the
	 * scene's main context menu.
	 * @param event    Context menu event.
	 */
	private void addObject(ActionEvent event) {

		SimulatedCarFormDialog dialog = new SimulatedCarFormDialog(
			// TODO: compensate for scaling
			fromViewPaneToWorldSpaceX(cursorPosition.getX()),
			fromViewPaneToWorldSpaceY(cursorPosition.getY()),
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
