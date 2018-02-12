package uk.ac.cam.cl.group_project.delta.algorithm;

import uk.ac.cam.cl.group_project.delta.DriveInterface;
import uk.ac.cam.cl.group_project.delta.SensorInterface;

public class BasicAlgorithm3 extends Algorithm {
//Basic Algorithm with collision prevention using sensor and predicted predecessor distance
    public BasicAlgorithm3(CommsInterface commsInterface, DriveInterface driveInterface, SensorInterface sensorInterface) {
        super(commsInterface, driveInterface, sensorInterface);
    }

    //combine the front proximity predicted from the vehicle states at the beginning of the previous time preriod,
    //and the sensor proximity data
    private double weightFrontProximity(double predictedFrontProximity, double sensorFrontProximity) {
        return 0.5*predictedFrontProximity+0.5*sensorFrontProximity;
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
            double sensorFrontProximity;

            double predecessorAcceleration;
            double predecessorSpeed;
            double predecessorTurnRate;
            double predecessorChosenAcceleration;
            double predecessorChosenSpeed;
            double predecessorChosenTurnRate;

            double predictedPredecessorMovement;
            double predictedMovement;
            double predictedFrontProximity;
            double weightedFrontProximity;

            double timePeriod = 1;

            //get initial distance reading from sensor
            double previousDistance = sensorInterface.getFrontProximity();
            double previousSpeed = sensorInterface.getSpeed();
            double previousAcceleration = sensorInterface.getAcceleration();

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
                sensorFrontProximity = sensorInterface.getFrontProximity();

                if(Thread.interrupted()) {
                        throw new InterruptedException();
                }
                //decide on chosen acceleration, speed and turnRate
                //calculate the distance us and our predecessor have travelled in the previous time period
                predictedPredecessorMovement = predecessorSpeed * timePeriod + 0.5*predecessorAcceleration*Math.pow(timePeriod,2);
                predictedMovement = previousSpeed * timePeriod + 0.5*previousAcceleration*Math.pow(timePeriod,2);
                predictedFrontProximity = predictedPredecessorMovement - predictedMovement + previousDistance;

                weightedFrontProximity = weightFrontProximity(predictedFrontProximity,sensorFrontProximity);

                //update previous state variables so that they are correct in next time period
                previousDistance = weightedFrontProximity;
                previousSpeed = speed;
                previousAcceleration = acceleration;

                chosenAcceleration = predecessorAcceleration ;
                if(weightedFrontProximity < 5) {
                    chosenAcceleration = chosenAcceleration * weightedFrontProximity/5;
                } else {
                    chosenAcceleration = chosenAcceleration * (0.75+weightedFrontProximity/20.0);
                }
                chosenSpeed = predecessorChosenSpeed;
                chosenTurnRate = predecessorTurnRate;

                if(Thread.interrupted()) {
                    throw new InterruptedException();
                }

                //create and send message to other cars
                VehicleData sendMessageData = new VehicleData(speed, acceleration, turnRate, chosenSpeed, chosenAcceleration, chosenTurnRate);
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