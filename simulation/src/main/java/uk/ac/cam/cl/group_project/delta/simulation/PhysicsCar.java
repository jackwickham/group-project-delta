package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Represents the physically simulated instantiation of a car.
 */
public class PhysicsCar extends KinematicBody {

	/**
	 * The angle at which the wheels are set. These are relative to the car
	 * body.
	 */
	private double wheelAngle = 0.0;

	/**
	 * The angle at which the car body is currently facing. The cardinal axis of
	 * the car body faces this direction, relative to a global north.
	 */
	private double heading = 0.0;

	/**
	 * Current power of the engine.
	 */
	private double enginePower = 0.0;

	/**
	 * Update the kinematic state of the car, considering friction forces.
	 * @param dt                      Timestep in seconds.
	 */
	@Override
	public void update(double dt) {
		// TODO: implementation
		Vector2D heading = new Vector2D(
			-Math.sin(wheelAngle), Math.cos(wheelAngle)
		);
		this.setAcceleration(heading.multiply(enginePower));
		super.update(dt);
	}

	/**
	 * Fetch current wheel angle.
	 * @return    Current wheel angle.
	 */
	public double getWheelAngle() {
		return wheelAngle;
	}

	/**
	 * Set the current wheel angle.
	 * @param wheelAngle    Wheel angle to set.
	 */
	public void setWheelAngle(double wheelAngle) {
		this.wheelAngle = wheelAngle;
	}

	/**
	 * Get current engine power.
	 * @return    Current engine power.
	 */
	public double getEnginePower() {
		return enginePower;
	}

	/**
	 * Set current engine power.
	 * @param enginePower    Engine power to set.
	 */
	public void setEnginePower(double enginePower) {
		this.enginePower = enginePower;
	}

}
