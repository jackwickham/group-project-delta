package uk.ac.cam.cl.group_project.delta;

public class Beacon {
	/**
	 * The identifier of the beacon corresponding to this location
	 */
	private final int beaconIdentifier;

	/**
	 * An approximate lower bound on distance to the beacon, in metres
	 *
	 * The true value may be lower than this, but should usually fall between `distanceLowerBound` and
	 * `distanceUpperBound`
	 */
	private final double distanceLowerBound;

	/**
	 * An approximate upper bound on the distance to the beacon, in metres
	 *
	 * The true value may be greater than this, but typical true values should usually fall between `distanceLowerBound`
	 * and `distanceUpperBound`
	 */
	private final double distanceUpperBound;

	/**
	 * The angle in radians to the beacon
	 *
	 * The angle is measured from straight ahead relative to this vehicle, with a positive value representing a
	 * deviation to the right and a negative value representing a deviation to the left.
	 */
	private final double angle;

	/**
	 * Construct a new beacon
	 * @param beaconIdentifier The integer identifier for this beacon
	 * @param distanceLowerBound An approximate lower bound on the distance (in metres) from the front of the vehicle
	 *                              to this beacon
	 * @param distanceUpperBound An approximate upper bound on the distance (in metres) from the front of the vehicle
	 *                              to this beacon. If the distance is known exactly, this should match
	 *                              `distanceLowerBound`
	 * @param angle The angle in radians between the front of this vehicle and the beacon
	 */
	public Beacon(int beaconIdentifier, double distanceLowerBound, double distanceUpperBound, double angle) {
		this.beaconIdentifier = beaconIdentifier;
		this.distanceLowerBound = distanceLowerBound;
		this.distanceUpperBound = distanceUpperBound;
		this.angle = angle;
	}

	/**
	 * Get the integer identifier for the beacon with the position described in this class
	 * @return Beacon identifier
	 */
	public int getBeaconIdentifier () {
		return beaconIdentifier;
	}

	/**
	 * Get an approximate lower bound for the distance between the front of the vehicle and the beacon
	 * @return The lower bound for the distance to the beacon, in metres
	 */
	public double getDistanceLowerBound () {
		return distanceLowerBound;
	}

	/**
	 * Get an approximate upper bound for the distance between the front of the vehicle and the beacon
	 * @return The upper bound for the distance to the beacon, in metres
	 */
	public double getDistanceUpperBound () {
		return distanceUpperBound;
	}

	/**
	 * Get the angle between the front of the vehicle and the beacon. If the value is positive, the vehicle would need
	 * to turn right to normalise it, while if the value is negative it would need to turn left to normalise it.
	 *
	 * @return The angle between the front of the vehicle and the beacon, in radians
	 */
	public double getAngle () {
		return angle;
	}
}
