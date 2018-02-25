package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.Beacon;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * A wrapper around the normal simulated sensors which provides failure injection
 *
 * @author Jack Wickham
 */
public class FaultySensorModule extends SimulatedSensorModule {
	/**
	 * Random number generator
	 */
	private final Random random;

	/**
	 * Constructs a sensor module for given car in provided world.
	 *
	 * @param car   Physical body to instrument about.
	 * @param world World to instrument.
	 */
	public FaultySensorModule (PhysicsCar car, World world) {
		super(car, world);
		random = new Random();
	}

	//#region Front proximity failure injection

	/**
	 * The standard deviation of the normal distribution used for the front proximity sensor
	 */
	private static double frontProximityStdDev = 0.0;

	/**
	 * The proportion of the time where the proximity sensor will return infinity when a reading is available
	 */
	private static double frontProximityFailureRate = 0.0;

	/**
	 * Whether the front proximity sensor should give values or return null
	 */
	private static boolean frontProximityEnabled = true;

	/**
	 * Returns a floating point value representing the distance
	 * to the nearest object. The value returned will likely be
	 * approximate. This method may not be implemented, depending
	 * on the hardware available, and will return null in this case.
	 *
	 * @return the distance in m or null if there is no hardware support
	 */
	@Override
	public Double getFrontProximity () {
		if (!frontProximityEnabled) {
			return null;
		}
		Double result = super.getFrontProximity();
		if (frontProximityFailureRate > random.nextFloat()) {
			// Fake a failed reading
			result = Double.POSITIVE_INFINITY;
		}
		if (result != null && frontProximityStdDev > 0) {
			// Sample from the normal distribution with mean result and std dev of frontProximityStdDev
			result = Math.max(0, random.nextGaussian() * frontProximityStdDev + result);
		}
		return result;
	}

	/**
	 * Set the standard deviation for the front proximity sensor's readings
	 * @param frontProximityStdDev The new standard deviation
	 */
	public static void setFrontProximityStdDev (double frontProximityStdDev) {
		FaultySensorModule.frontProximityStdDev = frontProximityStdDev;
	}

	/**
	 * Set the proportion of the time when the proximity sensor will incorrectly report no reading
	 * @param frontProximityFailureRate The new failure rate
	 */
	public static void setFrontProximityFailureRate (double frontProximityFailureRate) {
		FaultySensorModule.frontProximityFailureRate = frontProximityFailureRate;
	}

	public static void setFrontProximityEnabled(boolean enabled) {
		FaultySensorModule.frontProximityEnabled = enabled;
	}

	//#endregion
	//#region Beacon failure injection

	/**
	 * Whether the beacon values should be adjusted to match the behaviour of the Mindstorms
	 */
	private static boolean beaconsEmulateMindstorms = false;

	/**
	 * The standard deviation of the normal distribution used for updating the beacon distance value
	 */
	private static double beaconDistanceStdDev = 0.0;

	/**
	 * The standard deviation of the normal distribution used for updating the beacon angle
	 */
	private static double beaconAngleStdDev = 0.0;

	/**
	 * Returns a list of objects that represent the visible beacons
	 * and their positions relative to this vehicle. Beacons are installed
	 * in all platooning vehicles, but are not exclusive to these vehicles.
	 * Position accuracy may degrade with distance and have significant noise.
	 *
	 * @return list containing Beacons visible
	 */
	@Override
	public List<Beacon> getBeacons () {
		List<Beacon> beacons = super.getBeacons();
		ListIterator<Beacon> it = beacons.listIterator();
		while (it.hasNext()) {
			Beacon beacon = it.next();
			// Process the beacon distance
			double distance = beacon.getDistanceLowerBound(); // Lower and upper bounds are equal
			if (beaconDistanceStdDev > 0) {
				distance += random.nextGaussian() * beaconDistanceStdDev;
			}

			double lowerBound, upperBound;
			if (beaconsEmulateMindstorms) {
				// Convert to a mindstorms sensor value and back to produce a range
				double sensorValue = distanceToMindstormsBeaconValue(distance);
				lowerBound = mindstormsBeaconToDistanceBound(sensorValue - 1);
				upperBound = mindstormsBeaconToDistanceBound(sensorValue);
			} else {
				lowerBound = distance;
				upperBound = distance;
			}

			// Process the beacon angle
			double angle = beacon.getAngle();
			if (beaconAngleStdDev > 0) {
				angle += random.nextGaussian() * beaconAngleStdDev;
			}

			// Create the new beacon and replace it in the list
			Beacon updatedBeacon = new Beacon(beacon.getBeaconIdentifier(), lowerBound, upperBound, angle);
			it.set(updatedBeacon);
		}
		return beacons;
	}

	/**
	 * In the mindstorms, we take the value `v` from the sensors and convert it using d = 0.0683 + 0.0267v + 0.000259v²,
	 * so to emulate the mindstorms we need to invert that and round it.
	 *
	 * @param distance The distance that we are using
	 * @return The value that the Mindstorms would give for that distance
	 */
	private double distanceToMindstormsBeaconValue (double distance) {
		double a = 0.000259, b = 0.0267, c = 0.0683 - distance;
		double equivalentSensorValue = (-b + Math.sqrt(b*b - 4 * a * c)) / (2 * a);
		return (double) Math.round(Math.max(equivalentSensorValue, 1.0));
	}

	/**
	 * Copied from the equivalent function in LEGO's BeaconTracker
	 * @param sensorValue The value from the sensor
	 * @return The upper bound of the distance that it corresponds to
	 */
	private double mindstormsBeaconToDistanceBound(double sensorValue) {
		if (sensorValue == 0) {
			// The sensor never actually returns 0, so it is only used when getting a lower bound. The closest that the
			// beacon can be to the sensor is 0m, so that is the lower bound here.
			return 0.0;
		}
		return 0.0683 + 0.0267 * sensorValue + 0.000259 * sensorValue * sensorValue;
	}

	/**
	 * Set whether the beacons should attempt to match the Mindstorms' behaviour
	 * @param beaconsEmulateMindstorms Whether to match the behaviour
	 */
	public static void setBeaconsEmulateMindstorms (boolean beaconsEmulateMindstorms) {
		FaultySensorModule.beaconsEmulateMindstorms = beaconsEmulateMindstorms;
	}

	/**
	 * Set the standard deviation for the distance measured by the beacons
	 * @param beaconDistanceStdDev The new standard deviation
	 */
	public static void setBeaconDistanceStdDev (double beaconDistanceStdDev) {
		FaultySensorModule.beaconDistanceStdDev = beaconDistanceStdDev;
	}

	/**
	 * Set the standard deviation for the angle measured by the beacons
	 * @param beaconAngleStdDev The new standard deviation
	 */
	public static void setBeaconAngleStdDev (double beaconAngleStdDev) {
		FaultySensorModule.beaconAngleStdDev = beaconAngleStdDev;
	}

	//#endregion
	//#region Motion failure injection

	// The standard deviations for injecting faults into the acceleration, speed and turn rate values
	private static double accelerationStdDev = 0.0;
	private static double speedStdDev = 0.0;
	private static double turnRateStdDev = 0.0;

	/**
	 * Returns the current acceleration of the vehicle.
	 *
	 * @return acceleration in m/s^2
	 */
	@Override
	public double getAcceleration () {
		double acceleration = super.getAcceleration();
		if (accelerationStdDev > 0) {
			acceleration += random.nextGaussian() * accelerationStdDev;
		}
		return acceleration;
	}

	/**
	 * Returns the current speed.
	 *
	 * @return speed in m/s
	 */
	@Override
	public double getSpeed () {
		double speed = super.getSpeed();
		if (speedStdDev > 0) {
			speed += random.nextGaussian() * speedStdDev;
		}
		return speed;
	}

	/**
	 * Returns the current turn rate.
	 *
	 * @return turn rate in rad/s
	 */
	@Override
	public double getTurnRate () {
		double turnRate = super.getTurnRate();
		if (turnRateStdDev > 0) {
			turnRate += random.nextGaussian() * turnRateStdDev;
		}
		return turnRate;
	}

	/**
	 * Set the standard deviation for the acceleration error
	 * @param accelerationStdDev The new standard deviation
	 */
	public static void setAccelerationStdDev (double accelerationStdDev) {
		FaultySensorModule.accelerationStdDev = accelerationStdDev;
	}

	/**
	 * Set the standard deviation for the speed error
	 * @param speedStdDev The new standard deviation
	 */
	public static void setSpeedStdDev (double speedStdDev) {
		FaultySensorModule.speedStdDev = speedStdDev;
	}

	/**
	 * Set the standard deviation for the turn rate error
	 * @param turnRateStdDev The new standard deviation
	 */
	public static void setTurnRateStdDev (double turnRateStdDev) {
		FaultySensorModule.turnRateStdDev = turnRateStdDev;
	}

	//#endregion
}
