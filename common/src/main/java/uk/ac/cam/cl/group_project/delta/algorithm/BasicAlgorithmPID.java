package uk.ac.cam.cl.group_project.delta.algorithm;

/**
 * As basic algorithm 3.
 * Additionally, uses a PID to caculate the chosen acceleration
 */
public class BasicAlgorithmPID {

	//ID parameters
	public final static double PID_P = 0.5;
	public final static double PID_I = 0;
	public final static double PID_D = 1.8;

	//maximum and minimum acceleration in m/s
	public final static double MAX_ACC = 2;
	public final static double MIN_ACC = -2;

	//constant buffer distance in m
	public final static double BUFF_DIST = 2;
	//constant headway time in s
	public final static double HEAD_TIME = 2;

	//combine the front proximity predicted from the vehicle states at the beginning of the previous time preriod,
	//and the sensor proximity data
	private static double weightFrontProximity(double predictedFrontProximity, double sensorFrontProximity) {
		return 0.5 * predictedFrontProximity + 0.5 * sensorFrontProximity;
	}

	public static void initialise(AlgorithmData algorithmData) {

	}

	public static void makeDecision(AlgorithmData algorithmData) {
		//decide on chosen acceleration, speed and turnRate

		//calculate time since message received
		//TODO: add something to take into account network delay
		double delay = System.nanoTime() - algorithmData.receiveMessageData.getStartTime() / 100000000;
		//calculate the distance us and our predecessor have travelled since message received
		algorithmData.predictedPredecessorMovement = algorithmData.predecessorSpeed * delay
				+ 0.5 * algorithmData.predecessorAcceleration * delay * delay;
		algorithmData.predictedMovement = algorithmData.previousSpeed * delay
				+ 0.5 * algorithmData.previousAcceleration * delay * delay;
		algorithmData.predictedFrontProximity = algorithmData.predictedPredecessorMovement
				- algorithmData.predictedMovement + algorithmData.previousDistance;

		double weightedFrontProximity = weightFrontProximity(algorithmData.predictedFrontProximity, algorithmData.sensorFrontProximity);

		//update previous state variables so that they are correct in next time period
		algorithmData.previousDistance = weightedFrontProximity;
		algorithmData.previousSpeed = algorithmData.speed;
		algorithmData.previousAcceleration = algorithmData.acceleration;

		//calculate desired distance
		double desired_dist = BUFF_DIST + HEAD_TIME * (algorithmData.predecessorSpeed - algorithmData.speed);

		//get chosen acceleration from PID by giving it our proximity
		algorithmData.chosenAcceleration = algorithmData.miniPID.getOutput(desired_dist - weightedFrontProximity);

		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
