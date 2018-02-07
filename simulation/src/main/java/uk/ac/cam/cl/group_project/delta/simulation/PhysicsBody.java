package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Represents an abstract simulated body with position, velocity and
 * acceleration.
 */
abstract public class PhysicsBody {

	/**
	 * Absolute position in world space - in metres.
	 */
	private Vector2D position;

	/**
	 * Update this object.
	 * @param dt                      Timestep in seconds.
	 */
	abstract public void update(double dt);

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

}
