package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import uk.ac.cam.cl.group_project.delta.Log;
import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;
import uk.ac.cam.cl.group_project.delta.algorithm.AlgorithmData;
import uk.ac.cam.cl.group_project.delta.algorithm.CommsInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Communications;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;

import java.io.IOException;
import java.lang.reflect.Field;

public class SimulatedCarNode extends SimulatedBodyNode implements Paneable {

	/**
	 * Ratio of axle width to the wheel base.
	 */
	private static final double ASPECT_RATIO = 0.7;

	/**
	 * Ratio of car body width to axle width.
	 */
	private static final double BODY_WIDTH_RATIO = 0.95;

	/**
	 * Ratio of car body height to wheel base.
	 */
	private static final double BODY_HEIGHT_RATIO = 1.4;

	/**
	 * Opacity of circles drawn to present platoons.
	 */
	public static final double PLATOON_CIRCLE_OPACITY = 0.2;

	/**
	 * An internal group that will be rotated to align with the heading of the
	 * car.
	 */
	private Group alignedGroup;

	/**
	 * X-component of the car's velocity. Updated by a call to `update()`.
	 */
	private final DoubleProperty velX;

	/**
	 * Y-component of the car's velocity. Updated by a call to `update()`.
	 */
	private final DoubleProperty velY;

	/**
	 * Heading of the car. Updated by a call to `update()`.
	 */
	private final DoubleProperty heading;

	/**
	 * Angle of the car's front wheels. Updated by a call to `update()`.
	 */
	private final DoubleProperty wheelAngle;

	/**
	 * Car's engine power. Updated by a call to `update()`.
	 */
	private final DoubleProperty enginePower;

	/**
	 * Is this car the leader of its platoon?
	 */
	private final BooleanProperty isLeader;

	/**
	 * Is this car in emergency mode?
	 */
	//private final BooleanProperty isEmergency;

	/**
	 * This vehicle's ID.
	 */
	private final IntegerProperty vehicleId;

	/**
	 * The current platoon.
	 */
	private final IntegerProperty platoonId;

	/**
	 * The car's current position within its platoon.
	 */
	private final IntegerProperty platoonPosition;

	/**
	 * The ID of the leader of the car's platoon.
	 */
	private final IntegerProperty platoonLeaderId;

	/**
	 * The hashed colour of this platoon.
	 */
	private final ObjectProperty<Paint> platoonColour;

	/**
	 * The communications layer for this car, if found, null otherwise.
	 */
	private CommsInterface communications = null;

	/**
	 * The control layer for this car, if found, null otherwise.
	 */
	private ControlLayer controller = null;

	/**
	 * Construct a representation of the given car.
	 * @param car    Car to represent.
	 */
	public SimulatedCarNode(SimulatedCar car) {

		super(car);

		alignedGroup = new Group();

		// Construct properties
		velX = new SimpleDoubleProperty(car.getVelocity().getX());
		velY = new SimpleDoubleProperty(car.getVelocity().getY());
		wheelAngle = new SimpleDoubleProperty(Math.toDegrees(-car.getWheelAngle()));
		heading = new SimpleDoubleProperty(Math.toDegrees(-car.getHeading()));
		enginePower = new SimpleDoubleProperty(car.getEnginePower());
		isLeader = new SimpleBooleanProperty(false);
		vehicleId = new SimpleIntegerProperty(0);
		platoonId = new SimpleIntegerProperty(0);
		platoonPosition = new SimpleIntegerProperty(0);
		platoonLeaderId = new SimpleIntegerProperty(0);
		platoonColour = new SimpleObjectProperty<>(Color.TRANSPARENT);

		alignedGroup.rotateProperty().bind(headingProperty());

		constructAlgorithmInstrumentation(car);
		constructSimpleVisualRepresentation(car);

		getChildren().add(alignedGroup);

	}

	/**
	 * Construct GUI representation of algorithm state from instrumentation.
	 * @param car    Car to instrument.
	 */
	private void constructAlgorithmInstrumentation(SimulatedCar car) {
		try {

			// Reflect into a deep dark well...
			Field data = Algorithm.class.getDeclaredField("algorithmData");
			Field comms = AlgorithmData.class.getDeclaredField("commsInterface");
			Field ctrl = Communications.class.getDeclaredField("messageLayer");
			data.setAccessible(true);
			comms.setAccessible(true);
			ctrl.setAccessible(true);

			communications = (Communications) comms.get(
				data.get(
					car.getController()
				)
			);
			controller = (ControlLayer) ctrl.get(communications);

			// Setup properties
			isLeader.set(communications.isLeader());
			vehicleId.set(controller.getVehicleId());
			platoonId.set(controller.getPlatoonId());
			platoonPosition.set(controller.getCurrentPosition());
			platoonLeaderId.set(controller.getLeaderId());
			platoonColour.set(toPaint(platoonId.get()));

			double base = car.getWheelBase() * Controller.UNITS_PER_METRE;

			// Create circles for platoon representation
			Circle leaderCircle = new Circle(base * 1.6);
			leaderCircle.setFill(Color.TRANSPARENT);
			leaderCircle.setStrokeWidth(base * 0.1);
			leaderCircle.setOpacity(PLATOON_CIRCLE_OPACITY);
			leaderCircle.setMouseTransparent(true);
			leaderCircle.strokeProperty().bind(platoonColour);
			leaderCircle.visibleProperty().bind(isLeader);

			Circle platoonCircle = new Circle(base * 1.5);
			platoonCircle.setMouseTransparent(true);
			platoonCircle.setOpacity(PLATOON_CIRCLE_OPACITY);
			platoonCircle.fillProperty().bind(platoonColour);

			// Add text detail for car ID and platoon ID
			Font font = Font.font("monospace", 11);

			Text vid = new Text(base, base, null);
			vid.setScaleY(-1);
			vid.setFont(font);
			vid.textProperty().bind(vehicleId.asString());

			Text pid = new Text(base, base - 11, null);
			pid.setScaleY(-1);
			pid.setFont(font);
			pid.textProperty().bind(platoonId.asString().concat("[").concat(platoonPosition).concat("]"));

			getChildren().addAll(leaderCircle, platoonCircle, vid, pid);

		}
		catch (IllegalAccessException | NoSuchFieldException e) {
			Log.warn("Cannot snoop on the internal algorithm state");
			Log.warn(e);
		}
	}

	/**
	 * Generates a {@link Paint} based on input. This function is designed to be
	 * determinate, but not to be continuous; this could be considered to be a
	 * hashing function.
	 * @param value    Value to convert to a colour.
	 * @return         Hash output, a colour.
	 */
	public static Paint toPaint(int value) {
		double h = (Integer.hashCode(value) * 360.0) / Integer.MAX_VALUE;
		return Color.hsb(h, 0.5, 0.7); // h = h % 360, 0 <= s <= 1, 0 <= b <= 1
	}

	/**
	 * Construct visual representation of car's physical state.
	 * @param car    Car to represent.
	 */
	private void constructSimpleVisualRepresentation(SimulatedCar car) {
		// Create GUI representation
		double length = car.getWheelBase() * Controller.UNITS_PER_METRE;
		double width = length * ASPECT_RATIO;
		double hw = width / 2.0;
		double hl = length / 2.0;

		Rectangle rect = makeRect(
			-hw * BODY_WIDTH_RATIO,
			-hl * BODY_HEIGHT_RATIO,
			width * BODY_WIDTH_RATIO,
			length * BODY_HEIGHT_RATIO
		);
		alignedGroup.getChildren().add(rect);

		double wheelLength = length / 5.0;
		double wheelWidth = wheelLength / 3.0;

		Rectangle frontLeftWheel = makeRect(
			-hw,
			hl - 0.5 * wheelLength,
			wheelWidth,
			wheelLength
		);
		Rectangle frontRightWheel = makeRect(
			hw - wheelWidth,
			hl - 0.5 * wheelLength,
			wheelWidth,
			wheelLength
		);
		alignedGroup.getChildren().add(frontLeftWheel);
		alignedGroup.getChildren().add(frontRightWheel);
		frontLeftWheel.rotateProperty().bind(wheelAngle);
		frontRightWheel.rotateProperty().bind(wheelAngle);

		alignedGroup.getChildren().add(makeRect(
			-hw,
			-hl - 0.5 * wheelLength,
			wheelWidth,
			wheelLength
		));
		alignedGroup.getChildren().add(makeRect(
			hw - wheelWidth,
			-hl - 0.5 * wheelLength,
			wheelWidth,
			wheelLength
		));
	}

	/**
	 * Update the GUI representation of the simulated object by polling the
	 * simulation state.
	 */
	@Override
	public void update() {

		super.update();

		SimulatedCar car = getCar();
		synchronized (car) {
			velX.set(car.getVelocity().getX());
			velY.set(car.getVelocity().getY());
			heading.set(Math.toDegrees(-car.getHeading()));
			wheelAngle.set(Math.toDegrees(-car.getWheelAngle()));
			enginePower.set(car.getEnginePower());
			if (communications != null) {
				isLeader.set(communications.isLeader());
			}
			if (controller != null) {
				vehicleId.set(controller.getVehicleId());
				platoonId.set(controller.getPlatoonId());
				platoonPosition.set(controller.getCurrentPosition());
				platoonLeaderId.set(controller.getLeaderId());
				platoonColour.set(toPaint(platoonId.get()));
			}
		}

	}

	/**
	 * Fetch the car that this GUI element represents.
	 * @return    A {@link SimulatedCar}.
	 */
	public SimulatedCar getCar() {
		return (SimulatedCar) getBody();
	}

	/**
	 * Make a border-only rectangle - no fill and black borders.
	 * @param x    X-position.
	 * @param y    Y-position.
	 * @param w    Width.
	 * @param h    Height.
	 * @return     Constructed rectangle.
	 */
	private static Rectangle makeRect(double x, double y, double w, double h) {
		Rectangle rect = new Rectangle(x, y, w, h);
		rect.setFill(Color.TRANSPARENT);
		rect.setStroke(Color.BLACK);
		return rect;
	}

	/**
	 * Convert this object to a {@link Pane} for display in a properties panel.
	 * @return    GUI representation of this object.
	 */
	@Override
	public Pane toPane() {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("car.properties.fxml"));
			Pane pane = loader.load();
			CarPropertiesController controller = loader.getController();

			controller.uuid.setText(Integer.toString(getCar().getUuid()));
			controller.controller.setText(
				getCar().getController().getClass().getSimpleName()
			);
			controller.positionX.textProperty().bind(
				posXProperty().divide(Controller.UNITS_PER_METRE).asString("%.2f")
			);
			controller.positionY.textProperty().bind(
				posYProperty().divide(Controller.UNITS_PER_METRE).asString("%.2f")
			);
			controller.heading.textProperty().bind(
				headingProperty().asString("%.2f°")
			);
			controller.wheelAngle.textProperty().bind(
				wheelAngleProperty().asString("%.2f°")
			);
			controller.enginePower.textProperty().bind(
				enginePowerProperty().asString("%.2f")
			);
			controller.velocityX.textProperty().bind(
				velXProperty().asString("%.2f")
			);
			controller.velocityY.textProperty().bind(
				velYProperty().asString("%.2f")
			);

			return pane;

		}
		catch (IOException e) {
			return new Pane(new Text(e.getMessage()));
		}
	}

	public DoubleProperty velXProperty() {
		return velX;
	}

	public DoubleProperty velYProperty() {
		return velY;
	}

	public DoubleProperty headingProperty() {
		return heading;
	}

	public DoubleProperty wheelAngleProperty() {
		return wheelAngle;
	}

	public DoubleProperty enginePowerProperty() {
		return enginePower;
	}
}
