package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physically simulated collection of physics objects.
 */
public class World {

	/**
	 * A list of bodies contain within this environment.
	 */
	private List<PhysicsBody> bodies;

	/**
	 * A list of messages that have been sent within this environment.
	 */
	private List<MessageReceipt> messages;

	/**
	 * Initialise an empty world.
	 */
	public World() {
		this.bodies = new ArrayList<>();
		this.messages = new ArrayList<>();
	}

	/**
	 * Update all objects with this environment.
	 * @param dt                      Timestep in seconds.
	 */
	public void update(double dt) {
		for (PhysicsBody body : bodies) {
			body.update(dt);
		}
	}

	/**
	 * Fetch the list of bodies contained within this world.
	 * @return    List of bodies.
	 */
	public List<PhysicsBody> getBodies() {
		return this.bodies;
	}

	/**
	 * Fetch the list of messages within this world.
	 * @return    List of messages.
	 */
	public List<MessageReceipt> getMessages() {
		return this.messages;
	}

}
