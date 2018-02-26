package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.Log;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Communications;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;

public abstract class Algorithm {

	public final static int ALGORITHM_LOOP_DURATION = 10000000; // 10ms

	protected AlgorithmData algorithmData = new AlgorithmData();

	protected Algorithm(DriveInterface driveInterface, SensorInterface sensorInterface, NetworkInterface networkInterface) {
		algorithmData.controlLayer = new ControlLayer(networkInterface);
		algorithmData.commsInterface = new Communications(algorithmData.controlLayer);
		algorithmData.driveInterface = driveInterface;
		algorithmData.sensorInterface = sensorInterface;
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

	public void initialise() {

	}

	private void readSensors() {
		// try to get predecessors messages, trying next car infront if message null, upto the front of platoon
		// note: leader check not needed as if leader then getPredecessorMessages() returns an empty list
		//TODO: use timestamp in message to decide which to use
		// note: individual algorithms handle case in which no message ever received
		for (VehicleData message : algorithmData.commsInterface.getPredecessorMessages()) {
			algorithmData.receiveMessageData = message;
			if (algorithmData.receiveMessageData != null) {
				algorithmData.predecessorAcceleration = algorithmData.receiveMessageData.getAcceleration();
				algorithmData.predecessorSpeed = algorithmData.receiveMessageData.getSpeed();
				algorithmData.predecessorTurnRate = algorithmData.receiveMessageData.getTurnRate();
				algorithmData.predecessorChosenAcceleration = algorithmData.receiveMessageData.getChosenAcceleration();
				algorithmData.predecessorChosenSpeed = algorithmData.receiveMessageData.getChosenSpeed();
				algorithmData.predecessorChosenTurnRate = algorithmData.receiveMessageData.getChosenTurnRate();
				//break if predecessor has a message otherwise loop to try one vehicle infront
				break;
			}
		}


		// read data from sensors
		algorithmData.acceleration = algorithmData.sensorInterface.getAcceleration();
		algorithmData.speed = algorithmData.sensorInterface.getSpeed();
		algorithmData.turnRate = algorithmData.sensorInterface.getTurnRate();

		//note this could be null
		algorithmData.sensorFrontProximity = algorithmData.sensorInterface.getFrontProximity();

		// get initial distance reading from sensor, distance null if no distance reading
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
		algorithmData.driveInterface.setTurnRate(algorithmData.chosenTurnRate);
	}

	/**
	 * @return time in nanoseconds, if using update method then return set time otherwise return system time
	 */
	protected long getTime() {
		if(algorithmData.notUsingRealTime) {
			return algorithmData.time;
		} else {
			return System.nanoTime();
		}
	}

	/**
	 * Helper function, runs one loop of algorithm
	 * Called by update and run
	 */
	private void runOneLoop() {
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
		algorithmData.notUsingRealTime = true;
		algorithmData.time = time;
		runOneLoop();
	}

	/** Runs algorithm every ALGORITM_LOOP_DURATION  nanoseconds until an emergency occurs
	 */
	public void run() {
		algorithmData.notUsingRealTime = false;
		initialise();
		long startTime = System.nanoTime();

		while (!algorithmData.emergencyOccurred) {
			runOneLoop();
			try {
				long nanosToSleep = ALGORITHM_LOOP_DURATION - (System.nanoTime() - startTime);
				if(nanosToSleep > 0) {
					// Note: integer division desired
					Thread.sleep(nanosToSleep/1000000);
				} else {
					Log.warn(String.format("LOOP_DURATION is too low, algorithm can't keep up (%dms too slow)", -nanosToSleep/1000000));
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
