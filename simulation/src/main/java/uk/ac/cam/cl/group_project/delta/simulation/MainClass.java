package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Communications;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.PlatoonLookup;

import java.io.*;
import java.util.Date;

class MainClass {

	/**
	 * Number of vehicles to create.
	 */
	private static final int NUMBER_OF_VEHICLES = 1;

	/**
	 * Target time interval between CSV logs of the world state, in nanoseconds.
	 */
	private static final long POSITION_LOG_INTERVAL = 100000000; // 0.1 sec

	public static void main(String[] args) {

		// Instantiate world and virtual network
		World world = new World();
		SimulatedNetwork network = new SimulatedNetwork();

		// Create cars
		for (int i = 0; i < NUMBER_OF_VEHICLES; ++i) {
			SimulatedCar car = new SimulatedCar(2.5, world, network);
			world.getBodies().add(car);
			PlatoonLookup lookup = new PlatoonLookup();
			car.setController(
				new Algorithm(
					new Communications(
						new ControlLayer(
							car.getNetworkInterface(),
							lookup
						),
						lookup
					),
					car.getDriveInterface(),
					car.getSensorInterface()
				)
			);
			car.setPosition(new Vector2D(
				i * 10, 0
			));
			car.setEnginePower(0.05);
			car.setWheelAngle(0.1);
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
					long since = (start - time) % POSITION_LOG_INTERVAL;
					lastLog = time - (POSITION_LOG_INTERVAL - since);

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
}
