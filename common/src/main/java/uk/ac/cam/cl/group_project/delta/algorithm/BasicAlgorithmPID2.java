
package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 *Uses the formula found in the research paper
 */

public class BasicAlgorithmPID2 extends Algorithm {

	//ID parameters
	//increases response time
	private double PID_P = 0.5;
	//helps prevent steady-state errors
	private double PID_I = 0;
	//helps prevent overshoot
	private double PID_D = 1.8;

	//maximum and minimum acceleration in m/s
	private double MAX_ACC = 2;
	private double MIN_ACC = -2;

	//constant buffer distance in m
	private double BUFF_DIST = 0.3;
	//constant headway time in s
	private double HEAD_TIME = 0.2;

	//distance bellow which emergency stop happens
	private double EMER_DIST = 0.1;

	public BasicAlgorithmPID2(DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
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
			case EmergencyDistance:
				EMER_DIST = value;
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
			case EmergencyDistance:
				return EMER_DIST;
		}
		return null;
	}

	@Override
	public ParameterEnum[] getParameterList() {
		return new ParameterEnum[] {ParameterEnum.PID_P, ParameterEnum.PID_I, ParameterEnum.PID_D, ParameterEnum.MaxAcc,
				ParameterEnum.MinAcc, ParameterEnum.BufferDistance, ParameterEnum.HeadTime, ParameterEnum.EmergencyDistance};
	}

	@Override
	public void makeDecision() {
		double pTerm;
		if(algorithmData.frontProximity != null) {
			//decide on chosen acceleration, speed and turnRate
			if (algorithmData.frontProximity < EMER_DIST) {
				emergencyStop();
			}
			if(algorithmData.receiveMessageData != null) {
				//This multiplies the error by a constant term PID_P
				pTerm = PID_P * (algorithmData.frontProximity +
						HEAD_TIME * (algorithmData.predecessorSpeed - algorithmData.speed) - BUFF_DIST);
			} else {
				//if no message received just use sensor data
				pTerm = PID_P * (algorithmData.frontProximity - BUFF_DIST);
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
