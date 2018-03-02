package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.Beacon;
import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;

import java.util.List;

//class for storing and passing data used by algorithm
public class AlgorithmData {

	public ControlLayer controlLayer;
	public CommsInterface commsInterface;
	public SensorInterface sensorInterface;
	public DriveInterface driveInterface;

	public VehicleData receiveMessageData;

	// True when an emergency has occurred
	public boolean emergencyOccurred = false;

	//current vehicle state
	public double acceleration;
	public double speed;
	public double turnRate;

	//null if no reading
	public Double sensorFrontProximity;

	public List<Beacon> beacons;
	public Beacon closestBeacon;
	public Double angle;

	//frontProximity derived from beacons and sensor front proximity
	public Double frontProximity;

	public double chosenSpeed;
	public double chosenAcceleration;
	public double chosenTurnRate;


	//predecessor vehicle state
	public double predecessorAcceleration;
	public double predecessorSpeed;
	public double predecessorTurnRate;
	public double predecessorChosenAcceleration;
	public double predecessorChosenSpeed;
	public double predecessorChosenTurnRate;

	public double predictedPredecessorMovement;
	public double predictedMovement;
	public Double predictedFrontProximity;

	//null if no previous distance reading
	public Double previousDistance;
	public double previousSpeed;
	public double previousAcceleration;

	public Double previousAngle;

	//sum of errors used by PID
	public Double errorSum;
}
