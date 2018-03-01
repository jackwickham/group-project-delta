package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.*;
import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;

import java.util.List;

/**
 * Encapsulation of simulated car object and its associated interface modules.
 */
public class SimulatedCar extends PhysicsCar implements BeaconInterface {

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
		sensorInterface = new FaultySensorModule(this, world);
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
	 */
	public void updateControl() {
		if (controller == null) {
			throw new IllegalStateException("An algorithm must be attached to this vehicle before it can update");
		}
		controller.update();
	}

	/**
	 * Stop the vehicle
	 */
	public void stop() {
		//controller.emergencyStop();
	}

	/**
	 * Returns the beacon id of the current vehicle
	 *
	 * @return the vehicle's beacon id
	 */
	@Override
	public int getCurrentBeaconId() {
		return getUuid();
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
		return getSensorInterface().getBeacons();
	}

}
