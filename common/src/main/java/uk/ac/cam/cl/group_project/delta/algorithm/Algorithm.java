package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Communications;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;

import java.util.ArrayList;

public abstract class Algorithm {

	public final static int ALGORITHM_LOOP_DURATION = 10000000; // 10ms

	/*private final static ArrayList<Class<? extends Algorithm>> algorithmList =
			Arrays.asList(BasicAlgorithm.class, BasicAlgorithm2.class, BasicAlgorithm3.class, BasicAlgorithmPID.class, BasicAlgorithmPID2.class); */

	protected AlgorithmData algorithmData;

	protected Algorithm(DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
		algorithmData.commsInterface = new Communications(new ControlLayer(networkInterface));
		algorithmData.driveInterface = driveInterface;
		algorithmData.sensorInterface = sensorInterface;
	}

	public Algorithm createAlgorithm(int algorithmNum, DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
		switch (algorithmNum) {
			case 0: return new BasicAlgorithm(driveInterface, sensorInterface, networkInterface);
			case 1: return new BasicAlgorithm2(driveInterface,sensorInterface,networkInterface);
			case 2: return new BasicAlgorithm3(driveInterface,sensorInterface,networkInterface);
			case 3: return new BasicAlgorithmPID(driveInterface,sensorInterface,networkInterface);
			case 4: return new BasicAlgorithmPID2(driveInterface,sensorInterface,networkInterface);
		}
		return null;
	}

	protected abstract void initialise();

	private void readSensors() {
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

	protected abstract void makeDecision();

	private void sendMessage() {
		// create and send message to other cars
		VehicleData sendMessageData = new VehicleData(algorithmData.speed, algorithmData.acceleration,
				algorithmData.turnRate, algorithmData.chosenSpeed, algorithmData.chosenAcceleration,
				algorithmData.chosenTurnRate);
		algorithmData.commsInterface.sendMessage(sendMessageData);
	}

	protected void emergencyStop() {
		algorithmData.driveInterface.stop();
		algorithmData.commsInterface.notifyEmergency();
	}

	private void sendInstruction() {
		// send instructions to drive
		algorithmData.driveInterface.setAcceleration(algorithmData.chosenAcceleration);
		algorithmData.driveInterface.setTurnRate(algorithmData.chosenAcceleration);
	}

	public void run() {
		initialise();
		long startTime = System.nanoTime();

		while (!algorithmData.emergencyOccurred) {
			// read data from sensors into data class
			readSensors();

			if (Thread.interrupted()) {
				emergencyStop();
				break;
			}

			makeDecision();

			if (Thread.interrupted()) {
				emergencyStop();
				break;
			}

			sendMessage();

			// send instructions to drive
			sendInstruction();

			if (Thread.interrupted()) {
				emergencyStop();
				break;
			}

			try {
				long nanosToSleep = System.nanoTime() - startTime - ALGORITHM_LOOP_DURATION;
				if(nanosToSleep > 0) {
					// Note: integer division desired
					Thread.sleep(nanosToSleep/1000000);
				} else {
					// TODO: Log this as the LOOP_DURATION is too low, the algo can't keep up
				}
			} catch (InterruptedException e) {
				emergencyStop();
				break;
			}
			startTime = System.nanoTime();
		}

		// TODO: Log algorithm complete
	}
}
