package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * A generic exception that may occur during simulation.
 */
public class SimulationException extends Exception {

	/**
	 * Initialise exception with message.
	 * @param m    Information message.
	 */
	public SimulationException(String m) {
		super(m);
	}

}
