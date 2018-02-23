package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import org.junit.Test;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.MessageType;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Packet;

public class PacketTest {

	@Test
	public void isEmergencyPacketTest() {
		assertTrue(
				Packet.isEmergencyMessage(
						Packet.createPacket(new EmergencyMessage(), 0, 0)));
	}

	@Test
	public void createPacketTest() {
		int vehicle = 100, platoon = 500;
		byte[] bytes = Packet.createPacket(new EmergencyMessage(), vehicle, platoon);

		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		int initial = buffer.getInt();
		assertEquals(MessageType.valueOf(initial >>> 24), MessageType.Emergency);
		assertEquals(buffer.getInt(), platoon);
		assertEquals(buffer.getInt(), vehicle);
		assertEquals(buffer.position(), initial & 0x00FFFFFF);
	}

	@Test
	public void parsePacketTest() {
		int vehicle = 100, platoon = 500;
		byte[] bytes = Packet.createPacket(new EmergencyMessage(), vehicle, platoon);

		Packet p = new Packet(new MessageReceipt(bytes));

		assertEquals(p.length, Packet.SIZE_OF_HEADER);
		assertEquals(p.platoonId, platoon);
		assertEquals(p.vehicleId, vehicle);
		assertNotNull(p.message);
		assertEquals(p.message.getType(), MessageType.Emergency);
	}
}
