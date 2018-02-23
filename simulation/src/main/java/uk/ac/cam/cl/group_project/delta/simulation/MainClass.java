package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.Log;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;
import uk.ac.cam.cl.group_project.delta.algorithm.AlgorithmEnum;
import uk.ac.cam.cl.group_project.delta.algorithm.CommsInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.VehicleData;
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
	 * Time interval between CSV logs of the world state, in nanoseconds.
	 */
	private static final long POSITION_LOG_INTERVAL = 100000000; // 0.1 sec

	/**
	 * The time interval in world time nanoseconds between calling the algorithm
	 */
	private static final long UPDATE_INTERVAL = 10000000; // 10ms

	/**
	 * The number of simulation steps to perform
	 */
	private static long simulationSteps = 1001;

	public static void main(String[] args) {

		// Instantiate world and virtual network
		World world = new World();
		SimulatedNetwork network = new SimulatedNetwork();

		List<SimulatedCar> cars = new ArrayList<>(NUMBER_OF_VEHICLES);

		// Create cars
		for (int i = 0; i < NUMBER_OF_VEHICLES; ++i) {
			SimulatedCar car = new SimulatedCar(world, network);
			world.getBodies().add(car);

			car.setController(Algorithm.createAlgorithm(
					AlgorithmEnum.BasicAlgorithm3,
					car.getDriveInterface(),
					car.getSensorInterface(),
					car.getNetworkInterface()
			));

			car.setPosition(new Vector2D(
				i * 0.3, 0
			));

			cars.add(car);
		}

		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter("sim_" + (new Date()).getTime() / 1000 + ".csv")
		)) {

			// Add headers
			writer.write("time,uuid,x,y,class\n");

			long time = 0;

			for (int step = 0; step < simulationSteps; step++) {
				time += UPDATE_INTERVAL;

				// Update the positions of everything in the world
				world.update(UPDATE_INTERVAL / 1E9);
				// then run the algorithm
				for (SimulatedCar car : cars) {
					car.updateControl(time);
				}

				// If we should log, do it
				if (time % POSITION_LOG_INTERVAL == 0) {
					// Log the position of all objects
					for (PhysicsBody body : world.getBodies()) {
						Vector2D pos = body.getPosition();
						writer.write(
							time + ","
								+ body.getUuid() + ","
								+ pos.getX() + ","
								+ pos.getY() + ","
								+ body.getClass().getSimpleName() + "\n"
						);
					}

				}
			}
		}
		catch (IOException e) {
			// Log error and continue
			Log.critical(e);
		}

		for (PhysicsBody body : world.getBodies()) {
			if (body.getClass() == SimulatedCar.class) {
				((SimulatedCar) body).stop();
			}
		}

	}
}
