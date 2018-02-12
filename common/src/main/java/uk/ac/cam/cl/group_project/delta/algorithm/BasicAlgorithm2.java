package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

public class BasicAlgorithm2 extends Algorithm {
//Basic Algorithm with collision prevention
    public BasicAlgorithm2(CommsInterface commsInterface, DriveInterface driveInterface, SensorInterface sensorInterface) {
        super(commsInterface, driveInterface, sensorInterface);
    }

    @Override
    public void run() {
        try {
            double acceleration;
            double speed;
            double turnRate;
            double chosenSpeed;
            double chosenAcceleration;
            double chosenTurnRate;
            double frontProximity;

            double predecessorAcceleration;
            double predecessorSpeed;
            double predecessorTurnRate;
            double predecessorChosenAcceleration;
            double predecessorChosenSpeed;
            double predecessorChosenTurnRate;

            while (true) {
                //read data from predecessor's message
                VehicleData recieveMessageData = commsInterface.getPredecessorMessage(1);
                predecessorAcceleration = recieveMessageData.getAcceleration();
                predecessorSpeed = recieveMessageData.getSpeed();
                predecessorTurnRate = recieveMessageData.getTurnRate();
                predecessorChosenAcceleration = recieveMessageData.getChosenAcceleration();
                predecessorChosenSpeed = recieveMessageData.getChosenSpeed();
                predecessorChosenTurnRate = recieveMessageData.getChosenTurnRate();

                //read data from sensors
                acceleration = sensorInterface.getAcceleration();
                speed = sensorInterface.getSpeed();
                turnRate = sensorInterface.getTurnRate();
                frontProximity = sensorInterface.getFrontProximity();

                if(Thread.interrupted()) {
                        throw new InterruptedException();
                }
                //decide on chosen acceleration, speed and turnRate
                chosenAcceleration = predecessorAcceleration ;
                if(frontProximity< 5) {
                    chosenAcceleration = chosenAcceleration * frontProximity/5.0;
                } else {
                        chosenAcceleration = chosenAcceleration * (0.75+frontProximity/20.0);
                }
                chosenSpeed = predecessorChosenSpeed;
                chosenTurnRate = predecessorTurnRate;

                if(Thread.interrupted()) {
                    throw new InterruptedException();
                }

                //create and send message to other cars
                VehicleData sendMessageData = 
                		new VehicleData(speed, acceleration, turnRate, 
                				chosenSpeed, chosenAcceleration, chosenTurnRate);
                commsInterface.sendMessage(sendMessageData);

                if(Thread.interrupted()) {
                    throw new InterruptedException();
                }
                //send instructions to drive
                driveInterface.setAcceleration(chosenAcceleration);
                driveInterface.setTurnRate(chosenAcceleration);
            }
        } catch(InterruptedException e){
            driveInterface.stop();
        }
    }

}
