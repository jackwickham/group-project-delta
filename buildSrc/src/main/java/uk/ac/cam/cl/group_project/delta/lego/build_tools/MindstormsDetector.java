package uk.ac.cam.cl.group_project.delta.lego.build_tools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class for use in lego/build.gradle to detect Mindstorms devices
 *
 * @author Jack Wickham
 */
public class MindstormsDetector {
	/**
	 * How long to listen for, in milliseconds
	 */
	private static final int LISTEN_DURATION = 3000; // 3s

	/**
	 * The maximum size of the device names to receive
	 */
	private static final int RECEIVE_BUFFER_LENGTH = 15;

	/**
	 * The expected number of devices that we might detect
	 */
	private static final int EXPECTED_DEVICE_COUNT = 3;

	/**
	 * Detects all Mindstorms devices on the current wifi network (by their broadcasts on port 3016), and returns a list
	 * of IP addresses belonging to Mindstorms devices.
	 *
	 * This method takes at least 3 seconds to execute, because each device only identifies itself once per second, so
	 * to allow for awkward timing and packet loss we wait 3 seconds to have detected all devices.
	 *
	 * @return A list of IP addresses belonging to Mindstorms devices
	 * @throws IOException If the network fails
	 * @throws NoDevicesFoundException If no devices are located
	 */
	public static List<String> detectMindstorms() throws IOException, NoDevicesFoundException {
		try (DatagramSocket socket = new DatagramSocket(3016)) {
			socket.setBroadcast(true);
			// If there are no devices, we need to make sure we timeout
			socket.setSoTimeout(LISTEN_DURATION);

			Set<InetAddress> ips = new HashSet<>(EXPECTED_DEVICE_COUNT);

			byte[] receiveBuffer = new byte[RECEIVE_BUFFER_LENGTH];
			DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

			long startTime = System.currentTimeMillis();
			try {
				while (System.currentTimeMillis() < startTime + LISTEN_DURATION) {

					socket.receive(packet);

					InetAddress ip = packet.getAddress();
					if (!ips.contains(ip)) {
						ips.add(ip);
						String hostname = new String(packet.getData(), 0, Math.min(packet.getLength(), RECEIVE_BUFFER_LENGTH));
						System.out.printf("Detected device %s on IP %s\n", hostname, ip.getHostAddress());
					}
				}
			} catch (SocketTimeoutException e) {
				// That's fine (probably means no devices though)
			}
			if (ips.isEmpty()) {
				// There's no point continuing with the build, so abort here
				throw new NoDevicesFoundException();
			}

			List<String> result = new ArrayList<>(ips.size());
			for (InetAddress ip : ips) {
				result.add(ip.getHostAddress());
			}
			return result;
		}
	}


	/**
	 * Exception thrown when no Mindstorms devices are detected
	 */
	public static class NoDevicesFoundException extends Exception {
		public NoDevicesFoundException () {
			super("No mindstorms devices detected. Make sure you're on the same wifi network and try again.");
		}
	}
}

