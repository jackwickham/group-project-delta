package uk.ac.cam.cl.group_project.delta.log;

import uk.ac.cam.cl.group_project.delta.Log;

/**
 * A logger which writes the error to the standard error output
 *
 * This logger is included by default, and shouldn't be manually registered
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
		System.err.printf("%s: %s\n", severity.name, message);
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
}
