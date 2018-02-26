package uk.ac.cam.cl.group_project.delta.algorithm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.*;

import uk.ac.cam.cl.group_project.delta.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class AlgorithmTest {
	/**
	 * Test Algorithm with mock classes, which all do nothing / return 0 / null
	 * All tests just run algorithm twice not as leader with a range of sensor data
	 */

	private Double frontProximity;
	private List<VehicleData> predecessorMessages;

	public AlgorithmTest(Double frontProximity, Boolean Message, double speed, double acceleration, double turnRate, double chosenSpeed,
						 double chosenAcceleration, double chosenTurnRate) {
		this.frontProximity = frontProximity;
		if(Message) {
			this.predecessorMessages = Arrays.asList(new VehicleData[]{new VehicleData(speed, acceleration, turnRate, chosenSpeed,
					chosenAcceleration, chosenTurnRate)});
		} else {
			this.predecessorMessages = new ArrayList<>();
		}
	}

	@Parameterized.Parameters(name = "{index}: Proximity: {0}, Predecessor Message: {1}: " +
			"Speed: {2}, Acceleration:{3}, turnRate: {4}, ChosenSpeed: {5}, ChosenAcceleration: {6}, ChosenTurnRate: {7}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{null, false, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, {0.0, false, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, {20.0, false, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
				{null, true, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, {0.0, true, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, {20.0, true, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
				{null, true, 20.0, 2.0, 0.0, 20.0, 2.0, 0.0}, {0.0, true, 20.0, 2.0, 0.0, 20.0, 2.0, 0.0}, {20.0, true, 20.0, 2.0, 0.0, 20.0, 2.0, 0.0}});
	}

	@Test
	public void testBasicAlgorithm() {
		SensorInterface sensorInterface = mock(SensorInterface.class);
		when(sensorInterface.getFrontProximity()).thenReturn(frontProximity);

		DriveInterface driveInterface = mock(DriveInterface.class);
		NetworkInterface networkInterface = mock(NetworkInterface.class);

		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithm, driveInterface, sensorInterface, networkInterface);

		CommsInterface mockCommsInterface = mock(CommsInterface.class);
		when(mockCommsInterface.isLeader()).thenReturn(false);
		when(mockCommsInterface.getPredecessorMessages()).thenReturn(predecessorMessages);

		algorithm.algorithmData.commsInterface = mockCommsInterface;
		algorithm.initialise();
		algorithm.update(0);
		algorithm.update(10000000);
	}

	@Test
	public void testBasicAlgorithm2() {
		SensorInterface sensorInterface = mock(SensorInterface.class);
		when(sensorInterface.getFrontProximity()).thenReturn(frontProximity);

		DriveInterface driveInterface = mock(DriveInterface.class);
		NetworkInterface networkInterface = mock(NetworkInterface.class);

		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithm2, driveInterface, sensorInterface, networkInterface);

		CommsInterface mockCommsInterface = mock(CommsInterface.class);
		when(mockCommsInterface.isLeader()).thenReturn(false);
		when(mockCommsInterface.getPredecessorMessages()).thenReturn(predecessorMessages);

		algorithm.algorithmData.commsInterface = mockCommsInterface;
		algorithm.initialise();
		algorithm.update(0);
		algorithm.update(10000000);
	}

	@Test
	public void testBasicAlgorithm3() {
		SensorInterface sensorInterface = mock(SensorInterface.class);
		when(sensorInterface.getFrontProximity()).thenReturn(frontProximity);

		DriveInterface driveInterface = mock(DriveInterface.class);
		NetworkInterface networkInterface = mock(NetworkInterface.class);

		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithm3, driveInterface, sensorInterface, networkInterface);


		CommsInterface mockCommsInterface = mock(CommsInterface.class);
		when(mockCommsInterface.isLeader()).thenReturn(false);
		when(mockCommsInterface.getPredecessorMessages()).thenReturn(predecessorMessages);

		algorithm.algorithmData.commsInterface = mockCommsInterface;
		algorithm.initialise();
		algorithm.update(0);
		algorithm.update(10000000);
	}

	@Test
	public void testBasicAlgorithmPID() {
		SensorInterface sensorInterface = mock(SensorInterface.class);
		when(sensorInterface.getFrontProximity()).thenReturn(frontProximity);

		DriveInterface driveInterface = mock(DriveInterface.class);
		NetworkInterface networkInterface = mock(NetworkInterface.class);

		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithmPID, driveInterface, sensorInterface, networkInterface);


		CommsInterface mockCommsInterface = mock(CommsInterface.class);
		when(mockCommsInterface.isLeader()).thenReturn(false);
		when(mockCommsInterface.getPredecessorMessages()).thenReturn(predecessorMessages);

		algorithm.algorithmData.commsInterface = mockCommsInterface;
		algorithm.initialise();
		algorithm.update(0);
		algorithm.update(10000000);
	}

	@Test
	public void testBasicAlgorithmPID2() {
		SensorInterface sensorInterface = mock(SensorInterface.class);
		when(sensorInterface.getFrontProximity()).thenReturn(frontProximity);

		DriveInterface driveInterface = mock(DriveInterface.class);
		NetworkInterface networkInterface = mock(NetworkInterface.class);

		Algorithm algorithm = Algorithm.createAlgorithm(AlgorithmEnum.BasicAlgorithmPID2, driveInterface, sensorInterface, networkInterface);


		CommsInterface mockCommsInterface = mock(CommsInterface.class);
		when(mockCommsInterface.isLeader()).thenReturn(false);
		when(mockCommsInterface.getPredecessorMessages()).thenReturn(predecessorMessages);

		algorithm.algorithmData.commsInterface = mockCommsInterface;
		algorithm.initialise();
		algorithm.update(0);
		algorithm.update(10000000);
	}
}
