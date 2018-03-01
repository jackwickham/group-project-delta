package uk.ac.cam.cl.group_project.delta.lego;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import uk.ac.cam.cl.group_project.delta.Log;
import uk.ac.cam.cl.group_project.delta.algorithm.Algorithm;
import uk.ac.cam.cl.group_project.delta.algorithm.AlgorithmEnum;
import uk.ac.cam.cl.group_project.delta.algorithm.FrontVehicleRoute;

import java.io.IOException;

class MainClass {
	public static void main(String[] args) throws IOException {
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		//Keys keys = ev3.getKeys();

		lcd.drawString("This is " + ev3.getName(), 0, 4);

		Drive drive = new Drive(ev3);
		Sensor sensor = new Sensor(drive, ev3);
		LegoBeacon beacon = new LegoBeacon(sensor, ev3.getName());
		Network network = null;
		EV3ColorSensor colourSensor = new EV3ColorSensor(ev3.getPort("S3"));
		try {
			network = new Network(Thread.currentThread());
			Algorithm algo = Algorithm.createAlgorithm(
					AlgorithmEnum.BasicAlgorithmPID2,
					drive,
					sensor,
					network,
					beacon,
					FrontVehicleRoute.RouteNumber.ROUTE_ONE
			);
			new MindstormsColourManager(colourSensor, algo).start();
			algo.run();
		} finally {
			if (network != null) {
				network.close();
			}
		}

	}

	private static class MindstormsColourManager extends Thread {
		private EV3ColorSensor colourSensor;
		private Algorithm algorithm;

		public MindstormsColourManager(EV3ColorSensor sensor, Algorithm algorithm) {
			this.colourSensor = sensor;
			this.algorithm = algorithm;

			this.setDaemon(true);
		}

		@Override
		public void run() {
			while(true) {
				if (algorithm.algorithmData.commsInterface.isLeader()) {
					colourSensor.setFloodlight(Color.GREEN);
				} else {
					colourSensor.setFloodlight(Color.BLUE);
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					Log.error(e);
					// pass
				}
			}
		}
	}
}
