package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * Uses the predecessors acceleration as in the first basic algorithm but
 * additionally modifies the chosen acceleration by a linear function of the
 * front proximity
 */
public class BasicAlgorithm2 extends Algorithm{

	public BasicAlgorithm2(DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
		super(driveInterface, sensorInterface, networkInterface);
	}

	@Override
	protected void makeDecision() {
	// decide on chosen acceleration, speed and turnRate
		if(algorithmData.receiveMessageData != null) {
			algorithmData.chosenAcceleration = algorithmData.predecessorAcceleration;
		} else {
			algorithmData.chosenAcceleration = algorithmData.acceleration;
		}
		if(algorithmData.frontProximity != null) {
			if (algorithmData.frontProximity < 5) {
				if (algorithmData.chosenAcceleration >= 0) {
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration * algorithmData.frontProximity
							/ 5.0;
				} else {
					// if braking then divide by value so deceleration decreases if gap too small
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
							/ (algorithmData.frontProximity / 5.0);
				}
			} else {
				if (algorithmData.chosenAcceleration >= 0) {
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
							* (0.75 + algorithmData.frontProximity / 20.0);
				} else {
					// if braking then divide by value so deceleration decreases if gap too small
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
							/ (0.75 + algorithmData.frontProximity / 20.0);
				}
			}
		}
		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
