package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class RequstToMergeMessage extends MergeMessage {

	private int mergingPlatoonId;
	private List<Integer> newPlatoon;
	
	public RequstToMergeMessage(ByteBuffer bytes) {
		super(bytes);
		mergingPlatoonId = bytes.getInt();
		int length = 0x00FFFFFF & bytes.getInt();
		newPlatoon = new ArrayList<>(length);
		for(int i = 0; i < length; i++) {
			newPlatoon.set(i, bytes.getInt());
		}
	}

	public int getMergingPlatoonId() {
		return mergingPlatoonId;
	}

	public List<Integer> getNewPlatoon() {
		return newPlatoon;
	}

}
