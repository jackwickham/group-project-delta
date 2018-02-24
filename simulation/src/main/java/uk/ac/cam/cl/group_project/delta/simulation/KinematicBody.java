package uk.ac.cam.cl.group_project.delta.simulation;

public class KinematicBody extends PhysicsBody {

	/**
	 * Vector velocity in metres per second.
	 */
	private Vector2D velocity = new Vector2D(0.0, 0.0);

	/**
	 * Vector acceleration in metres per second per second.
	 */
	private Vector2D acceleration = new Vector2D(0.0, 0.0);

	/**
	 * Update this object, solving kinematic motion equations.
	 * @param dt    Timestep in seconds.
	 */
	@Override
	public void update(double dt) {
		this.setPosition(
			this.getPosition().add(
				this.getVelocity().multiply(dt)
			)
		);
		this.setVelocity(
			this.getVelocity().add(
				this.getAcceleration().multiply(dt)
			)
		);
	}

	/**
	 * Fetch body's velocity.
	 * @return    Velocity of this body.
	 */
	public Vector2D getVelocity() {
		return velocity;
	}

	/**
	 * Set this body's velocity.
	 * @param velocity    Velocity to set to.
	 */
	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	/**
	 * Fetch body's acceleration.
	 * @return    Acceleration of this body.
	 */
	public Vector2D getAcceleration() {
		return acceleration;
	}

	/**
	 * Set this body's acceleration.
	 * @param acceleration    Acceleration to set.
	 */
	public void setAcceleration(Vector2D acceleration) {
		this.acceleration = acceleration;
	}
}
