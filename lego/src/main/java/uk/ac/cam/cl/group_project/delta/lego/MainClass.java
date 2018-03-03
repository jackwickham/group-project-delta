package uk.ac.cam.cl.group_project.delta.lego;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
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
		try {
			network = new Network(Thread.currentThread());
			Algorithm algo = Algorithm.createAlgorithm(
					AlgorithmEnum.Cooperative_Adaptive_Cruise_Control,
					drive,
					sensor,
					network,
					beacon,
					FrontVehicleRoute.RouteNumber.ROUTE_THREE
			);
			algo.run();
		} finally {
			if (network != null) {
				network.close();
			}
		}

	}
}
