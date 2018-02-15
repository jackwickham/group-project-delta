package uk.ac.cam.cl.group_project.delta.algorithm;

public interface CommsInterface {

	/**
	 * Broadcasts a message to all other vehicles in the platoon.
	 * This may be an unreliable broadcast and messages may
	 * never reach some nodes.
	 *
	 * @param message to be sent
	 */
	public void sendMessage(VehicleData message);

	/**
	 * Retrieves the last received message from the platoon leader.
	 * This may not be the most recently sent message. The behaviour
	 * is undefined if we are the leader.
	 *
	 * @return the latest leader's message
	 */
	public VehicleData getLeaderMessage();

	/**
	 * Retrieves the last received message from a vehicle ahead,
	 * where an input of 1 will retrieve the message from the vehicle
	 * directly ahead. Throws an exception if the input is negative
	 * or zero, and returns null if it is out of the bounds of the platoon.
	 *
	 * @param inFront - the number of vehicles in front to receive
	 * @return the latest message from the vehicle (inFront)
	 * 		or null if this is beyond the leader
	 */
	public VehicleData getPredecessorMessage(int inFront);

	/**
	 * Returns a boolean value indicating if this vehicle is the current
	 * leader of its platoon.
	 *
	 * @return whether the vehicle is the leader
	 */
	public boolean isLeader();

	/**
	 * Broadcasts an emergency signal to all other vehicles, indicating
	 * that the vehicle is performing emergency braking.
	 */
	public void notifyEmergency();

	//public void registerEmergencyHandler(Function handler);

}
