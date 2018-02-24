package uk.ac.cam.cl.group_project.delta.simulation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.cam.cl.group_project.delta.Beacon;

import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * Test the FaultySensorModule's failure injection
 *
 * @author Jack Wickham
 */
public class FaultySensorModuleTest {
	private PhysicsCar mainCar;
	private World world;
	private FaultySensorModule classUnderTest;
	private SimulatedSensorModule realSensorModule;

	@Before
	public void createMocks() {
		mainCar = new PhysicsCar(0.15);
		world = new World();
		world.getBodies().add(mainCar);
		classUnderTest = new FaultySensorModule(mainCar, world);
		realSensorModule = new SimulatedSensorModule(mainCar, world);
	}

	@After
	public void resetSensorModuleFailureInjection() {
		FaultySensorModule.setAccelerationStdDev(0);
		FaultySensorModule.setBeaconAngleStdDev(0);
		FaultySensorModule.setBeaconDistanceStdDev(0);
		FaultySensorModule.setBeaconsEmulateMindstorms(false);
		FaultySensorModule.setFrontProximityEnabled(true);
		FaultySensorModule.setFrontProximityFailureRate(0);
		FaultySensorModule.setFrontProximityStdDev(0);
		FaultySensorModule.setSpeedStdDev(0);
		FaultySensorModule.setTurnRateStdDev(0);
	}

	private void addOtherCar() {
		PhysicsCar otherCar = new PhysicsCar(0.15);
		otherCar.setPosition(new Vector2D(0, 0.3));
		world.getBodies().add(otherCar);
	}


	//#region Front proximity tests

	@Test
	public void testFrontProximityGivesCorrectValueWithoutFailuresInjected() {
		addOtherCar();

		assertNotNull(classUnderTest.getFrontProximity());
		assertEquals(realSensorModule.getFrontProximity(), classUnderTest.getFrontProximity());
	}

	@Test
	public void testFrontProximityGivesDistributedValues() {
		addOtherCar();

		FaultySensorModule.setFrontProximityStdDev(0.07);

		double expectedTrueValue = realSensorModule.getFrontProximity();
		int matchTrueValueCount = 0;
		double differenceFromTrueValue = 0.0;

		for (int i = 0; i < 100; i++) {
			Double value = classUnderTest.getFrontProximity();
			assertNotNull(value);
			double val = value;
			if (val == expectedTrueValue) {
				matchTrueValueCount++;
			} else {
				differenceFromTrueValue += (val - expectedTrueValue);
			}

			assertThat("Sensor gave a value < 0", val, greaterThanOrEqualTo(0.0));
		}

		assertThat("Too many faulty readings matched the true value", matchTrueValueCount, lessThan(5));
		assertThat("Cumulative difference from the true value was out of range", differenceFromTrueValue, allOf(greaterThan(-4.0), lessThan(4.0)));
	}

	@Test
	public void testFrontProximityReturnsNullOnDisabled() {
		FaultySensorModule.setFrontProximityEnabled(false);
		assertNull(classUnderTest.getFrontProximity());
	}

	@Test
	public void testFrontProximityReturnsInfinityOnNoReading() {
		addOtherCar();

		FaultySensorModule.setFrontProximityFailureRate(0.5);
		int infinityCount = 0;
		for (int i = 0; i < 200; i++) {
			if (classUnderTest.getFrontProximity() == Double.POSITIVE_INFINITY) {
				infinityCount++;
			}
		}
		assertThat("Infinities returned were out of range", infinityCount, allOf(greaterThanOrEqualTo(75), lessThanOrEqualTo(125)));
	}

	//#endregion
	//#region Beacon tests

	@Test
	public void testBeaconsGiveCorrectValuesWithoutFailuresInjected() {
		addOtherCar();

		List<Beacon> expectedBeacons = realSensorModule.getBeacons();
		List<Beacon> actualBeacons = classUnderTest.getBeacons();
		assertEquals("Different number of beacons returned", expectedBeacons.size(), actualBeacons.size());
		for (int i = 0; i < expectedBeacons.size(); i++) {
			Beacon expected = expectedBeacons.get(i);
			Beacon actual = actualBeacons.get(i);
			assertEquals(expected.getBeaconIdentifier(), actual.getBeaconIdentifier());
			assertEquals(expected.getDistanceLowerBound(), actual.getDistanceLowerBound(), 0.0);
			assertEquals(expected.getDistanceUpperBound(), actual.getDistanceUpperBound(), 0.0);
			assertEquals(expected.getAngle(), actual.getAngle(), 0.0);
		}
	}

	@Test
	public void testBeaconsGiveDistributedValues() {
		addOtherCar();

		FaultySensorModule.setBeaconDistanceStdDev(0.07);

		List<Beacon> expectedTrueValues = realSensorModule.getBeacons();
		int matchTrueValueCount = 0;
		double differenceFromTrueValue = 0.0;

		for (int i = 0; i < 100; i++) {
			List<Beacon> actualValues = classUnderTest.getBeacons();
			assertEquals("Different number of beacons returned", expectedTrueValues.size(), actualValues.size());
			for (int j = 0; j < expectedTrueValues.size(); j++) {
				Beacon expected = expectedTrueValues.get(j);
				Beacon actual = actualValues.get(j);
				assertEquals("Upper and lower bound were different", expected.getDistanceUpperBound(), expected.getDistanceLowerBound(), 0.0);
				double diff = actual.getDistanceUpperBound() - expected.getDistanceUpperBound();
				if (diff == 0.0) {
					matchTrueValueCount++;
				} else {
					differenceFromTrueValue += diff;
				}

				assertThat("Sensor gave a value < 0", actual.getDistanceLowerBound(), greaterThanOrEqualTo(0.0));
				assertEquals("Angle was changed when it shouldn't have been", expected.getAngle(), actual.getAngle(), 0.0);
			}
		}

		assertThat("Too many faulty readings matched the true value", matchTrueValueCount, lessThan(7));
		assertThat("Cumulative difference from the true value was out of range", differenceFromTrueValue, allOf(greaterThan(-4.0), lessThan(4.0)));
	}

	@Test
	public void testBeaconAnglesDistributed() {
		addOtherCar();

		FaultySensorModule.setBeaconAngleStdDev(0.07);

		List<Beacon> expectedTrueValues = realSensorModule.getBeacons();
		int matchTrueValueCount = 0;
		double differenceFromTrueValue = 0.0;

		for (int i = 0; i < 100; i++) {
			List<Beacon> actualValues = classUnderTest.getBeacons();
			assertEquals("Different number of beacons returned", expectedTrueValues.size(), actualValues.size());
			for (int j = 0; j < expectedTrueValues.size(); j++) {
				Beacon expected = expectedTrueValues.get(j);
				Beacon actual = actualValues.get(j);
				double diff = actual.getAngle() - expected.getAngle();
				if (diff == 0.0) {
					matchTrueValueCount++;
				} else {
					differenceFromTrueValue += diff;
				}

				assertEquals("Distance was changed when it shouldn't have been", expected.getDistanceUpperBound(), actual.getDistanceUpperBound(), 0.0);
			}
		}

		assertThat("Too many faulty readings matched the true value", matchTrueValueCount, lessThan(7));
		assertThat("Cumulative difference from the true value was out of range", differenceFromTrueValue, allOf(greaterThan(-4.0), lessThan(4.0)));
	}

	//#endregion
}
