package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Specialisation of a matrix with a single dimension.
 */
class Vector extends Matrix {

	/**
	 * Initialise vector using superclass constructor.
	 * @param n    Number of elements in this vector.
	 */
	public Vector(int n) {
		super(n, 1);
	}

	/**
	 * Wrapper for access to matrix element with single dimension.
	 * @param i    Zero-indexed reference to element.
	 * @return     The value in the element.
	 */
	public double get(int i) {
		return this.get(i, 0);
	}

	/**
	 * Wrapper for setting matrix element with single dimension.
	 * @param i    Zero-indexed reference to element.
	 * @param val  Value to set element to.
	 */
	public void set(int i, double val) {
		this.set(i, 0, val);
	}

}
