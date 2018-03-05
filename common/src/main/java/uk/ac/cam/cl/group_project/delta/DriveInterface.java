package uk.ac.cam.cl.group_project.delta;

public interface DriveInterface {

	/**
	 * Attempts to set the acceleration of the vehicle to the specified
	 * signed amount, where a negative value indicates deceleration.
	 * The vehicle will, as quickly as possible, try to attain as close
	 * to the given acceleration as it is able.
	 *
	 * @param acceleration in m/s^2
	 */
	public void setAcceleration(double acceleration);


	/**
	 * Attempts to set the turn rate to the specified signed amount,
	 * where a negative value indicates a left turn and a positive value
	 * indicates a right turn. The vehicle will, as quickly as possible,
	 * try to attain as close to the given turn rate as it is able.
	 *
	 * @param turnRate in rad/s
	 */
	public void setTurnRate(double turnRate);


	/**
	 * Brings the vehicle to a stop as quickly as possible. Suitable for emergency use.
	 */
	public void stop();
}
