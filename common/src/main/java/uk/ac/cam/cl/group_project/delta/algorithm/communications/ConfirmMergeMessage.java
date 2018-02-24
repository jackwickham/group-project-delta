package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

public class ConfirmMergeMessage extends MergeMessage {

	public ConfirmMergeMessage(ByteBuffer bytes) {
		super(bytes);
	}

	public ConfirmMergeMessage(int transactionId) {
		super(transactionId);
	}

	@Override
	public MessageType getType() {
		return MessageType.ConfirmMerge;
	}

}
