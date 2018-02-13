package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
/**
 * Basic version of algorithm which sets acceleration and turn rate to that of its predecessors
 */
public class BasicAlgorithm {

    public static void readSensors(AlgorithmData algorithmData) {
		//read data from predecessor's message
		VehicleData recieveMessageData = algorithmData.commsInterface.getPredecessorMessage(1);
		algorithmData.predecessorAcceleration = recieveMessageData.getAcceleration();
		algorithmData.predecessorSpeed = recieveMessageData.getSpeed();
		algorithmData.predecessorTurnRate = recieveMessageData.getTurnRate();
		algorithmData.predecessorChosenAcceleration = recieveMessageData.getChosenAcceleration();
		algorithmData.predecessorChosenSpeed = recieveMessageData.getChosenSpeed();
		algorithmData.predecessorChosenTurnRate = recieveMessageData.getChosenTurnRate();

		//TODO: values could be null if no data available
		//read data from sensors
		algorithmData.acceleration = algorithmData.sensorInterface.getAcceleration();
		algorithmData.speed = algorithmData.sensorInterface.getSpeed();
		algorithmData.turnRate = algorithmData.sensorInterface.getTurnRate();
		algorithmData.sensorFrontProximity = algorithmData.sensorInterface.getFrontProximity();

		//get initial distance reading from sensor
		algorithmData.previousDistance = algorithmData.sensorInterface.getFrontProximity();
		algorithmData.previousSpeed = algorithmData.sensorInterface.getSpeed();
		algorithmData.previousAcceleration = algorithmData.sensorInterface.getAcceleration();
	}

	public static void makeDecision(AlgorithmData algorithmData) {
		//decide on chosen acceleration, speed and turnRate
		algorithmData.chosenAcceleration = algorithmData.predecessorAcceleration;
		algorithmData.chosenSpeed = algorithmData.predecessorChosenSpeed;
		algorithmData.chosenTurnRate = algorithmData.predecessorTurnRate;
	}

	public static void sendMessage(AlgorithmData algorithmData) {
		//create and send message to other cars
		VehicleData sendMessageData = new VehicleData(algorithmData.speed, algorithmData.acceleration, algorithmData.turnRate,
				algorithmData.chosenSpeed, algorithmData.chosenAcceleration, algorithmData.chosenTurnRate);
		algorithmData.commsInterface.sendMessage(sendMessageData);
	}

	public static void sendInstruction(AlgorithmData algorithmData) {
		//send instructions to drive
		algorithmData.driveInterface.setAcceleration(algorithmData.chosenAcceleration);
		algorithmData.driveInterface.setTurnRate(algorithmData.chosenAcceleration);
	}

	public static void emergencyStop(AlgorithmData algorithmData) {
    	algorithmData.driveInterface.stop();
	}
}
