package uk.ac.cam.cl.group_project.delta;

public class MessageReceipt {

	private final byte[] data;
	private final long time;
	
	/**
	 * Timestamps a message as it arrives at the device
	 * 
	 * @param the message which was received
	 */
	public MessageReceipt(byte[] data) {
		this.data = data;
		time = System.nanoTime();
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

}
