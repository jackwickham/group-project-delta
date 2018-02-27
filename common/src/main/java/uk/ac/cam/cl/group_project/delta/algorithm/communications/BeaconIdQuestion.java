package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.nio.ByteBuffer;

public class BeaconIdQuestion extends Message {

	/**
	 * The id of the platoon which asked the question
	 */
	private final int returnPlatoonId;

	/**
	 * The id which is being asked about
	 */
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
		bytes.putInt(beaconId);
		return bytes;
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
