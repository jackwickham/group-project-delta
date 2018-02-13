package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

public class Algorithm {
    private AlgorithmData algorithmData;

    public Algorithm(CommsInterface commsInterface, DriveInterface driveInterface, SensorInterface sensorInterface) {
        algorithmData = new AlgorithmData();
        algorithmData.commsInterface = commsInterface;
        algorithmData.driveInterface = driveInterface;
        algorithmData.sensorInterface = sensorInterface;
    }

    //set these functions to call version of algorithm required
	private void initialise() {
	}

    private void readSensors() {
    	BasicAlgorithm.readSensors(algorithmData);
	}
    private void makeDecision() {
    	BasicAlgorithm.makeDecision(algorithmData);
	}
    private void sendMessage() {
    	BasicAlgorithm.sendMessage(algorithmData);
	}
    private void emergencyStop() {
    	BasicAlgorithm.emergencyStop(algorithmData);
	}
    private void sendInstruction() {
    	BasicAlgorithm.sendInstruction(algorithmData);
	}

	public void run() {
		initialise();

		while (true) {
			//read data from sensors into data class
			readSensors();

			if(Thread.interrupted()) {
				emergencyStop();
			}

			makeDecision();

			if(Thread.interrupted()) {
				emergencyStop();
			}

			sendMessage();

			//send instructions to drive
			sendInstruction();

			if(Thread.interrupted()) {
				emergencyStop();
			}
		}
	}
}
