package uk.ac.cam.cl.group_project.delta.algorithm;

public class MessageData {

	private final double speed;
	private final double acceleration;
	private final double turnRate;
	private final double chosenSpeed;
	private final double chosenAcceleration;
	private final double chosenTurnRate;
	
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
	public MessageData(double speed, double acceleration, double turnRate, double chosenSpeed,
			double chosenAcceleration, double chosenTurnRate) {
		this.speed = speed;
		this.acceleration = acceleration;
		this.turnRate = turnRate;
		this.chosenSpeed = chosenSpeed;
		this.chosenAcceleration = chosenAcceleration;
		this.chosenTurnRate = chosenTurnRate;
	}

	/**
	 * Recreate the message from the specific byte array
	 * 
	 * @param rawBytes the bytes to be converted
	 * @return a new data packet with the specific data
	 */
	public static MessageData generateDataFromBytes(byte[] rawBytes) {
		return null;
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
	
	
}
