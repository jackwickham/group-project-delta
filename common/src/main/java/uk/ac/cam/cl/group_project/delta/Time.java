package uk.ac.cam.cl.group_project.delta;

public class Time {

	/**
	 * The current global time, can be set by the simulation.
	 */
	public static long currentTime = -1;

	/**
	 * A flag indicating whether to use the system time or the set time.
	 */
	public static boolean useSetTime = false;

	/**
	 * @return The current 'global' time
	 */
	public static long getTime() {
		if(useSetTime) {
			return currentTime;
		} else {
			return System.nanoTime();
		}
	}
}
