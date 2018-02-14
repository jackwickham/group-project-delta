package uk.ac.cam.cl.group_project.delta;

/**
 * Base log class, shared between LEGO and simulation code. Either may extend it, but it will function as it is.
 *
 * A singleton
 *
 * @author Jack Wickham
 */
public class Logger {
	/**
	 * The stored singleton instance of this class
	 */
	private static Logger instance = null;

	/**
	 * The class to use when constructing a new instance of Logger
	 */
	private static Class<? extends Logger> instantiationClass = Logger.class;

	protected Logger() {
		if (instance != null) {
			throw new IllegalStateException("Can't construct a new Logger when one already exists");
		}
	}

	/**
	 * Get the singleton instance
	 *
	 * If no instance currently exists, this will attempt to construct a new instance of {@link #instantiationClass},
	 * and fall back to a standard Logger if that instantiation fails.
	 *
	 * @return The Logger instance
	 */
	public static Logger getInstance() {
		if (instance == null) {
			try {
				instance = instantiationClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				instance = new Logger();
				instance.log(Severity.ERROR, e);
			}
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
		System.err.printf("%s: %s", severity.name, message);
	}

	/**
	 * Log an exception
	 *
	 * @param severity The severity of the log entry
	 * @param err The exception
	 */
	public void log(Severity severity, Throwable err) {
		System.err.printf("%s: ", severity.name);
		err.printStackTrace();
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
	 * @param c The class that extends Logger to use
	 */
	protected static void setLoggerClass(Class<? extends Logger> c) {
		if (instance != null) {
			// Too late
			throw new RuntimeException("Can't change log class after a Logger instance has already been created");
		}
		instantiationClass = c;
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
