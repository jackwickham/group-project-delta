package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.SensorInterface;

import java.util.List;

/**
 * Implementation of sensor interface for simulated vehicles.
 */
public class SimulatedSensorModule implements SensorInterface {

	/**
	 * Returns a floating point value representing the distance
	 * to the nearest object. The value returned will likely be
	 * approximate. This method may not be implemented, depending
	 * on the hardware available, and will return null in this case.
	 *
	 * @return the distance in m or null if there is no hardware support
	 */
	public Double getFrontProximity() {

	}

	/**
	 * Returns a list of objects that represent the visible beacons
	 * and their positions relative to this vehicle. Beacons are installed
	 * in all platooning vehicles, but are not exclusive to these vehicles.
	 * Position accuracy may degrade with distance and have significant noise.
	 *
	 * @return list containing Beacons visible
	 */
	public List<Beacon> getBeacons() {

	}

	/**
	 * Returns the current acceleration of the vehicle.
	 * @return acceleration in m/s^2
	 */
	public double getAcceleration() {

	}

	/**
	 * Returns the current speed.
	 * @return speed in m/s
	 */
	public double getSpeed() {

	}

	/**
	 * Returns the current turn rate.
	 * @return turn rate in rad/s
	 */
	public double getTurnRate() {

	}

	/**
	 * Returns the current position of the vehicle in some standard
	 * coordinate system.
	 * @returns current position 2D vector
	 */
	// public Vector getAbsolutePosition()

}
