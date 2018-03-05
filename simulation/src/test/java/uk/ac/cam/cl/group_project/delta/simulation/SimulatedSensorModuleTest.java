package uk.ac.cam.cl.group_project.delta.simulation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimulatedSensorModuleTest {
	private World world;
	private SimulatedSensorModule classUnderTest;
	private double carLength;

	@Before
	public void setup() {
		world = new World();
		PhysicsCar car = new PhysicsCar(1);
		carLength = car.getLength();
		world.getBodies().add(car);
		classUnderTest = new SimulatedSensorModule(car, world);
	}

	private void addCar(Vector2D position) {
		PhysicsCar car = new PhysicsCar(1);
		car.setPosition(position);
		world.getBodies().add(car);
	}

	@Test
	public void testFrontProximityWhenAlone() {
		assertEquals(Double.POSITIVE_INFINITY, classUnderTest.getFrontProximity(), 0);
	}

	@Test
	public void testFrontProximityWhenStraightAhead() {
		addCar(new Vector2D(0, 3));

		// The distance from the front of this car to the back of the other should be the 3 (the
		// distance between their centres) - 2 * 1/2 * carLength
		assertEquals(3.0 - carLength, classUnderTest.getFrontProximity(), 0.02);
	}

	@Test
	public void testFrontProximityWhenOffsetSlightly() {
		addCar(new Vector2D(0.5, 3));

		double yDistance = 3 - carLength / 2;
		double xDistance = 0.5;
		assertEquals(Math.sqrt(xDistance*xDistance + yDistance*yDistance) - carLength / 2, classUnderTest.getFrontProximity(), 0.05);
	}

	@Test
	public void testFrontProximityWhen45Deg() {
		addCar(new Vector2D(2, 2));
		assertEquals(Double.POSITIVE_INFINITY, classUnderTest.getFrontProximity(), 0);
	}

	@Test
	public void testFrontProximityWhenOffsetNegative() {
		addCar(new Vector2D(-0.5, 3));

		double yDistance = 3 - carLength / 2;
		double xDistance = -0.5;
		assertEquals(Math.sqrt(xDistance*xDistance + yDistance*yDistance) - carLength / 2, classUnderTest.getFrontProximity(), 0.05);
	}

	@Test
	public void testFrontProximityWhenNeg45Deg() {
		addCar(new Vector2D(-2, 2));
		assertEquals(Double.POSITIVE_INFINITY, classUnderTest.getFrontProximity(), 0);
	}

	@Test
	public void testFrontProximityWhenBehind() {
		addCar(new Vector2D(0, -2));
		assertEquals(Double.POSITIVE_INFINITY, classUnderTest.getFrontProximity(), 0);
	}
}
