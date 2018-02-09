package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Encapsulates modelling of simulated wireless (broadcast) network.
 */
public class SimulatedNetwork {

	/**
	 * List of nodes in the simulated network.
	 */
	private List<SimulatedNode> handlers;

	/**
	 * Construct network.
	 */
	public SimulatedNetwork() {
		handlers = new ArrayList<>();
	}

	/**
	 * Register a message callback, which will be invoked every time a message
	 * is sent.
	 * @param handler    Concrete instance of {@link SimulatedNode}.
	 */
	public synchronized void register(SimulatedNode handler) {
		this.handlers.add(handler);
	}

	/**
	 * Remove message callback.
	 * @param handler    Handler to remove.
	 */
	public synchronized void deregister(SimulatedNode handler) {
		this.handlers.remove(handler);
	}

	/**
	 * Broadcast a message from the given sender node.
	 * @param sender     Node from which this message was sent.
	 * @param message    Byte array to send as a message.
	 */
	public synchronized void broadcast(SimulatedNode sender, byte[] message) {
		for (SimulatedNode handler : handlers) {
			// TODO: Perform quality degradation with distance
			handler.handleMessage(Arrays.copyOf(message, message.length));
		}
	}

	/**
	 * Represents a network node in the simulated network.
	 */
	public interface SimulatedNode {

		/**
		 * Fetches the node's current position.
		 * @return    The current position.
		 */
		Vector2D getPosition();

		/**
		 * Handle a received message.
		 * @param message    The message received.
		 */
		void handleMessage(byte[] message);

	}

}
