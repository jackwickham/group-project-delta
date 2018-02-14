package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Gives objects a (runtime) unique identifier, which are assigned in ascending
 * numerical order starting at 1.
 */
public abstract class UniquelyIdentifiable {

	/**
	 * Static tracker of next ID to assign.
	 */
	private static int next = 1;

	/**
	 * Unique identifier for this object.
	 */
	private final int uuid;

	/**
	 * Initialise ID with next available identifier.
	 */
	public UniquelyIdentifiable() {
		uuid = next++;
	}

	/**
	 * Fetch the ID of this object.
	 * @return    Unique identifier.
	 */
	public int getUuid() {
		return uuid;
	}

}
