package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;

//class for storing and passing data used by algorithm
public class AlgorithmData {

	ControlLayer controlLayer;
	CommsInterface commsInterface;
	SensorInterface sensorInterface;
	DriveInterface driveInterface;

	VehicleData receiveMessageData;

	// True when an emergency has occurred
	boolean emergencyOccurred = false;

	//current vehicle state
	double acceleration;
	double speed;
	double turnRate;

	//null if no reading
	Double sensorFrontProximity;

	double chosenSpeed;
	double chosenAcceleration;
	double chosenTurnRate;


	//predecessor vehicle state
	double predecessorAcceleration;
	double predecessorSpeed;
	double predecessorTurnRate;
	double predecessorChosenAcceleration;
	double predecessorChosenSpeed;
	double predecessorChosenTurnRate;

	double predictedPredecessorMovement;
	double predictedMovement;
	Double predictedFrontProximity;

	//null if no previous distance reading
	Double previousDistance;
	double previousSpeed;
	double previousAcceleration;

	MiniPID miniPID;

	//only used if update method calls getTime
	long time;

	//flag which is true if update method in Algorithm is being used
	boolean usingUpdate;
}
