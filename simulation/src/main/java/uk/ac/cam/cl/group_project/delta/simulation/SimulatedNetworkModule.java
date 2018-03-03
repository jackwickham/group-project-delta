package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.MessageReceipt;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the NetworkInterface for simulated vehicles.
 */
public class SimulatedNetworkModule implements NetworkInterface
{

	/**
	 * The car which sends messages.
	 */
	private PhysicsCar car;

	/**
	 * Simulated communications medium.
	 */
	private SimulatedNetwork network;

	/**
	 * Buffer of messages received since last call to `pollData`.
	 */
	private List<MessageReceipt> messageBuffer;

	/**
	 * Handler function to call when an emergency message is received.
	 */
	private EmergencyHandler emergencyHandler;

	/**
	 * Construct simulated network interface, for the world given.
	 * @param car        The car that transmits and receives messages.
	 * @param network    The network on which to communicate.
	 */
	public SimulatedNetworkModule(PhysicsCar car, SimulatedNetwork network) {
		this.car = car;
		this.network = network;
		this.network.register(this);
		this.messageBuffer = new ArrayList<>();
	}

	/**
	 * Broadcasts raw data to all of the other vehicles on the network.
	 * @param message   in bytes to be sent
	 */
	@Override
	public void sendData(byte[] message) {
		this.network.broadcast(this, message);
	}

	/**
	 * Returns a list of raw messages received from other vehicles since
	 * the last time this method was called. These messages are byte
	 * arrays wrapped in a class which adds a local timestamp upon their arrival.
	 * @return    List of messages.
	 */
	@Override
	public synchronized List<MessageReceipt> pollData() {
		List<MessageReceipt> messages = new ArrayList<>(this.messageBuffer);
		this.messageBuffer.clear();
		return messages;
	}

	/**
	 * Handle a received message.
	 * @param message    The message received.
	 */
	public synchronized void handleMessage(byte[] message) {
		if (emergencyHandler != null && MessageReceipt.isEmergencyMessage(message)) {
			emergencyHandler.handle(message);
		}
		else {
			messageBuffer.add(new MessageReceipt(message));
		}
	}

	/**
	 * Fetches the node's current position.
	 * @return    The current position.
	 */
	public Vector2D getPosition() {
		return this.car.getPosition();
	}

	/**
	 * Set the emergency message handler.
	 * @param emergencyHandler    Handler callback.
	 */
	public void setEmergencyHandler(EmergencyHandler emergencyHandler) {
		this.emergencyHandler = emergencyHandler;
	}

	/**
	 * Functional interface for emergency message handling.
	 */
	@FunctionalInterface
	public interface EmergencyHandler {
		/**
		 * Handle an emergency message.
		 * @param message    The byte array received that contains the emergency
		 *                   message.
		 */
		void handle(byte[] message);
	}

}
