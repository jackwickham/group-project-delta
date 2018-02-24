package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

//class for storing and passing data used by algorithm
public class AlgorithmData {

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
	double sensorFrontProximity;
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
	double predictedFrontProximity;

	double timePeriod;

	double previousDistance;
	double previousSpeed;
	double previousAcceleration;

	MiniPID miniPID;

	long time;

	//flag which is true if update method in Algorithm is being used
	boolean usingUpdate;
}
