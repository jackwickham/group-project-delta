package uk.ac.cam.cl.group_project.delta.algorithm;

import org.junit.Test;
import uk.ac.cam.cl.group_project.delta.*;

import java.util.List;

public class AlgorithmTest {
	/**
	 * Test Algorithm with mock classes, which all do nothing / return null
	 */
	@Test
	public void testBasicAlgorithm() {
		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithm, new MockDrive(), new MockSensor(), new MockNetwork());
		algorithm.update(0);
		algorithm.update(10000000);
	}

	@Test
	public void testBasicAlgorithm2() {
		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithm2, new MockDrive(), new MockSensor(), new MockNetwork());
		algorithm.update(0);
		algorithm.update(10000000);
	}

	@Test
	public void testBasicAlgorithm3() {
		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithm3, new MockDrive(), new MockSensor(), new MockNetwork());
		algorithm.update(0);
		algorithm.update(10000000);
	}

	@Test
	public void testBasicAlgorithmPID() {
		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithmPID, new MockDrive(), new MockSensor(), new MockNetwork());
		algorithm.update(0);
		algorithm.update(10000000);
	}

	@Test
	public void testBasicAlgorithmPID2() {
		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithmPID2, new MockDrive(), new MockSensor(), new MockNetwork());
		algorithm.update(0);
		algorithm.update(10000000);
	}

	public static class MockDrive implements DriveInterface {

		@Override
		public void setAcceleration(double acceleration) {

		}

		@Override
		public void setTurnRate(double turnRate) {

		}

		@Override
		public void stop() {

		}
	}

	public static class MockSensor implements SensorInterface {

		@Override
		public Double getFrontProximity() {
			return null;
		}

		@Override
		public List<Beacon> getBeacons() {
			return null;
		}

		@Override
		public double getAcceleration() {
			return 0;
		}

		@Override
		public double getSpeed() {
			return 0;
		}

		@Override
		public double getTurnRate() {
			return 0;
		}
	}

	public static class MockNetwork implements NetworkInterface {

		@Override
		public void sendData(byte[] message) {

		}

		@Override
		public List<MessageReceipt> pollData() {
			return null;
		}
	}
}
