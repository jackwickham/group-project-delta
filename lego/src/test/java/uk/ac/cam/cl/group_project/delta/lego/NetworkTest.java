package uk.ac.cam.cl.group_project.delta.lego;

import org.junit.Test;
import static org.junit.Assert.*;

public class NetworkTest {
	@Test
	public void broadcastConversionTestUnaligned() {
		byte[] testIp = {10, 0, 2, 3};
		byte[] expectedResult = {10, 0, (byte) 2 | 0x0F, (byte) 0xFF};
		Network.convertToBroadcastAddress(testIp, 20);
		assertArrayEquals("Incorrect broadcast IP with unaligned netmask", expectedResult, testIp);
	}

	@Test
	public void broadcastConversionTestByteAligned() {
		byte[] testIp = {10, 0, 2, 3};
		byte[] expectedResult = {10, 0, 2, (byte) 0xFF};
		Network.convertToBroadcastAddress(testIp, 24);
		assertArrayEquals("Incorrect broadcast IP when netmask is multiple of 8", expectedResult, testIp);
	}
}
