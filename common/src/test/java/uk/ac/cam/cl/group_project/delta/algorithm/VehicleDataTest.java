package uk.ac.cam.cl.group_project.delta.algorithm;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

import uk.ac.cam.cl.group_project.delta.algorithm.VehicleData;

public class VehicleDataTest {
	
	@Test
	public void appendToBufferTest() {
		double speed = 1.0, acceleration = 2.0, turnRate = 3.0, chosenSpeed = 4.0;
		double chosenAcceleration = 5.0, chosenTurnRate = 6.0;
		
		VehicleData md = new VehicleData(
				speed, acceleration, turnRate, 
				chosenSpeed, chosenAcceleration, chosenTurnRate);
		ByteBuffer bytes = ByteBuffer.allocate(8*6+1);
		md.appendToBuffer(bytes);
		bytes.rewind();
		assertEquals(speed, bytes.getDouble(), 0);
		assertEquals(acceleration, bytes.getDouble(), 0);
		assertEquals(turnRate, bytes.getDouble(), 0);
		assertEquals(chosenSpeed, bytes.getDouble(), 0);
		assertEquals(chosenAcceleration, bytes.getDouble(), 0);
		assertEquals(chosenTurnRate, bytes.getDouble(), 0);
		
	}
	@Test
	public void generateDataFromBytesTest() {
		double speed = 1.0, acceleration = 2.0, turnRate = 3.0, chosenSpeed = 4.0;
		double chosenAcceleration = 5.0, chosenTurnRate = 6.0;
		
		ByteBuffer bytes = ByteBuffer.allocate(8*6+1);
		bytes.putDouble(speed);
		bytes.putDouble(acceleration);
		bytes.putDouble(turnRate);
		bytes.putDouble(chosenSpeed);
		bytes.putDouble(chosenAcceleration);
		bytes.putDouble(chosenTurnRate);
		bytes.rewind();
		
		VehicleData md = new VehicleData(bytes);
		
		assertEquals(speed, md.getSpeed(), 0);
		assertEquals(acceleration, md.getAcceleration(), 0);
		assertEquals(turnRate, md.getTurnRate(), 0);
		assertEquals(chosenSpeed, md.getChosenSpeed(), 0);
		assertEquals(chosenAcceleration, md.getChosenAcceleration(), 0);
		assertEquals(chosenTurnRate, md.getChosenTurnRate(), 0);
	}

}
