package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.Beacon;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of sensor interface for simulated vehicles.
 */
public class SimulatedSensorModule implements SensorInterface {

	/**
	 * The world that we instrument.
	 */
	private World world;

	/**
	 * A physics body that represents the position from which we instrument.
	 */
	private PhysicsCar car;

	/**
	 * Maximum angle from view normal that the sensor can detect, in radians.
	 */
	public static final double VIEW_HALF_ANGLE = Math.PI / 4; // 45°

	/**
	 * Constructs a sensor module for given car in provided world.
	 * @param world    World to instrument.
	 * @param car      Physical body to instrument about.
	 */
	public SimulatedSensorModule(PhysicsCar car, World world) {
		this.car = car;
		this.world = world;
	}

	/**
	 * Returns a floating point value representing the distance
	 * to the nearest object. The value returned will likely be
	 * approximate. This method may not be implemented, depending
	 * on the hardware available, and will return null in this case.
	 *
	 * @return the distance in m or null if there is no hardware support
	 */
	public Double getFrontProximity() {
		List<PhysicsBody> bodies = world.getBodies();

		Vector2D vecHeading = car.getHeadingVector();

		double distance = Double.POSITIVE_INFINITY;

		for (PhysicsBody body : bodies) {
			if (body != car) {
				Vector2D ray = body.getPosition().subtract(car.getSensorPosition()).normalise();
				Vector2D collisionLocation = body.getRayCollisionPosition(ray);
				Vector2D relPos = collisionLocation.subtract(car.getSensorPosition());
				double relDistance = relPos.magnitude();
				double angle = vecHeading.angleTo(relPos);

				if (Math.abs(angle) < VIEW_HALF_ANGLE) {
					distance = Math.min(distance, relDistance);
				}
			}
		}

		return distance;

	}

	/**
	 * Returns a list of objects that represent the visible beacons
	 * and their positions relative to this vehicle. Beacons are installed
	 * in all platooning vehicles, but are not exclusive to these vehicles.
	 * Position accuracy may degrade with distance and have significant noise.
	 *
	 * @return list containing Beacons visible
	 */
	public List<Beacon> getBeacons() {

		List<Beacon> beacons = new ArrayList<>();
		List<PhysicsBody> bodies = world.getBodies();

		Vector2D vecHeading = car.getHeadingVector();

		for (PhysicsBody body : bodies) {
			if (body != car && body instanceof SimulatedCar) {
				SimulatedCar otherCar = (SimulatedCar) body;
				Vector2D relPos = otherCar.getBeaconPosition().subtract(car.getSensorPosition());
				double relDistance = relPos.magnitude();
				double angle = vecHeading.angleTo(relPos);

				if (Math.abs(angle) < VIEW_HALF_ANGLE) {
					beacons.add(new Beacon(
						otherCar.getUuid(),
						relDistance,
						relDistance,
						angle
					));
				}
			}
		}

		return beacons;

	}

	/**
	 * Returns the current acceleration of the vehicle.
	 * @return acceleration in m/s^2
	 */
	public double getAcceleration() {
		return car.getAcceleration();
	}

	/**
	 * Returns the current speed.
	 * @return speed in m/s
	 */
	public double getSpeed() {
		return car.getSpeed();
	}

	/**
	 * Returns the current turn rate.
	 * @return turn rate in rad/s
	 */
	public double getTurnRate() {
		return car.getTurnRate();
	}

	/**
	 * Returns the current position of the vehicle in some standard
	 * coordinate system.
	 * @returns current position 2D vector
	 */
	// public Vector getAbsolutePosition()

}
