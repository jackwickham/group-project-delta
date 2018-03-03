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
public class NaiveAlgorithm2 extends Algorithm{

	private double buffDist = 0.3;
	//larger values will result in more deceleration/acceleration when distance is too low/high
	private double breakingConstant = 4;
	private double accelerationConstant = 4;
	private double maxSensorDist = 0.5;

	public NaiveAlgorithm2(DriveInterface driveInterface,
			SensorInterface sensorInterface, NetworkInterface networkInterface,
			BeaconInterface beacons, FrontVehicleRoute.RouteNumber routeNumber) {
		super(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
	}

	@Override
	public void setParameter(ParameterEnum parameterEnum, double value) {
		switch(parameterEnum) {
			case BufferDistance:
				buffDist = value;
				return;
			case AccConst:
				accelerationConstant = value;
				return;
			case BreakingConst:
				breakingConstant = value;
				return;
			case MaxSensorDist:
				maxSensorDist = value;
		}
	}

	@Override
	public Double getParameter(ParameterEnum parameterEnum) {
		switch(parameterEnum) {
			case BufferDistance:
				return buffDist;
			case AccConst:
				return accelerationConstant;
			case BreakingConst:
				return breakingConstant;
			case MaxSensorDist:
				return maxSensorDist;
		}
		return null;
	}

	@Override
	public ParameterEnum[] getParameterList() {
		return new ParameterEnum[]{ParameterEnum.BufferDistance, ParameterEnum.MaxSensorDist, ParameterEnum.BreakingConst,
			ParameterEnum.AccConst, ParameterEnum.MaxSensorDist};
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
			if (algorithmData.frontProximity < maxSensorDist) {
				if (algorithmData.frontProximity < buffDist) {
					if (algorithmData.chosenAcceleration >= 0) {
						algorithmData.chosenAcceleration = algorithmData.chosenAcceleration * algorithmData.frontProximity
								/ buffDist;
					} else {
						// if braking then divide by value so deceleration decreases if gap too small
						algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
								/ (algorithmData.frontProximity / buffDist);
					}
				} else {
					if (algorithmData.chosenAcceleration >= 0) {
						algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
								* ((1/accelerationConstant) + algorithmData.frontProximity / (accelerationConstant * buffDist));
					} else {
						// if braking then divide by value so deceleration decreases if gap too small
						algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
								/ ((1/breakingConstant) + algorithmData.frontProximity / (breakingConstant * buffDist));
					}
				}
			}
		}
		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
