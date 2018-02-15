package uk.ac.cam.cl.group_project.delta.lego;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import uk.ac.cam.cl.group_project.delta.log.StderrLogger;

class MainClass {
	public static void main(String[] args) {
		StderrLogger.register();

		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();

		lcd.drawString("This is " + ev3.getName(), 0, 4);
		keys.waitForAnyPress();
	}
}
