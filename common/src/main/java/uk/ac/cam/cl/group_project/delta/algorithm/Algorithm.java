package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.Log;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Communications;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;

public abstract class Algorithm {

	public final static int ALGORITHM_LOOP_DURATION = 10000000; // 10ms

	protected AlgorithmData algorithmData;

	protected Algorithm(DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
		algorithmData.commsInterface = new Communications(new ControlLayer(networkInterface));
		algorithmData.driveInterface = driveInterface;
		algorithmData.sensorInterface = sensorInterface;
		algorithmData = new AlgorithmData();
	}

	/**
	 *Builds and returns algorithm of type specified by AlgorithmEnum input
	 */
	public static Algorithm createAlgorithm(AlgorithmEnum algorithmEnum, DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
		switch (algorithmEnum) {
			case BasicAlgorithm: return new BasicAlgorithm(driveInterface, sensorInterface, networkInterface);
			case BasicAlgorithm2: return new BasicAlgorithm2(driveInterface,sensorInterface,networkInterface);
			case BasicAlgorithm3: return new BasicAlgorithm3(driveInterface,sensorInterface,networkInterface);
			case BasicAlgorithmPID: return new BasicAlgorithmPID(driveInterface,sensorInterface,networkInterface);
			case BasicAlgorithmPID2: return new BasicAlgorithmPID2(driveInterface,sensorInterface,networkInterface);
		}
		return null;
	}

	public static AlgorithmEnum[] getAlgorithmList() {
		return AlgorithmEnum.values();
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
		algorithmData.previousDistance = algorithmData.sensorFrontProximity;
		algorithmData.previousSpeed = algorithmData.speed;
		algorithmData.previousAcceleration = algorithmData.acceleration;
	}

	protected abstract void makeDecision();

	private void sendMessage() {
		// create and send message to other cars
		VehicleData sendMessageData;
		if(algorithmData.commsInterface.isLeader()) {
			sendMessageData = new VehicleData(algorithmData.speed, algorithmData.acceleration,
					algorithmData.turnRate, algorithmData.speed, algorithmData.acceleration,
					algorithmData.turnRate);
		} else {
			sendMessageData = new VehicleData(algorithmData.speed, algorithmData.acceleration,
					algorithmData.turnRate, algorithmData.chosenSpeed, algorithmData.chosenAcceleration,
					algorithmData.chosenTurnRate);
		}
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

	protected long getTime() {
		if(algorithmData.usingUpdate) {
			return algorithmData.time;
		}
		 else {
			return System.nanoTime();
		}
	}

	/**
	 * Run one loop of algorithm
	 */
	private void loop() {
		// read data from sensors into data class
		readSensors();

		if (Thread.interrupted()) {
			emergencyStop();
		}

		if(!algorithmData.commsInterface.isLeader()) {
			makeDecision();
		}

		if (Thread.interrupted()) {
			emergencyStop();
		}

		sendMessage();

		// send instructions to drive if not leader
		if(!algorithmData.commsInterface.isLeader()) {
			sendInstruction();
		}

		if (Thread.interrupted()) {
			emergencyStop();
		}
	}
	/**
	 * Runs one loop of algorithm
	 * @param time -time in nanoseconds, algorithm assumes this is the current time
	 */
	public void update(long time) {
		algorithmData.usingUpdate = true;
		algorithmData.time = time;
		loop();
	}

	public void run() {
		algorithmData.usingUpdate = false;
		initialise();
		long startTime = System.nanoTime();

		while (!algorithmData.emergencyOccurred) {
			loop();
			try {
				long nanosToSleep = System.nanoTime() - startTime - ALGORITHM_LOOP_DURATION;
				if(nanosToSleep > 0) {
					// Note: integer division desired
					Thread.sleep(nanosToSleep/1000000);
				} else {
					Log.warn("LOOP_DURATION is too low, algorithm can't keep up");
				}
			} catch (InterruptedException e) {
				emergencyStop();
				break;
			}
			startTime = System.nanoTime();
		}
		Log.debug("Algorithm has finished running");
	}
}
