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

		assertEquals(comms.getPredecessorMessages().get(comms.getPredecessorMessages().size()-1), data);
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

		assertEquals(comms.getPredecessorMessages().get(0), data);
	}
}
