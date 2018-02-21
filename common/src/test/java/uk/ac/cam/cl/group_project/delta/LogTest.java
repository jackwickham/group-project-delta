package uk.ac.cam.cl.group_project.delta;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.cam.cl.group_project.delta.log.LoggerInterface;

import static org.junit.Assert.*;

/**
 * Test Log with a custom logger
 */
public class LogTest {
	@BeforeClass
	public static void setCustomLogger() {
		CustomLogger.register();
	}

	@Test
	public void getInstanceReturnsSameInstance() {
		assertSame(Log.getInstance(), Log.getInstance());
	}

	@Test
	public void debugWithStringWorksCorrectly() {
		Log.debug("Test 1");
		assertEquals("Test 1", CustomLogger.lastMessage);
		assertEquals(Log.Severity.DEBUG, CustomLogger.lastSeverity);
	}

	@Test
	public void debugWithExceptionWorksCorrectly() {
		Exception e = new Exception();
		Log.debug(e);
		assertSame(e, CustomLogger.lastMessage);
		assertEquals(Log.Severity.DEBUG, CustomLogger.lastSeverity);
	}

	@Test
	public void warnWithStringWorksCorrectly() {
		Log.warn("Test 2");
		assertEquals("Test 2", CustomLogger.lastMessage);
		assertEquals(Log.Severity.WARN, CustomLogger.lastSeverity);
	}

	@Test
	public void warnWithExceptionWorksCorrectly() {
		Exception e = new Exception();
		Log.warn(e);
		assertSame(e, CustomLogger.lastMessage);
		assertEquals(Log.Severity.WARN, CustomLogger.lastSeverity);
	}

	@Test
	public void errorWithStringWorksCorrectly() {
		Log.error("Test 3");
		assertEquals("Test 3", CustomLogger.lastMessage);
		assertEquals(Log.Severity.ERROR, CustomLogger.lastSeverity);
	}

	@Test
	public void errorWithExceptionWorksCorrectly() {
		Exception e = new Exception();
		Log.error(e);
		assertSame(e, CustomLogger.lastMessage);
		assertEquals(Log.Severity.ERROR, CustomLogger.lastSeverity);
	}

	@Test
	public void criticalWithStringWorksCorrectly() {
		Log.critical("Test 4");
		assertEquals("Test 4", CustomLogger.lastMessage);
		assertEquals(Log.Severity.CRITICAL, CustomLogger.lastSeverity);
	}

	@Test
	public void criticalWithExceptionWorksCorrectly() {
		Exception e = new Exception();
		Log.critical(e);
		assertSame(e, CustomLogger.lastMessage);
		assertEquals(Log.Severity.CRITICAL, CustomLogger.lastSeverity);
	}


	/**
	 * A custom logger to test that custom logging is working
	 */
	public static class CustomLogger implements LoggerInterface {
		/**
		 * The severity of the last log entry
		 */
		public static Log.Severity lastSeverity;

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
		public void log (Log.Severity severity, String message) {
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
		public void log (Log.Severity severity, Throwable err) {
			lastSeverity = severity;
			lastMessage = err;
		}

		/**
		 * Register this logger
		 */
		public static void register() {
			Log.getInstance().registerLogger(new CustomLogger());
		}
	}
}
