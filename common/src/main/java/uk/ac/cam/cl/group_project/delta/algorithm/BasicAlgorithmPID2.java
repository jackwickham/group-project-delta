
package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.BeaconInterface;
import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 *Uses the formula found in the research paper
 */

public class BasicAlgorithmPID2 extends Algorithm {

	//ID parameters
	//increases response time
	public final static double PID_P = 0.5;
	//helps prevent steady-state errors
	public final static double PID_I = 0;
	//helps prevent overshoot
	public final static double PID_D = 1.8;

	//maximum and minimum acceleration in m/s
	public final static double MAX_ACC = 2;
	public final static double MIN_ACC = -2;

	//constant buffer distance in m
	public final static double BUFF_DIST = 3;
	//constant headway time in s
	public final static double HEAD_TIME = 0.1;

	//distance bellow which emergency stop happens
	public final static double EMER_DIST = 0.1;

	public BasicAlgorithmPID2(DriveInterface driveInterface,
			SensorInterface sensorInterface,
			NetworkInterface networkInterface,
			BeaconInterface beacons,
			FrontVehicleRoute.RouteNumber routeNumber) {
		super(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
	}

	@Override
	public void makeDecision() {
		double pTerm;
		if(algorithmData.sensorFrontProximity != null) {
			//decide on chosen acceleration, speed and turnRate
			if (algorithmData.sensorFrontProximity < EMER_DIST) {
				emergencyStop();
			}
			if(algorithmData.receiveMessageData != null) {
				//This multiplies the error by a constant term PID_P
				pTerm = PID_P * (algorithmData.sensorFrontProximity +
						HEAD_TIME * (algorithmData.predecessorSpeed - algorithmData.speed) - BUFF_DIST);
			} else {
				//if no message received just use sensor data
				pTerm = PID_P * algorithmData.sensorFrontProximity - BUFF_DIST;
			}
		} else {
			//without front proximity reading p Term is not used
			pTerm = 0;
		}
		double dTerm;
		if(algorithmData.receiveMessageData != null) {
			//Multiplies the rate of change of error by a constant term PID_D
			dTerm = PID_D * (algorithmData.predecessorSpeed -
					algorithmData.speed + HEAD_TIME * (algorithmData.predecessorChosenAcceleration - algorithmData.acceleration));
		} else {
			//if no message has ever been received d Term not used
			dTerm = 0;
		}

		//clamp chosen acceleration within range min and max acceleration
		double chosenAcceleration = pTerm + dTerm;

		if(chosenAcceleration > MAX_ACC) {
			chosenAcceleration = MAX_ACC;
		} else if(chosenAcceleration < MIN_ACC) {
			chosenAcceleration = MIN_ACC;
		}

		algorithmData.chosenAcceleration = chosenAcceleration;

		//TODO: This is not calculated
		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
