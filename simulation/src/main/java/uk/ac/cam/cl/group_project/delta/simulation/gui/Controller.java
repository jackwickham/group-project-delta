package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import uk.ac.cam.cl.group_project.delta.simulation.*;

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
	 * Root GUI element.
	 */
	@FXML
	public AnchorPane root;

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
	 * Network parameter spinner.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.SimulatedNetwork#setMessageDeliveryModifier(double)
	 */
	@FXML
	public EditableSpinner<Double> networkDeliveryModifier;

	/**
	 * Sensor parameter spinner.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.FaultySensorModule#setFrontProximityStdDev(double)
	 */
	@FXML
	public EditableSpinner<Double> frontProximityStdDev;

	/**
	 * Sensor parameter spinner.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.FaultySensorModule#setFrontProximityFailureRate(double)
	 */
	@FXML
	public EditableSpinner<Double> frontProximityFailureRate;

	/**
	 * Sensor parameter checkbox.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.FaultySensorModule#setFrontProximityEnabled(boolean)
	 */
	@FXML
	public CheckBox frontProximityEnabled;

	/**
	 * Beacon detection parameter checkbox.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.FaultySensorModule#setBeaconsEmulateMindstorms(boolean)
	 */
	@FXML
	public CheckBox beaconsEmulateMindstorms;

	/**
	 * Beacon detection parameter spinner.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.FaultySensorModule#setBeaconDistanceStdDev(double)
	 */
	@FXML
	public EditableSpinner<Double> beaconDistanceStdDev;

	/**
	 * Beacon detection parameter spinner.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.FaultySensorModule#setBeaconAngleStdDev(double)
	 */
	@FXML
	public EditableSpinner<Double> beaconAngleStdDev;

	/**
	 * Motion detection parameter spinner.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.FaultySensorModule#setAccelerationStdDev(double)
	 */
	@FXML
	public EditableSpinner<Double> accelerationStdDev;

	/**
	 * Motion detection parameter spinner.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.FaultySensorModule#setSpeedStdDev(double)
	 */
	@FXML
	public EditableSpinner<Double> speedStdDev;

	/**
	 * Motion detection parameter spinner.
	 * @see uk.ac.cam.cl.group_project.delta.simulation.FaultySensorModule#setTurnRateStdDev(double)
	 */
	@FXML
	public EditableSpinner<Double> turnRateStdDev;

	/**
	 * The JavaFX GUI updater - an "animation".
	 */
	private Timeline timeline;

	/**
	 * Context menu displayed when the user right-clicks the main view pane.
	 */
	private ContextMenu sceneContextMenu;

	/**
	 * Context menu displayed when the user right-clicks an vehicles.
	 */
	private ContextMenu vehicleContextMenu;

	/**
	 * Context menu item for following the clicked vehicle.
	 */
	private MenuItem vehicleContextMenuFollowItem;

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

		// Create timeline
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(
			Duration.seconds(1.0 / 40.0),
			e -> this.update()
		));

		// Create scene context menu
		MenuItem item = new MenuItem("Add object");
		item.setOnAction(this::addObject);
		sceneContextMenu = new ContextMenu(item);

		// Create vehicle context menu
		vehicleContextMenuFollowItem = new MenuItem("Follow");
		vehicleContextMenu = new ContextMenu(vehicleContextMenuFollowItem);

	}

	/**
	 * FXML initialisation.
	 */
	@FXML
	public void initialize() {

		root.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
		root.addEventHandler(KeyEvent.KEY_RELEASED, this::onKeyReleased);

		networkLog.setItems(networkLogStore);

		// Construct table columns
		TableColumn<NetworkLogMessage, String> time = new TableColumn<>("Time");
		time.setCellValueFactory(new PropertyValueFactory<>("time"));
		time.setPrefWidth(150);
		networkLog.getColumns().add(time);

		TableColumn<NetworkLogMessage, String> src = new TableColumn<>("From (VID)");
		src.setCellValueFactory(new PropertyValueFactory<>("senderId"));
		src.setPrefWidth(110);
		networkLog.getColumns().add(src);

		TableColumn<NetworkLogMessage, String> dst = new TableColumn<>("To (PID)");
		dst.setCellValueFactory(new PropertyValueFactory<>("platoonId"));
		dst.setPrefWidth(110);
		networkLog.getColumns().add(dst);

		TableColumn<NetworkLogMessage, String> message = new TableColumn<>("Message");
		message.setCellValueFactory(new PropertyValueFactory<>("message"));
		message.setPrefWidth(600);
		networkLog.getColumns().add(message);

		// Time controls
		pausedPane.visibleProperty().bind(pauseButton.selectedProperty());
		timeDilationSlider.valueProperty().addListener(
			(observableValue, oldValue, newValue) -> {
				if (!pauseButton.isSelected()) {
					simulation.setTimeDilationFactor(newValue.doubleValue());
				}
			}
		);

		// Bind options
		networkDeliveryModifier.valueProperty().addListener(
			(value, prev, next) -> SimulatedNetwork.setMessageDeliveryModifier(next)
		);
		frontProximityStdDev.valueProperty().addListener(
			(value, prev, next) -> FaultySensorModule.setFrontProximityStdDev(next)
		);
		frontProximityFailureRate.valueProperty().addListener(
			(value, prev, next) -> FaultySensorModule.setFrontProximityFailureRate(next)
		);
		frontProximityEnabled.selectedProperty().addListener(
			(value, prev, next) -> FaultySensorModule.setFrontProximityEnabled(next)
		);
		beaconsEmulateMindstorms.selectedProperty().addListener(
			(value, prev, next) -> FaultySensorModule.setBeaconsEmulateMindstorms(next)
		);
		beaconDistanceStdDev.valueProperty().addListener(
			(value, prev, next) -> FaultySensorModule.setBeaconDistanceStdDev(next)
		);
		beaconAngleStdDev.valueProperty().addListener(
			(value, prev, next) -> FaultySensorModule.setBeaconAngleStdDev(next)
		);
		accelerationStdDev.valueProperty().addListener(
			(value, prev, next) -> FaultySensorModule.setAccelerationStdDev(next)
		);
		speedStdDev.valueProperty().addListener(
			(value, prev, next) -> FaultySensorModule.setSpeedStdDev(next)
		);
		turnRateStdDev.valueProperty().addListener(
			(value, prev, next) -> FaultySensorModule.setTurnRateStdDev(next)
		);

		// And we are ready to begin...
		start();

	}

	/**
	 * Start background tasks: the simulation, GUI updater, and packet sniffer.
	 */
	private void start() {

		// Register network packet sniffer
		simulation.getNetwork().register(msg ->
			Platform.runLater(() -> addToNetworkLog(new MessageReceipt(msg)))
		);

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

		boolean consume = true;

		switch (keyEvent.getCode()) {
			case W:
				if (currentSelection != null) {
					SimulatedCar car = currentSelection.getCar();
					synchronized (car) {
						car.setEnginePower(0.25);
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
			case SPACE:
				pauseButton.fire();
				break;
			case SEMICOLON:
				stepButton.fire();
				break;
			case DIGIT0:
				timeDilationSlider.adjustValue(1.0);
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
			default:
				consume = false;
				break;
		}

		if (consume) {
			keyEvent.consume();
		}

	}

	/**
	 * Handle a key released event.
	 * @param keyEvent    Structure containing event information (e.g. key code)
	 */
	public void onKeyReleased(KeyEvent keyEvent) {
		if (currentSelection != null) {
			SimulatedCar car = currentSelection.getCar();
			boolean consume = true;
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
					default:
						consume = false;
						break;
				}
			}
			if (consume) {
				keyEvent.consume();
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
		viewPane.requestFocus();
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
	 * @param wheelBase     User input wheel base.
	 * @param posX          User input x-coordinate.
	 * @param posY          User input y-coordinate.
	 * @param controller    User selected algorithm controller.
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
	 * @param e       Mouse click event.
	 * @param node    Node representing the car.
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
				vehicleContextMenuFollowItem.setOnAction(e2 -> {
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
				vehicleContextMenu.show(node, e.getScreenX(), e.getScreenY());

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

	public void clearNetworkLog() {
		networkLogStore.clear();
	}

	/**
	 * Reset the simulation world state.
	 */
	public void reset() {

		// Terminate the simulation
		simulation.terminate();
		simulation.interrupt();
		timeline.stop();

		// Reset view
		scene.translateXProperty().unbind();
		scene.translateYProperty().unbind();
		scene.setTranslateX(0);
		scene.setTranslateY(0);
		scene.setScaleX(1);
		scene.setScaleY(1);

		// Clear the scene
		List<Node> nodes = new ArrayList<>(scene.getChildren());
		for (Node node : nodes) {
			if (node instanceof SimulatedCarNode) {
				scene.getChildren().remove(node);
			}
		}

		// Clear network log
		clearNetworkLog();

		// Clear selection
		currentSelection = null;
		propertiesPane.getChildren().clear();

		// Restart the simulation
		simulation = new SimulationThread();

		// And we may begin (again)...
		start();

	}
}
