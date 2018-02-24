package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class AcceptToMergeMessageTest {

	@Test
	public void dataConstructorTest() {
		List<Integer> platoon = Arrays.asList(1, 5, 3, 10);
		int transactionId = 10304;
		Map<Integer, Integer> renames = new HashMap<>();
		renames.put(100, 200);
		renames.put(300, 400);
		AcceptToMergeMessage msg = new AcceptToMergeMessage(true, platoon, renames, transactionId);

		assertEquals(msg.getType(), MessageType.AcceptToMerge);
		assertEquals(transactionId, msg.getTransactionId());
		assertEquals(platoon, msg.getMainPlatoon());
		assertEquals(renames, msg.getRenames());
	}

	@Test
	public void appendToBufferTest() {
		List<Integer> platoon = Arrays.asList(1, 5, 3, 10);
		int transactionId = 10304;
		Map<Integer, Integer> renames = new HashMap<>();
		renames.put(100, 200);
		renames.put(300, 400);
		AcceptToMergeMessage msg = new AcceptToMergeMessage(true, platoon, renames, transactionId);

		ByteBuffer bytes = ByteBuffer.allocate(200);
		msg.appendToBuffer(bytes);
		bytes.rewind();
		assertEquals(bytes.getInt(), transactionId);
		assertEquals(bytes.getInt(), (1<<24) | 4); // Length
		assertEquals(bytes.getInt(), 1);
		assertEquals(bytes.getInt(), 5);
		assertEquals(bytes.getInt(), 3);
		assertEquals(bytes.getInt(), 10);
		assertEquals(bytes.getInt(), 2);
		assertEquals(bytes.getInt(), 100);
		assertEquals(bytes.getInt(), 200);
		assertEquals(bytes.getInt(), 300);
		assertEquals(bytes.getInt(), 400);
	}

	@Test
	public void bufferConstructorTest() {
		List<Integer> platoon = Arrays.asList(1, 5, 3, 10);
		int transactionId = 10304;
		boolean isAccepted = true;

		Map<Integer, Integer> renames = new HashMap<>();
		renames.put(100, 200);
		renames.put(300, 400);

		ByteBuffer bytes = ByteBuffer.allocate(200);
		bytes.putInt(transactionId);
		bytes.putInt(((isAccepted)? 1<<24 : 0) | platoon.size());
		bytes.putInt(platoon.get(0));
		bytes.putInt(platoon.get(1));
		bytes.putInt(platoon.get(2));
		bytes.putInt(platoon.get(3));
		bytes.putInt(renames.size());
		bytes.putInt(100);
		bytes.putInt(200);
		bytes.putInt(300);
		bytes.putInt(400);
		bytes.rewind();

		AcceptToMergeMessage msg = new AcceptToMergeMessage(bytes);

		assertEquals(msg.getTransactionId(), transactionId);
		assertEquals(msg.getMainPlatoon(), platoon);
		assertEquals(msg.getRenames(), renames);

	}

}
