package uk.ac.cam.cl.group_project.delta.lego;

import lejos.hardware.ev3.EV3;
import uk.ac.cam.cl.group_project.delta.Beacon;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

import java.util.List;

public class Sensor implements SensorInterface {

	private Drive drive;
	private Ultrasound ultrasound;

	public Sensor(EV3 ev3, Drive drive) {
		this.drive = drive;
		this.ultrasound = new Ultrasound(ev3);
	}

	/**
	 * Returns a floating point value representing the distance
	 * to the nearest object. The value returned will likely be
	 * approximate. This method may not be implemented, depending
	 * on the hardware available, and will return null in this case.
	 *
	 * @return the distance in m or null if there is no hardware support
	 */
	@Override
	public Double getFrontProximity() {
		return ultrasound.getProximity();
	}

	/**
	 * Returns a list of objects that represent the visible beacons
	 * and their positions relative to this vehicle. Beacons are installed
	 * in all platooning vehicles, but are not exclusive to these vehicles.
	 * Position accuracy may degrade with distance and have significant noise.
	 *
	 * @return list containing Beacons visible
	 */
	@Override
	public List<Beacon> getBeacons() {
		return null;
	}

	/**
	 * Returns the current acceleration of the vehicle.
	 *
	 * @return acceleration in m/s^2
	 */
	@Override
	public double getAcceleration() {
		return drive.getAcceleration();
	}

	/**
	 * Returns the current speed.
	 *
	 * @return speed in m/s
	 */
	@Override
	public double getSpeed() {
		return drive.getSpeed();
	}

	/**
	 * Returns the current turn rate.
	 *
	 * @return turn rate in rad/s
	 */
	@Override
	public double getTurnRate() {
		return drive.getTurnRate();
	}
}
