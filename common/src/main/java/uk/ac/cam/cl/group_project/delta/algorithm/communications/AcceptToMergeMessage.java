package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The implementation of an AcceptToMerge message, containing whether the merge was accepted
 * and the main platoon order
 * @author Aaron
 *
 */
public class AcceptToMergeMessage extends MergeMessage {

	private boolean accepted;
	private List<Integer> mainPlatoon;
	private Map<Integer, Integer> renames;

	/**
	 * The constructor to create a new AcceptToMerge from a bytebuffer which represents
	 * the payload of a packet.
	 * @param bytes - the location of the data
	 */
	public AcceptToMergeMessage(ByteBuffer bytes) {
		super(bytes);

		int tmp = bytes.getInt();
		accepted = tmp != 0;
		int length = 0x00FFFFFF & tmp;
		mainPlatoon = new ArrayList<>(length);
		for(int i = 0; i < length; i++) {
			mainPlatoon.add(i, bytes.getInt());
		}
		int numReplaced = bytes.getInt();
		renames = new HashMap<>(numReplaced);
		for(int i = 0; i < numReplaced; i++) {
			renames.put(bytes.getInt(), bytes.getInt());
		}
	}

	/**
	 * The constructor to create a new AcceptToMerge from the data it holds.
	 * @param isAccepted - whether the merge is accepted
	 * @param mainPlatoon - the ordered list of the ids of the main platoon
	 * @param renames - the renames in the merging platoon which need to be done
	 * @param transactionId - the transactionId of the current merge
	 */
	public AcceptToMergeMessage(boolean isAccepted,
			List<Integer> mainPlatoon,
			Map<Integer, Integer> renames,
			int transactionId) {

		super(transactionId);
		this.accepted = isAccepted;
		this.mainPlatoon = mainPlatoon;
		this.renames = renames;
	}

	/**
	 * Append the data currently held in this message to the passed byte buffer
	 *
	 * @param bytes
	 *            - the buffer to be written to
	 */
	@Override
	public ByteBuffer appendToBuffer(ByteBuffer bytes) {
		super.appendToBuffer(bytes);
		int tmp = (accepted)? 1 << 24 : 0;
		bytes.putInt((mainPlatoon.size() & 0x00FFFFFF) | tmp);
		for(int i : mainPlatoon) {
			bytes.putInt(i);
		}
		bytes.putInt(renames.size());
		for(Map.Entry<Integer, Integer> renamesRequired : renames.entrySet()) {
			bytes.putInt(renamesRequired.getKey());
			bytes.putInt(renamesRequired.getValue());
		}
		return bytes;
	}

	@Override
	public MessageType getType() {
		return MessageType.AcceptToMerge;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public List<Integer> getMainPlatoon() {
		return mainPlatoon;
	}

	public Map<Integer, Integer> getRenames() {
		return renames;
	}

}
