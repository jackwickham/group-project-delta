package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.MessageReceipt;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the NetworkInterface for simulated vehicles.
 */
public class SimulatedNetworkModule implements NetworkInterface {

	/**
	 * The world in which this network is emulated.
	 */
	private World world;

	/**
	 * The index of the first message in the message buffer of `world` which we
	 * have not seen.
	 */
	private int messageBufferIndex;

	/**
	 * Construct simulated network interface, for the world given.
	 * @param world    World in which messages are transmitted and received.
	 */
	public SimulatedNetworkModule(World world) {
		this.world = world;
		this.messageBufferIndex = world.getMessages().size();
	}

	/**
	 * Broadcasts raw data to all of the other vehicles on the network.
	 * @param message in bytes to be sent
	 */
	public void sendData(byte[] message) {
		world.getMessages().add(new MessageReceipt(message));
	}

	/**
	 * Returns a list of raw messages received from other vehicles since
	 * the last time this method was called. These messages are byte
	 * arrays wrapped in a class which adds a local timestamp upon their arrival.
	 * @return
	 */
	public List<MessageReceipt> pollData() {
		List<MessageReceipt> foundMessages = new ArrayList<>();
		List<MessageReceipt> messages = world.getMessages();
		for (int i = messageBufferIndex; i < messages.size(); ++i) {
			foundMessages.add(messages.get(i));
		}
		return foundMessages;
	}

}
