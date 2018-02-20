package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

public abstract class Message {
	
	public abstract ByteBuffer appendToBuffer(ByteBuffer bytes);
	public abstract MessageType getType();
}
