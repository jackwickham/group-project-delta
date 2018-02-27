package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.BeaconInterface;
import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * As basic algorithm 3.
 * Additionally, uses a PID to calculate the chosen acceleration
 */
public class BasicAlgorithmPID extends Algorithm{

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

	public BasicAlgorithmPID(DriveInterface driveInterface,
			SensorInterface sensorInterface,
			NetworkInterface networkInterface,
			BeaconInterface beacons,
			FrontVehicleRoute.RouteNumber routeNumber) {
		super(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
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
				algorithmData.sensorFrontProximity);

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
