package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;

public class SimulatedCarNode extends SimulatedBodyNode {

	private Line frontLeftWheel;

	private Line frontRightWheel;

	private static final double ASPECT_RATIO = 0.5;

	public SimulatedCarNode(SimulatedCar car) {

		super(car);

		double height = car.getWheelBase() * Controller.UNITS_PER_METRE;
		double width = height * ASPECT_RATIO;
		double hw = width / 2.0;
		double hh = height / 2.0;

		Rectangle rect = new Rectangle(-hw, -hh, width, height);
		rect.setFill(Color.TRANSPARENT);
		rect.setStroke(Color.BLACK);
		getChildren().add(rect);

		double halfWheel = height / 20.0;
		frontLeftWheel = new Line(-hw, hh - halfWheel, -hw, hh + halfWheel);
		frontRightWheel = new Line(hw, hh - halfWheel, hw, hh + halfWheel);
		getChildren().add(frontLeftWheel);
		getChildren().add(frontRightWheel);

		getChildren().add(new Line(-hw, -hh - halfWheel, -hw, -hh + halfWheel));
		getChildren().add(new Line(hw, -hh - halfWheel, hw, -hh + halfWheel));

	}

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

	public SimulatedCar getCar() {
		return (SimulatedCar) getBody();
	}

}
