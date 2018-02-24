package uk.ac.cam.cl.group_project.delta.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Encapsulates modelling of simulated wireless (broadcast) network.
 */
public class SimulatedNetwork {

	/**
	 * List of nodes in the simulated network.
	 */
	private List<SimulatedNetworkModule> handlers;

	/**
	 * A modifier for the rate at which packets should be dropped.
	 * @see #setMessageDeliveryModifier(double) for full details.
	 */
	private static double messageDeliveryModifier = 0.0;

	// Random is used for deciding whether a message should be delivered
	private Random random;

	/**
	 * Construct network.
	 */
	public SimulatedNetwork() {
		handlers = new ArrayList<>();
		random = new Random();
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
			double distance = sender.getPosition().subtract(handler.getPosition()).magnitude();
			if (!shouldDropPacket(distance)) {
				handler.handleMessage(Arrays.copyOf(message, message.length));
			} // else the packet was lost
		}
	}

	/**
	 * Set the modifier for when packets should be dropped.
	 *
	 * A value of 0 means that packets should never be dropped, and any value larger than that is permitted. A value of
	 * 1 will give about 95% packet delivery at 1m, 85% at 2m, and 50% at 3m, while a value of 2 will give about 90% at
	 * 1m, 50% at 1.5m and 25% at 2m
	 *
	 * @param value The new delivery modifier
	 */
	public static synchronized void setMessageDeliveryModifier (double value) {
		if (value < 0) {
			throw new IllegalArgumentException("Message delivery modifier must be at least 0");
		}
		messageDeliveryModifier = value;
	}

	/**
	 * Determine whether this packet should be dropped when simulating packet loss. The probability distribution curve
	 * chosen to simulate packet loss is an arctan curve which has been flipped, shifted and stretched so that it has
	 * a domain from 0..Infinity and range of 0..1.
	 *
	 * The message delivery modifier stretches the curve in the x direction, so the closer the value is to 0, the slower
	 * the probabilities will increase and therefore the lower the chance of the packet being dropped. A value of 0
	 * means that no packets will be dropped.
	 *
	 *
	 * @link https://www.wolframalpha.com/input/?i=y+%3D+0.55705+-+0.35463+*+arctan+(0.7+*+x+-+2)+for+0+<+x+<+10
	 *
	 * @param distance The distance between the two vehicles involved in this communication
	 * @return Whether the packet should be dropped
	 */
	private boolean shouldDropPacket(double distance) {
		if (messageDeliveryModifier == 0) {
			// Modifier of 0 means always succeed
			return false;
		}
		// The magic probability distribution function
		//
		// The shift of 3 within the atan function was chosen to give a nice looking curve, and the other constants were
		// calculated to give the desired range. They are hard coded here because they shouldn't need to be changed, but
		// if f(x) = atan(modifier * x - shift) then the multiplier is 1/[f(∞) - f(0)] and the added constant is
		// 1 - multiplier*f(0), to force it to 1 at 0.
		double valueFromDistribution = 0.55705 - 0.35463 * Math.atan(messageDeliveryModifier * distance - 3);

		// Generate a random value between 0 and 1
		double randomValue = random.nextDouble();
		return randomValue > valueFromDistribution;
	}

}
