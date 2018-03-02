
package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.BeaconInterface;
import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 *Uses the formula found in the research paper
 */

public class BasicAlgorithmPID2 extends Algorithm {

	//not these defaults are not well configured
	//ID parameters
	//increases response time
	private double pidP = 4;
	//helps prevent steady-state errors
	private double pidI = 0;
	//helps prevent overshoot
	private double pidD= 2;

	//turning PD parameters
	private double turnP = 0.5;//3;
	private double turnD = 0.6;

	//maximum and minimum acceleration in m/s
	private double maxAcc = 0.2;
	private double minAcc = -2;

	//constant buffer distance in m
	private double buffDist = 0.3;
	//constant headway time in s
	private double headTime = 0.0;

	//distance below which emergency stop happens
	private double emerDist = 0.1;

	private double maxSensorDist = 2;

	public BasicAlgorithmPID2(DriveInterface driveInterface,
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
			case EmergencyDistance:
				emerDist = value;
			case MaxSensorDist:
				maxSensorDist = value;
		}
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
			case EmergencyDistance:
				return emerDist;
			case MaxSensorDist:
				return maxSensorDist;
		}
		return null;
	}

	@Override
	public ParameterEnum[] getParameterList() {
		return new ParameterEnum[] {ParameterEnum.PID_P, ParameterEnum.PID_I, ParameterEnum.PID_D, ParameterEnum.MaxAcc,
				ParameterEnum.MinAcc, ParameterEnum.BufferDistance, ParameterEnum.HeadTime,
				ParameterEnum.EmergencyDistance, ParameterEnum.MaxSensorDist};
	}

	@Override
	public void makeDecision() {
		double pTerm;
		double iTerm =0;
		if (algorithmData.frontProximity != null && algorithmData.frontProximity > maxSensorDist) {
			algorithmData.frontProximity = null;
		}
		if (algorithmData.frontProximity == null && algorithmData.closestBeacon != null && algorithmData.closestBeacon.getDistanceLowerBound() < maxSensorDist) {
			algorithmData.frontProximity = algorithmData.closestBeacon.getDistanceLowerBound();
		}
		if (algorithmData.frontProximity != null) {
			//decide on chosen acceleration, speed and turnRate
			if (algorithmData.frontProximity < emerDist) {
				emergencyStop();
			}
			if (algorithmData.receiveMessageData != null) {
				//This multiplies the error by a constant term PID_P
				double error = (algorithmData.frontProximity -
						(headTime * algorithmData.speed + buffDist));
				pTerm = pidP * error;
				if(algorithmData.errorSum != null && error < 2 && error > -10) {
					algorithmData.errorSum += error;
				} else {
					algorithmData.errorSum = error;
				}
				iTerm = pidI * algorithmData.errorSum;

			} else {
				//if no message received just use sensor data
				pTerm = pidP * (algorithmData.frontProximity - buffDist);
			}
		} else {
			//without front proximity reading p Term is not used
			pTerm = 0;
			iTerm = 0;
		}
		double dTerm = 0;
		if (algorithmData.receiveMessageData != null) {
			//Multiplies the rate of change of error by a constant term PID_D
			dTerm = pidD * (algorithmData.predecessorSpeed -
					algorithmData.speed - headTime * algorithmData.acceleration);
		} else {
			//if no message has ever been received d Term not used
			dTerm = 0;
		}

		//use predecessors acceleration as feedforward
		double chosenAcceleration = pTerm + dTerm + iTerm + algorithmData.predecessorAcceleration;

		//clamp chosen acceleration within range min and max acceleration
		if (chosenAcceleration > maxAcc) {
			chosenAcceleration = maxAcc;
		} else if (chosenAcceleration < minAcc) {
			chosenAcceleration = minAcc;
		}

		algorithmData.chosenAcceleration = chosenAcceleration;

		//TODO: This is not calculated
		algorithmData.chosenSpeed = algorithmData.speed;

		//basic turning PD
		if (algorithmData.closestBeacon != null && algorithmData.closestBeacon.getDistanceLowerBound() < maxSensorDist) {
			double p = turnP * algorithmData.angle;
			if (algorithmData.speed < 0.1) {
				// Prevent swerving at low speeds
				p *= algorithmData.speed * 10;
			}
			if (algorithmData.closestBeacon.getDistanceLowerBound() < 0.5) {
				// Turn harder
				p *= 2;
			}
			double d;
			if (algorithmData.previousAngle == null) {
				d = 0;
			} else {
				d = turnD * (algorithmData.angle - algorithmData.previousAngle);
			}
			algorithmData.chosenTurnRate = p + d;
		} else {
			algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
		}
	}
}
