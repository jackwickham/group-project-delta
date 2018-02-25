package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Represents the physically simulated instantiation of a car.
 */
public class PhysicsCar extends PhysicsBody {

	/**
	 * The turn rate of the vehicle in radians/second
	 *
	 * A positive value means that it is turning to the right (clockwise), and
	 * a negative value means that it is turning to the left (anticlockwise)
	 */
	private double turnRate = 0.0;

	/**
	 * The angle at which the car body is currently facing. The cardinal axis of
	 * the car body faces this direction, relative to a global north.
	 */
	private double heading = 0.0;

	/**
	 * The speed of the car in direction `heading`, in ms^-1
	 *
	 * This value can never be negative
	 */
	private double speed = 0.0;

	/**
	 * Length from rear to front axle, in metres.
	 */
	private double wheelBase;

	/**
	 * Current power of the engine (equivalent to acceleration).
	 */
	private double enginePower = 0.0;

	/**
	 * The coefficient used for acceleration = engineForce - c * speed^2 - friction
	 */
	private static final double AIR_RESISTANCE_COEFFICIENT = 1.0;

	/**
	 * The constant that is used to reduce the acceleration
	 */
	private static final double FRICTION = 0.1;

	/**
	 * Initialise physically simulated representation of a car.
	 * @param wheelBase    Distance from rear to front axle.
	 */
	public PhysicsCar(double wheelBase) {
		this.setWheelBase(wheelBase);
	}

	/**
	 * Update the kinematic state of the car, considering friction forces.
	 *
	 * @param dt                      Timestep in seconds.
	 */
	@Override
	public void update(double dt) {
		// First handle the vehicle translation
		Vector2D translation;
		double distanceTravelled = speed * dt;

		if (speed != 0.0) { // Prevent division by 0
			if (turnRate == 0.0) {
				// Straight line, with heading = 0 in y direction
				translation = new Vector2D(
					distanceTravelled * Math.sin(heading),
					distanceTravelled * Math.cos(heading)
				);
			} else {
				// The vehicle will travel around the circle with radius speed/turn rate
				double radius = speed / turnRate;
				double angleMovedAroundCircle = distanceTravelled / radius; // angle measured clockwise

				double startAngle = heading;
				double endAngle = heading + angleMovedAroundCircle;

				double dx = radius * (Math.cos(startAngle) - Math.cos(endAngle));
				double dy = radius * (Math.sin(endAngle) - Math.sin(startAngle));

				translation = new Vector2D(dx, dy);
				heading = endAngle;
			}

			setPosition(getPosition().add(translation));
		}

		// Now update the velocity, making sure that we don't go backwards
		speed = Math.max(speed + getAcceleration() * dt, 0.0);

		super.update(dt);
	}

	/**
	 * Fetch current turn rate
	 * @return    Current turn rate
	 */
	public double getTurnRate() {
		return turnRate;
	}

	/**
	 * Set the current turn rate
	 * @param turnRate    New turn rate
	 */
	public void setTurnRate(double turnRate) {
		this.turnRate = turnRate;
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

	public double getAcceleration() {
		double acceleration = enginePower - AIR_RESISTANCE_COEFFICIENT * speed * speed;
		if (speed > 0) {
			acceleration -= FRICTION;
		}
		return acceleration;
	}

	/**
	 * Get the current acceleration
	 * @return The acceleration vector
	 */
	public Vector2D getAccelerationVector() {
		// Currently, engine power sets acceleration directly
		// In future, this may take into account the turn rate and any limits
		// on the speed that the vehicle can obtain (air resistance/friction)
		return new Vector2D(
			getAcceleration() * Math.cos(heading),
			getAcceleration() * Math.sin(heading)
		);
	}

	/**
	 * Get the current speed in the direction of `heading`
	 * @return Current speed in ms^-1
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Set the current speed, which will be in the direction of `heading`
	 * @param speed The new speed
	 */
	private void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Get the current velocity of the car
	 * @return The velocity vector
	 */
	public Vector2D getVelocity() {
		return new Vector2D(
			speed * Math.sin(heading),
			speed * Math.cos(heading)
		);
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

	/**
	 * Get the angle between the wheels and the vehicle
	 * @return Wheel angle
	 */
	public double getWheelAngle() {
		if (turnRate == 0.0) {
			return 0.0;
		} else {
			double radius = speed / turnRate;
			return Math.atan2(wheelBase, radius);
		}
	}

	/**
	 * Set the angle between the wheels and the vehicle
	 * @param angle New wheel angle
	 */
	public void setWheelAngle(double angle) {
		if (angle == 0.0) {
			turnRate = 0.0;
		} else {
			double radius = wheelBase / Math.tan(angle);
			turnRate = speed / radius;
		}
	}

}
