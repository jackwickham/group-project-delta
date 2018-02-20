package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

import uk.ac.cam.cl.group_project.delta.algorithm.VehicleData;

public abstract class Message {
	
	public abstract ByteBuffer appendToBuffer(ByteBuffer bytes);
	public abstract MessageType getType();
	
	/**
	 * Bundle the bytes of a packet into a Message class. Choosing the correct
	 * class based on the type.
	 * 
	 * @param bytes - the payload of the packet
	 * @param type - the type of the message
	 * @return the Message to be handled.
	 */
	public static Message decodeMessage(ByteBuffer bytes, MessageType type) {
		switch(type) {
		case AcceptToMerge:
			return new AcceptToMergeMessage(bytes);
		case ConfirmMerge:
			return new ConfirmMergeMessage(bytes);
		case Data:
			return new VehicleData(bytes);
		case Emergency:
			return new EmergencyMessage();
		case MergeComplete:
			return new MergeCompleteMessage(bytes);
		case RequestToMerge:
			return new RequestToMergeMessage(bytes);
		default:
			// Log this error
			return null;
		
		}
	}
}
