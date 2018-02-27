package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.BeaconInterface;
import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * As basic algorithm 3.
 * Additionally, uses a PID to calculate the chosen acceleration
 */
public class BasicAlgorithmPID extends Algorithm{
	//ID parameters
	private double pidP = 0.5;
	private double pidI = 0;
	private double pidD = 1.8;

	//maximum and minimum acceleration in m/s
	private double maxAcc = 2;
	private double minAcc = -2;

	//constant buffer distance in m
	private double buffDist = 0.3;
	//constant headway time in s
	private double headTime = 0.2;

	public BasicAlgorithmPID(DriveInterface driveInterface,
			SensorInterface sensorInterface,
			NetworkInterface networkInterface,
			BeaconInterface beacons,
			FrontVehicleRoute.RouteNumber routeNumber) {
		super(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
	}

	@Override
	public void setParameter(ParameterEnum parameterEnum, double value) {
		switch (parameterEnum) {
			case PID_P:
				pidP = value;
				break;
			case PID_I:
				pidI = value;
				break;
			case PID_D:
				pidD = value;
				break;
			case MaxAcc:
				maxAcc = value;
				break;
			case MinAcc:
				minAcc = value;
				break;
			case BufferDistance:
				buffDist = value;
				break;
			case HeadTime:
				headTime = value;
				break;
		}
		super.setParameter(parameterEnum, value);
	}

	@Override
	public Double getParameter(ParameterEnum parameterEnum) {
		switch (parameterEnum) {
			case PID_P:
				return pidP;
			case PID_I:
				return pidI;
			case PID_D:
				return pidD;
			case MaxAcc:
				return maxAcc;
			case MinAcc:
				return minAcc;
			case BufferDistance:
				return buffDist;
			case HeadTime:
				return headTime;
		}
		return super.getParameter(parameterEnum);
	}

	@Override
	public ParameterEnum[] getParameterList() {
		return new ParameterEnum[] {ParameterEnum.PID_P, ParameterEnum.PID_I, ParameterEnum.PID_D, ParameterEnum.MaxAcc,
				ParameterEnum.MinAcc, ParameterEnum.BufferDistance, ParameterEnum.HeadTime};
	}

	//combine the front proximity predicted from the vehicle states at the beginning of the previous time period,
	//and the sensor proximity data
	private static Double weightFrontProximity(Double predictedFrontProximity, Double sensorFrontProximity) {
		if (predictedFrontProximity != null && sensorFrontProximity != null) {
			return 0.5 * predictedFrontProximity + 0.5 * sensorFrontProximity;
		}
		if(predictedFrontProximity != null){
			return predictedFrontProximity;
		}
		if(sensorFrontProximity != null) {
			return sensorFrontProximity;
		}
		else return null;
	}

	public void initialise() {
		algorithmData.miniPID = new MiniPID(pidP, pidI, pidD);
		algorithmData.miniPID.setOutputLimits(minAcc, maxAcc);
	}

	public void makeDecision() {
		//decide on chosen acceleration, speed and turnRate

		//calculate time since message received
		//TODO: add something to take into account network delay
		double desired_dist;
		Double weightedFrontProximity;
		if(algorithmData.receiveMessageData != null && algorithmData.previousDistance != null)  {
			double delay = (getTime() - algorithmData.receiveMessageData.getStartTime()) / 100000000;
			//calculate the distance us and our predecessor have travelled since message received
			algorithmData.predictedPredecessorMovement = algorithmData.predecessorSpeed * delay
					+ 0.5 * algorithmData.predecessorAcceleration * delay * delay;
			algorithmData.predictedMovement = algorithmData.previousSpeed * delay
					+ 0.5 * algorithmData.previousAcceleration * delay * delay;
			algorithmData.predictedFrontProximity = algorithmData.predictedPredecessorMovement
					- algorithmData.predictedMovement + algorithmData.previousDistance;

			//calculate desired distance
			desired_dist = buffDist + headTime * (algorithmData.predecessorSpeed - algorithmData.speed);

			algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
			algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
		} else {
			//no message received or no previous distance
			algorithmData.predictedFrontProximity = null;
			desired_dist = buffDist;
			algorithmData.chosenSpeed = algorithmData.speed;
			algorithmData.chosenTurnRate = algorithmData.turnRate;
		}
		if(algorithmData.frontProximity > maxSensorDist) {
			algorithmData.frontProximity = null;
		}
		weightedFrontProximity = weightFrontProximity(algorithmData.predictedFrontProximity,
				algorithmData.frontProximity);

		//update previous state variables so that they are correct in next time period
		algorithmData.previousDistance = weightedFrontProximity;
		algorithmData.previousSpeed = algorithmData.speed;
		algorithmData.previousAcceleration = algorithmData.acceleration;

		if(weightedFrontProximity != null) {
			//get chosen acceleration from PID by giving it our proximity
			algorithmData.chosenAcceleration = algorithmData.miniPID.getOutput(desired_dist - weightedFrontProximity);
		} else {
			//no messages received and proximity sensor not working
			algorithmData.chosenAcceleration = 0;
		}
	}
}
