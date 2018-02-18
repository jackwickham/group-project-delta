package uk.ac.cam.cl.group_project.delta.algorithm;

/**
 * Uses the predecessors acceleration as in the first basic algorithm but
 * additionally modifies the chosen acceleration by a linear function of the
 * front proximity
 */
public class BasicAlgorithm2 {

	public static void makeDecision(AlgorithmData algorithmData) {
		// decide on chosen acceleration, speed and turnRate
		algorithmData.chosenAcceleration = algorithmData.predecessorAcceleration;
		if (algorithmData.sensorFrontProximity < 5) {
			if (algorithmData.chosenAcceleration >= 0) {
				algorithmData.chosenAcceleration = algorithmData.chosenAcceleration * algorithmData.sensorFrontProximity
						/ 5.0;
			} else {
				// if braking then divide by value so deceleration decreases if gap too small
				algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
						/ (algorithmData.sensorFrontProximity / 5.0);
			}
		} else {
			if (algorithmData.chosenAcceleration >= 0) {
				algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
						* (0.75 + algorithmData.sensorFrontProximity / 20.0);
			} else {
				// if braking then divide by value so deceleration decreases if gap too small
				algorithmData.chosenAcceleration = algorithmData.chosenAcceleration
						/ (0.75 + algorithmData.sensorFrontProximity / 20.0);
			}
		}
		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
