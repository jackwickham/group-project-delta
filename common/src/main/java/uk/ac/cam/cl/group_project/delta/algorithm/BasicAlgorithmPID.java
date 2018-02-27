package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * As basic algorithm 3.
 * Additionally, uses a PID to calculate the chosen acceleration
 */
public class BasicAlgorithmPID extends Algorithm{
	//ID parameters
	private double PID_P = 0.5;
	private double PID_I = 0;
	private double PID_D = 1.8;

	//maximum and minimum acceleration in m/s
	private double MAX_ACC = 2;
	private double MIN_ACC = -2;

	//constant buffer distance in m
	private double BUFF_DIST = 0.3;
	//constant headway time in s
	private double HEAD_TIME = 0.2;

	public BasicAlgorithmPID(DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
		super(driveInterface, sensorInterface, networkInterface);
	}

	@Override
	public void setParameter(ParameterEnum parameterEnum, double value) {
		switch (parameterEnum) {
			case PID_P:
				PID_P = value;
				break;
			case PID_I:
				PID_I = value;
				break;
			case PID_D:
				PID_D = value;
				break;
			case MaxAcc:
				MAX_ACC = value;
				break;
			case MinAcc:
				MIN_ACC = value;
				break;
			case BufferDistance:
				BUFF_DIST = value;
				break;
			case HeadTime:
				HEAD_TIME = value;
				break;
		}
	}

	@Override
	public Double getParameter(ParameterEnum parameterEnum) {
		switch (parameterEnum) {
			case PID_P:
				return PID_P;
			case PID_I:
				return PID_I;
			case PID_D:
				return PID_D;
			case MaxAcc:
				return MAX_ACC;
			case MinAcc:
				return MIN_ACC;
			case BufferDistance:
				return BUFF_DIST;
			case HeadTime:
				return HEAD_TIME;
		}
		return null;
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
		algorithmData.miniPID = new MiniPID(PID_P, PID_I, PID_D);
		algorithmData.miniPID.setOutputLimits(MIN_ACC, MAX_ACC);
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
			desired_dist = BUFF_DIST + HEAD_TIME * (algorithmData.predecessorSpeed - algorithmData.speed);

			algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
			algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
		} else {
			//no message received or no previous distance
			algorithmData.predictedFrontProximity = null;
			desired_dist = BUFF_DIST;
			algorithmData.chosenSpeed = algorithmData.speed;
			algorithmData.chosenTurnRate = algorithmData.turnRate;
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
