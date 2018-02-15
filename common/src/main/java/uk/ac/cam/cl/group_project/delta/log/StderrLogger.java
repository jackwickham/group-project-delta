package uk.ac.cam.cl.group_project.delta.log;

import uk.ac.cam.cl.group_project.delta.Log;

/**
 * A logger which writes the error to the standard error output
 *
 * @author Jack Wickham
 */
public class StderrLogger implements LoggerInterface {
	/**
	 * Log a message
	 *
	 * @param severity The severity of the log entry
	 * @param message The message
	 */
	public void log(Log.Severity severity, String message) {
		System.err.printf("%s: %s", severity.name, message);
	}

	/**
	 * Log an exception
	 *
	 * @param severity The severity of the log entry
	 * @param err The exception
	 */
	public void log(Log.Severity severity, Throwable err) {
		System.err.printf("%s: ", severity.name);
		err.printStackTrace();
	}

	/**
	 * Register a StderrLogger to receive log messages
	 */
	public static void register() {
		Log.getInstance().registerLogger(new StderrLogger());
	}

	/**
	 * Prevent people from constructing an instance of this - the register method should be used instead
	 */
	private StderrLogger() { }
}
