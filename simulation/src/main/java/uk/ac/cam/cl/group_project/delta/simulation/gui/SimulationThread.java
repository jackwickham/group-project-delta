package uk.ac.cam.cl.group_project.delta.simulation.gui;

import uk.ac.cam.cl.group_project.delta.Log;
import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;
import uk.ac.cam.cl.group_project.delta.algorithm.ParameterEnum;
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
	private static final long UPDATE_INTERVAL = 1000000; // 0.1 sec

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

		long start = System.nanoTime();
		long time = start;
		long cumulative = 0;

		long lastAlgorithmUpdate = 0; // w.r.t. cumulative

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

			if (tmp - time > UPDATE_INTERVAL) {

				// Fetch bodies from world
				List<PhysicsBody> bodies;
				synchronized (world) {
					bodies = new ArrayList<>(world.getBodies());
				}

				// Update world
				long l_dt = (long) ((tmp - time) * timeDilationFactor);
				double dt = l_dt / 1e9;
				for (PhysicsBody body : bodies) {
					synchronized (body) {
						body.update(dt);
					}
				}

				// Update cars
				cumulative += l_dt;
				if (cumulative - lastAlgorithmUpdate > CONTROLLER_INTERVAL) {
					for (PhysicsBody body : bodies) {
						if (body instanceof SimulatedCar) {
							((SimulatedCar) body).updateControl(cumulative);
						}
					}
					if ((cumulative - lastAlgorithmUpdate) / CONTROLLER_INTERVAL != 1) {
						Log.warn("Simulation thread cannot keep algorithms up-to-date");
					}
					lastAlgorithmUpdate += CONTROLLER_INTERVAL;
				}

				time = tmp;

			}

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
		SimulatedCar car = new SimulatedCar(wheelBase, world, network);
		car.setController(StubAlgorithm.getInstance());
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
	public double getTimeDilationFactor() {
		return timeDilationFactor;
	}

	/**
	 * Set the time distortion.
	 * @param timeDilationFactor    Factor by which to distort time.
	 */
	public void setTimeDilationFactor(double timeDilationFactor) {
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

	/**
	 * A singleton implementation of {@link Algorithm} that does nothing.
	 */
	public static class StubAlgorithm extends Algorithm {

		private static StubAlgorithm instance = new StubAlgorithm();

		private StubAlgorithm() {
			super(null, null, null);
		}

		public static StubAlgorithm getInstance() {
			return instance;
		}

		@Override
		public void setParameter(ParameterEnum parameterEnum, double value) {}

		@Override
		public Double getParameter(ParameterEnum parameterEnum) {
			return null;
		}

		@Override
		public ParameterEnum[] getParameterList() {
			return new ParameterEnum[0];
		}

		@Override
		public void initialise() {}

		@Override
		public void update(long time) {}

		@Override
		public void run() {}

		@Override
		protected void makeDecision() {}
	}

}
