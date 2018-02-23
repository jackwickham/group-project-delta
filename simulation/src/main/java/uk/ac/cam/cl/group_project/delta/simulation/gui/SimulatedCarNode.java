package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;

import java.io.IOException;

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

	private final DoubleProperty velX;

	private final DoubleProperty velY;

	private final DoubleProperty heading;

	private final DoubleProperty wheelAngle;

	private final DoubleProperty enginePower;

	/**
	 * Construct a representation of the given car.
	 * @param car    Car to represent.
	 */
	public SimulatedCarNode(SimulatedCar car) {

		super(car);

		// Construct observable properties
		velX = new SimpleDoubleProperty(car.getVelocity().getX());
		velY = new SimpleDoubleProperty(car.getVelocity().getY());
		wheelAngle = new SimpleDoubleProperty(car.getWheelAngle());
		heading = new SimpleDoubleProperty(car.getHeading());
		enginePower = new SimpleDoubleProperty(car.getEnginePower());

		rotateProperty().bind(heading);

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
		getChildren().add(rect);

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
		getChildren().add(frontLeftWheel);
		getChildren().add(frontRightWheel);
		frontLeftWheel.rotateProperty().bind(wheelAngle);
		frontRightWheel.rotateProperty().bind(wheelAngle);

		getChildren().add(makeRect(
			-hw,
			-hl - 0.5 * wheelLength,
			wheelWidth,
			wheelLength
		));
		getChildren().add(makeRect(
			hw - wheelWidth,
			-hl - 0.5 * wheelLength,
			wheelWidth,
			wheelLength
		));

		Line velocity = new Line(0, 0, 0, 0);
		velocity.endXProperty().bind(velXProperty().multiply(Controller.UNITS_PER_METRE));
		velocity.endYProperty().bind(velYProperty().multiply(Controller.UNITS_PER_METRE));
		velocity.getStyleClass().add("debug");
		getChildren().add(velocity);

	}

	/**
	 * Update the GUI representation of the simulated object according to the
	 * simulation state.
	 */
	@Override
	public void update() {

		super.update();

		SimulatedCar car = getCar();
		synchronized (car) {
			velX.set(car.getVelocity().getX());
			velY.set(car.getVelocity().getY());
			heading.set(Math.toDegrees(car.getHeading()));
			wheelAngle.set(Math.toDegrees(car.getWheelAngle()));
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
