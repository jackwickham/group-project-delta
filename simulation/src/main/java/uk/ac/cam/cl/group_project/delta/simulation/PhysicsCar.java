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
	 * Length from rear to front axle, in metres.
	 */
	private double wheelBase = 2.5;

	/**
	 * Current power of the engine.
	 */
	private double enginePower = 0.0;

	/**
	 * Initialise physically simulated representation of a car.
	 * @param wheelBase    Distance from rear to front axle.
	 */
	public PhysicsCar(double wheelBase) {
		this.setWheelBase(wheelBase);
	}

	/**
	 * Update the kinematic state of the car, considering friction forces.
	 * @param dt                      Timestep in seconds.
	 */
	@Override
	public void update(double dt) {

		Vector2D vecHeading = new Vector2D(
			-Math.sin(heading), Math.cos(heading)
		);

		// Calculate wheel speed (discounting any lateral drift)
		double speed = getVelocity().dot(vecHeading);

		// Don't brake so hard we go backwards
		double acceleration = Math.max(enginePower, -speed);
		this.setAcceleration(vecHeading.multiply(acceleration));

		/* Geometry dictates that the radius of the circle traced is
		   wheelBase / sin(wheelAngle) and the angular velocity is
		   speed / radius. */
		double radius = wheelBase / Math.sin(wheelAngle);
		this.setHeading(getHeading() + (speed / radius) * dt);

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

	/**
	 * Get current heading of the car body.
	 * @return     Current heading.
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * Set the heading of the car body.
	 * @param heading    Heading to set.
	 */
	private void setHeading(double heading) {
		this.heading = heading;
	}

	/**
	 * Get the current wheel base (distance from rear to front axle)
	 * @return    Current wheel base.
	 */
	public double getWheelBase() {
		return wheelBase;
	}

	/**
	 * Set the distance from rear to front axle.
	 * @param wheelBase    Distance to set.
	 */
	private void setWheelBase(double wheelBase) {
		this.wheelBase = wheelBase;
	}

}
