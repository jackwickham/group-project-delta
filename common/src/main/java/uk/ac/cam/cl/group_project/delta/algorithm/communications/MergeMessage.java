package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

public abstract class MergeMessage extends Message {

	private int transactionId;
	
	public MergeMessage(ByteBuffer bytes) {
		transactionId = bytes.getInt();
	}
	
	public MergeMessage(int transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public ByteBuffer appendToBuffer(ByteBuffer bytes) {
		return bytes.putInt(transactionId);
	}
	
	public int getTransactionId() {
		return transactionId;
	}
}
