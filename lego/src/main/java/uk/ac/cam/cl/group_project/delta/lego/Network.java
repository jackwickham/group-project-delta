package uk.ac.cam.cl.group_project.delta.lego;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Network communication code for the Mindstorms
 *
 * Utilises broadcast on UDP port 5187 to communicate with the other robots
 *
 * Assumes that the robot is on the same network as all the others, and that network has address 10.0.2.0/24
 */
public class Network implements NetworkInterface {
	private DatagramSocket socket;
	private InetAddress address;
	private static final int port = 5187;
	private final List<MessageReceipt> receivedMessages;

	/**
	 * Construct a new network instance
	 *
	 * @throws IOException if it is unable to bind to udp:0.0.0.0:5187
	 */
	public Network () throws IOException {
		// Bind the socket to listen to udp port 5187 on 0.0.0.0
		byte[] listenAddress = {0, 0, 0, 0};
		socket = new DatagramSocket(port, InetAddress.getByAddress(listenAddress));
		socket.setBroadcast(true);

		// Create the send address
		byte[] broadcastAddress = {(byte) 10, (byte) 0, (byte) 2, (byte) 255};
		address = InetAddress.getByAddress(broadcastAddress);

		receivedMessages = new ArrayList<>();

		// Start the listener
		ListenerThread lt = new ListenerThread();
		lt.setDaemon(true);
		lt.start();
	}

	/**
	 * Broadcasts raw data to all of the other vehicles on the network.
	 *
	 * @param message in bytes to be sent
	 */
	@Override
	public void sendData (byte[] message) {
		try {
			DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
			socket.send(packet);
		} catch (IOException e) {
			// This isn't good, but we want to try to keep going
		}
	}

	/**
	 * Returns a list of raw messages received from other vehicles since
	 * the last time this method was called. These messages are byte
	 * arrays wrapped in a class which adds a local timestamp upon their arrival.
	 *
	 * @return A list of messages received since the last call
	 */
	@Override
	public List<MessageReceipt> pollData () {
		List<MessageReceipt> messages;
		synchronized (receivedMessages) {
			messages = new ArrayList<>(receivedMessages);
			receivedMessages.clear();
		}
		return messages;
	}

	/**
	 * Shut down the connection
	 */
	public void close () {
		socket.close();
	}

	/**
	 * A worker thread that receives data from the network and adds it to the list of received messages
	 */
	private class ListenerThread extends Thread {
		@Override
		public void run () {
			try {
				while (true) {
					byte[] data = new byte[200];
					DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
					socket.receive(receivedPacket);
					synchronized (receivedMessages) {
						receivedMessages.add(new MessageReceipt(receivedPacket.getData()));
					}
				}
			} catch (IOException e) {
				// :( TODO: this will happen when the socket closes, but hopefully not for any other reason
			}
		}
	}
}
