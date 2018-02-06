package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Represents an abstract simulated body with position, velocity and
 * acceleration.
 */
public class PhysicsBody {

	/**
	 * Absolute position in world space - in metres.
	 */
	private Vector2D position;

	/**
	 * Vector velocity in metres per second.
	 */
	private Vector2D velocity;

	/**
	 * Vector acceleration in metres per second per second.
	 */
	private Vector2D acceleration;

	/**
	 * Solve motion equations for this body.
	 * @param dt                      Timestep in seconds.
	 * @throws SimulationException    An error occurred during simulation.
	 */
	public void update(double dt) throws SimulationException {
		try {
			position = (Vector2D) position.add(velocity.multiply(dt));
			velocity = (Vector2D) velocity.add(acceleration.multiply(dt));
		}
		catch (Matrix.MatrixException e) {
			throw new SimulationException("Matrix operations failed");
		}
	}

}
