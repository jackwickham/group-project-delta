package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.CommsInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.VehicleData;

public class Communications implements CommsInterface {

	/**
	 * The layer which sits above the network layer which messages are
	 * passed through
	 */
	private ControlLayer messageLayer;
	
	/**
	 * The mapping from relative positions in the platoon to the latest message
	 * this vehicle has received from them. Where get(0) indicates the current vehicle
	 * and get(1) indicates the vehicle in front.
	 */
	private PlatoonLookup messageLookup;
	
	/**
	 * The implementation of the top layer of the communications stack.
	 * Passes messages down to the MessageReceiver as gets messages through the PlatoonLookup
	 * 
	 * @param network - the network interface
	 * @param messageLayer - the layer through which messages are passed
	 * @param messageLookup - the mapping from relative positions to their latest message
	 */
	public Communications(NetworkInterface network, 
			ControlLayer messageLayer, 
			PlatoonLookup messageLookup) {
		messageLookup = new PlatoonLookup();
		this.messageLayer = messageLayer;
	}
	
	/**
	 * Send a message by passing it to the lower layer
	 * 
	 * @param message - the message to be sent
	 */
	@Override
	public void sendMessage(VehicleData message) {
		messageLayer.sendMessage(message);
	}

	/**
	 * Return the message last received by the leader
	 * 
	 * @return the latest message from the leader
	 */
	@Override
	public VehicleData getLeaderMessage() {
		messageLayer.updateMessages();
		return messageLookup.getOrDefault(messageLayer.getCurrentPosition(), null);
	}

	/**
	 * Return the latest message from the vehicle which is inFront ahead
	 * 
	 * @param inFront - the relative position ahead to return
	 * @throws IllegalArgumentException - if the inFront argument is negative or 0
	 * @return the latest message for the requested vehicle, null if there is no data
	 */
	@Override
	public VehicleData getPredecessorMessage(int inFront) {
		messageLayer.updateMessages();
		if(inFront <= 0) {
			throw new IllegalArgumentException("Tried to get the message from vehicles behind.");
		}
		return messageLookup.getOrDefault(inFront, null);
	}

	/**
	 * @return whether the vehicle is the leader
	 */
	@Override
	public boolean isLeader() {
		return messageLayer.getCurrentPosition() == 0;
	}

	/**
	 * Send an emergency stop notification to the network
	 */
	@Override
	public void notifyEmergency() {
		messageLayer.notifyEmergency();
	}

}
