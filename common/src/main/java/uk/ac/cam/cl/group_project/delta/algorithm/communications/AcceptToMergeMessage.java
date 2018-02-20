package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AcceptToMergeMessage extends MergeMessage {

	private boolean accepted;
	private List<Integer> newPlatoon;
	
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

	public boolean isAccepted() {
		return accepted;
	}

	public List<Integer> getNewPlatoon() {
		return newPlatoon;
	}
	
}
