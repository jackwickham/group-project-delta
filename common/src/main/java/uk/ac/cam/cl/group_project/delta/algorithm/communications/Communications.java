package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.util.ArrayList;
import java.util.List;

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
	 * @param messageLayer - the layer through which messages are passed
	 * @param messageLookup - the mapping from relative positions to their latest message
	 */
	public Communications(
			ControlLayer messageLayer) {
		this.messageLookup = messageLayer.getPlatoonLookup();
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
	 * Return the messages from vehicles in front of this one
	 */
	@Override
	public List<VehicleData> getPredecessorMessages() {
		messageLayer.updateMessages();
		List<VehicleData> data = new ArrayList<>(messageLayer.getCurrentPosition());
		for(int i = 1; i <= messageLayer.getCurrentPosition(); i++) {
			if(messageLookup.containsKey(i)) {
				data.add(messageLookup.get(i));
			} else {
				data.add(null);
			}
		}
		return data;
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
