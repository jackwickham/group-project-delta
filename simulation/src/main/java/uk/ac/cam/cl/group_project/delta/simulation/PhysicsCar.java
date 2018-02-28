package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Represents the physically simulated instantiation of a car.
 */
public class PhysicsCar extends PhysicsBody {

	/**
	 * The angle of the wheels relative to the car, in radians
	 *
	 * A positive value means that it is turning to the right (clockwise), and
	 * a negative value means that it is turning to the left (anticlockwise)
	 */
	private double wheelAngle = 0.0;

	/**
	 * If not null, this means that we are trying to maintain a constant turn
	 * rate rather than a constant wheel angle, so the wheel angle needs to be
	 * updated in each simulation step
	 */
	private Double turnRate = null;

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
	 * Current power of the engine (at low velocities, when resistance is
	 * negligible, this is equivalent to acceleration)
	 */
	private double enginePower = 0.0;

	/**
	 * If not null, this means we are trying to maintain a constant acceleration
	 * rather than a constant engine power, so we need to update the engine
	 * power in each simulation step to account for the increase in air
	 * resistance
	 */
	private Double targetAcceleration = null;

	/**
	 * The maximum permitted wheel angle, in radians
	 */
	private static final double MAX_WHEEL_ANGLE = Math.PI / 4;

	/**
	 * The maximum velocity.
	 */
	private static final double MAX_VELOCITY = 2;

	/**
	 * The coefficient used for acceleration = engineForce - c * speed^2 - friction
	 */
	private static final double AIR_RESISTANCE_COEFFICIENT = 0.7;

	/**
	 * A constant factor to reduce acceleration
	 */
	private static final double FRICTION = 0.1;

	/**
	 * The maximum power output of the engine is the power output required to
	 * maintain the maximum velocity
	 */
	private static final double MAX_ENGINE_POWER = AIR_RESISTANCE_COEFFICIENT *
			MAX_VELOCITY * MAX_VELOCITY - FRICTION;

	/**
	 * The maximum engine power for deceleration. Air resistance will mean
	 * that the true deceleration is higher than this.
	 */
	private static final double MAX_DECELERATION = 2;

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

		if (turnRate != null) {
			setTurnRate(turnRate);
		}

		if (speed != 0.0) { // Prevent division by 0
			if (wheelAngle == 0.0) {
				// Straight line, with heading = 0 in y direction
				translation = new Vector2D(
					distanceTravelled * Math.sin(heading),
					distanceTravelled * Math.cos(heading)
				);
			} else {
				// The vehicle will travel around the circle with radius speed/turn rate
				double radius = speed / getTurnRate();
				double angleMovedAroundCircle = distanceTravelled / radius; // angle measured clockwise

				double startAngle = heading;
				double endAngle = heading + angleMovedAroundCircle;

				// nb. Sin and cos are swapped here because heading is perpendicular to motion around circle
				double dx = radius * (Math.cos(startAngle) - Math.cos(endAngle));
				double dy = radius * (Math.sin(endAngle) - Math.sin(startAngle));

				translation = new Vector2D(dx, dy);
				heading = endAngle % (Math.PI * 2);
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
		if (wheelAngle == 0.0) {
			return 0.0;
		} else {
			double radius = wheelBase / Math.tan(wheelAngle);
			return speed / radius;
		}
	}

	/**
	 * Set the current turn rate. The simulation will try to maintain this turn
	 * rate in subsequent simulation steps by updating the wheel angle based on
	 * the car's speed
	 * @param turnRate    New turn rate
	 */
	public void setTurnRate(double turnRate) {
		this.turnRate = turnRate;
		if (turnRate == 0.0) {
			setWheelAngleInternal(0.0);
		} else {
			double radius = speed / turnRate;
			setWheelAngleInternal(Math.atan2(wheelBase, radius));
		}
	}

	/**
	 * Get the angle between the wheels and the vehicle
	 * @return Wheel angle
	 */
	public double getWheelAngle() {
		return wheelAngle;
	}

	/**
	 * Set the angle between the wheels and the vehicle
	 * @param angle New wheel angle
	 */
	public void setWheelAngle(double angle) {
		// We set an angle, so stop trying to maintain a turn rate
		this.turnRate = null;
		setWheelAngleInternal(angle);
	}

	/**
	 * Set the wheel angle without changing the target turn rate
	 * @param angle The angle to use
	 */
	private void setWheelAngleInternal(double angle) {
		wheelAngle = Math.max(Math.min(angle, MAX_WHEEL_ANGLE), -MAX_WHEEL_ANGLE);
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
		// We are now targeting an engine power rather than acceleration
		this.targetAcceleration = null;
		setEnginePowerInternal(enginePower);
	}

	/**
	 * Get the rate of acceleration of the vehicle in the direction of motion
	 * @return The acceleration value
	 */
	public double getAcceleration() {
		double acceleration = enginePower - AIR_RESISTANCE_COEFFICIENT * speed * speed;
		if (speed > 0) {
			acceleration -= FRICTION;
		}
		return acceleration;
	}

	/**
	 * Set the acceleration. The engine power will be increased to achieve the
	 * target acceleration while taking into account air resistance.
	 * @param acceleration The new acceleration
	 */
	public void setAcceleration(double acceleration) {
		this.targetAcceleration = acceleration;
		double requiredEnginePower = acceleration + AIR_RESISTANCE_COEFFICIENT * speed * speed;
		if (speed > 0 || acceleration > 0) {
			requiredEnginePower += FRICTION;
		}
		setEnginePowerInternal(requiredEnginePower);
	}

	private void setEnginePowerInternal(double enginePower) {
		this.enginePower = Math.max(Math.min(enginePower, MAX_ENGINE_POWER), -MAX_DECELERATION);
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
	 * Get the vehicle's current heading as a vector
	 * @return The acceleration vector
	 */
	public Vector2D getHeadingVector() {
		return new Vector2D(Math.sin(heading), Math.cos(heading));
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
