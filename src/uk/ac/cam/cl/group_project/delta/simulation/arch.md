# Simulation System Architecture

## Mathematics

```
class Matrix<T> {

	const rows, cols
	T[] data

	Matrix(n, m) {
		rows, cols = n, m
		data = new T[rows * cols]
	}

	T get(i, j) {
		return data[i + j * cols]
	}

	void set(i, j, v) {
		data[i + j * cols] = v
	}

	Matrix<T> add(m) {
		assert(m.getRows() == this.getRows() && m.getCols() == this.getCols())
		Matrix<T> res = new Matrix(this.getRows(), this.getCols())
		for i, j in 0..getRows(), 0..getCols() {
			res.set(i, j, this.get(i, j) + m.get(i, j))
		}
	}
	
	// multiply, subtract, negate, dot, cross, ...

}

class Vector<T> extends Matrix<T> {
	Vector(n) {
		super(n, 1)
	}
}

class Vector2D<T> extends Vector<T> {
	Vector2D() {
		super(2)
	}
}

// ...
```

## Physics

```
class PhysicsBody {

	Vector2D position
	Vector2D velocity
	Vector2D acceleration

	void update() {
		position += velocity
		velocity += acceleration
	}

}

class Car
	extends PhysicsBody
	implements NetworkInterface, DriveInterface, SensorInterface
{

	void update() {
		acceleration += friction(this)
		super.update()
	}

}

class Environment {

	List<PhysicsBody> bodies

	void update() {
		foreach body in bodies {
			body.update()
		}
	}

}

```
