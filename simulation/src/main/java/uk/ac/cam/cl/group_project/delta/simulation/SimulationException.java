package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * A generic exception that may occur during simulation.
 */
public class SimulationException extends Exception {

	/**
	 * Constructs a new exception with `null` as its detail message.
	 */
	public SimulationException() {
		super();
	}

	/**
	 * Initialise exception with message.
	 * @param message    Information message.
	 */
	public SimulationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * @param message    Information message.
	 * @param cause      The cause - `null` is permitted.
	 */
	public SimulationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified detail message, cause,
	 * suppression enabled or disabled, and writable stack trace enabled or
	 * disabled.
	 * @param message               Information message.
	 * @param cause                 The cause - `null` is permitted.
	 * @param enableSuppression     Allow exception suppression?
	 * @param writableStackTrace    Should the stack trace be writable?
	 */
	public SimulationException(
		String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message
	 * of `(cause == null ? null : cause.toString())`.
	 * @param cause    The cause - `null` is permitted
	 */
	public SimulationException(Throwable cause) {
		super(cause);
	}

}
