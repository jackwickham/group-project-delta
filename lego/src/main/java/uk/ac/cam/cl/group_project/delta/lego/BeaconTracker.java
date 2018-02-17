package uk.ac.cam.cl.group_project.delta.lego;

import lejos.hardware.ev3.EV3;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import uk.ac.cam.cl.group_project.delta.Beacon;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the beacons from other vehicles.
 *
 * Distance is annoyingly not granular, with correspondences:
 * 1.0 = 0-12cm
 * 2.0 = 12-13cm
 * 3.0 = 13-14cm
 * ...
 *
 * @author Jack Wickham
 */
public class BeaconTracker {
	/**
	 * The `SensorMode` (in seek mode) that allows the beacon distance and angle to be retrieved. Calling `fetchSamples`
	 * on this will return an array `samples`, where `samples[2i]` is the angle in degrees to the beacon on channel
	 * `i+1`, and `samples[2i + 1]` is the distance in arbitrary non-linear units to the beacon on channel `i+1`.
	 */
	private SensorMode seekMode;

	/**
	 * The number of radians represented by a value of 1 in the angle field of the beacon
	 *
	 * From my measurements, 45°=15 arbitrary turn units, which gives this conversion for radians
	 */
	private final double radiansPerAngleUnit = 0.052359878;

	/**
	 * Create a new instance for the provided robot
	 * @param ev3 The EV3 device to use, with an IR sensor connected to port 4
	 */
	public BeaconTracker(EV3 ev3) {
		EV3IRSensor sensor = new EV3IRSensor(ev3.getPort("S4"));
		seekMode = sensor.getSeekMode();
	}

	/**
	 * Create a new instance to use with the provided sensor
	 * @param sensor The IR sensor to use
	 */
	public BeaconTracker(EV3IRSensor sensor) {
		seekMode = sensor.getSeekMode();
	}

	/**
	 * Get a list of `Beacon` instances for each of the beacons that can currently be detected by the IR sensor on the
	 * front of the vehicle
	 * @return A list containing a `Beacon` instance for each visible beacon
	 */
	public List<Beacon> getBeaconData() {
		float[] samples = new float[seekMode.sampleSize()];
		seekMode.fetchSample(samples, 0);

		List<Beacon> beacons = new ArrayList<>(4);

		for (int i = 0; i < samples.length; i += 2) {
			if (samples[i+1] != Float.POSITIVE_INFINITY) {
				int beaconId = i / 2 + 1;
				DblRange distanceMetres = convertDistanceToMetres(samples[i+1]);
				double angleRadians = samples[i] * radiansPerAngleUnit;
				beacons.add(new Beacon(beaconId, distanceMetres.lb, distanceMetres.ub, angleRadians));
			}
		}

		return beacons;
	}

	/**
	 * Convert the arbitrary distance given by the IR sensor into some sort of meaningful distance. Note that this is
	 * heavily (?) dependent on the current lighting.
	 *
	 * The value returned is the range of values that, under typical conditions, would give the sensor reading provided,
	 * so ranges share common endpoints but don't overlap.
	 *
	 * @param sensorDistance The distance provided by the sensor
	 * @return An approximate range in metres
	 */
	private DblRange convertDistanceToMetres(float sensorDistance) {
		return new DblRange(sensorToDistanceBound(sensorDistance - 1), sensorToDistanceBound(sensorDistance));
	}

	/**
	 * Convert an IR sensor distance value to an approximate upper bound on the distance that the beacon is from the
	 * sensor.
	 *
	 * From the data collected, available in the drive, the best fit curve for the upper bound of the range is
	 * ub = 0.0683 + 0.0267x + 0.000259x²
	 *
	 * @param sensorValue The value given by the IR sensor, which should be an integer between 0 and 127
	 * @return The approximate distance in metres
	 */
	private double sensorToDistanceBound(double sensorValue) {
		if (sensorValue == 0) {
			// The sensor never actually returns 0, so it is only used when getting a lower bound. The closest that the
			// beacon can be to the sensor is 0m, so that is the lower bound here.
			return 0.0;
		}
		return 0.0683 + 0.0267 * sensorValue + 0.000259 * sensorValue * sensorValue;
	}

	/**
	 * A helper class to hold a pair of doubles
	 */
	private class DblRange {
		/**
		 * The lower bound of the range
		 */
		public double lb;
		/**
		 * The upper bound of the range
		 */
		public double ub;

		/**
		 * Create a new DblRange
		 * @param lb The first number in the range
		 * @param ub The last number in the range
		 */
		public DblRange(double lb, double ub) {
			this.lb = lb;
			this.ub = ub;
		}
	}
}
