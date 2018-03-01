package uk.ac.cam.cl.group_project.delta.simulation;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the 2D vector class.
 */
public class Vector2DTest {

	@Test
	public void initialisationTest() {

		Vector2D vecA = new Vector2D();
		assertTrue(
			"Default vector initialiser failed",
			vecA.getX() == 0.0 && vecA.getY() == 0.0
		);

		Vector2D vecB = new Vector2D(3, 1);
		assertTrue(
			"Vector initialiser failed",
			vecB.getX() == 3.0 && vecB.getY() == 1.0
		);

	}

	@Test
	public void additionTest() {

		Vector2D a = new Vector2D(5, 2);
		Vector2D b = new Vector2D(10, -4);

		Vector2D c = a.add(b);
		assertEquals(c.getX(), 15.0, 0.0);
		assertEquals(c.getY(), -2.0, 0.0);

		Vector2D d = Vector2D.add(a, b);
		assertEquals(d.getX(), 15.0, 0.0);
		assertEquals(d.getY(), -2.0, 0.0);

	}

	@Test
	public void multiplicationTest() {

		Vector2D vec = new Vector2D(1.2, 2.5);
		Vector2D res = vec.multiply(201.3);

		assertEquals(res.getX(), 1.2 * 201.3, 0.0);
		assertEquals(res.getY(), 2.5 * 201.3, 0.0);

	}

	@Test
	public void dotTest() {

		Vector2D a = new Vector2D(6.0, 10.1);
		Vector2D b = new Vector2D(-1.0, 0.8);

		assertEquals(a.dot(b), (6.0 * -1.0) + (10.1 * 0.8), 0.0);

	}

	@Test
	public void magnitudeTest() {

		Vector2D vec = new Vector2D(-35.2, 23.1);
		double mag = Math.sqrt((-35.2)*(-35.2) + 23.1*23.1);
		assertEquals(vec.magnitude(), mag, 1e-10);

	}

	@Test
	public void angleTest() {

		// dot(a, b) = |a|*|b|*cos(theta)

		Vector2D a = new Vector2D(2, 0);
		Vector2D b = new Vector2D(-1, 1);

		double dotP = a.magnitude() * b.magnitude() * Math.cos(3 * Math.PI / 4);
		assertEquals(a.dot(b), dotP, 1e-10);

	}

	@Test
	public void normalTest() {

		Vector2D a = new Vector2D(13.3, -12.2);
		Vector2D n = a.normal();

		assertEquals(a.dot(n), 0.0, 1e-10);

	}

	@Test
	public void leftOfTestTrue() {
		Vector2D a = new Vector2D(0, 1);
		Vector2D b = new Vector2D(1, 0);

		assertEquals(true, a.leftOf(b));
	}

	@Test
	public void leftOfTestFalse() {
		Vector2D a = new Vector2D(0, 1);
		Vector2D b = new Vector2D(-0.1, -1);

		assertEquals(false, a.leftOf(b));
	}

	@Test
	public void leftOfTestSame() {
		Vector2D a = new Vector2D(0, 1);

		assertEquals(false, a.leftOf(a));
	}

	@Test
	public void angleToTestSame() {
		Vector2D a = new Vector2D(0, 1);

		assertEquals(0.0, a.angleTo(a), 0.005);
	}

	@Test
	public void angleToTestPositive() {
		Vector2D a = new Vector2D(0, 1);
		Vector2D b = new Vector2D(1, 1);
		assertEquals(Math.PI / 4, a.angleTo(b), 0.01);
	}

	@Test
	public void angleToTestNegative() {
		Vector2D a = new Vector2D(0, 1);
		Vector2D b = new Vector2D(-1, 1);
		assertEquals(-Math.PI / 4, a.angleTo(b), 0.01);
	}

	@Test
	public void angleToTestLarge() {
		Vector2D a = new Vector2D(0, 1);
		Vector2D b = new Vector2D(1, -1);
		assertEquals(3 * Math.PI / 4, a.angleTo(b), 0.01);
	}

}
