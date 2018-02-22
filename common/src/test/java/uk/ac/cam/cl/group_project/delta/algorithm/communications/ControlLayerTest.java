package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;
import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.ac.cam.cl.group_project.delta.algorithm.VehicleData;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;

public class ControlLayerTest {


	@Test
	public void sendMessageTest() {
		List<Integer> initialPlatoon = Arrays.asList(100, 200);
		NetworkInterface network = mock(NetworkInterface.class);
		
		VehicleData data = new VehicleData(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
		
		ControlLayer control = new ControlLayer(network, 200, 123, initialPlatoon);
		
		control.sendMessage(data);
		
		ArgumentCaptor<byte[]> argument = ArgumentCaptor.forClass(byte[].class);
		verify(network).sendData(argument.capture());
		
		byte[] byteData = argument.getValue();
		Packet p = new Packet(new MessageReceipt(byteData));
		assertEquals(p.vehicleId, 200);
		assertEquals(p.platoonId, 123);
		assertEquals(p.message.getType(), MessageType.Data);
		
		assertEquals(data.getSpeed(), ((VehicleData) p.message).getSpeed(), 0.0);
		assertEquals(data.getChosenAcceleration(), ((VehicleData)p.message).getChosenAcceleration(), 0.0);
	}

	@Test
	public void updateMessagesDataTest() {
		List<Integer> initialPlatoon = Arrays.asList(100, 200);
		NetworkInterface network = mock(NetworkInterface.class);
		
		VehicleData data = new VehicleData(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
		
		when(network.pollData())
			.thenReturn(
				Arrays.asList(
						new MessageReceipt(
								Packet.createPacket(data, 100, 123))));
		
		ControlLayer control = new ControlLayer(network, 200, 123, initialPlatoon);
		
		control.updateMessages();
		
		assertEquals(data.getSpeed(), control.getPlatoonLookup().get(1).getSpeed(), 0.0);
		assertEquals(data.getChosenAcceleration(), 
				control.getPlatoonLookup().get(1).getChosenAcceleration(), 0.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void sendBlankMergingMessageTest() {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(101);
		assertEquals(ControlLayer.getFirstInt(b.array()), 101);
		
		// Throws Exception
		ControlLayer.getFirstInt(new byte[0]);
	}
	
	@Test
	public void sortMapByValuesTest() {
		Map<Integer, Integer> testMap = new HashMap<>();
		testMap.put(283, 3);
		testMap.put(124, 7);
		testMap.put(8765, 1);
		testMap.put(643, 5);
		testMap.put(9764, 2);
		
		List<Map.Entry<Integer, Integer>> sortedPairs = 
				ControlLayer.sortMapByValues(testMap);
		
		assertEquals(sortedPairs.get(0).getKey().intValue(), 8765);
		assertEquals(sortedPairs.get(1).getKey().intValue(), 9764);
		assertEquals(sortedPairs.get(2).getKey().intValue(), 283);
		assertEquals(sortedPairs.get(3).getKey().intValue(), 643);
		assertEquals(sortedPairs.get(4).getKey().intValue(), 124);
	}

}
