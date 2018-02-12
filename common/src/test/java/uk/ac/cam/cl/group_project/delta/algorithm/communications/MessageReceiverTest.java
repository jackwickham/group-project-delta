package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.group_project.delta.algorithm.communications.ControlLayer;

public class MessageReceiverTest {

	
	@Test
	public void Test() {
		
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
