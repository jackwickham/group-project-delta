package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.RowFilter.Entry;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.MessageData;

/**
 * This class handles the passing of messages to the network interface
 * and provides the control layer of the platoons.
 * 
 * @author Aaron Hutton
 *
 */
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
	 * The latest attempt at a merge
	 */
	private Merge currentMerge = null;
	
	/**
	 * The id of the leader of the platoon
	 */
	private int leaderId;
	
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
		leaderId = vehicleId;
		
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
				} else {
					if(position == 0 && (currentMerge == null || !currentMerge.isValid())) {
						currentMerge = new Merge(packet.platoonId, platoonId, idToPositionLookup.size());
						byte[] payload = createNewMergeRequest(currentMerge.getTransactionId());
						network.sendData(Packet.createPacket(
								payload, vehicleId, packet.platoonId, MessageType.RequestToMerge));
					}
				}
				break;
			case RequestToMerge:
				if(packet.platoonId == platoonId || packet.vehicleId == leaderId) {
					currentMerge = new Merge(packet.platoonId, platoonId, packet.payload);
					
					if(position == 0) {
						// This is the leader of the main platoon, so make a response
						
						// Currently always accept merge
						byte[] payload = createNewMergeAccept(
								currentMerge.getTransactionId(), true, currentMerge.getAdditionalIdLookups());
						network.sendData(Packet.createPacket(
								payload, vehicleId, packet.platoonId, MessageType.AcceptToMerge));
					}
				}
				break;
			case AcceptToMerge:
				if(packet.platoonId == platoonId || packet.vehicleId == leaderId) {
					currentMerge = new Merge(packet.platoonId, platoonId, packet.payload);
					
					if(position == 0) {
						// This is the leader of the main platoon, so make a response
						
						// Currently always accept merge
						byte[] payload = createNewMergeAccept(
								currentMerge.getTransactionId(), true, currentMerge.getAdditionalIdLookups());
						network.sendData(Packet.createPacket(
								payload, vehicleId, packet.platoonId, MessageType.AcceptToMerge));
					}
				}
				break;
			case Emergency:
				// Already processed, fall through
			default:
				// Do nothing
			}
		}
	}
	
	/**
	 * Used to generate the payload for a RequestToMerge packet
	 * 
	 * @param transactionId - The Id of the transaction this packet belongs to
	 * @return the RTM payload
	 */
	private byte[] createNewMergeRequest(int transactionId) {
		assert(position == 0);
		
		ByteBuffer result = ByteBuffer.allocate(8 + 4*idToPositionLookup.size());
		result.putInt(transactionId);
		result.putInt(idToPositionLookup.size());
		
		for(Map.Entry<Integer, Integer> item:sortMapByValues(idToPositionLookup)) {
			result.putInt(item.getKey());
		}
		return result.array();
	}
	
	/**
	 * Used to generate the payload for an AcceptToMerge packet and also
	 * the new id mappings
	 * 
	 * @param transactionId - The Id of the transaction this packet belongs to
	 * @param allowMerge - Whether the merge is accepted or not
	 * @param newIds - The new ids of the vehicles in the merging platoon
	 * @return the ATM payload
	 */
	private byte[] createNewMergeAccept(int transactionId, boolean allowMerge, List<Integer> newIds) {
		assert(position == 0);
		List<Integer> conflictingIds = new ArrayList<>();
		for(Integer i : newIds) {
			if(idToPositionLookup.containsKey(i)) {
				conflictingIds.add(i);
			}
		}
		
		ByteBuffer result = ByteBuffer.allocate(12 + 4*idToPositionLookup.size()+4*conflictingIds.size());
		result.putInt((transactionId &0x00FFFFFF) | (allowMerge? 0x01000000:0));
		result.putInt(idToPositionLookup.size());
		
		for(Map.Entry<Integer, Integer> item:sortMapByValues(idToPositionLookup)) {
			result.putInt(item.getKey());
		}
		result.putInt(conflictingIds.size());
		Random r = new Random();
		for(Integer i : conflictingIds) {
			result.putInt(i);
			int newId = r.nextInt();
			while(idToPositionLookup.containsKey(newId) || newIds.contains(newId)) {
				newId = r.nextInt();
			}
			result.putInt(newId);
		}
		return result.array();
	}
	/**
	 * Return a list of <Key, values> pairs for the given list which is sorted by 
	 * the value of the item in the list
	 * 
	 * @param unsorted - the unsorted map structure
	 * @return a list of sorted pairs
	 */
	private static List<Map.Entry<Integer, Integer>> sortMapByValues(Map<Integer, Integer> unsorted) {
		List<Map.Entry<Integer, Integer>> list = new LinkedList<>(unsorted.entrySet());
	    Collections.sort( list, new Comparator<Map.Entry<Integer, Integer>>() {
	        @Override
	        public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
	            return (o1.getValue()).compareTo(o2.getValue());
	        }
	    });
	    return list;
	}
}
