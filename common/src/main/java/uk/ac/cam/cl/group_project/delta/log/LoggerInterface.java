package uk.ac.cam.cl.group_project.delta.log;

import uk.ac.cam.cl.group_project.delta.Log;

public interface LoggerInterface {
	/**
	 * Log a message
	 *
	 * @param severity The severity of the log entry
	 * @param message The message
	 */
	void log(Log.Severity severity, String message);

	/**
	 * Log an exception
	 *
	 * @param severity The severity of the log entry
	 * @param err The exception
	 */
	void log(Log.Severity severity, Throwable err);
}
