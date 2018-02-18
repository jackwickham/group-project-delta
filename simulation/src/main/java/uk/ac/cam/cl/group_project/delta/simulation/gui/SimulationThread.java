package uk.ac.cam.cl.group_project.delta.simulation.gui;

import uk.ac.cam.cl.group_project.delta.simulation.PhysicsBody;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedNetwork;
import uk.ac.cam.cl.group_project.delta.simulation.World;

/**
 * Encapsulates simulation running in separate thread.
 */
public class SimulationThread extends Thread {

	/**
	 * Simulated world.
	 */
	private World world;

	/**
	 * Simulated network.
	 */
	private SimulatedNetwork network;

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
	}

	/**
	 * Run this thread until `running` is set to `false`; that is, update the
	 * world as quickly as possible in real-time.
	 */
	@Override
	public void run() {

		long start = System.nanoTime();
		long time = start;

		running = true;

		while (true) {
			synchronized (this) {

				// To prevent concurrency issues, this is in here
				if (running) {
					break;
				}

				// Update world
				long tmp = System.nanoTime();
				world.update((tmp - time) / 1e9);
				time = tmp;

			}
		}

	}

	/**
	 * Add a physics body to the simulated world.
	 * @param body    Body to add.
	 */
	public synchronized void add(PhysicsBody body) {
		world.getBodies().add(body);
	}

	/**
	 * Create a {@link SimulatedCar} within this simulated world.
	 * @param wheelBase    Distance from front- to rear-axle.
	 * @return             The car created.
	 */
	public SimulatedCar createCar(double wheelBase) {
		SimulatedCar car = new SimulatedCar(wheelBase, world, network);
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
