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

	/**
	 * Get the singleton instance
	 *
	 * @return The Logger
	 */
	public static Logger getInstance() {
		if (instance == null) {
			try {
				instance = instantiationClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				instance = new Logger();
				instance.log(e);
			}
		}

		return instance;
	}

	/**
	 * Log a message
	 *
	 * @param message The message
	 */
	public void log(String message) {
		System.err.println(message);
	}

	/**
	 * Log an exception
	 *
	 * @param err The exception
	 */
	public void log(Throwable err) {
		err.printStackTrace();
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
}
