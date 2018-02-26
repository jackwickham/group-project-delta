package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.Log;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;

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
	 * The algorithm that is controlling this car.
	 */
	private Algorithm controller;

	/**
	 * The default wheel base of created cars. Set to 15cm for compatibility
	 * with the LEGO(R) vehicles.
	 */
	private static final double DEFAULT_WHEEL_BASE = 0.15;

	/**
	 * Constructs a car, but do not add it to the world.
	 * @param length     Wheel base of the vehicle.
	 * @param world      Simulated world in which this car exists.
	 * @param network    Simulated network on which this car will communicate.
	 */
	public SimulatedCar(double length, World world, SimulatedNetwork network) {
		super(length);
		networkInterface = new SimulatedNetworkModule(this, network);
		sensorInterface = new SimulatedSensorModule(this, world);
		driveInterface = new SimulatedDriveModule(this);
	}

	/**
	 * Constructs a car, but do not add it to the world.
	 * @param world      Simulated world in which this car exists.
	 * @param network    Simulated network on which this car will communicate.
	 */
	public SimulatedCar(World world, SimulatedNetwork network) {
		this(DEFAULT_WHEEL_BASE, world, network);
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

	/**
	 * Set the current algorithm controller for this car.
	 * @param algorithm    Algorithm that will make decisions for this vehicle.
	 */
	public void setController(Algorithm algorithm) {
		controller = algorithm;
		algorithm.initialise();
	}

	/**
	 * Get the current algorithm controller for this car.
	 * @return    The current {@link Algorithm} that controls this car.
	 */
	public Algorithm getController() {
		return controller;
	}

	/**
	 * Call the algorithm to update this car's state and communicate it to the other vehicles
	 * @param timeNanos The world time in nanoseconds
	 */
	public void updateControl(long timeNanos) {
		if (controller == null) {
			throw new IllegalStateException("An algorithm must be attached to this vehicle before it can update");
		}
		controller.update(timeNanos);
	}

	/**
	 * Stop the vehicle
	 */
	public void stop() {
		//controller.emergencyStop();
	}
}
