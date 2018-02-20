package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

public class MergeCompleteMessage extends MergeMessage {

	public MergeCompleteMessage(ByteBuffer bytes) {
		super(bytes);
	}

	@Override
	public ByteBuffer appendToBuffer(ByteBuffer bytes) {
		return super.appendToBuffer(bytes);
	}

	
}
