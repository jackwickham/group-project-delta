package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.BeaconInterface;
import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.Time;

/**
 * As naive algorithm 2: additionally modifies the chosen acceleration by a
 * linear function of the front proximity Additionally, combine the front
 * proximity predicted from the vehicle states at the beginning of the previous
 * time period, and the sensor proximity data
 */

public class NaiveAlgorithm3 extends Algorithm{

	private double buffDist = 0.3;
	private double maxSensorDist = 0.5;

	public NaiveAlgorithm3(DriveInterface driveInterface,
	                        SensorInterface sensorInterface,
	                        NetworkInterface networkInterface,
	                        BeaconInterface beacons,
	                        FrontVehicleRoute.RouteNumber routeNumber) {
		super(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
	}

	@Override
	public void setParameter(ParameterEnum parameterEnum, double value) {
		switch(parameterEnum) {
			case BufferDistance:
				buffDist = value;
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
			case MaxSensorDist:
				return maxSensorDist;
		}
		return null;
	}

	@Override
	public ParameterEnum[] getParameterList() {
		return new ParameterEnum[]{ParameterEnum.BufferDistance, ParameterEnum.MaxSensorDist};
	}

	//combine the front proximity predicted from the vehicle states at the beginning of the previous time period,
	//and the sensor proximity data
	private  Double weightFrontProximity(Double predictedFrontProximity, Double frontProximity) {
		if (predictedFrontProximity != null && frontProximity != null) {
				return 0.5 * predictedFrontProximity + 0.5 * frontProximity;
		}
		if(predictedFrontProximity != null){
			return predictedFrontProximity;
		}
		if(frontProximity != null) {
			return frontProximity;
		}
		else return null;
	}

	@Override
	public void makeDecision() {
		// decide on chosen acceleration, speed and turnRate
		// calculate the distance us and our predecessor have travelled in the previous
		// time period
		Double weightedFrontProximity;
		if (algorithmData.receiveMessageData != null && algorithmData.previousDistance != null) {
			double delay = (Time.getTime() - algorithmData.receiveMessageData.getStartTime()) / 100000000;
			//calculate the distance us and our predecessor have travelled since message received
			algorithmData.predictedPredecessorMovement = algorithmData.predecessorSpeed * delay
					+ 0.5 * algorithmData.predecessorAcceleration * delay * delay;
			algorithmData.predictedMovement = algorithmData.previousSpeed * delay
					+ 0.5 * algorithmData.previousAcceleration * delay * delay;
			algorithmData.predictedFrontProximity = algorithmData.predictedPredecessorMovement
					- algorithmData.predictedMovement + algorithmData.previousDistance;

			algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
			algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
		} else {
			//no message received or no previous distance
			algorithmData.predictedFrontProximity = null;
			algorithmData.chosenSpeed = algorithmData.speed;
			algorithmData.chosenTurnRate = algorithmData.turnRate;
		}
		if (algorithmData.frontProximity != null && algorithmData.frontProximity > maxSensorDist) {
			algorithmData.frontProximity = null;
		}
		weightedFrontProximity = weightFrontProximity(algorithmData.predictedFrontProximity,
				algorithmData.frontProximity);

		// update previous state variables so that they are correct in next time period
		algorithmData.previousDistance = weightedFrontProximity;
		algorithmData.previousSpeed = algorithmData.speed;
		algorithmData.previousAcceleration = algorithmData.acceleration;

		if (weightedFrontProximity != null) {
			if (weightedFrontProximity < buffDist) {
				if (algorithmData.chosenAcceleration >= 0) {
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration * weightedFrontProximity / buffDist;
				} else {
					// if braking then divide by value so deceleration decreases if gap too small
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration / (weightedFrontProximity / buffDist);
				}
			} else {
				if (algorithmData.chosenAcceleration >= 0) {
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
							* (0.75 + weightedFrontProximity / (4* buffDist));
				} else {
					// if braking then divide by value so deceleration decreases if gap too small
					algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
							/ (0.75 + weightedFrontProximity / (4* buffDist));
				}
			}
		} else {
			//no messages received and proximity sensor not working
			algorithmData.chosenAcceleration = 0;
		}


	}
}
