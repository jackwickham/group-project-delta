package uk.ac.cam.cl.group_project.delta.simulation;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

class MainClass {
    public static void main(String[] args) {

		World world = new World();
		SimulatedNetwork network = new SimulatedNetwork();

		for (int i = 0; i < 100; ++i) {
			SimulatedCar car = new SimulatedCar(2.5, world, network);
			world.getBodies().add(car);
		}

		long start = System.nanoTime();
		long time = start;
		while (time - start < 10 * 1e9) {
			long tmp = System.nanoTime();
			world.update((tmp - time) / 1e9);
			time = tmp;
		}

    }
}
