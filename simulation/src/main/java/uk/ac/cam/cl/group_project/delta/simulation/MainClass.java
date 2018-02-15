package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Communications;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.PlatoonLookup;

import java.io.*;
import java.util.Date;

class MainClass {

	private static final int NUMBER_OF_VEHICLES = 1;

	private static final long POSITION_LOG_INTERVAL = 100000000; // 0.1sec

	public static void main(String[] args) {

		World world = new World();
		SimulatedNetwork network = new SimulatedNetwork();

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

		for (PhysicsBody body : world.getBodies()) {
			if (body.getClass() == SimulatedCar.class) {
				//((SimulatedCar) body).start();
			}
		}

		try {

			BufferedWriter writer = new BufferedWriter(
				new FileWriter(
					"sim_" + (new Date()).getTime() / 1000 + ".csv"
				)
			);
			writer.write("time,uuid,x,y,class\n");

			long lastLog = start - POSITION_LOG_INTERVAL - 1;

			while (time - start < 10 * 1e9) {
				long tmp = System.nanoTime();
				world.update((tmp - time) / 1e9);
				time = tmp;

				if (time - lastLog > POSITION_LOG_INTERVAL) {
					lastLog += POSITION_LOG_INTERVAL;
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
