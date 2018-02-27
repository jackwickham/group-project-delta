package uk.ac.cam.cl.group_project.delta;

public abstract class BeaconInterface {

	/**
	 * Returns the beacon id of the current vehicle
	 *
	 * @return the vehichle's beacon id
	 */
	public abstract int getCurrentBeaconId();

	/**
	 * Returns the beacon id which corresponds to the closest
	 * vehicle which is visible to the beacon sensor. The value
	 * is null if no beacon is visible to the sensors.
	 * @return
	 */
	public abstract Integer getVisibleBeaconId();

}
