package uk.ac.cam.cl.group_project.delta.lego;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Network communication code for the Mindstorms
 *
 * Utilises IP broadcast on UDP port 5187 to communicate with the other robots
 *
 * @author Jack Wickham
 */
public class Network implements NetworkInterface, Closeable {
	/**
	 * The socket used to communicate with other robots
	 */
	private DatagramSocket socket;

	/**
	 * The broadcast address that messages are transmitted to
	 */
	private InetAddress broadcastAddress;

	/**
	 * The UDP port used for communication
	 */
	private static final int port = 5187;

	/**
	 * A list of messages received since the last call to {@link #pollData}
	 */
	private final List<MessageReceipt> receivedMessages;

	/**
	 * The thread which will run the algorithm, this can be interrupted when
	 * an emergency message is received.
	 */
	private final Thread algorithmThread;

	/**
	 * Construct a new network instance, the algorithm thread is passed as an argument
	 * so that the thread which creates the interfaces isn't then tied to running the
	 * algorithm.
	 *
	 * @param algorithmThread - the thread the algorithm will run on
	 *
	 * @throws IOException if no suitable broadcast address can be found
	 * @throws IOException if it is unable to bind to udp:0.0.0.0:5187
	 */
	public Network (Thread algorithmThread) throws IOException {
		this.algorithmThread = algorithmThread;

		// Find the broadcast IP
		java.net.NetworkInterface iface = java.net.NetworkInterface.getByName("wlan0");
		List<InterfaceAddress> interfaceAddresses = iface.getInterfaceAddresses();
		if (iface.isUp() && interfaceAddresses.size() > 0) {
			InterfaceAddress addr = interfaceAddresses.get(0);
			// InterfaceAddress has a .getBroadcast() method, but for some reason it returns 0.0.0.0 for this
			// interface, so lets just construct the broadcast address ourselves.
			byte[] address = addr.getAddress().getAddress();
			convertToBroadcastAddress(address, addr.getNetworkPrefixLength());
			broadcastAddress = InetAddress.getByAddress(address);
		} else {
			throw new IOException("No supported network found - wifi network is not up or has no address");
		}

		// Bind the socket to listen to UDP port 5187 on 0.0.0.0
		byte[] listenAddress = {0, 0, 0, 0};
		socket = new DatagramSocket(port, InetAddress.getByAddress(listenAddress));
		socket.setBroadcast(true);

		// Start the listener
		receivedMessages = new ArrayList<>();
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
			DatagramPacket packet = new DatagramPacket(message, message.length, broadcastAddress, port);
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
	@Override
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
					byte[] data = new byte[NetworkInterface.MAXIMUM_PACKET_SIZE];
					DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
					socket.receive(receivedPacket);
					if (receivedPacket.getLength() > NetworkInterface.MAXIMUM_PACKET_SIZE) {
						// TODO: log error - packet too large
						continue;
					}
					byte[] receivedData = Arrays.copyOf(receivedPacket.getData(), receivedPacket.getLength());

					if(MessageReceipt.isEmergencyMessage(receivedData)) {
						algorithmThread.interrupt();
						// Can continue, but shouldn't be necessary
						// Might be useful to log the messages after this or something
					}
					synchronized (receivedMessages) {
						receivedMessages.add(new MessageReceipt(receivedData));
					}
				}
			} catch (IOException e) {
				if (!socket.isClosed()) {
					// TODO: Log error
				}
			}
		}
	}

	/**
	 * Convert an IPv4 address and CIDR netmask length into a network broadcast address
	 *
	 * Broadcast = ip | ~netmask
	 *
	 * @param address The address to convert (modifies in place)
	 * @param netmaskLen The length of the netmask (for example, in 10.0.2.3/24, this is 24)
	 */
	public static void convertToBroadcastAddress(byte[] address, int netmaskLen) {
		// Get the index of the first octet that we need to change
		int octet = netmaskLen / 8;
		int octetOffset = netmaskLen % 8;
		if (octetOffset != 0) {
			int mask = 0;
			while (octetOffset < 8) {
				mask = (mask << 1) | 1;
				octetOffset++;
			}
			address[octet] |= mask;
			++octet;
		}

		// Update all the rest of the octets
		for (; octet < 4; octet++) {
			address[octet] = (byte) 0xFF;
		}
	}
}
