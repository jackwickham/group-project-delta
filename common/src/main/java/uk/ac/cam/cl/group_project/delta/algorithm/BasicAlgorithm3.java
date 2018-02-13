package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * As basic algorithm 2: additionally modifies the chosen acceleration by a linear function of the front proximity
 * Additionally, combine the front proximity predicted from the vehicle states at the beginning of the previous time preriod,
 * and the sensor proximity data
 */

public class BasicAlgorithm3 {

    private static double weightFrontProximity(double predictedFrontProximity, double sensorFrontProximity) {
        return 0.5*predictedFrontProximity+0.5*sensorFrontProximity;
    }

    public static void makeDecision(AlgorithmData algorithmData) {
		//decide on chosen acceleration, speed and turnRate
		//calculate the distance us and our predecessor have travelled in the previous time period
		algorithmData.predictedPredecessorMovement = algorithmData.predecessorSpeed * algorithmData.timePeriod +
				0.5*algorithmData.predecessorAcceleration*algorithmData.predecessorAcceleration;
		algorithmData.predictedMovement = algorithmData.previousSpeed * algorithmData.timePeriod +
				0.5*algorithmData.previousAcceleration*algorithmData.previousAcceleration;
		algorithmData.predictedFrontProximity = algorithmData.predictedPredecessorMovement
				- algorithmData.predictedMovement + algorithmData.previousDistance;

		double weightedFrontProximity = weightFrontProximity(algorithmData.predictedFrontProximity,algorithmData.sensorFrontProximity);

		//update previous state variables so that they are correct in next time period
		algorithmData.previousDistance = weightedFrontProximity;
		algorithmData.previousSpeed = algorithmData.speed;
		algorithmData.previousAcceleration = algorithmData.acceleration;

		algorithmData.chosenAcceleration = algorithmData.predecessorAcceleration ;
		if(weightedFrontProximity < 5) {
			algorithmData.chosenAcceleration = algorithmData.chosenAcceleration * weightedFrontProximity/5;
		} else {
			algorithmData.chosenAcceleration = algorithmData.chosenAcceleration * (0.75+weightedFrontProximity/20.0);
		}
		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
