package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of an RequestToMerge message, containing whether the merging
 * platoon order.
 * @author Aaron
 *
 */
public class RequestToMergeMessage extends MergeMessage {

	private int mergingPlatoonId;
	private List<Integer> newPlatoon;

	/**
	 * The constructor to create a new RequestToMerge message from a bytebuffer 
	 * which represents the payload of a packet.
	 * @param bytes - the location of the data
	 */
	public RequestToMergeMessage(ByteBuffer bytes) {
		super(bytes);
		mergingPlatoonId = bytes.getInt();
		int length = 0x00FFFFFF & bytes.getInt();
		newPlatoon = new ArrayList<>(length);
		for(int i = 0; i < length; i++) {
			newPlatoon.set(i, bytes.getInt());
		}
	}

	/**
	 * Append the data currently held in this message to the passed byte buffer
	 * 
	 * @param bytes - the buffer to be written to
	 */
	@Override
	public ByteBuffer appendToBuffer(ByteBuffer bytes) {
		super.appendToBuffer(bytes);
		bytes.putInt(newPlatoon.size() & 0x00FFFFFF);
		for(int i : newPlatoon) {
			bytes.putInt(i);
		}
		return bytes;
	}
	
	@Override
	public MessageType getType() {
		return MessageType.RequestToMerge;
	}
	
	public int getMergingPlatoonId() {
		return mergingPlatoonId;
	}

	public List<Integer> getNewPlatoon() {
		return newPlatoon;
	}
}
