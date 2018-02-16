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

import uk.ac.cam.cl.group_project.delta.MessageReceipt;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.VehicleData;

/**
 * This class handles the passing of messages to the network interface and
 * provides the control layer of the platoons.
 * 
 * @author Aaron Hutton
 *
 */
public class ControlLayer {

	/**
	 * The current id of this vehicle.
	 */
	private int vehicleId;

	/**
	 * The current id for the platoon this vehicle belongs to.
	 */
	private int platoonId;

	/**
	 * The current position in the platoon, 0 indicates the leader. Begin life as
	 * the leader of a platoon.
	 */
	private int position = 0;

	/**
	 * The mapping from positions to messages read by {@link Communications} and
	 * updated by this class.
	 */
	private PlatoonLookup messageLookup;

	/**
	 * This is the mapping from vehicle IDs to positions in the platoon. A map was
	 * chosen because the lookup is likely to be common. When merges occur the map
	 * needs to be sorted but this is much rarer than a normal lookup, so can be
	 * completed when necessary.
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
	 * @param network
	 *            - the network interface to be used
	 * @param map
	 *            - the position to message map to be used
	 */
	public ControlLayer(NetworkInterface network) {
		messageLookup = new PlatoonLookup();
		this.network = network;
		Random r = new Random();
		vehicleId = r.nextInt();
		platoonId = r.nextInt();
		leaderId = vehicleId;

		idToPositionLookup = new HashMap<>();
	}

	/**
	 * Create a new platoon instance initialised with the specific platoon given
	 * 
	 * @param network
	 *            - the network interface to be used
	 * @param map
	 *            - the position to message map to be used
	 * @param vehicleId
	 *            - this vehicles initial id
	 * @param platoonId
	 *            - the initial platoon id
	 * @param platoonOrder
	 *            - a list of the current platoon in terms of their ids
	 */
	public ControlLayer(NetworkInterface network, int vehicleId, int platoonId,
			List<Integer> platoonOrder) {
		this.vehicleId = vehicleId;
		this.platoonId = platoonId;
		this.network = network;
		this.messageLookup = new PlatoonLookup();
		this.leaderId = platoonOrder.get(0);
		idToPositionLookup = new HashMap<>();

		for (int i = 0; i < platoonOrder.size(); i++) {
			if (vehicleId == platoonOrder.get(i)) {
				this.position = i;
				break;
			}
		}

		for (int i = 0; i < platoonOrder.size(); i++) {
			idToPositionLookup.put(platoonOrder.get(i), position - i);
		}
	}

	/**
	 * Send the specific message across the network
	 * 
	 * @param message
	 *            - the message to be sent
	 */
	public void sendMessage(VehicleData message) {
		network.sendData(Packet.createDataPacket(message, vehicleId, platoonId));
	}

	public int getCurrentPosition() {
		return position;
	}
	
	public PlatoonLookup getPlatoonLookup() {
		return messageLookup;
	}

	/**
	 * Send an emergency packet to the network
	 */
	public void notifyEmergency() {
		network.sendData(Packet.createPacket(new byte[0], vehicleId, platoonId, MessageType.Emergency));
	}

	/**
	 * This updates the messages in the messageLookup to reflect the most recent
	 * messages. It handles the messages used in the merge protocol, only looking at
	 * messages which are from the platoon leader or have the correct platoon id. If
	 * the message does not have either of these characteristics then it is ignored.
	 */
	public void updateMessages() {
		for (MessageReceipt msg : network.pollData()) {
			Packet packet = new Packet(msg);

			if (packet.vehicleId == vehicleId) {
				// Ignore packets sent by this vehicle
				continue;
			}

			switch (packet.type) {
			case Data:
				if (packet.platoonId == platoonId) {
					// Update the data for that vehicle
					messageLookup.put(idToPositionLookup.get(packet.vehicleId), packet.message);
				} else {
					beginMergeProtocol(packet);
				}
				break;
			case RequestToMerge:
				if (packet.platoonId == platoonId || packet.vehicleId == leaderId) {
					handleRequestToMerge(packet);
				}
				break;
			case AcceptToMerge:
				if (packet.platoonId == platoonId || packet.vehicleId == leaderId) {
					handleAcceptToMerge(packet);
				}
				break;
			case ConfirmMerge:
				if (packet.platoonId == platoonId && position == 0) {
					handleConfirmMerge(packet);
				}
				break;
			case MergeComplete:
				if (packet.platoonId == platoonId) {
					// Check the correct transaction id and commit
					if (currentMerge != null && getFirstInt(packet.payload) == currentMerge.getTransactionId()) {
						commitMerge();
					}
				}
				break;
			case Emergency:
				// Already processed, fall through
			default:
				// TODO: This indicates an Emergency which wasn't triggered or something
				break;
			}
		}
	}

	/**
	 * Begin the merge protocol by sending a RequestToMerge to the other platoon
	 * 
	 * @param packet
	 *            - the data in Packet format
	 */
	private void beginMergeProtocol(Packet packet) {
		// Found a new platoon which we could merge with
		if (position == 0 && (currentMerge == null || !currentMerge.isValid())) {
			currentMerge = new Merge(packet.platoonId, platoonId, idToPositionLookup.size());

			// Send an initial request to join
			byte[] payload = createNewMergeRequest(currentMerge.getTransactionId());
			network.sendData(Packet.createPacket(payload, vehicleId, packet.platoonId, MessageType.RequestToMerge));
		}
	}

	/**
	 * Handle a RequestToMerge packet by creating a new Merge Object and replying if
	 * necessary
	 * 
	 * @param packet
	 *            - the data in Packet format
	 */
	private void handleRequestToMerge(Packet packet) {
		// Everyone need to remember this info
		currentMerge = new Merge(packet.platoonId, platoonId, packet.payload);

		if (position == 0) {
			// This is the leader of the main platoon, so make a response

			// Currently always accept merge
			byte[] payload = createNewMergeAccept(currentMerge.getTransactionId(), true,
					currentMerge.getAdditionalIdLookups());
			network.sendData(Packet.createPacket(payload, vehicleId, currentMerge.getMergingPlatoonId(),
					MessageType.AcceptToMerge));

			// Also send confirm message
			sendBlankMergingMessage(currentMerge.getTransactionId(), currentMerge.getMergingPlatoonId(),
					MessageType.ConfirmMerge);
		}
	}

	/**
	 * Handle an AcceptToMerge packet by updating the current Merge Object and send
	 * a confirmation if accepted
	 * 
	 * @param packet
	 *            - the data in Packet format
	 */
	private void handleAcceptToMerge(Packet packet) {
		// The merge has been agreed, update the merge information
		// with the new info from the leader of the main platoon
		if (currentMerge != null && currentMerge.isValid()) {
			currentMerge.handlePayload(packet.type, packet.payload);

			if (currentMerge.doesAccept() && (position != 0)) {
				// This vehicle is happy so sends a confirmation
				sendBlankMergingMessage(currentMerge.getTransactionId(), currentMerge.getMergingPlatoonId(),
						MessageType.ConfirmMerge);
			}
		}
	}

	/**
	 * Handle a ConfirmMerge packet by updating the current Merge Object and
	 * committing the merge by sending a MergeComplete message to both platoons, if
	 * everyone has agreed.
	 * 
	 * @param packet
	 *            - the data in Packet format
	 */
	private void handleConfirmMerge(Packet packet) {
		if (currentMerge != null && currentMerge.isValid()) {
			currentMerge.handlePayload(packet.type, packet.payload);

			// The merge has been agreed by all parties, so commits
			if (currentMerge.isConfirmed()) {
				// Tell everyone in both platoons to agree the merge
				sendBlankMergingMessage(currentMerge.getTransactionId(), currentMerge.getMergingPlatoonId(),
						MessageType.ConfirmMerge);
				sendBlankMergingMessage(currentMerge.getTransactionId(), currentMerge.getMainPlatoonId(),
						MessageType.ConfirmMerge);
				commitMerge();
			}
		}
	}

	/**
	 * Used to generate the payload for a RequestToMerge packet
	 * 
	 * @param transactionId
	 *            - The Id of the transaction this packet belongs to
	 * @return the RTM payload
	 */
	private byte[] createNewMergeRequest(int transactionId) {
		assert (position == 0);

		ByteBuffer result = ByteBuffer.allocate(12 + 4 * idToPositionLookup.size());
		result.putInt(transactionId);
		result.putInt(platoonId);
		result.putInt(idToPositionLookup.size());

		for (Map.Entry<Integer, Integer> item : sortMapByValues(idToPositionLookup)) {
			result.putInt(item.getKey());
		}
		return result.array();
	}

	/**
	 * Used to generate the payload for an AcceptToMerge packet and also the new id
	 * mappings
	 * 
	 * @param transactionId
	 *            - The Id of the transaction this packet belongs to
	 * @param allowMerge
	 *            - Whether the merge is accepted or not
	 * @param newIds
	 *            - The new ids of the vehicles in the merging platoon
	 * @return the ATM payload
	 */
	private byte[] createNewMergeAccept(int transactionId, boolean allowMerge, List<Integer> newIds) {
		assert (position == 0);
		// Calculate which ids conflict
		List<Integer> conflictingIds = new ArrayList<>();
		for (Integer i : newIds) {
			if (idToPositionLookup.containsKey(i)) {
				conflictingIds.add(i);
			}
		}

		ByteBuffer result = ByteBuffer.allocate(12 + 4 * idToPositionLookup.size() + 4 * conflictingIds.size());
		result.putInt((transactionId & 0x00FFFFFF) | (allowMerge ? 0x01000000 : 0));
		result.putInt(idToPositionLookup.size());

		// First add the members of the main platoon
		for (Map.Entry<Integer, Integer> item : sortMapByValues(idToPositionLookup)) {
			result.putInt(item.getKey());
		}
		result.putInt(conflictingIds.size());
		Random r = new Random();
		// Record new names to fix any conflicts
		for (Integer i : conflictingIds) {
			result.putInt(i);
			int newId = r.nextInt();
			while (idToPositionLookup.containsKey(newId) || newIds.contains(newId)) {
				newId = r.nextInt();
			}
			result.putInt(newId);
		}
		return result.array();
	}

	/**
	 * Sends a message which contains only the transaction id which is of the
	 * specified type
	 * 
	 * @param transactionId
	 *            - The id of the merging transaction
	 * @param platoonId
	 *            - The id of the main platoon
	 * @param type
	 *            - The type of the message to be sent
	 */
	public void sendBlankMergingMessage(int transactionId, int platoonId, MessageType type) {
		network.sendData(Packet.createPacket(new byte[] { (byte) (currentMerge.getTransactionId() >>> 24),
				(byte) (currentMerge.getTransactionId() >>> 16), (byte) (currentMerge.getTransactionId() >>> 8),
				(byte) (currentMerge.getTransactionId()), }, vehicleId, platoonId, type));
	}

	/**
	 * Commit the current merge by changing all of the data structures
	 */
	private void commitMerge() {
		if (currentMerge.getChangePosition() != 0) {
			// So switching platoons

			// Might need to replace some ids
			updateIdsFromMerge();

			// Add new vehicles to start of platoon
			addVechiclesToHeadOfPlatoon();

			// Change the leader
			leaderId = currentMerge.getAdditionalIdLookups().get(0);

			// Change the recorded id
			position += currentMerge.getChangePosition();
		} else {
			// Add new vehicles to end of platoon
			addVechiclesToEndOfPlatoon();
		}

		platoonId = currentMerge.getMainPlatoonId();

		currentMerge = null;

	}

	/**
	 * Used during a merge commit to update the ids to the replaced ids to remove
	 * conflicts
	 */
	private void updateIdsFromMerge() {
		for (Integer i : currentMerge.getIdClashReplacements().keySet()) {
			if (idToPositionLookup.containsKey(i)) {
				idToPositionLookup.put(currentMerge.getIdClashReplacements().get(i), idToPositionLookup.get(i));
				idToPositionLookup.remove(i);
			}
		}

		// Change the vehicle id if necessary
		if (currentMerge.getIdClashReplacements().containsKey(vehicleId)) {
			vehicleId = currentMerge.getIdClashReplacements().get(vehicleId);
		}
	}

	/**
	 * Add the new vehicles from the merge to the head of the platoon, used during
	 * the merge commit
	 */
	private void addVechiclesToHeadOfPlatoon() {
		for (int i = 0; i < currentMerge.getAdditionalIdLookups().size(); i++) {
			idToPositionLookup.put(currentMerge.getAdditionalIdLookups().get(i),
					position + currentMerge.getAdditionalIdLookups().size() - i);
		}
	}

	/**
	 * Add the new vehicles from the merge to the end of the platoon, used during
	 * the merge commit
	 */
	private void addVechiclesToEndOfPlatoon() {
		for (int i = 0; i < currentMerge.getAdditionalIdLookups().size(); i++) {
			idToPositionLookup.put(currentMerge.getAdditionalIdLookups().get(i),
					position - idToPositionLookup.size() + i);
		}
	}

	/**
	 * Return the first 4 bytes of the argument interpreting them as a big-endian
	 * integer
	 * 
	 * @param bytes
	 *            - the byte source to be read
	 * @return the first 4 bytes as an int
	 */
	public static int getFirstInt(byte[] bytes) {
		if (bytes.length < 4) {
			throw new IllegalArgumentException("Tried to read an int from an empty array.");
		}
		return (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
	}

	/**
	 * Return a list of <Key, values> pairs for the given list which is sorted by
	 * the value of the item in the list
	 * 
	 * @param unsorted - the unsorted map structure
	 * @return a list of sorted pairs
	 */
	public static List<Map.Entry<Integer, Integer>> sortMapByValues(Map<Integer, Integer> unsorted) {
		List<Map.Entry<Integer, Integer>> list = new LinkedList<>(unsorted.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			@Override
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		return list;
	}
}
