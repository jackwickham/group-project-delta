
package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.*;

/**
 * Main Cooperative cruise control Algorithm
 * If message received over network:
 * then takes the acceleration of the car in-front
 * and corrects for errors in the buffer distance by using a PID.
 * The PID uses the velocity and acceleration from the predecessor as the
 *
 * If no message has been received:
 * then just uses a PID, with the D term calculated using the rate of change of error in the buffer distance.
 */

public class CACC_Algorithm extends Algorithm {

	//note these defaults are not well configured
	//ID parameters
	//increases response time
	private double pidP = 4;
	//helps prevent steady-state errors
	private double pidI = 0;
	//helps prevent overshoot
	private double pidD = 2;

	//these control the PID parameters when no network packets are detected
	//Note: these will probably have to be higher than the normal values to prevent collisions when networking is down
	private double pidP_NoNetwork = 0.5;
	private double pidD_NoNetwork = 10;

	//turning PD parameters
	private double turnP = 0.5;
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

	//determines if state variables(speed, acc) are used to improve the previous proximity value which is used in smoothing
	private boolean usePrediction = true;

	//higher value will help to smooth out spikes in the proximity sensor but will decrease the response time
	private double proximitySmoothing = 0.5;

	public CACC_Algorithm(DriveInterface driveInterface,
	        SensorInterface sensorInterface, NetworkInterface networkInterface,
	        BeaconInterface beacons, FrontVehicleRoute.RouteNumber routeNumber) {
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
				break;
			case pidP_NoNetwork:
				pidP_NoNetwork = value;
				break;
			case pidD_NoNetwork:
				pidD = value;
				break;
			case proximitySmoothing:
				proximitySmoothing = value;
				break;
			case usePrediction:
				if(value > 0) {
					usePrediction = true;
				} else {
					usePrediction = false;
				}
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
			case pidP_NoNetwork:
				return pidP_NoNetwork;
			case pidD_NoNetwork:
				return pidD_NoNetwork;
			case proximitySmoothing:
				return proximitySmoothing;
			case usePrediction:
				if(usePrediction) {
					return 1.0;
				} else {
					return 0.0;
				}
		}
		return null;
	}
	@Override
	public ParameterEnum[] getParameterList() {
		return new ParameterEnum[]{ParameterEnum.PID_P, ParameterEnum.PID_I, ParameterEnum.PID_D,
			ParameterEnum.MaxAcc, ParameterEnum.MinAcc, ParameterEnum.BufferDistance, ParameterEnum.HeadTime,
			ParameterEnum.EmergencyDistance, ParameterEnum.MaxSensorDist, ParameterEnum.pidP_NoNetwork,
			ParameterEnum.pidD_NoNetwork, ParameterEnum.proximitySmoothing, ParameterEnum.usePrediction};
	}

	@Override
	public void makeDecision() {
		// Fall back to beacon proximity if no real proximity available
		if (algorithmData.frontProximity != null && algorithmData.frontProximity > maxSensorDist) {
			algorithmData.frontProximity = null;
		}
		if (algorithmData.frontProximity == null && algorithmData.closestBeacon != null && algorithmData.closestBeacon.getDistanceLowerBound() < maxSensorDist) {
			algorithmData.frontProximity = algorithmData.closestBeacon.getDistanceLowerBound();
		}

		// if prediction is turned on use the data from the message
		// and previous front proximity to estimate the current front proximity
		if(usePrediction) {
			if (algorithmData.lastTime != null && algorithmData.predictedFrontProximity != null) {
				double delay = (Time.getTime() - algorithmData.lastTime) / 1E9;
				//calculate the distance us and our predecessor have travelled since message received
				if(algorithmData.predecessorSpeed > 0.1) {
					algorithmData.predictedPredecessorMovement = Math.max(0, algorithmData.predecessorSpeed * delay
							+ 0.5 * algorithmData.predecessorAcceleration * delay * delay);
				} else {
					algorithmData.predictedMovement = 0;
				}
				if(algorithmData.previousSpeed > 0.1) {
					algorithmData.predictedMovement = Math.max(0, algorithmData.previousSpeed * delay
							+ 0.5 * algorithmData.previousAcceleration * delay * delay);
				} else {
					algorithmData.predictedMovement = 0;
				}
				// for safety reasons do not predict that distance has increased
				algorithmData.predictedFrontProximity = Math.min(0,algorithmData.predictedPredecessorMovement
						- algorithmData.predictedMovement) + algorithmData.predictedFrontProximity;
			}
		}

		double pTerm;
		double iTerm = 0;
		double dTerm = 0;
		if(algorithmData.frontProximity != null && algorithmData.frontProximity < maxSensorDist) {
			if(algorithmData.predictedFrontProximity != null) {
				//update predicted proximity with new sensor data, weighting by proximitySmoothing coefficient
				algorithmData.predictedFrontProximity = proximitySmoothing * algorithmData.predictedFrontProximity + (1 - proximitySmoothing) * algorithmData.frontProximity;
			} else {
				//if first time sensor is working then use front proximity as smoothed prediction
				algorithmData.predictedFrontProximity = algorithmData.frontProximity;
			}

			//decide on chosen acceleration, speed and turnRate
			if (algorithmData.predictedFrontProximity < emerDist) {
				emergencyStop();
			}
			if (algorithmData.receiveMessageData != null) {
				//Proximity, Networking

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
				//Proximity, No Networking

				//if no message received just use sensor data
				pTerm = pidP_NoNetwork * (algorithmData.predictedFrontProximity - buffDist);
				if(algorithmData.previousPredictedProximity != null) {
					dTerm = pidD_NoNetwork * (algorithmData.predictedFrontProximity - algorithmData.previousPredictedProximity);
				}
			}
		} else {
			//No Proximity

			//without front proximity reading p Term is not used
			pTerm = 0;
			iTerm = 0;

			if(algorithmData.receiveMessageData == null) {
				//if no network packet and no proximity sensor then emergency stop
				emergencyStop();
			}
		}
		if (algorithmData.receiveMessageData != null) {
			//Multiplies the rate of change of error by a constant term PID_D
			dTerm = pidD * (algorithmData.predecessorSpeed -
					algorithmData.speed - headTime * algorithmData.acceleration);
		}

		double chosenAcceleration;
		//use predecessors acceleration as feed-forward
		if(algorithmData.receiveMessageData != null) {
			chosenAcceleration = pTerm + dTerm + iTerm + algorithmData.predecessorAcceleration;
		} else {
			chosenAcceleration = pTerm + dTerm + iTerm;
		}

		//clamp chosen acceleration within range min and max acceleration
		if (chosenAcceleration > maxAcc) {
			chosenAcceleration = maxAcc;
		} else if (chosenAcceleration < minAcc) {
			chosenAcceleration = minAcc;
		}

		algorithmData.chosenAcceleration = chosenAcceleration;

		//Note: This is not calculated
		algorithmData.chosenSpeed = algorithmData.speed;

		//basic turning PD
		if (algorithmData.closestBeacon != null && algorithmData.closestBeacon.getDistanceLowerBound() < maxSensorDist) {
			double p = turnP * algorithmData.angle;
			if (algorithmData.speed < 0.1) {
				// Prevent swerving at low speeds
				p *= algorithmData.speed * 10;
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

		algorithmData.lastTime = Time.getTime();
		algorithmData.previousPredictedProximity = algorithmData.predictedFrontProximity;
	}
}
