package uk.ac.cam.cl.group_project.delta.simulation;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for Matrix, Vector, and Vector2D
 */
public class MatrixTest {

	@Test
	public void additionTest() throws Matrix.MatrixException {
		Matrix a = new Matrix(3, 3);
		a.set(0, 0, 1);
		a.set(0, 1, 2);
		a.set(0, 2, 3);
		a.set(1, 0, 4);
		a.set(1, 1, 5);
		a.set(1, 2, 6);
		a.set(2, 0, 7);
		a.set(2, 1, 8);
		a.set(2, 2, 9);

		Matrix b = new Matrix(3, 3);
		b.set(0, 0, 9);
		b.set(0, 1, 8);
		b.set(0, 2, 7);
		b.set(1, 0, 6);
		b.set(1, 1, 5);
		b.set(1, 2, 4);
		b.set(2, 0, 3);
		b.set(2, 1, 2);
		b.set(2, 2, 1);

		Matrix c = a.add(b);
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				assertTrue("Incorrect matrix addition", c.get(i, j) == 10);
			}
		}

	}

}
