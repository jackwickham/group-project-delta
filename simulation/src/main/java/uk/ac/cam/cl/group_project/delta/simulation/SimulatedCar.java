package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * Encapsulation of simulated car object and its associated interface modules.
 */
public class SimulatedCar extends PhysicsCar {

	/**
	 * Network interface for this car.
	 */
	private NetworkInterface networkInterface;

	/**
	 * Sensor interface for this car.
	 */
	private SensorInterface sensorInterface;

	/**
	 * Drive interface for this car.
	 */
	private DriveInterface driveInterface;

	/**
	 * Constructs a car, but do not add it to the world.
	 * @param length     Wheel base of this car.
	 * @param world      Simulated world in which this car exists.
	 * @param network    Simulated network on which this car will communicate.
	 */
	public SimulatedCar(double length, World world, SimulatedNetwork network) {
		super(length);
		networkInterface = new SimulatedNetworkModule(this, network);
		sensorInterface = new SimulatedSensorModule(world, this);
		driveInterface = new SimulatedDriveModule(this);
	}

	/**
	 * Fetch this car's network interface.
	 * @return    The network interface for this car.
	 */
	public NetworkInterface getNetworkInterface() {
		return networkInterface;
	}

	/**
	 * Fetch this car's sensor interface.
	 * @return    The sensor interface for this car.
	 */
	public SensorInterface getSensorInterface() {
		return sensorInterface;
	}

	/**
	 * Fetch this car's drive interface.
	 * @return    The drive interface for this car.
	 */
	public DriveInterface getDriveInterface() {
		return driveInterface;
	}

}
