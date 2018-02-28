package uk.ac.cam.cl.group_project.delta.lego;

import uk.ac.cam.cl.group_project.delta.Beacon;
import uk.ac.cam.cl.group_project.delta.BeaconInterface;
import uk.ac.cam.cl.group_project.delta.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegoBeacon implements BeaconInterface {

	/**
	 * Map from vehicle names to beacon IDs.
	 */
	private static final Map<String, Integer> nameToID = new HashMap<>();
	static {
		nameToID.put("DeLorean", 1);
		nameToID.put("SpaceTesla", 2);
		nameToID.put("Batmobile", 3);
	}

	private Sensor sensor;
	private int beaconID;

	public LegoBeacon(Sensor sensor, String name) {
		this.sensor = sensor;
		if (nameToID.containsKey(name)) {
			this.beaconID = nameToID.get(name);
		} else {
			this.beaconID = -1;
		}
	}

	/**
	 * Returns the beacon id of the current vehicle.
	 * If the vehicle's name is unknown, return -1.
	 *
	 * @return the vehicle's beacon id
	 */
	public int getCurrentBeaconId() {
		return beaconID;
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
		return sensor.getBeacons();
	}

}
