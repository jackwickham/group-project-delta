package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.BeaconInterface;
import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * Basic version of algorithm which sets acceleration and turn rate to that of
 * its predecessors
 */
public class NaiveAlgorithm1 extends Algorithm {

	public NaiveAlgorithm1(DriveInterface driveInterface,
			SensorInterface sensorInterface, NetworkInterface networkInterface,
			BeaconInterface beacons, FrontVehicleRoute.RouteNumber routeNumber) {
		super(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
	}

	@Override
	public void setParameter(ParameterEnum parameterEnum, double value) {}

	@Override
	public Double getParameter(ParameterEnum parameterEnum) {
		return null;
	}

	@Override
	public ParameterEnum[] getParameterList() {
		return new ParameterEnum[0];
	}

	@Override
	public void makeDecision() {
		if (algorithmData.receiveMessageData != null) {
			// decide on chosen acceleration, speed and turnRate
			algorithmData.chosenAcceleration = algorithmData.predecessorAcceleration;
			algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
			algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
		} else {
			//if no message ever received
			algorithmData.chosenAcceleration = 0;
			algorithmData.chosenSpeed = algorithmData.speed;
			algorithmData.chosenTurnRate = algorithmData.turnRate;
		}
	}
}
