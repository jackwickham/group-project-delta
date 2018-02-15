package uk.ac.cam.cl.group_project.delta.algorithm;

/**
 * As basic algorithm 3.
 * Additionally, uses a PID to caculate the chosen acceleration
 */
public class BasicAlgorithmPID {

	//combine the front proximity predicted from the vehicle states at the beginning of the previous time preriod,
	//and the sensor proximity data
	private static double weightFrontProximity(double predictedFrontProximity, double sensorFrontProximity) {
		return 0.5 * predictedFrontProximity + 0.5 * sensorFrontProximity;
	}

	public static void initialise(AlgorithmData algorithmData) {
		//create PID and set p, i, and d parameters
		algorithmData.miniPID = new MiniPID(0.5, 0, 1.8);
		//set minimum and maximum acceleration
		algorithmData.miniPID.setOutputLimits(-2,2);
		//set target distance
		algorithmData.miniPID.setSetpoint(5);
	}

	public static void makeDecision(AlgorithmData algorithmData) {
		//decide on chosen acceleration, speed and turnRate
		//calculate the distance us and our predecessor have travelled in the previous time period
		algorithmData.predictedPredecessorMovement = algorithmData.predecessorSpeed * algorithmData.timePeriod +
				0.5*algorithmData.predecessorAcceleration*algorithmData.predecessorAcceleration;
		algorithmData.predictedMovement = algorithmData.previousSpeed * algorithmData.timePeriod +
				0.5*algorithmData.previousAcceleration*algorithmData.timePeriod*algorithmData.timePeriod;
		algorithmData.predictedFrontProximity = algorithmData.predictedPredecessorMovement
				- algorithmData.predictedMovement + algorithmData.previousDistance;

		double weightedFrontProximity = weightFrontProximity(algorithmData.predictedFrontProximity,algorithmData.sensorFrontProximity);

		//update previous state variables so that they are correct in next time period
		algorithmData.previousDistance = weightedFrontProximity;
		algorithmData.previousSpeed = algorithmData.speed;
		algorithmData.previousAcceleration = algorithmData.acceleration;

		//get chosen acceleration from PID by giving it our proximity
		algorithmData.chosenAcceleration =  algorithmData.miniPID.getOutput(weightedFrontProximity);

		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
