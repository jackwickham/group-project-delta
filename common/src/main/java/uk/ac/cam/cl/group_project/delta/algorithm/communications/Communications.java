package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.CommsInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.MessageData;

public class Communications implements CommsInterface {

	private MessageReceiver messageLayer;
	private PlatoonLookup messageLookup;
	
	/**
	 * The implementation of the top layer of the communications stack.
	 * Passes messages down to the MessageReceiver as gets messages through the PlatoonLookup
	 * 
	 * @param network - the network interface
	 */
	public Communications(NetworkInterface network) {
		messageLookup = new PlatoonLookup();
		messageLayer = new MessageReceiver(network, messageLookup);
	}
	
	/**
	 * Send a message by passing it to the lower layer
	 * 
	 * @param message - the message to be sent
	 */
	@Override
	public void sendMessage(MessageData message) {
		messageLayer.sendMessage(message);
	}

	/**
	 * Return the message last received by the leader
	 * 
	 * @return the latest message from the leader
	 */
	@Override
	public MessageData getLeaderMessage() {
		messageLayer.updateMessages();
		return messageLookup.getOrDefault(messageLayer.getCurrentPosition(), null);
	}

	/**
	 * Return the latest message from the vehicle which is inFront ahead
	 * 
	 * @param inFront - the relative position ahead to return
	 * @return the latest message for the requested vehicle, null if there is no data
	 */
	@Override
	public MessageData getPredecessorMessage(int inFront) {
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
