package uk.ac.cam.cl.group_project.delta.lego;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;

class MainClass {
	public static void main(String[] args) {
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();

		Drive drive = new Drive(ev3);
		Sensor sensor = new Sensor(drive);

		//lcd.drawString("This is " + ev3.getName(), 0, 4);
		//drive.setAcceleration(0.01);
		drive.setTurnRate(0.1);
		keys.waitForAnyPress();
		lcd.drawString("" + sensor.getAcceleration(),0,0);
		lcd.drawString("" + sensor.getSpeed(),0,1);
		lcd.drawString("" + sensor.getTurnRate(),0,2);
		keys.waitForAnyPress();
		drive.stop();
		keys.waitForAnyPress();
	}
}
