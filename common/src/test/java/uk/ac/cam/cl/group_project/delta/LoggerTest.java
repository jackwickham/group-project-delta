package uk.ac.cam.cl.group_project.delta;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test Logger with a custom logger
 */
public class LoggerTest {
	@BeforeClass
	public static void setCustomLogger() {
		Logger.setLoggerClass(CustomLogger.class);
	}

	@Test
	public void getInstanceReturnsCorrectClass() {
		Logger instance = Logger.getInstance();
		assertTrue("Incorrect class used by logger - actually returned " + instance.getClass().getName(), instance instanceof CustomLogger);
	}

	@Test
	public void getInstanceReturnsSameInstance() {
		assertSame(Logger.getInstance(), Logger.getInstance());
	}

	@Test
	public void debugWithStringWorksCorrectly() {
		Logger.debug("Test 1");
		assertEquals("Test 1", CustomLogger.lastMessage);
		assertEquals(Logger.Severity.DEBUG, CustomLogger.lastSeverity);
	}

	@Test
	public void debugWithExceptionWorksCorrectly() {
		Exception e = new Exception();
		Logger.debug(e);
		assertSame(e, CustomLogger.lastMessage);
		assertEquals(Logger.Severity.DEBUG, CustomLogger.lastSeverity);
	}

	@Test
	public void warnWithStringWorksCorrectly() {
		Logger.warn("Test 2");
		assertEquals("Test 2", CustomLogger.lastMessage);
		assertEquals(Logger.Severity.WARN, CustomLogger.lastSeverity);
	}

	@Test
	public void warnWithExceptionWorksCorrectly() {
		Exception e = new Exception();
		Logger.warn(e);
		assertSame(e, CustomLogger.lastMessage);
		assertEquals(Logger.Severity.WARN, CustomLogger.lastSeverity);
	}

	@Test
	public void errorWithStringWorksCorrectly() {
		Logger.error("Test 3");
		assertEquals("Test 3", CustomLogger.lastMessage);
		assertEquals(Logger.Severity.ERROR, CustomLogger.lastSeverity);
	}

	@Test
	public void errorWithExceptionWorksCorrectly() {
		Exception e = new Exception();
		Logger.error(e);
		assertSame(e, CustomLogger.lastMessage);
		assertEquals(Logger.Severity.ERROR, CustomLogger.lastSeverity);
	}

	@Test
	public void criticalWithStringWorksCorrectly() {
		Logger.critical("Test 4");
		assertEquals("Test 4", CustomLogger.lastMessage);
		assertEquals(Logger.Severity.CRITICAL, CustomLogger.lastSeverity);
	}

	@Test
	public void criticalWithExceptionWorksCorrectly() {
		Exception e = new Exception();
		Logger.critical(e);
		assertSame(e, CustomLogger.lastMessage);
		assertEquals(Logger.Severity.CRITICAL, CustomLogger.lastSeverity);
	}


	/**
	 * A custom logger to test that custom logging is working
	 */
	public static class CustomLogger extends Logger {
		/**
		 * The severity of the last log entry
		 */
		public static Severity lastSeverity;

		/**
		 * The message associated with the last log entry
		 */
		public static Object lastMessage;

		/**
		 * Log a message
		 *
		 * @param severity The severity of the log entry
		 * @param message  The message
		 */
		@Override
		public void log (Severity severity, String message) {
			lastSeverity = severity;
			lastMessage  = message;
		}

		/**
		 * Log an exception
		 *
		 * @param severity The severity of the log entry
		 * @param err      The exception
		 */
		@Override
		public void log (Severity severity, Throwable err) {
			lastSeverity = severity;
			lastMessage = err;
		}

		public CustomLogger() {
			super();
		}
	}
}
