package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class RequestToMergeMessageTest {

	@Test
	public void dataConstructorTest() {
		List<Integer> platoon = Arrays.asList(1, 5, 3, 10);
		int transactionId = 10304;
		int platoonId = 101;
		RequestToMergeMessage msg = new RequestToMergeMessage(platoon, platoonId, transactionId);

		assertEquals(msg.getType(), MessageType.RequestToMerge);
		assertEquals(platoon, msg.getNewPlatoon());
		assertEquals(transactionId, msg.getTransactionId());
		assertEquals(platoonId, msg.getMergingPlatoonId());
	}

	@Test
	public void appendToBufferTest() {
		List<Integer> platoon = Arrays.asList(1, 5, 3, 10);
		int transactionId = 10304;
		int platoonId = 101;
		RequestToMergeMessage msg = new RequestToMergeMessage(platoon, platoonId, transactionId);

		ByteBuffer bytes = ByteBuffer.allocate(200);
		msg.appendToBuffer(bytes);
		bytes.rewind();
		assertEquals(bytes.getInt(), transactionId);
		assertEquals(bytes.getInt(), platoonId);
		assertEquals(bytes.getInt(), 4); // Length
		assertEquals(bytes.getInt(), 1);
		assertEquals(bytes.getInt(), 5);
		assertEquals(bytes.getInt(), 3);
		assertEquals(bytes.getInt(), 10);
	}

	@Test
	public void bufferConstructorTest() {
		List<Integer> platoon = Arrays.asList(1, 5, 3, 10);
		int transactionId = 10304;
		int platoonId = 101;

		ByteBuffer bytes = ByteBuffer.allocate(200);
		bytes.putInt(transactionId);
		bytes.putInt(platoonId);
		bytes.putInt(platoon.size());
		bytes.putInt(platoon.get(0));
		bytes.putInt(platoon.get(1));
		bytes.putInt(platoon.get(2));
		bytes.putInt(platoon.get(3));
		bytes.rewind();

		RequestToMergeMessage msg = new RequestToMergeMessage(bytes);

		assertEquals(msg.getTransactionId(), transactionId);
		assertEquals(msg.getMergingPlatoonId(), platoonId);
		assertEquals(msg.getNewPlatoon(), platoon);
	}

}
