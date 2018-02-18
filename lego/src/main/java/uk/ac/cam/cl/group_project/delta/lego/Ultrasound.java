package uk.ac.cam.cl.group_project.delta.lego;

import lejos.hardware.ev3.EV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Ultrasound {

	private EV3UltrasonicSensor sensor;

	public Ultrasound (EV3 ev3) {
		Port port = ev3.getPort("S4");
		sensor = new EV3UltrasonicSensor(port);
		sensor.enable();
	}

	public Double getProximity() {
		SampleProvider distance = sensor.getDistanceMode();
		float[] sample = new float[distance.sampleSize()];
		distance.fetchSample(sample, 0);
		if (sample[0] < 0) return null;
		return (double) sample[0]*1000;
	}

}
