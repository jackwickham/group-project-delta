package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates modelling of simulated wireless (broadcast) network.
 */
public class SimulatedNetwork {

	/**
	 * List of nodes in the simulated network.
	 */
	private List<SimulatedNetworkModule> handlers;

	/**
	 * Construct network.
	 */
	public SimulatedNetwork() {
		handlers = new ArrayList<>();
	}

	/**
	 * Register a message callback, which will be invoked every time a message
	 * is sent.
	 * @param handler    Concrete instance of {@link SimulatedNetworkModule}.
	 */
	public synchronized void register(SimulatedNetworkModule handler) {
		this.handlers.add(handler);
	}

	/**
	 * Remove message callback.
	 * @param handler    Handler to remove.
	 */
	public synchronized void deregister(SimulatedNetworkModule handler) {
		this.handlers.remove(handler);
	}

	/**
	 * Broadcast a message from the given sender node.
	 * @param sender     Node from which this message was sent.
	 * @param message    Byte array to send as a message.
	 */
	public synchronized void broadcast(SimulatedNetworkModule sender, byte[] message) {
		for (SimulatedNetworkModule handler : handlers) {
			// TODO: Perform quality degradation with distance
			handler.handleMessage(Arrays.copyOf(message, message.length));
		}
	}

}
