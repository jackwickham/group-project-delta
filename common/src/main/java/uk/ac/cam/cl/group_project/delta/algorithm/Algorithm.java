package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

public abstract class Algorithm {
    CommsInterface commsInterface;
    DriveInterface driveInterface;
    SensorInterface sensorInterface;

    public Algorithm(CommsInterface commsInterface, DriveInterface driveInterface, SensorInterface sensorInterface) {
        this.commsInterface = commsInterface;
        this.driveInterface = driveInterface;
        this.sensorInterface = sensorInterface;
    }

    public abstract void run();
}
