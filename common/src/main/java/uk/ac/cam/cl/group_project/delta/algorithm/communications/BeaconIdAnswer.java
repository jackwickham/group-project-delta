package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

public class BeaconIdAnswer extends Message {

	private final int askedPlatoonId;
	private final int beaconId;

	public BeaconIdAnswer(ByteBuffer bytes) {
		askedPlatoonId = bytes.getInt();
		beaconId = bytes.getInt();
	}

	public BeaconIdAnswer(int askedPlatoonId, int beaconId) {
		this.askedPlatoonId = askedPlatoonId;
		this.beaconId = beaconId;
	}

	@Override
	public ByteBuffer appendToBuffer(ByteBuffer bytes) {
		bytes.putInt(askedPlatoonId);
		return bytes.putInt(beaconId);
	}

	@Override
	public MessageType getType() {
		return MessageType.BeaconIdAnswer;
	}

	public int getAskedPlatoonId() {
		return askedPlatoonId;
	}

	public int getBeaconId() {
		return beaconId;
	}

}
