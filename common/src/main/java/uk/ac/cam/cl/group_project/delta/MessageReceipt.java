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
		this.data = data;
		this.time = Time.getTime();
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

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (int i = 0; i < 12 && i < this.data.length; i++) {
			// The static Byte.hashCode method is only available from Java 8...
			// But it is effectively implemented as
			hashCode ^= (int) this.data[i];
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MessageReceipt)) {
			return false;
		}
		MessageReceipt otherReceipt = (MessageReceipt) obj;
		if (otherReceipt.data.length != this.data.length) {
			return false;
		}
		for (int i = 0; i < 12 && i < this.data.length; i++) {
			if (otherReceipt.data[i] != this.data[i]) {
				return false;
			}
		}
		// Headers match, so we consider this packet to match
		return true;
	}
}
