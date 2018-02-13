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
 */
public class BeaconTracker {
	/**
	 * The `SensorMode` (in seek mode) that allows the beacon distance and angle to be retrieved. Calling `fetchSamples`
	 * on this will return an array `samples`, where `samples[2i]` is the angle in degrees to the beacon on channel
	 * `i+1`, and `samples[2i + 1]` is the distance in arbitrary non-linear units to the beacon on channel `i+1`.
	 */
	private SensorMode seekMode;

	/**
	 * Create a new instance for the provided robot
	 * @param ev3 The EV3 device to use, with an IR sensor connected to port 4
	 */
	public BeaconTracker(EV3 ev3) {
		EV3IRSensor sensor = new EV3IRSensor(ev3.getPort("S4"));
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
				double angleRadians = samples[i] * 0.0174533; // 1 deg = 0.0174533 rad
				beacons.add(new Beacon(beaconId, distanceMetres.lb, distanceMetres.ub, angleRadians));
			}
		}

		return beacons;
	}

	/**
	 * Convert the arbitrary distance given by the IR sensor into some sort of meaningful distance. Note that this is
	 * heavily dependent on the current lighting.
	 * @param sensorDistance The distance provided by the sensor
	 * @return An approximate range in metres
	 */
	private DblRange convertDistanceToMetres(float sensorDistance) {
		if (sensorDistance <= 1.0) {
			// real distance < 12cm
			return new DblRange(0.0,  0.12);
		} else if (sensorDistance <= 2.0) {
			// 12cm <= d <= 14cm
			return new DblRange(0.12, 0.14);
		} else if (sensorDistance <= 3.0) {
			// 14cm <= d <= 15.5cm
			return new DblRange(0.14, 0.155);
		} else if (sensorDistance <= 4.0) {
			// 15.5 <= d <= 17.5
			return new DblRange(0.155, 0.175);
		} else if (sensorDistance <= 5.0) {
			// 17.5 <= d <= 20
			return new DblRange(0.175, 0.2);
		} else if (sensorDistance <= 6.0) {
			// 20 <= d <= 22ish
			return new DblRange(0.2, 0.22);
		} else if (sensorDistance <= 7.0) {
			// 22ish <= d <= 25.5ish
			return new DblRange(0.22, 0.255);
		} else if (sensorDistance <= 8.0) {
			// 25.5 <= d <= 30
			return new DblRange(0.255, 0.3);
		} else if (sensorDistance <= 9.0) {
			// 30 <= d <= 33
			return new DblRange(0.3, 0.33);
		} else if (sensorDistance <= 10.0) {
			// 33 <= d <= 35
			return new DblRange(0.33, 0.35);
		} else if (sensorDistance <= 11.0) {
			// 35 <= d <= 38
			return new DblRange(0.35, 0.38);
		} else {
			// It was at this point that I got bored of measuring it, and discovered that the distances are dependent
			// on how much shadow is being cast on the sensor. I also discovered that it has a maximum range of longer
			// than my room, so I can't measure it fully
			return new DblRange(0.38, Double.POSITIVE_INFINITY);
		}
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
