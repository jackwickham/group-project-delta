package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

import uk.ac.cam.cl.group_project.delta.MessageReceipt;
import uk.ac.cam.cl.group_project.delta.algorithm.VehicleData;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.MessageType;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.Packet;

public class PacketTest {

	@Test
	public void isEmergencyPacketTest() {
		assertTrue(
				Packet.isEmergencyMessage(
						Packet.createPacket(new byte[0], 0, 0, MessageType.Emergency)));
	}

	@Test
	public void createPacketTest() {
		int vehicle = 100, platoon = 500;
		byte[] bytes = Packet.createPacket(new byte[0], vehicle, platoon, MessageType.RequestToMerge);

		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		int initial = buffer.getInt();
		assertEquals(MessageType.valueOf(initial >>> 24), MessageType.RequestToMerge);
		assertEquals(buffer.getInt(), platoon);
		assertEquals(buffer.getInt(), vehicle);
		assertEquals(buffer.position(), initial & 0x00FFFFFF);
	}

	@Test
	public void parsePacketTest() {
		int vehicle = 100, platoon = 500;
		MessageType mt = MessageType.RequestToMerge;
		byte[] bytes = Packet.createPacket(new byte[0], vehicle, platoon, mt);

		Packet p = new Packet(new MessageReceipt(bytes));

		assertEquals(p.length, Packet.SIZE_OF_HEADER);
		assertEquals(p.platoonId, platoon);
		assertEquals(p.vehicleId, vehicle);
		assertEquals(p.type, mt);
		assertNotNull(p.payload);
		assertNull(p.message);
		assertEquals(p.payload.length, 0);
	}

	@Test
	public void parseDataPacketTest() {
		int vehicle = 100, platoon = 500;
		VehicleData md = new VehicleData(0.0, 1.0, 2.0, 3.0, 4.0, 5.0);
		byte[] bytes = Packet.createDataPacket(md, vehicle, platoon);

		Packet p = new Packet(new MessageReceipt(bytes));

		assertEquals(p.platoonId, platoon);
		assertEquals(p.vehicleId, vehicle);
		assertEquals(p.type, MessageType.Data);
		assertNull(p.payload);
		assertNotNull(p.message);
	}
}
