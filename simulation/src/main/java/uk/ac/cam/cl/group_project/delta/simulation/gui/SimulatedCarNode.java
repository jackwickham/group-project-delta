package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;

public class SimulatedCarNode extends SimulatedBodyNode {

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
	 * Front left wheel GUI element.
	 */
	private Shape frontLeftWheel;

	/**
	 * Front right wheel GUI element.
	 */
	private Shape frontRightWheel;

	/**
	 * Construct a representation of the given car.
	 * @param car    Car to represent.
	 */
	public SimulatedCarNode(SimulatedCar car) {

		super(car);

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

		frontLeftWheel = makeRect(
			-hw,
			hl - 0.5 * wheelLength,
			wheelWidth,
			wheelLength
		);
		frontRightWheel = makeRect(
			hw - wheelWidth,
			hl - 0.5 * wheelLength,
			wheelWidth,
			wheelLength
		);
		getChildren().add(frontLeftWheel);
		getChildren().add(frontRightWheel);

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
			setRotate(Math.toDegrees(car.getHeading()));
			frontLeftWheel.setRotate(Math.toDegrees(car.getWheelAngle()));
			frontRightWheel.setRotate(Math.toDegrees(car.getWheelAngle()));
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

}
