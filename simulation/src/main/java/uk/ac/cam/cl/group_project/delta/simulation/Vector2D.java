package uk.ac.cam.cl.group_project.delta.simulation;

/**
 * Represents a vector of two components.
 */
public class Vector2D {

	/**
	 * First vector component.
	 */
	private double x;

	/**
	 * Second vector component;
	 */
	private double y;

	/**
	 * Initialise a zero-vector.
	 */
	public Vector2D() {
		this.x = 0;
		this.y = 0;
	}

	/**
	 * Initialise vector to given values.
	 * @param x    First component.
	 * @param y    Second component.
	 */
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Fetch x component.
	 * @return    First vector component.
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * Fetch y component.
	 * @return    Second vector component.
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * Set the x component.
	 * @param x    Value to set to.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Set the y component.
	 * @param y    Value to set to.
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Sum the two vectors, piecewise.
	 * @param a    Left-hand side vector.
	 * @param b    Right-hand side vector.
	 * @return     2D vector with components equal to the sum of the inputs.
	 */
	public static Vector2D add(Vector2D a, Vector2D b) {
		return new Vector2D(
			a.getX() + b.getX(),
			a.getY() + b.getY()
		);
	}

	/**
	 * Add this vector to another returning a new vector as the result.
	 * @param other    Vector to add.
	 * @return         Sum of this vector and the other.
	 */
	public Vector2D add(Vector2D other) {
		return Vector2D.add(this, other);
	}

	/**
	 * Subtract the right-hand vector from the left, piecewise.
	 * @param a    Left-hand side vector.
	 * @param b    Right-hand side vector.
	 * @return     Vector result.
	 */
	public static Vector2D subtract(Vector2D a, Vector2D b) {
		return new Vector2D(
			a.getX() - b.getX(),
			a.getY() - b.getY()
		);
	}

	/**
	 * Subtract from this vector another, returning a new vector as the result.
	 * @param other    Vector to subtract.
	 * @return         Result vector.
	 */
	public Vector2D subtract(Vector2D other) {
		return Vector2D.subtract(this, other);
	}

	/**
	 * Multiply both vector components by given scale to produce new vector.
	 * @param vec      Vector to scale.
	 * @param scale    Scale factor.
	 * @return         Resulting vector.
	 */
	public static Vector2D multiply(Vector2D vec, double scale) {
		return new Vector2D(
			vec.getX() * scale,
			vec.getY() * scale
		);
	}

	/**
	 * Multiply both vector components by given scale to produce new vector.
	 * @param scale    Scale factor.
	 * @return         Scaled vector.
	 */
	public Vector2D multiply(double scale) {
		return Vector2D.multiply(this, scale);
	}

	/**
	 * Computes the dot product of the input vectors, that is for inputs
	 * (x1, y1) and (x2, y2) the output is (x1 * x2 + y1 * y2).
	 * @param a    Left-hand side vector.
	 * @param b    Right-hand side vector.
	 * @return     Dot product result.
	 */
	public static double dot(Vector2D a, Vector2D b) {
		return a.getX() * b.getX() + a.getY() * b.getY();
	}

	/**
	 * Computes the dot product of the input vectors, that is for inputs
	 * (x1, y1) and (x2, y2) the output is (x1 * x2 + y1 * y2).
	 * @param other    Vector to dot with.
	 * @return         Dot product result.
	 */
	public double dot(Vector2D other) {
		return Vector2D.dot(this, other);
	}

	/**
	 * Computes a normal vector to the provided argument. In this case, the
	 * vector returned has undergone a 90 degree clockwise rotation in the
	 * XY-plane.
	 * @param vec    Initial vector.
	 * @return       Orthogonal vector.
	 */
	public static Vector2D normal(Vector2D vec) {
		return new Vector2D(vec.getY(), -vec.getX());
	}

	/**
	 * Computes a normal vector to this vector. In this case, the vector
	 * returned has undergone a 90 degree clockwise rotation in the XY-plane.
	 * @return    Orthogonal vector.
	 */
	public Vector2D normal() {
		return Vector2D.normal(this);
	}

	/**
	 * Computes magnitude length of given vector.
	 * @param vec    Vector.
	 * @return       Vector's length.
	 */
	public static double magnitude(Vector2D vec) {
		return Math.sqrt(dot(vec, vec));
	}

	/**
	 * Computes magnitude length of this vector.
	 * @return    This vector's length.
	 */
	public double magnitude() {
		return Vector2D.magnitude(this);
	}

}
