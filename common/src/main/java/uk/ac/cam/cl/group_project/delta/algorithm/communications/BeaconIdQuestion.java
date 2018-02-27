package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

public class BeaconIdQuestion extends Message {

	private final int returnPlatoonId;
	private final int beaconId;

	public BeaconIdQuestion(int returnPlatoon, int beaconId) {
		this.returnPlatoonId = returnPlatoon;
		this.beaconId = beaconId;
	}

	public BeaconIdQuestion(ByteBuffer bytes) {
		this.returnPlatoonId = bytes.getInt();
		this.beaconId = bytes.getInt();
	}

	@Override
	public ByteBuffer appendToBuffer(ByteBuffer bytes) {
		bytes.putInt(returnPlatoonId);
		return bytes.putInt(beaconId);
	}

	@Override
	public MessageType getType() {
		return MessageType.BeaconIdQuestion;
	}

	public int getReturnPlatoonId() {
		return returnPlatoonId;
	}

	public int getBeaconId() {
		return beaconId;
	}

}
