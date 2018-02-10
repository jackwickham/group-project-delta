package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.MessageData;

public class MessageReceiver {

	/**
	 * The current id of this vehicle.
	 */
	private int vehicleId;
	
	/**
	 * The current id for the platoon this vehicle belongs to.
	 */
	private int platoonId;
	
	/**
	 * The current position in the platoon, 0 indicates the leader.
	 * Begin life as the leader of a platoon.
	 */
	private int position = 0;
	
	/**
	 * The mapping from positions to messages read by {@link Communications}
	 * and updated by this class.
	 */
	private PlatoonLookup messageLookup;
	
	/**
	 * This is the mapping from vehicle IDs to positions in the platoon.
	 * A map was chosen because the lookup is likely to be common.
	 * When merges occur the map needs to be sorted but this is much rarer
	 * than a normal lookup, so can be completed when necessary.
	 */
	private Map<Integer, Integer> idToPositionLookup;
	
	/**
	 * The network interface used to send and receive data.
	 */
	private NetworkInterface network;

	
	
	/**
	 * Create a new platoon instance by making a new MessageReceiver Object
	 * 
	 * @param network - the network interface to be used
	 * @param map - the position to message map to be used
	 */
	public MessageReceiver(NetworkInterface network, PlatoonLookup map) {
		messageLookup = map;
		Random r = new Random();
		vehicleId = r.nextInt();
		platoonId = r.nextInt();
		
		idToPositionLookup = new HashMap<>();
	}

	/**
	 * Send the specific message across the network
	 * 
	 * @param message - the message to be sent
	 */
	public void sendMessage(MessageData message) {
		network.sendData(Packet.createDataPacket(message, vehicleId, platoonId));
	}

	public int getCurrentPosition() {
		return position;
	}

	public void notifyEmergency() {
		network.sendData(Packet.createPacket(
				new byte[0], vehicleId, platoonId, MessageType.Emergency));
	}
	
	/**
	 * This updates the messages in the messageLookup to reflect the most
	 * recent messages
	 */
	public void updateMessages() {
		for(MessageReceipt message : network.pollData()) {
			Packet packet = new Packet(message);
			
			switch(packet.type) {
			case Data:
				if(packet.platoonId == platoonId) {
					messageLookup.put(
							idToPositionLookup.get(packet.vehicleId), packet.message);
				}
			
			case Emergency:
				// Already processed, fall through
			default:
				// Do nothing
			}
		}
	}
}
