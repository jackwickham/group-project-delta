package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

public class Algorithm {

	public final static int ALGORITHM_LOOP_DURATION = 10000000; // 10ms

	private AlgorithmData algorithmData;

	public Algorithm(CommsInterface commsInterface, DriveInterface driveInterface, SensorInterface sensorInterface) {
		algorithmData = new AlgorithmData();
		algorithmData.commsInterface = commsInterface;
		algorithmData.driveInterface = driveInterface;
		algorithmData.sensorInterface = sensorInterface;
	}

	// set these functions to call version of algorithm required
	private void initialise() {
	}

	private void readSensors() {
		BasicAlgorithm.readSensors(algorithmData);
	}

	private void makeDecision() {
		BasicAlgorithm.makeDecision(algorithmData);
	}

	private void sendMessage() {
		// create and send message to other cars
		VehicleData sendMessageData = new VehicleData(algorithmData.speed, algorithmData.acceleration,
				algorithmData.turnRate, algorithmData.chosenSpeed, algorithmData.chosenAcceleration,
				algorithmData.chosenTurnRate);
		algorithmData.commsInterface.sendMessage(sendMessageData);
	}

	private void emergencyStop() {
		algorithmData.driveInterface.stop();
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
