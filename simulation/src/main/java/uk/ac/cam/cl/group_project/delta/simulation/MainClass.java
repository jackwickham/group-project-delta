package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;
import uk.ac.cam.cl.group_project.delta.algorithm.CommsInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Communications;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.PlatoonLookup;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class MainClass {

	/**
	 * Number of vehicles to create.
	 */
	private static final int NUMBER_OF_VEHICLES = 10;

	/**
	 * Target time interval between CSV logs of the world state, in nanoseconds.
	 */
	private static final long POSITION_LOG_INTERVAL = 100000000; // 0.1 sec

	public static void main(String[] args) {

		// Instantiate world and virtual network
		World world = new World();
		SimulatedNetwork network = new SimulatedNetwork();

		// Create platoon for cars -- their IDs will be in the order of creation
		List<Integer> platoonOrder = new ArrayList<>(NUMBER_OF_VEHICLES);
		for (int i = 0; i < NUMBER_OF_VEHICLES; ++i) {
			platoonOrder.add(i);
		}

		// Create cars
		for (int i = 0; i < NUMBER_OF_VEHICLES; ++i) {
			SimulatedCar car = new SimulatedCar(world, network);
			world.getBodies().add(car);

			// Initialise Algorithm subsystems
			PlatoonLookup lookup = new PlatoonLookup();
			CommsInterface comms = new Communications(
				new ControlLayer(
					car.getNetworkInterface(),    	// Network interface
					lookup,                       	// Message lookup
					i,                            	// This car's ID
					1,                            	// Platoon ID
					platoonOrder                	// Platoon order [1-N]
				),
				lookup
			);

			if (i == 0) { // Leader behaviour
				car.setController(new LeaderAlgorithm(
					comms,
					car.getDriveInterface(),
					car.getSensorInterface()
				));
				car.setEnginePower(0.05);
			}
			else { // Follower behaviour
				car.setController(
					new Algorithm(
						comms,
						car.getDriveInterface(),
						car.getSensorInterface()
					)
				);
			}

			car.setPosition(new Vector2D(
				i * 10, 0
			));
		}

		long start = System.nanoTime();
		long time = start;

		// Start all the algorithm instances running
		// NB: algorithm instances use the system clock, so we actually need to
		//     run the simulation for the number of seconds we are simulating :(
		for (PhysicsBody body : world.getBodies()) {
			if (body.getClass() == SimulatedCar.class) {
				((SimulatedCar) body).start();
			}
		}

		try {

			// Output to file "sim_<time>.csv"
			BufferedWriter writer = new BufferedWriter(
				new FileWriter(
					"sim_" + (new Date()).getTime() / 1000 + ".csv"
				)
			);

			// Add headers
			writer.write("time,uuid,x,y,class\n");

			// Last time we logged a position
			long lastLog = start - POSITION_LOG_INTERVAL - 1;

			while (time - start < 10 * 1e9) {

				// Fetch new time and update world
				long tmp = System.nanoTime();
				world.update((tmp - time) / 1e9);
				time = tmp;

				// If we should log, do it
				if (time - lastLog > POSITION_LOG_INTERVAL) {

					// Set last log to most recent log-point, so we don't get
					// behind - we're not worried about missing them
					long since = (time - start) % POSITION_LOG_INTERVAL;
					lastLog = time - since;

					// Log the position of all objects
					for (PhysicsBody body : world.getBodies()) {
						Vector2D pos = body.getPosition();
						writer.write(
							(time - start) + ","
								+ body.getUuid() + ","
								+ pos.getX() + ","
								+ pos.getY() + ","
								+ body.getClass().getSimpleName() + "\n"
						);
					}

				}
			}

			// Don't forget to flush
			writer.close();

		}
		catch (IOException e) {
			// Pass.
		}

		for (PhysicsBody body : world.getBodies()) {
			if (body.getClass() == SimulatedCar.class) {
				((SimulatedCar) body).stop();
			}
		}

	}

	/**
	 * A nasty hack which attempts to fool the leader vehicle to continually
	 * broadcast its location and not execute `BasicAlgorithm` logic.
	 */
	private static class LeaderAlgorithm extends Algorithm {

		/**
		 * Initialise algorithm with given interfaces to the vehicle it
		 * controls.
		 * @param commsInterface     Network I/O.
		 * @param driveInterface     Motor and other actionable output.
		 * @param sensorInterface    Sensory input.
		 */
		public LeaderAlgorithm(CommsInterface commsInterface, DriveInterface driveInterface, SensorInterface sensorInterface) {
			super(commsInterface, driveInterface, sensorInterface);
		}

		/**
		 * Broadcast our position every 0.1 secs, until this thread is
		 * interrupted.
		 */
		@Override
		public void run() {
			try {
				while (true) {
					readSensors();
					sendMessage();
					Thread.sleep(100);
				}
			}
			catch (InterruptedException e) {
				// Expected, when we terminate the simulation
			}
		}
	}

}
