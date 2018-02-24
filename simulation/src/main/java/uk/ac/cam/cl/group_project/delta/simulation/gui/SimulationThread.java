package uk.ac.cam.cl.group_project.delta.simulation.gui;

import uk.ac.cam.cl.group_project.delta.simulation.PhysicsBody;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedNetwork;
import uk.ac.cam.cl.group_project.delta.simulation.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates simulation running in separate thread.
 */
public class SimulationThread extends Thread {

	/**
	 * Simulated world.
	 */
	private final World world;

	/**
	 * Simulated network.
	 */
	private final SimulatedNetwork network;

	/**
	 * Whether this thread should be running.
	 */
	private boolean running;

	/**
	 * Construct thread, and the world and network it will simulate.
	 */
	public SimulationThread() {
		this.world = new World();
		this.network = new SimulatedNetwork();
		running = false;
		this.setDaemon(true);
	}

	/**
	 * Run this thread until `running` is set to `false`; that is, update the
	 * world as quickly as possible in real-time.
	 */
	@Override
	public void run() {

		long start = System.nanoTime();
		long time = start;

		synchronized (this) {
			running = true;
		}

		// For thread safety
		boolean localRunning = true;

		while (localRunning) {
			synchronized (this) {
				localRunning = running;
			}

			long tmp = System.nanoTime();

			// Fetch bodies from world
			List<PhysicsBody> bodies;
			synchronized (world) {
				bodies = new ArrayList<>(world.getBodies());
			}

			// Update world
			for (PhysicsBody body : bodies) {
				synchronized (body) {
					body.update((tmp - time) / 1e9);
				}
			}

			time = tmp;

		}

	}

	/**
	 * Add a physics body to the simulated world.
	 * @param body    Body to add.
	 */
	public void add(PhysicsBody body) {
		synchronized (world) {
			world.getBodies().add(body);
		}
	}

	/**
	 * Create a {@link SimulatedCar} within this simulated world.
	 * @param wheelBase    Distance from front- to rear-axle.
	 * @return             The car created.
	 */
	public SimulatedCar createCar(double wheelBase) {
		SimulatedCar car;
		synchronized (network) { // network.register(...) is called
			car = new SimulatedCar(wheelBase, world, network);
		}
		add(car);
		return car;
	}

	/**
	 * Get the status of this thread.
	 * @return    Returns true if the thread is running.
	 */
	public synchronized boolean isRunning() {
		return running;
	}

	/**
	 * Stop this thread from running. The thread will stop after the next world
	 * update.
	 */
	public synchronized void terminate() {
		running = false;
	}

}
