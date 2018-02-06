package uk.ac.cam.cl.group_project.delta.simulation;

import java.lang.Exception;

/**
 * Represents a double precision floating-point matrix of arbitary dimensions.
 */
class Matrix {

	/**
	 * Number of rows in this matrix. A non-negative value.
	 */
	private final int rows;

	/**
	 * Number of columns in this matrix. A non-negative value.
	 */
	private final int cols;

	/**
	 * Element data for this matrix.
	 */
	private double[] data;

	/**
	 * Initialise a matrix with given dimensions.
	 * @param rows    Number of matrix rows.
	 * @param cols    Number of matrix columns.
	 */
	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.data = new double[rows * cols];
	}

	/**
	 * Get the number of rows in this matrix.
	 * @return    Number of rows.
	 */
	public int getRows() {
		return this.rows;
	}

	/**
	 * Get the number of columns in this matrix.
	 * @return    Number of columns.
	 */
	public int getCols() {
		return this.cols;
	}

	/**
	 * Fetch element (i, j) from the matrix.
	 * @param i       Zero-indexed row position.
	 * @param j       Zero-indexed column position.
	 * @return        The reference held in position (i, j).
	 */
	public double get(int i, int j) {
		return this.data[i * cols + j];
	}

	/**
	 * Set element (i, j) in the matrix.
	 * @param i       Zero-indexed row position.
	 * @param j       Zero-indexed col position.
	 * @param val     Value to set the element to.
	 */
	public void set(int i, int j, double val) {
		this.data[i * cols + j] = val;
	}

	/**
	 * Sum this matrix with another, giving a new matrix as a result.
	 * @param m    Other matrix.
	 * @return     Reference to a new matrix that holds the result.
	 */
	public Matrix add(Matrix m) throws MatrixException {
		if (m.getRows() != this.getRows() || m.getCols() != this.getCols()) {
			throw new MatrixException("Incompatible matricies");
		}
		Matrix result = new Matrix(this.getRows(), this.getCols());
		for (int i = 0; i < this.getRows(); ++i) {
			for (int j = 0; j < this.getCols(); ++j) {
				result.set(i, j, this.get(i, j) + m.get(i, j));
			}
		}
		return result;
	}

	// TODO: multiply, subtract, negate, dot, ...

	/**
	 * Generic exception for matrix issues.
	 */
	public static class MatrixException extends Exception {

		/**
		 * Initialise exception.
		 * @param s    String message for this exception.
		 */
		public MatrixException(String s) {
			super(s);
		}

	}

}
