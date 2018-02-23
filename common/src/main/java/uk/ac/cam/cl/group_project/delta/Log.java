package uk.ac.cam.cl.group_project.delta;

import uk.ac.cam.cl.group_project.delta.log.LoggerInterface;
import uk.ac.cam.cl.group_project.delta.log.StderrLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Base singleton log class, shared between LEGO and simulation code. Classes extending LoggerInterface are required
 * for this class to function
 *
 * @author Jack Wickham
 */
public final class Log {
	/**
	 * The stored singleton instance of this class
	 */
	private static Log instance = null;

	/**
	 * The class to use when constructing a new instance of Log
	 */
	private List<LoggerInterface> loggers;

	/**
	 * Don't allow this class to be instantiated or extended
	 *
	 * Create with an StderrLogger by default
	 */
	private Log () {
		loggers = new ArrayList<>();
		loggers.add(new StderrLogger());
	}

	/**
	 * Get the singleton instance
	 *
	 * If no instance currently exists, this will construct a new one
	 *
	 * @return The Log instance
	 */
	public static Log getInstance() {
		if (instance == null) {
			instance = new Log();
		}

		return instance;
	}

	/**
	 * Log a message
	 *
	 * @param severity The severity of the log entry
	 * @param message The message
	 */
	public void log(Severity severity, String message) {
		for (LoggerInterface logger : loggers) {
			logger.log(severity, message);
		}
	}

	/**
	 * Log an exception
	 *
	 * @param severity The severity of the log entry
	 * @param err The exception
	 */
	public void log(Severity severity, Throwable err) {
		for (LoggerInterface logger : loggers) {
			logger.log(severity, err);
		}
	}



	/**
	 * Log a debug message
	 * @param message The debug message to log
	 */
	public static void debug(String message) {
		getInstance().log(Severity.DEBUG, message);
	}

	/**
	 * Log an exception with debug severity
	 * @param err The exception to log
	 */
	public static void debug(Throwable err) {
		getInstance().log(Severity.DEBUG, err);
	}

	/**
	 * Log a warning
	 * @param message The warning message to log
	 */
	public static void warn(String message) {
		getInstance().log(Severity.WARN, message);
	}

	/**
	 * Log an exception with warning severity
	 * @param err The exception to log
	 */
	public static void warn(Throwable err) {
		getInstance().log(Severity.WARN, err);
	}

	/**
	 * Log an error
	 * @param message The error message to log
	 */
	public static void error(String message) {
		getInstance().log(Severity.ERROR, message);
	}

	/**
	 * Log an exception with error severity
	 * @param err The exception to log
	 */
	public static void error(Throwable err) {
		getInstance().log(Severity.ERROR, err);
	}

	/**
	 * Log a critical error, which means that the vehicle can't continue to operate correctly
	 * @param message The error message to log
	 */
	public static void critical(String message) {
		getInstance().log(Severity.CRITICAL, message);
	}

	/**
	 * Log an fatal exception
	 * @param err The exception to log
	 */
	public static void critical(Throwable err) {
		getInstance().log(Severity.CRITICAL, err);
	}



	/**
	 * Set the class to be used for logging
	 *
	 * @param c The class that extends Log to use
	 */
	public void registerLogger(LoggerInterface c) {
		loggers.add(c);

	}

	/**
	 * Log severities which could be filtered on
	 */
	public static enum Severity {
		DEBUG("Debug"),
		WARN("Warn"),
		ERROR("Error"),
		CRITICAL("Critical");

		public final String name;

		private Severity(String name) {
			this.name = name;
		}
	}
}
