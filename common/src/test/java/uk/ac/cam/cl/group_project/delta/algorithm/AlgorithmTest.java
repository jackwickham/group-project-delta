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
	private List<Beacon> beacons;

	public AlgorithmTest(Double frontProximity, Double beaconDist, Double[] message) {
	if(message != null) {
			this.predecessorMessages = Arrays.asList(new VehicleData[]{new VehicleData(message[0], message[1], message[2], message[3],
					message[4], message[5])});
		} else {
			this.predecessorMessages = new ArrayList<>();
		}
		if(beaconDist != null) {
			this.beacons = Arrays.asList(new Beacon[]{new Beacon(0,beaconDist,beaconDist,0.0)});
		} else {
			this.beacons = new ArrayList<>();
		}
		this.frontProximity = frontProximity;
	}

	@Parameterized.Parameters(name = "{index}: Proximity: {0}, Beacons:{1}, Message:{2}")
	public static Collection<Object[]> data() {
		Double[] proximities = new Double[] {null, 0.0, 20.0};
		Double[] beacons = new Double[] {null, 0.0, 20.0};
		Double[][] messages = {null, {0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, {5.0, 2.0, 0.0, 5.0, 2.0, 0.0}};

		ArrayList<Object[]> result = new ArrayList<>();
		for(Double p : proximities) {
			for(Double b : beacons) {
				for(Double[] m : messages) {
					result.add(new Object[]{p, b, m});
				}
			}
		}
		return result;
	}

	@Test
	public void testBasicAlgorithm() {
		SensorInterface sensorInterface = mock(SensorInterface.class);
		when(sensorInterface.getFrontProximity()).thenReturn(frontProximity);
		when(sensorInterface.getBeacons()).thenReturn(beacons);

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
		when(sensorInterface.getBeacons()).thenReturn(beacons);

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
		when(sensorInterface.getBeacons()).thenReturn(beacons);

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
		when(sensorInterface.getBeacons()).thenReturn(beacons);

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
		when(sensorInterface.getBeacons()).thenReturn(beacons);

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
