package uk.ac.cam.cl.group_project.delta;

import uk.ac.cam.cl.group_project.delta.algorithm.communications.Packet;

public class MessageReceipt {

	private final byte[] data;
	private final long time;

	/**
	 * Timestamps a message as it arrives at the device
	 *
	 * @param data The message which was received
	 */
	public MessageReceipt(byte[] data) {
		this(data, System.nanoTime());
	}

	/**
	 * Timestamps a message as it arrives at the device
	 *
	 * @param data The message which was received
	 * @param time The timestamp to assign to the message (in nanoseconds)
	 */
	public MessageReceipt(byte[] data, long time) {
		this.data = data;
		this.time = time;
	}

	/**
	 * @return the message data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return the relative time it was received
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Tests whether the data passed in contains an emergency message
	 *
	 * @param data - the message to be tested
	 * @return whether the message is an emergency
	 */
	public static boolean isEmergencyMessage(byte[] data) {
		return Packet.isEmergencyMessage(data);
	}
}
