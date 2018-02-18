package uk.ac.cam.cl.group_project.delta.lego;

import lejos.hardware.ev3.EV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import uk.ac.cam.cl.group_project.delta.DriveInterface;

public class Drive implements DriveInterface {

	private EV3LargeRegulatedMotor L;
	private EV3LargeRegulatedMotor R;
	private EV3MediumRegulatedMotor steer;

	/**
	 * The number of degrees turned by the wheels over a metre.
	 * Measured by experimentation and calculation.
	 */
	private static final int DEGREES_PER_METRE = 2640;

	/**
	 * The number of degrees to rotate the steering motor to move
	 * the wheels from as far left as possible to directly forward.
	 */
	private static final int STRAIGHT_AHEAD = -35;

	/**
	 * The ratio of angle rotation, from wheels to motor.
	 * The motor is connected to a small cog with 12 teeth,
	 * which is connected to a large cog with 20 teeth,
	 * which is in turn connected to the wheels.
	 */
	private static final double GEAR_RATIO = 20.0/12.0;

	/**
	 * The maximum possible speed of the vehicle.
	 */
	private final int MAX_SPEED;

	public Drive(EV3 ev3) {
		Port portL = ev3.getPort("B");
		Port portR = ev3.getPort("C");
		Port portSteer = ev3.getPort("D");
		L = new EV3LargeRegulatedMotor(portL);
		R = new EV3LargeRegulatedMotor(portR);
		steer = new EV3MediumRegulatedMotor(portSteer);
		MAX_SPEED = (int) (Math.min(L.getMaxSpeed(), R.getMaxSpeed()));
		rotateTo(0);
	}

	/**
	 * Attempts to set the acceleration of the vehicle to the specified
	 * signed amount, where a negative value indicates deceleration.
	 * The vehicle will, as quickly as possible, try to attain as close
	 * to the given acceleration as it is able.
	 *
	 * @param acceleration in m/s^2
	 */
	@Override
	public void setAcceleration(double acceleration) {
		// metres/second/second -> degrees/second/second
		int accelerationDegrees = (int) Math.round(acceleration * DEGREES_PER_METRE);
		L.setAcceleration(accelerationDegrees);
		R.setAcceleration(accelerationDegrees);
		int targetSpeed = acceleration > 0 ? MAX_SPEED : 0;
		L.setSpeed(targetSpeed);
		R.setSpeed(targetSpeed);
		L.backward();
		R.backward();
	}

	/**
	 * Attempts to set the turn rate to the specified signed amount,
	 * where a negative value indicates a left turn and a positive value
	 * indicates a right turn. The vehicle will, as quickly as possible,
	 * try to attain as close to the given turn rate as it is able.
	 *
	 * If the vehicle changes speed, its turn rate will become inaccurate.
	 *
	 * @param turnRate in rad/s
	 */
	@Override
	public void setTurnRate(double turnRate) {
		double speedMetres = -L.getRotationSpeed() / ((double) DEGREES_PER_METRE);
		double sine = turnRate * 0.15 / speedMetres;
		sine = Math.max(Math.min(sine, 1), -1);
		double angle = Math.toDegrees(Math.asin(sine));
		double clamped = Math.max(Math.min(angle, -STRAIGHT_AHEAD), STRAIGHT_AHEAD);
		rotateTo(clamped);
	}

	/**
	 * Brings the vehicle to a stop as quickly as possible. Suitable for emergency use.
	 */
	@Override
	public void stop() {
		L.setAcceleration(6000);
		R.setAcceleration(6000);
		L.stop(true);
		R.stop(true);
	}

	/**
	 * Rotate the front wheels to a given angle, where a negative value
	 * indicates a left turn and a positive value indicates a right turn.
	 *
	 * @param angle in rad
	 */
	private void rotateTo(double angle) {
		steer.rotateTo((int) ((STRAIGHT_AHEAD - angle) * GEAR_RATIO));
	}

}
