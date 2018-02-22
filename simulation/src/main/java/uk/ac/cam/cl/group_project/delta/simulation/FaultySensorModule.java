package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.Beacon;

import java.util.List;
import java.util.Random;

public class FaultySensorModule extends SimulatedSensorModule {
	/**
	 * Random number generator
	 */
	private Random random;

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
	private double frontProximityStdDev = 0.0;

	/**
	 * The proportion of the time where the proximity sensor will return null when a reading is available
	 */
	private double frontProximityFailureRate = 0.0;

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
		Double result = super.getFrontProximity();
		if (frontProximityFailureRate > random.nextFloat()) {
			// Fake a failed reading
			result = null;
		}
		if (result != null && frontProximityStdDev > 0) {
			// Sample from the normal distribution with mean result and std dev of frontProximityStdDev
			result = random.nextGaussian() * frontProximityStdDev + result;
		}
		return result;
	}

	/**
	 * Set the standard deviation for the front proximity sensor's readings
	 * @param frontProximityStdDev The new standard deviation
	 */
	public void setFrontProximityStdDev (double frontProximityStdDev) {
		this.frontProximityStdDev = frontProximityStdDev;
	}

	/**
	 * Set the proportion of the time when the proximity sensor will incorrectly report no reading
	 * @param frontProximityFailureRate The new failure rate
	 */
	public void setFrontProximityFailureRate (double frontProximityFailureRate) {
		this.frontProximityFailureRate = frontProximityFailureRate;
	}

	//#endregion

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
		return super.getBeacons();
	}

	/**
	 * Returns the current acceleration of the vehicle.
	 *
	 * @return acceleration in m/s^2
	 */
	@Override
	public double getAcceleration () {
		return super.getAcceleration();
	}

	/**
	 * Returns the current speed.
	 *
	 * @return speed in m/s
	 */
	@Override
	public double getSpeed () {
		return super.getSpeed();
	}

	/**
	 * Returns the current turn rate.
	 *
	 * @return turn rate in rad/s
	 */
	@Override
	public double getTurnRate () {
		return super.getTurnRate();
	}
}
