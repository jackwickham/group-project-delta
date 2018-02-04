package uk.ac.cam.cl.group_project.delta;

import java.util.List;

public interface NetworkInterface {

	/**
	 * Broadcasts raw data to all of the other vehicles on the network.
	 * @param message in bytes to be sent
	 */
	public void sendData(byte[] message);
	
	/**
	 * Returns a list of raw messages received from other vehicles since 
	 * the last time this method was called. These messages are byte 
	 * arrays wrapped in a class which adds a local timestamp upon their arrival.
	 * @return
	 */
	public List<MessageReceipt> pollData();
}
