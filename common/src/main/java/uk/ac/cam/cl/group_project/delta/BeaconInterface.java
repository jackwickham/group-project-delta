package uk.ac.cam.cl.group_project.delta;

import java.util.List;

public interface BeaconInterface {

	/**
	 * Returns the beacon id of the current vehicle
	 *
	 * @return the vehicle's beacon id
	 */
	public abstract int getCurrentBeaconId();

	/**
	 * Returns a list of objects that represent the visible beacons
	 * and their positions relative to this vehicle. Beacons are installed
	 * in all platooning vehicles, but are not exclusive to these vehicles.
	 * Position accuracy may degrade with distance and have significant noise.
	 *
	 * @return list containing Beacons visible
	 */
	public abstract List<Beacon> getBeacons();

}
