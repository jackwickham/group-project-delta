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

	@Test
	public void testTurnRateDoesntAffectHeadingImmediately() {
		classUnderTest.setTurnRate(1);
		assertEquals(0.0, classUnderTest.getHeading(), 0.0);
	}

	@Test
	public void testHeadingCorrectlyUpdatedAfterTurn() {
		classUnderTest.setTurnRate(Math.PI / 4);
		for (int i = 0; i < 10; i++) {
			classUnderTest.update(0.1);
		}
		// We should now have turned 45° right
		assertEquals(Math.PI / 4, classUnderTest.getHeading(), 0.05);
	}

	@Test
	public void testHeadingCorrectlyUpdatedAfterNegativeTurn() {
		classUnderTest.setTurnRate(-Math.PI / 4);

		for (int i = 0; i < 10; i++) {
			classUnderTest.update(0.1);
		}
		// We should now have turned 45° left
		assertEquals(-Math.PI / 4, classUnderTest.getHeading(), 0.05);
	}

	@Test
	public void testGetHeadingVectorCorrect() {
		classUnderTest.setTurnRate(Math.PI / 6);

		for (int i = 0; i < 10; i++) {
			classUnderTest.update(0.1);
		}
		// We should now have turned 30° right
		Vector2D headingVector = classUnderTest.getHeadingVector();
		assertEquals(0.5, headingVector.getX(), 0.05);
		assertEquals(Math.sqrt(3) / 2.0, headingVector.getY(), 0.05);
	}

	@Test
	public void testGetNegativeHeadingVectorCorrect() {
		classUnderTest.setTurnRate(-Math.PI / 6);

		for (int i = 0; i < 10; i++) {
			classUnderTest.update(0.1);
		}
		// We should now have turned 30° left
		Vector2D headingVector = classUnderTest.getHeadingVector();
		assertEquals(-0.5, headingVector.getX(), 0.05);
		assertEquals(Math.sqrt(3) / 2.0, headingVector.getY(), 0.05);
	}
}
