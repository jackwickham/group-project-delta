package uk.ac.cam.cl.group_project.delta.simulation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PhysicsCarTest {
	PhysicsCar classUnderTest;

	@Before
	public void makeCarDriveForwards() {
		classUnderTest = new PhysicsCar(0.15);
		classUnderTest.setAcceleration(1);
		classUnderTest.update(1.0);
		// velocity should now be 1ms^-1
	}

	@Test
	public void testSetTurnRateZero() {
		classUnderTest.setTurnRate(0);
		assertEquals(0.0, classUnderTest.getWheelAngle(), 0.01);
	}

	@Test
	public void testSetTurnRatePositive() {
		classUnderTest.setTurnRate(0.1);
		// pi/2 - atan((1/0.1) / 0.15) = 0.015
		assertEquals(0.015, classUnderTest.getWheelAngle(), 0.005);
	}

	@Test
	public void testSetTurnRateNegative() {
		classUnderTest.setTurnRate(-0.1);
		assertEquals(-0.015, classUnderTest.getWheelAngle(), 0.005);
	}

	@Test
	public void testSetTurnRateLargePositive() {
		classUnderTest.setTurnRate(100);
		assertEquals(Math.PI / 4, classUnderTest.getWheelAngle(), 0.02);
	}

	@Test
	public void testSetTurnRateLargeNegative() {
		classUnderTest.setTurnRate(-100);
		assertEquals(-Math.PI / 4, classUnderTest.getWheelAngle(), 0.02);
	}
}
