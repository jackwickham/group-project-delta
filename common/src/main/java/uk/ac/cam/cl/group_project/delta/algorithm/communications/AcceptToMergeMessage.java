package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of an AcceptToMerge message, containing whether the merge was accepted
 * and the main platoon order
 * @author Aaron
 *
 */
public class AcceptToMergeMessage extends MergeMessage {

	private boolean accepted;
	private List<Integer> newPlatoon;
	
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
		int tmp = (accepted)? 1 << 24 : 0;
		bytes.putInt((newPlatoon.size() & 0x00FFFFFF) | tmp);
		for(int i : newPlatoon) {
			bytes.putInt(i);
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

	public List<Integer> getNewPlatoon() {
		return newPlatoon;
	}

}
