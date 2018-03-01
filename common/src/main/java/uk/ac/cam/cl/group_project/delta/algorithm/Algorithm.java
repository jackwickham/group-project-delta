package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.*;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Communications;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;

import java.util.ArrayList;

public abstract class Algorithm {

	public static final int ALGORITHM_LOOP_DURATION = 50000000; // 50ms

	public AlgorithmData algorithmData = new AlgorithmData();
	protected FrontVehicleRoute frontVehicleRoute;

	protected Algorithm(DriveInterface driveInterface,
			SensorInterface sensorInterface,
			NetworkInterface networkInterface,
			BeaconInterface beacons,
			FrontVehicleRoute.RouteNumber routeNumber) {
		algorithmData.controlLayer = new ControlLayer(networkInterface, beacons);
		algorithmData.commsInterface = new Communications(algorithmData.controlLayer);
		algorithmData.driveInterface = driveInterface;
		algorithmData.sensorInterface = sensorInterface;
		frontVehicleRoute = new FrontVehicleRoute(algorithmData, ALGORITHM_LOOP_DURATION, routeNumber);
	}

	/**
	 * Default constructor, uses ROUTE_ZERO
	 */
	protected Algorithm(DriveInterface driveInterface,
						SensorInterface sensorInterface,
						NetworkInterface networkInterface,
						BeaconInterface beacons) {
		this(driveInterface,
				sensorInterface,
				networkInterface,
				beacons,
				FrontVehicleRoute.RouteNumber.ROUTE_ZERO);
	}

	/**
	 *Builds and returns algorithm of type specified by AlgorithmEnum input
	 */
	public static Algorithm createAlgorithm(
			AlgorithmEnum algorithmEnum,
			DriveInterface driveInterface,
			SensorInterface sensorInterface,
			NetworkInterface networkInterface,
			BeaconInterface beacons,
			FrontVehicleRoute.RouteNumber routeNumber) {
		switch (algorithmEnum) {
		case BasicAlgorithm:
			return new BasicAlgorithm(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
		case BasicAlgorithm2:
			return new BasicAlgorithm2(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
		case BasicAlgorithm3:
			return new BasicAlgorithm3(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
		case BasicAlgorithmPID:
			return new BasicAlgorithmPID(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
		case BasicAlgorithmPID2:
			return new BasicAlgorithmPID2(driveInterface, sensorInterface, networkInterface, beacons, routeNumber);
		}
		return null;
	}

	/**
	 *Builds and returns algorithm of type specified by AlgorithmEnum input
	 *By default, uses ROUTE_ZERO, which makes the front vehicle do nothing
	 */
	public static Algorithm createAlgorithm(
			AlgorithmEnum algorithmEnum,
			DriveInterface driveInterface,
			SensorInterface sensorInterface,
			NetworkInterface networkInterface,
			BeaconInterface beacons) {
		return createAlgorithm(algorithmEnum,
				driveInterface,
				sensorInterface,
				networkInterface,
				beacons,
				FrontVehicleRoute.RouteNumber.ROUTE_ZERO);
	}

	public static AlgorithmEnum[] getAlgorithmList() {
		return AlgorithmEnum.values();
	}

	/** Sets an algorithms parameter.
	 *  Will do nothing if that algorithm does not have the parameter **/
	public abstract void setParameter(ParameterEnum parameterEnum, double value);

	/**
	 * @param parameterEnum enum for parameter
	 * @return if algorithm uses parameter then its value otherwise null
	 */
	public abstract Double getParameter(ParameterEnum parameterEnum);

	/**
	 * @return Array of all parameters this algorithm uses
	 */
	public abstract ParameterEnum[] getParameterList();

	public void initialise() {
		algorithmData.travelDist = 0;
		algorithmData.turnBuffer = new ArrayList<>();
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

		algorithmData.beacons = algorithmData.sensorInterface.getBeacons();

		algorithmData.previousAngle = algorithmData.angle;
		//find closest beacon within maximum sensor distance
		double min = Double.POSITIVE_INFINITY;
		for (Beacon beacon : algorithmData.beacons) {
			if (beacon.getDistanceLowerBound() <= min) {
				min = beacon.getDistanceLowerBound();
				algorithmData.closestBeacon = beacon;
				algorithmData.angle = algorithmData.closestBeacon.getAngle();
			}
		}

		//note this could be null
		algorithmData.sensorFrontProximity = algorithmData.sensorInterface.getFrontProximity();

		//combines beacon distance lower bound and sensor front proximity
		if (algorithmData.closestBeacon != null && algorithmData.sensorFrontProximity != null) {
			algorithmData.frontProximity = 0.5 * algorithmData.closestBeacon.getDistanceLowerBound() + 0.5 * algorithmData.sensorFrontProximity;
		} else if (algorithmData.closestBeacon != null) {
			algorithmData.frontProximity = algorithmData.closestBeacon.getDistanceLowerBound();
		} else if (algorithmData.sensorFrontProximity != null) {
			algorithmData.frontProximity = algorithmData.sensorFrontProximity;
		} else {
			algorithmData.frontProximity = null;
		}
		// get initial distance reading from sensor, distance null if no distance reading
		algorithmData.previousDistance = algorithmData.frontProximity;
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

		boolean shouldSendInstruction;
		if(!algorithmData.commsInterface.isLeader()) {
			makeDecision();
			shouldSendInstruction = true;
		} else {
			shouldSendInstruction = frontVehicleRoute.nextStep();
		}

		if (Thread.interrupted()) {
			emergencyStop();
		}

		sendMessage();

		// send instructions to drive if not leader
		if(shouldSendInstruction) {
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
