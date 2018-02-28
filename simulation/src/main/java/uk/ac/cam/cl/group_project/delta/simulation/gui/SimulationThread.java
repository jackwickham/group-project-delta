package uk.ac.cam.cl.group_project.delta.simulation.gui;

import uk.ac.cam.cl.group_project.delta.Log;
import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;
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
	 * Minimum number of real nanoseconds between simulation updates.
	 */
	private static final long UPDATE_INTERVAL = 1000000; // 1ms

	/**
	 * Target number of simulation nanoseconds between algorithm controller
	 * updates.
	 */
	private static final long CONTROLLER_INTERVAL = Algorithm.ALGORITHM_LOOP_DURATION;

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
	 * The scale factor by which to distort time, where 0 represents a paused
	 * timeline and 1 is normal speed.
	 */
	private double timeDilationFactor;

	/**
	 * Cumulative simulation time since start.
	 */
	private long cumulative;

	/**
	 * Last time algorithm instances were updated, with respect to the
	 * cumulative time.
	 */
	private long lastAlgorithmUpdate;

	/**
	 * Construct thread, and the world and network it will simulate.
	 */
	public SimulationThread() {
		this.world = new World();
		this.network = new SimulatedNetwork();
		running = false;
		timeDilationFactor = 1.0;
		this.setDaemon(true);
	}

	/**
	 * Run this thread until `running` is set to `false`; that is, update the
	 * world as quickly as possible in real-time.
	 */
	@Override
	public void run() {

		long time = System.nanoTime();
		cumulative = 0;
		lastAlgorithmUpdate = 0;

		synchronized (this) {
			running = true;
		}

		while (true) {

			synchronized (this) {
				if (!running) {
					break;
				}
			}

			long tmp = System.nanoTime();
			long dt = tmp - time;
			if (dt > UPDATE_INTERVAL) {


				if (getTimeDilationFactor() > 0) {
					update((long) (dt * getTimeDilationFactor()));
				}
				time = tmp;
			}

			try {
				Thread.sleep(
					UPDATE_INTERVAL / 1000000,
					(int)(UPDATE_INTERVAL % 1000000)
				);
			}
			catch (InterruptedException e) {
				// Fired when another thread interrupts this, which is unlikely
				// but may indicate that we should check that the simulation is
				// still running, which we do on the next loop.
			}

		}

	}

	/**
	 * Update the simulation state.
	 * @param dt    True time delta, should have already been warped, in
	 *                 nanoseconds.
	 */
	public synchronized void update(long dt) {

		// Fetch bodies from world
		List<PhysicsBody> bodies;
		synchronized (world) {
			bodies = new ArrayList<>(world.getBodies());
		}

		// Update world
		double d_dt = dt / 1e9;
		for (PhysicsBody body : bodies) {
			synchronized (body) {
				body.update(d_dt);
			}
		}

		// Update cars
		cumulative += dt;
		if (cumulative - lastAlgorithmUpdate > CONTROLLER_INTERVAL) {
			for (PhysicsBody body : bodies) {
				if (body instanceof SimulatedCar) {
					((SimulatedCar) body).updateControl(cumulative);
				}
			}
			if ((cumulative - lastAlgorithmUpdate) / CONTROLLER_INTERVAL > 1) {
				Log.warn("Simulation thread cannot keep algorithms up-to-date");
			}
			lastAlgorithmUpdate = (cumulative / CONTROLLER_INTERVAL) * CONTROLLER_INTERVAL;
		}

	}

	/**
	 * Update the simulation state in increments of UPDATE_INTERVAL, except the
	 * last update which may be a different size if `dt` is not a multiple of
	 * UPDATE_INTERVAL.
	 * @param dt    Simulation delta-t in ns, should have already been warped.
	 */
	public void smoothUpdate(long dt) {
		// TODO: aim for step size uniformity
		for (long i = 0; i <= dt; i += UPDATE_INTERVAL) {
			update(UPDATE_INTERVAL);
		}
		if (dt % UPDATE_INTERVAL != 0) {
			update(dt % UPDATE_INTERVAL);
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
	 * Remove a physics body from the simulated world.
	 */
	public void remove(PhysicsBody body) {
		synchronized (world) {
			world.getBodies().remove(body);
		}
	}

	/**
	 * Create a {@link SimulatedCar} within this simulated world.
	 * @param wheelBase     Distance from front- to rear-axle.
	 * @return              The car created.
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

	/**
	 * Get the current time dilation.
	 * @return    Time distortion scale factor.
	 */
	public synchronized double getTimeDilationFactor() {
		return timeDilationFactor;
	}

	/**
	 * Set the time distortion.
	 * @param timeDilationFactor    Factor by which to distort time.
	 */
	public synchronized void setTimeDilationFactor(double timeDilationFactor) {
		this.timeDilationFactor = timeDilationFactor;
	}

	/**
	 * Get the simulated world.
	 * @return    The physics world.
	 */
	public synchronized World getWorld() {
		return world;
	}

	/**
	 * Get the simulated network.
	 * @return    The virtual network.
	 */
	public synchronized SimulatedNetwork getNetwork() {
		return network;
	}

}
