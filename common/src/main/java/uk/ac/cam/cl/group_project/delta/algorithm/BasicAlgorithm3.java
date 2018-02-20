package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * As basic algorithm 2: additionally modifies the chosen acceleration by a
 * linear function of the front proximity Additionally, combine the front
 * proximity predicted from the vehicle states at the beginning of the previous
 * time preriod, and the sensor proximity data
 */

public class BasicAlgorithm3 extends Algorithm{

	public BasicAlgorithm3(DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
		super(driveInterface, sensorInterface, networkInterface);
	}

	private static double weightFrontProximity(double predictedFrontProximity, double sensorFrontProximity) {
		return 0.5 * predictedFrontProximity + 0.5 * sensorFrontProximity;
	}

	@Override
	protected void initialise() {

	}

	@Override
	public void makeDecision() {
		// decide on chosen acceleration, speed and turnRate
		// calculate the distance us and our predecessor have travelled in the previous
		// time period
		double delay = getTime() - algorithmData.receiveMessageData.getStartTime() / 100000000;
		//calculate the distance us and our predecessor have travelled since message received
		algorithmData.predictedPredecessorMovement = algorithmData.predecessorSpeed * delay
				+ 0.5 * algorithmData.predecessorAcceleration * delay * delay;
		algorithmData.predictedMovement = algorithmData.previousSpeed * delay
				+ 0.5 * algorithmData.previousAcceleration * delay * delay;
		algorithmData.predictedFrontProximity = algorithmData.predictedPredecessorMovement
				- algorithmData.predictedMovement + algorithmData.previousDistance;

		double weightedFrontProximity = weightFrontProximity(algorithmData.predictedFrontProximity,
				algorithmData.sensorFrontProximity);

		// update previous state variables so that they are correct in next time period
		algorithmData.previousDistance = weightedFrontProximity;
		algorithmData.previousSpeed = algorithmData.speed;
		algorithmData.previousAcceleration = algorithmData.acceleration;

		algorithmData.chosenAcceleration = algorithmData.predecessorAcceleration;
		if (algorithmData.sensorFrontProximity < 5) {
			if (algorithmData.chosenAcceleration >= 0) {
				algorithmData.chosenAcceleration = algorithmData.chosenAcceleration * weightedFrontProximity / 5.0;
			} else {
				// if braking then divide by value so deceleration decreases if gap too small
				algorithmData.chosenAcceleration = algorithmData.chosenAcceleration / (weightedFrontProximity / 5.0);
			}
		} else {
			if (algorithmData.chosenAcceleration >= 0) {
				algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
						* (0.75 + weightedFrontProximity / 20.0);
			} else {
				// if braking then divide by value so deceleration decreases if gap too small
				algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
						/ (0.75 + weightedFrontProximity / 20.0);
			}
		}

		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
