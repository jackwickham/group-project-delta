package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

public class EmergencyMessage extends Message {

	@Override
	public ByteBuffer appendToBuffer(ByteBuffer bytes) {
		// Nothing to do
		return bytes;
	}

	@Override
	public MessageType getType() {
		return MessageType.Emergency;
	}

}
