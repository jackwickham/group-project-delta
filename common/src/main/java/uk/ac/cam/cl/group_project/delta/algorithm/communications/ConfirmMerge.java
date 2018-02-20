package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

public class ConfirmMerge extends MergeMessage {

	public ConfirmMerge(ByteBuffer bytes) {
		super(bytes);
	}

}
