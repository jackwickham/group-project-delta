package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import uk.ac.cam.cl.group_project.delta.algorithm.VehicleData;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Communications;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.PlatoonLookup;

public class CommunicationsTest {

	@Test
	public void getLeaderMessageTest() {
		ControlLayer controlLayer = mock(ControlLayer.class);
		when(controlLayer.getCurrentPosition()).thenReturn(2);
		
		PlatoonLookup lookup = new PlatoonLookup();
		
		when(controlLayer.getPlatoonLookup()).thenReturn(lookup);
		
		VehicleData data = new VehicleData(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		lookup.put(2, data);
		
		Communications comms = new Communications(controlLayer);
		
		assertEquals(comms.getLeaderMessage(), data);
	}
	
	@Test
	public void getPredecessorMessageValidTest() {
		ControlLayer controlLayer = mock(ControlLayer.class);
		when(controlLayer.getCurrentPosition()).thenReturn(2);
		
		PlatoonLookup lookup = new PlatoonLookup();
		
		when(controlLayer.getPlatoonLookup()).thenReturn(lookup);
		
		VehicleData data = new VehicleData(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		lookup.put(1, data);
		
		Communications comms = new Communications(controlLayer);
		
		assertEquals(comms.getPredecessorMessage(1), data);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getPredecessorMessageNegativeTest() {
		ControlLayer controlLayer = mock(ControlLayer.class);
		when(controlLayer.getCurrentPosition()).thenReturn(2);
		when(controlLayer.getPlatoonLookup()).thenReturn(new PlatoonLookup());
		
		Communications comms = new Communications(controlLayer);
		
		// Throws IllegalArgumentException
		comms.getPredecessorMessage(-1);
	}

	@Test
	public void getPredecessorMessageOutOfBoundsTest() {
		ControlLayer controlLayer = mock(ControlLayer.class);
		when(controlLayer.getCurrentPosition()).thenReturn(2);
		when(controlLayer.getPlatoonLookup()).thenReturn(new PlatoonLookup());
		
		Communications comms = new Communications(controlLayer);
		
		assertNull(comms.getPredecessorMessage(5));
	}
}
