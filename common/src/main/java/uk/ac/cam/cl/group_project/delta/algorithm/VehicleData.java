package uk.ac.cam.cl.group_project.delta.algorithm;

import java.nio.ByteBuffer;

import uk.ac.cam.cl.group_project.delta.algorithm.communications.Message;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.MessageType;

public class VehicleData extends Message {

	private final double speed;
	private final double acceleration;
	private final double turnRate;
	private final double chosenSpeed;
	private final double chosenAcceleration;
	private final double chosenTurnRate;
	private long startTime;

	/**
	 * Create an immutable MessageData object to be passed to the algorithm
	 * or to be broadcast over the network
	 *
	 * @param speed
	 * @param acceleration
	 * @param turnRate
	 * @param chosenSpeed
	 * @param chosenAcceleration
	 * @param chosenTurnRate
	 */
	public VehicleData(double speed, double acceleration, double turnRate, double chosenSpeed,
			double chosenAcceleration, double chosenTurnRate) {
		this.speed = speed;
		this.acceleration = acceleration;
		this.turnRate = turnRate;
		this.chosenSpeed = chosenSpeed;
		this.chosenAcceleration = chosenAcceleration;
		this.chosenTurnRate = chosenTurnRate;
	}

	/**
	 * Recreate the message from the bytebuffer, the bytebuffer should be
	 * positioned at the start of the data, not at the start of the packet
	 *
	 * @param rawBytes the bytes to be converted
	 * @return a new data packet with the specific data
	 */
	public VehicleData(ByteBuffer bytes) {
		this.speed = bytes.getDouble();
		this.acceleration = bytes.getDouble();
		this.turnRate = bytes.getDouble();
		this.chosenSpeed = bytes.getDouble();
		this.chosenAcceleration = bytes.getDouble();
		this.chosenTurnRate = bytes.getDouble();
	}

	/**
	 * Append the data in this object to the bytebuffer, the bytebuffer
	 * should be positioned at the start of the data section or information
	 * will be overwritten
	 *
	 * @return a byte representation of the data
	 */
	@Override
	public ByteBuffer appendToBuffer(ByteBuffer bytes) {
		bytes.putDouble(speed);
		bytes.putDouble(acceleration);
		bytes.putDouble(turnRate);
		bytes.putDouble(chosenSpeed);
		bytes.putDouble(chosenAcceleration);
		bytes.putDouble(chosenTurnRate);
		return bytes;
	}

	@Override
	public MessageType getType() {
		return MessageType.Data;
	}

	public double getSpeed() {
		return speed;
	}

	public double getAcceleration() {
		return acceleration;
	}

	public double getTurnRate() {
		return turnRate;
	}

	public double getChosenSpeed() {
		return chosenSpeed;
	}

	public double getChosenAcceleration() {
		return chosenAcceleration;
	}

	public double getChosenTurnRate() {
		return chosenTurnRate;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long time) {
		this.startTime = time;
	}
}
