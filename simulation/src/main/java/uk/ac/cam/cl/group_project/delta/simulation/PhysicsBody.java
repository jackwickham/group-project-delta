package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Represents an abstract simulated body with position, velocity and
 * acceleration.
 */
public class PhysicsBody extends UniquelyIdentifiable {

	/**
	 * Absolute position in world space - in metres.
	 */
	private Vector2D position = new Vector2D(0.0, 0.0);

	/**
	 * Update this object.
	 * @param dt                      Timestep in seconds.
	 */
	public void update(double dt) {
		// Nothing to do.
	}

	/**
	 * Fetch body's position.
	 * @return    Position of body.
	 */
	public Vector2D getPosition() {
		return position;
	}

	/**
	 * Set body's position.
	 * @param position    Position to set to.
	 */
	public void setPosition(Vector2D position) {
		this.position = position;
	}

	/**
	 * Get the position at which the provided ray intersects with this object
	 * if the ray passes through the location returned by `getPosition()`.
	 *
	 * This is used as a cheap approximation for finding the closest point on
	 * a vehicle.
	 *
	 * @param ray The ray to calculate the intersection with, as a normalised
	 *            vector
	 * @return The position in world space where the collision occurs
	 */
	public Vector2D getRayCollisionPosition(Vector2D ray) {
		return getPosition();
	}

}
