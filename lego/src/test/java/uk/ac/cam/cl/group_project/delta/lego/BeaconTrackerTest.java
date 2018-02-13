package uk.ac.cam.cl.group_project.delta.lego;

import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import org.junit.Before;
import org.junit.Test;
import uk.ac.cam.cl.group_project.delta.Beacon;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test BeaconTracker
 *
 * @author Jack Wickham
 */
public class BeaconTrackerTest {
	private BeaconTracker classUnderTest;
	private MockSensorMode mockSensorMode;

	@Before
	public void createBeaconInstance() {
		EV3IRSensor mockSensor = mock(EV3IRSensor.class);
		mockSensorMode = new MockSensorMode();

		when(mockSensor.getSeekMode()).thenReturn(mockSensorMode);

		classUnderTest = new BeaconTracker(mockSensor);
	}

	@Test
	public void testCorrectValueOnWhenSensorReturns1() {
		float[] sample = {0, 1};
		mockSensorMode.setMockSample(sample);

		List<Beacon> result = classUnderTest.getBeaconData();
		assertEquals("Wrong number of beacon results returned", 1, result.size());
		assertEquals("Wrong beacon identifier for the beacon on channel 1", 1, result.get(0).getBeaconIdentifier());
		assertEquals("Lower bound of sensor value 1 was not 0", 0.0, result.get(0).getDistanceLowerBound(), 0.0);
		assertEquals("Upper bound of sensor value 1 was out of range", 12.0, result.get(0).getDistanceUpperBound(), 2.0);
	}

	@Test
	public void testNoBeaconsFound() {
		mockSensorMode.setMockSample(new float[0]);

		List<Beacon> result = classUnderTest.getBeaconData();
		assertEquals("Wrong number of beacon results returned", 0, result.size());
	}

	@Test
	public void testMultipleBeacons() {
		float[] sample = {0, 1, 0, 2};
		mockSensorMode.setMockSample(sample);

		List<Beacon> result = classUnderTest.getBeaconData();
		// Sort the results so we know the indices to expect
		Collections.sort(result, new Comparator<Beacon>() {
			@Override
			public int compare (Beacon o1, Beacon o2) {
				return Integer.compare(o1.getBeaconIdentifier(), o2.getBeaconIdentifier());
			}
		});
		assertEquals("Wrong number of beacon results returned", 2, result.size());

		assertEquals("Wrong beacon identifier for the beacon on channel 1", 1, result.get(0).getBeaconIdentifier());
		assertEquals("Lower bound of first sample was incorrect", 0.0, result.get(0).getDistanceLowerBound(), 0.0);

		assertEquals("Wrong beacon identifier for the beacon on channel 2", 2, result.get(1).getBeaconIdentifier());
		assertEquals("Lower bound of second sample was incorrect", 12.0, result.get(1).getDistanceLowerBound(), 2.0);
	}

	@Test
	public void testSparseBeacons() {
		float[] sample = {0, 1, 0, Float.POSITIVE_INFINITY, 0, 2};
		mockSensorMode.setMockSample(sample);

		List<Beacon> result = classUnderTest.getBeaconData();
		// Sort the results so we know which indices to expect the data
		Collections.sort(result, new Comparator<Beacon>() {
			@Override
			public int compare (Beacon o1, Beacon o2) {
				return Integer.compare(o1.getBeaconIdentifier(), o2.getBeaconIdentifier());
			}
		});
		assertEquals("Wrong number of beacon results returned", 2, result.size());

		assertEquals("Wrong beacon identifier for the beacon on channel 1", 1, result.get(0).getBeaconIdentifier());
		assertEquals("Lower bound of first sample was incorrect", 0.0, result.get(0).getDistanceLowerBound(), 0.0);

		assertEquals("Wrong beacon identifier for the beacon on channel 3", 3, result.get(1).getBeaconIdentifier());
		assertEquals("Lower bound of second sample was incorrect", 12.0, result.get(1).getDistanceLowerBound(), 2.0);
	}

	@Test
	public void testAngle0() {
		float[] sample = {0, 1};
		mockSensorMode.setMockSample(sample);

		List<Beacon> result = classUnderTest.getBeaconData();
		assertEquals("Wrong angle returned", 0.0, result.get(0).getAngle(), 0.01);
	}

	@Test
	public void testAngle15deg() {
		// 15 deg = pi/12 rad
		float[] sample = {15.0f, 1};
		mockSensorMode.setMockSample(sample);

		List<Beacon> result = classUnderTest.getBeaconData();
		assertEquals("Wrong angle returned", Math.PI / 12, result.get(0).getAngle(), 0.02);
	}

	@Test
	public void testNegativeAngle() {
		float[] sample = {-15.0f, 1};
		mockSensorMode.setMockSample(sample);

		List<Beacon> result = classUnderTest.getBeaconData();
		assertEquals("Wrong angle returned", -Math.PI / 12, result.get(0).getAngle(), 0.02);
	}


	/**
	 * A mock version of SensorMode, where the value provided by fetchSample can be preset.
	 *
	 * The mockito library doesn't appear to provide this functionality because the method mutates its argument rather
	 * than returning a value.
	 */
	private class MockSensorMode implements SensorMode {
		private float[] mockSample = new float[0];

		/**
		 * return a string description of this sensor mode
		 *
		 * @return The description/name of this mode
		 */
		@Override
		public String getName () {
			return "Seek";
		}

		/**
		 * Returns the number of elements in a sample.<br>
		 * The number of elements does not change during runtime.
		 *
		 * @return the number of elements in a sample
		 */
		@Override
		public int sampleSize () {
			return 8;
		}

		/**
		 * Fetches a sample from a sensor or filter.
		 *
		 * @param sample The array to store the sample in.
		 * @param offset
		 */
		@Override
		public void fetchSample (float[] sample, int offset) {
			for (int i = 0; i < sample.length - offset; i++) {
				if (i < mockSample.length) {
					sample[i + offset] = mockSample[i];
				} else {
					sample[i + offset] = (i % 2 == 0) ? 0 : Float.POSITIVE_INFINITY;
				}
			}
		}

		/**
		 * Set the sample that should be returned
		 *
		 * @param sample The sample to use for future calls
		 */
		public void setMockSample(float[] sample) {
			this.mockSample = sample;
		}
	}
}
