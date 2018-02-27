package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.BeaconInterface;
import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * Uses the predecessors acceleration as in the first basic algorithm but
 * additionally modifies the chosen acceleration by a linear function of the
 * front proximity
 */
public class BasicAlgorithm2 extends Algorithm{

	private double BUFF_DIST = 0.3;

	public BasicAlgorithm2(DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
		super(driveInterface, sensorInterface, networkInterface);
	public BasicAlgorithm2(DriveInterface driveInterface,
			SensorInterface sensorInterface,
			NetworkInterface networkInterface,
			BeaconInterface beacons,
			FrontVehicleRoute.RouteNumber routeNumber) {
		super(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
	}

	@Override
	public void setParameter(ParameterEnum parameterEnum, double value) {
		if(parameterEnum == parameterEnum.BufferDistance) {
			BUFF_DIST = value;
		}
	}

	@Override
	public Double getParameter(ParameterEnum parameterEnum) {
		if(parameterEnum == parameterEnum.BufferDistance) {
			return BUFF_DIST;
		}
		return null;
	}

	@Override
	public ParameterEnum[] getParameterList() {
		return new ParameterEnum[]{ParameterEnum.BufferDistance};
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
			if (algorithmData.frontProximity < BUFF_DIST) {
				if (algorithmData.chosenAcceleration >= 0) {
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration * algorithmData.frontProximity
							/ BUFF_DIST;
				} else {
					// if braking then divide by value so deceleration decreases if gap too small
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
							/ (algorithmData.frontProximity / BUFF_DIST);
				}
			} else {
				if (algorithmData.chosenAcceleration >= 0) {
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
							* (0.75 + algorithmData.frontProximity / (4*BUFF_DIST));
				} else {
					// if braking then divide by value so deceleration decreases if gap too small
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
							/ (0.75 + algorithmData.frontProximity / (4*BUFF_DIST));
				}
			}
		}
		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
