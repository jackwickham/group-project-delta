package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Represents the physically simulated instantiation of a car.
 */
public class PhysicsCar extends KinematicBody {

	/**
	 * The angle at which the wheels are set.
	 */
	private double wheelAngle = 0.0;

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

}
