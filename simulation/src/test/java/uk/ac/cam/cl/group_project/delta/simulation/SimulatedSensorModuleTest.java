package uk.ac.cam.cl.group_project.delta.simulation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimulatedSensorModuleTest {
	private World world;
	private SimulatedSensorModule classUnderTest;

	@Before
	public void setup() {
		world = new World();
		PhysicsCar car = new PhysicsCar(1);
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
		addCar(new Vector2D(0, 2));

		assertEquals(2.0, classUnderTest.getFrontProximity(), 0.1);
	}

	@Test
	public void testFrontProximityWhenOffsetSlightly() {
		addCar(new Vector2D(0.5, 2));

		assertEquals(Math.sqrt(0.5*0.5 + 2*2), classUnderTest.getFrontProximity(), 0.05);
	}

	@Test
	public void testFrontProximityWhen45Deg() {
		addCar(new Vector2D(2, 2));
		assertEquals(Double.POSITIVE_INFINITY, classUnderTest.getFrontProximity(), 0);
	}

	@Test
	public void testFrontProximityWhenOffsetNegative() {
		addCar(new Vector2D(-0.5, 2));

		assertEquals(Math.sqrt(0.5*0.5 + 2*2), classUnderTest.getFrontProximity(), 0.05);
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
