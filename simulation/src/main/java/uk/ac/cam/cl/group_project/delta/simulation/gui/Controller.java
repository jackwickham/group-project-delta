package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import uk.ac.cam.cl.group_project.delta.MessageReceipt;
import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;
import uk.ac.cam.cl.group_project.delta.algorithm.AlgorithmEnum;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.MessageType;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Packet;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;
import uk.ac.cam.cl.group_project.delta.simulation.Vector2D;

import java.util.ArrayList;
import java.util.LinkedList;
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
	 * Number of GUI units per simulation metre.
	 */
	public static final double UNITS_PER_METRE = 400.0;

	/**
	 * Number of historic messages to show in the network log.
	 */
	public static final int NETWORK_LOG_CAPACITY = 100;
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
	private Pane scene;
	/**
	 * GUI element containing the hierarchical information for a selected
	 * object.
	 */
	@FXML
	private AnchorPane propertiesPane;

	/**
	 * GUI element containing a log of network messages.
	 */
	@FXML
	private TableView<NetworkLogMessage> networkLog;

	/**
	 * Backend store for network logs.
	 */
	private ObservableList<NetworkLogMessage> networkLogStore;

	/**
	 * Emergency messages filter button.
	 */
	@FXML
	private CheckBox filterEmergency;

	/**
	 * Data messages filter button.
	 */
	@FXML
	private CheckBox filterData;

	/**
	 * Merge coordination filter button.
	 */
	@FXML
	private CheckBox filterMerges;

	/**
	 * Beacon query filter button.
	 */
	@FXML
	public CheckBox filterQueries;

	/**
	 * Indicator to show the simulation is paused.
	 */
	@FXML
	public Pane pausedPane;

	/**
	 * Pause button.
	 */
	@FXML
	public ToggleButton pauseButton;

	/**
	 * Simulation step button.
	 */
	@FXML
	public Button stepButton;

	/**
	 * Time dilation slider.
	 */
	@FXML
	public Slider timeDilationSlider;

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
	 * The currently user-selected car.
	 */
	private SimulatedCarNode currentSelection;

	/**
	 * Construct the application's simulation thread.
	 */
	public Controller() {

		simulation = new SimulationThread();
		simulatedNodes = new ArrayList<>();
		cursorPosition = new Vector2D();
		networkLogStore = FXCollections.observableList(new LinkedList<>());

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

		networkLog.setItems(networkLogStore);

		// Construct table columns
		TableColumn<NetworkLogMessage, String> time = new TableColumn<>("Time");
		time.setCellValueFactory(new PropertyValueFactory<>("time"));
		time.setPrefWidth(150);
		networkLog.getColumns().add(time);

		TableColumn<NetworkLogMessage, String> sender = new TableColumn<>("Sender");
		sender.setCellValueFactory(new PropertyValueFactory<>("senderId"));
		sender.setPrefWidth(110);
		networkLog.getColumns().add(sender);

		TableColumn<NetworkLogMessage, String> message = new TableColumn<>("Message");
		message.setCellValueFactory(new PropertyValueFactory<>("message"));
		message.setPrefWidth(600);
		networkLog.getColumns().add(message);

		// Register network packet sniffer
		simulation.getNetwork().register(msg ->
			Platform.runLater(() -> addToNetworkLog(new MessageReceipt(msg)))
		);

		// Start background tasks
		simulation.start();
		timeline.play();

		pausedPane.visibleProperty().bind(pauseButton.selectedProperty());
		timeDilationSlider.valueProperty().addListener(
			(observableValue, oldValue, newValue) -> {
				if (!pauseButton.isSelected()) {
					simulation.setTimeDilationFactor(newValue.doubleValue());
				}
			}
		);

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
	private void showProperties(Paneable obj) {
		Pane root = obj.toPane();
		propertiesPane.getChildren().setAll(root);
	}

	/**
	 * Add a message to the network log tab.
	 * @param message   Message to add.
	 */
	private void addToNetworkLog(MessageReceipt message) {

		Packet packet = new Packet(message);
		NetworkLogMessage msg = new NetworkLogMessage(message.getTime(), packet);

		MessageType type = packet.message.getType();
		boolean isMergeMessage =
			type == MessageType.RequestToMerge
			|| type == MessageType.AcceptToMerge
			|| type == MessageType.ConfirmMerge
			|| type == MessageType.MergeComplete;
		boolean isQuery =
			type == MessageType.BeaconIdQuestion
			|| type == MessageType.BeaconIdAnswer;

		if (
			(type == MessageType.Emergency && filterEmergency.isSelected())
			|| (type == MessageType.Data && filterData.isSelected())
			|| (isMergeMessage && filterMerges.isSelected())
			|| (isQuery && filterQueries.isSelected())
		) {
			networkLogStore.add(0, msg);
			if (networkLogStore.size() > NETWORK_LOG_CAPACITY) {
				// Range is start-inclusive, end-exclusive
				networkLogStore.remove(NETWORK_LOG_CAPACITY, networkLogStore.size());
			}
		}

	}

	/**
	 * Translate an x-coordinate from relative to the bottom-right
	 * of the view-pane in pixels to relative to the displayed world origin.
	 * @param x    X-coordinate, in view-pane space.
	 * @return     X-coordinate, in world space.
	 */
	private double fromViewPaneToWorldSpaceX(double x) {
		return (x - scene.getTranslateX()) / (scene.getScaleX() * UNITS_PER_METRE);
	}

	/**
	 * Translate a y-coordinate from relative to the bottom-right
	 * of the view-pane in pixels to relative to the displayed world origin.
	 * @param y    Y-coordinate, in view-pane space.
	 * @return     Y-coordinate, in world space.
	 */
	private double fromViewPaneToWorldSpaceY(double y) {
		return (y - scene.getTranslateY()) / (scene.getScaleY() * UNITS_PER_METRE);
	}

	/**
	 * Translate an x-coordinate from relative to the displayed world origin to
	 * relative to the bottom-right of the view-pane in pixels.
	 * @param x    X-coordinate, in world space.
	 * @return     X-coordinate, in view-pane space.
	 */
	private double fromWorldToViewPaneSpaceX(double x) {
		return x * scene.getScaleX() * UNITS_PER_METRE + scene.getTranslateX();
	}

	/**
	 * Translate a y-coordinate from relative to the displayed world origin to
	 * relative to the bottom-right of the view-pane in pixels.
	 * @param y    Y-coordinate, in world space.
	 * @return     Y-coordinate, in view-pane space.
	 */
	private double fromWorldToViewPaneSpaceY(double y) {
		return y * scene.getScaleY() * UNITS_PER_METRE + scene.getTranslateY();
	}

	/**
	 * Handle a key press event.
	 * @param keyEvent    Structure containing event information (e.g. key code)
	 */
	public void onKeyPressed(KeyEvent keyEvent) {
		switch (keyEvent.getCode()) {
			case W:
				if (currentSelection != null) {
					SimulatedCar car = currentSelection.getCar();
					synchronized (car) {
						car.setEnginePower(0.5);
					}
				}
				break;
			case S:
				if (currentSelection != null) {
					SimulatedCar car = currentSelection.getCar();
					synchronized (car) {
						car.setEnginePower(-1000.0);
					}
				}
				break;
			case A:
				if (currentSelection != null) {
					SimulatedCar car = currentSelection.getCar();
					synchronized (car) {
						car.setWheelAngle(-Math.PI / 8);
					}
				}
				break;
			case D:
				if (currentSelection != null) {
					SimulatedCar car = currentSelection.getCar();
					synchronized (car) {
						car.setWheelAngle(Math.PI / 8);
					}
				}
				break;
			case P:
				pauseButton.fire();
				break;
			case SEMICOLON:
				stepButton.fire();
				break;
			case PLUS:
			case EQUALS:
				timeDilationSlider.adjustValue(
					timeDilationSlider.getValue() + 0.1
				);
				break;
			case MINUS:
			case UNDERSCORE:
				timeDilationSlider.adjustValue(
					timeDilationSlider.getValue() - 0.1
				);
				break;
		}
	}

	/**
	 * Handle a key released event.
	 * @param keyEvent    Structure containing event information (e.g. key code)
	 */
	public void onKeyReleased(KeyEvent keyEvent) {
		if (currentSelection != null) {
			SimulatedCar car = currentSelection.getCar();
			synchronized (car) {
				switch (keyEvent.getCode()) {
					case W:
					case S:
						car.setEnginePower(0.0);
						break;
					case A:
					case D:
						car.setWheelAngle(0.0);
						break;
				}
			}
		}
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

		// Unbind follow view
		scene.translateXProperty().unbind();
		scene.translateYProperty().unbind();

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

		if (!scene.translateXProperty().isBound()) {
			scene.setTranslateX(event.getX() - mouseWorldX * scene.getScaleX() * UNITS_PER_METRE);
			scene.setTranslateY(event.getY() - mouseWorldY * scene.getScaleY() * UNITS_PER_METRE);
		}

	}

	/**
	 * Handle the user directive to add an object at the cursor - given via the
	 * scene's main context menu.
	 * @param event    Context menu event.
	 */
	private void addObject(ActionEvent event) {
		SimulatedCarFormDialog dialog = new SimulatedCarFormDialog(
			fromViewPaneToWorldSpaceX(cursorPosition.getX()),
			fromViewPaneToWorldSpaceY(cursorPosition.getY()),
			this::onDialogConfirmed
		);
		dialog.show();
	}

	/**
	 * Handle the user's confirmed input to the dialog form.
	 * @param wheelBase
	 * @param posX
	 * @param posY
	 * @param controller
	 */
	private void onDialogConfirmed(double wheelBase, double posX, double posY, AlgorithmEnum controller) {

		SimulatedCar car = simulation.createCar(wheelBase);

		synchronized (car) {
			car.getPosition().setX(posX);
			car.getPosition().setY(posY);
			if (controller != null) {
				car.setController(
					Algorithm.createAlgorithm(
						controller,
						car.getDriveInterface(),
						car.getSensorInterface(),
						car.getNetworkInterface(),
						car
					)
				);
			}
		}

		SimulatedCarNode node = new SimulatedCarNode(car);
		node.addEventFilter(
			MouseEvent.MOUSE_CLICKED,
			e -> onCarClicked(e, node)
		);

		scene.getChildren().add(node);
		simulatedNodes.add(node);

	}

	/**
	 * If a car is left-clicked show its properties and control it (if we're not
	 * following another car), and if it's right-clicked then show the context
	 * menu.
	 * @param e
	 * @param node
	 */
	public void onCarClicked(MouseEvent e, SimulatedCarNode node) {

		switch (e.getButton()) {

			case PRIMARY:

				// Display this car's properties in the sidebar
				showProperties(node);

				// If we're not following a car, control this one
				if (!scene.translateXProperty().isBound()) {
					currentSelection = node;
				}

				break;

			case SECONDARY:

				// Create a new context menu
				MenuItem item = new MenuItem("Follow");
				item.setOnAction(e2 -> {
					currentSelection = node;
					// (x, y) = (viewDimension / 2 - pos) * scale
					scene.translateXProperty().bind(
						viewPane.widthProperty()
							.divide(2)
							.subtract(node.translateXProperty())
							.multiply(scene.scaleXProperty())
					);
					scene.translateYProperty().bind(
						viewPane.heightProperty()
							.divide(2)
							.subtract(node.translateYProperty())
							.multiply(scene.scaleYProperty())
					);
				});
				(new ContextMenu(item)).show(node, e.getScreenX(), e.getScreenY());

				break;

		}
		e.consume();
	}

	/**
	 * Step once through the simulation, by a number of seconds defined by the
	 * current time dilation slider. Each increment of the slider equates to
	 * a second of stepping.
	 */
	public void onStep() {
		simulation.smoothUpdate((long)(1e9 * timeDilationSlider.getValue()));
	}

	/**
	 * Toggle the simulation paused state.
	 */
	public void onPause() {
		if (simulation.getTimeDilationFactor() > 0) {
			simulation.setTimeDilationFactor(0);
		}
		else {
			simulation.setTimeDilationFactor(timeDilationSlider.getValue());
		}
	}
}
