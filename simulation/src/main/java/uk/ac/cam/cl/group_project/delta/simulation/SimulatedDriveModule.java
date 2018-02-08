package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.DriveInterface;

/**
 * @author jamie
 * @date 08/02/18.
 */
public class SimulatedDriveModule implements DriveInterface {

	/**
	 * Physical car object that this wraps.
	 */
	private PhysicsCar car;

	/**
	 * Constructs interface that wraps simulated car.
	 * @param car    Car to wrap.
	 */
	public SimulatedDriveModule(PhysicsCar car) {
		this.car = car;
	}

	/**
	 * Attempts to set the acceleration of the vehicle to the specified
	 * signed amount, where a negative value indicates deceleration.
	 * The vehicle will, as quickly as possible, try to attain as close
	 * to the given acceleration as it is able.
	 *
	 * @param acceleration in m/s^2
	 */
	public void setAcceleration(double acceleration) {
		car.setEnginePower(acceleration);
	}

	/**
	 * Attempts to set the turn rate to the specified signed amount,
	 * where a negative value indicates a left turn and a positive value
	 * indicates a right turn. The vehicle will, as quickly as possible,
	 * try to attain as close to the given turn rate as it is able.
	 *
	 * @param turnRate in rad/s
	 */
	public void setTurnRate(double turnRate) {
		double heading = car.getHeading();
		Vector2D vecHeading = new Vector2D(
			-Math.sin(heading), Math.cos(heading)
		);
		car.setWheelAngle(
			turnRate * car.getVelocity().dot(vecHeading)
		);
	}

	/**
	 * Brings the vehicle to a stop as quickly as possible. Suitable for emergency use.
	 */
	public void stop() {
		car.setEnginePower(-1000.0);
	}

}
