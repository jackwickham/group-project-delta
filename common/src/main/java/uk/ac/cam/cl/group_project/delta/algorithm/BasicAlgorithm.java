package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

/**
 * Basic version of algorithm which sets acceleration and turn rate to that of
 * its predecessors
 */
public class BasicAlgorithm {

	public static void readSensors(AlgorithmData algorithmData) {
		// read data from predecessor's message
		algorithmData.receiveMessageData = algorithmData.commsInterface.getPredecessorMessage(1);
		algorithmData.predecessorAcceleration = algorithmData.receiveMessageData.getAcceleration();
		algorithmData.predecessorSpeed = algorithmData.receiveMessageData.getSpeed();
		algorithmData.predecessorTurnRate = algorithmData.receiveMessageData.getTurnRate();
		algorithmData.predecessorChosenAcceleration = algorithmData.receiveMessageData.getChosenAcceleration();
		algorithmData.predecessorChosenSpeed = algorithmData.receiveMessageData.getChosenSpeed();
		algorithmData.predecessorChosenTurnRate = algorithmData.receiveMessageData.getChosenTurnRate();

		// TODO: values could be null if no data available
		// read data from sensors
		algorithmData.acceleration = algorithmData.sensorInterface.getAcceleration();
		algorithmData.speed = algorithmData.sensorInterface.getSpeed();
		algorithmData.turnRate = algorithmData.sensorInterface.getTurnRate();
		algorithmData.sensorFrontProximity = algorithmData.sensorInterface.getFrontProximity();

		// get initial distance reading from sensor
		algorithmData.previousDistance = algorithmData.sensorInterface.getFrontProximity();
		algorithmData.previousSpeed = algorithmData.sensorInterface.getSpeed();
		algorithmData.previousAcceleration = algorithmData.sensorInterface.getAcceleration();
	}

	public static void makeDecision(AlgorithmData algorithmData) {
		// decide on chosen acceleration, speed and turnRate
		algorithmData.chosenAcceleration = algorithmData.predecessorAcceleration;
		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}
}
