
package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.*;

/**
 *Uses the formula found in the research paper
 */

public class BasicAlgorithmPID2 extends Algorithm {

	//not these defaults are not well configured
	//ID parameters
	//increases response time
	private double pidP = 1.5;
	//helps prevent steady-state errors
	private double pidI = 0;
	//helps prevent overshoot
	private double pidD= 3;

	//turning PD parameters
	private double turnP = 10;
	private double turnD = 0;

	//maximum and minimum acceleration in m/s
	private double maxAcc = 2;
	private double minAcc = -2;

	//constant buffer distance in m
	private double buffDist = 0.3;
	//constant headway time in s
	private double headTime = 0.1;

	//distance below which emergency stop happens
	private double emerDist = 0.1;

	private double maxSensorDist = 2;

	//determines if state variables(speed, acc) are used to improve the previous proximity value which is used in smoothing
	private boolean usePrediction = true;

	//higher value will help to smooth out spikes in the proximity sensor but will decrease the response time
	private double proximitySmoothing = 0.8;

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
		//if prediction is turned on use messagedata and previous front proximity to estimate the current front proximity
		if(usePrediction) {
			if (algorithmData.receiveMessageData != null && algorithmData.predictedFrontProximity != null) {
				double delay = (Time.getTime() - algorithmData.receiveMessageData.getStartTime()) / 100000000;
				//calculate the distance us and our predecessor have travelled since message received
				algorithmData.predictedPredecessorMovement = algorithmData.predecessorSpeed * delay
						+ 0.5 * algorithmData.predecessorAcceleration * delay * delay;
				algorithmData.predictedMovement = algorithmData.previousSpeed * delay
						+ 0.5 * algorithmData.previousAcceleration * delay * delay;
				algorithmData.predictedFrontProximity = algorithmData.predictedPredecessorMovement
						- algorithmData.predictedMovement + algorithmData.predictedFrontProximity;
			}
		}

		if(algorithmData.frontProximity != null && algorithmData.frontProximity < maxSensorDist) {
			if(algorithmData.predictedFrontProximity != null) {
				//update predicted proximity with new sensor data, weighting by proximitySmoothing coefficient
				algorithmData.predictedFrontProximity = proximitySmoothing * algorithmData.predictedFrontProximity + (1 - proximitySmoothing) * algorithmData.frontProximity;
			} else {
				//if first time sensor is working then use front proximity as smoothed prediction
				algorithmData.predictedFrontProximity = algorithmData.frontProximity;
			}
		} else {
			//if front proximity is null or above maxSensorDistance set distance to null to not use it
			algorithmData.predictedFrontProximity = null;
		}

		double pTerm;
		double iTerm =0;
		if (algorithmData.predictedFrontProximity != null) {
			//decide on chosen acceleration, speed and turnRate
			if (algorithmData.predictedFrontProximity < emerDist) {
				emergencyStop();
			}
			if (algorithmData.receiveMessageData != null) {
				//This multiplies the error by a constant term PID_P
				double error = (algorithmData.predictedFrontProximity -
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
				pTerm = pidP * (algorithmData.predictedFrontProximity - buffDist);
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
		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;

		//basic turning PD
		//d term not currently not used as overshoot is not a problem
		if (algorithmData.closestBeacon != null && algorithmData.closestBeacon.getDistanceLowerBound() < maxSensorDist) {
			double p = turnP * algorithmData.angle;
			double d = turnD * (algorithmData.angle - algorithmData.angle);
			algorithmData.chosenTurnRate = p + d;
		} else {
			algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
		}
	}
}
