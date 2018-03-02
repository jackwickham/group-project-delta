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
	private AlgorithmEnum algorithmEnum;

	public AlgorithmTest(AlgorithmEnum algorithmEnum, Double frontProximity, Double beaconDist, Double[] message) {
		this.algorithmEnum = algorithmEnum;
		if (message != null) {
			this.predecessorMessages = Arrays.asList(new VehicleData(message[0], message[1], message[2], message[3],
					message[4], message[5]));
		} else {
			this.predecessorMessages = new ArrayList<>();
		}
		if (beaconDist != null) {
			this.beacons = Arrays.asList(new Beacon(0, beaconDist, beaconDist, 0.0));
		} else {
			this.beacons = new ArrayList<>();
		}
		this.frontProximity = frontProximity;
	}

	@Parameterized.Parameters(name = "{0}: Proximity: {1}, Beacons:{2}, Message:{3}")
	public static Collection<Object[]> data() {
		AlgorithmEnum[] algorithmEnums = AlgorithmEnum.values();
		Double[] proximities = new Double[]{null, 0.0, 20.0};
		Double[] beacons = new Double[]{null, 0.0, 20.0};
		Double[][] messages = {null, {0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, {5.0, 2.0, 0.0, 5.0, 2.0, 0.0}};

		ArrayList<Object[]> result = new ArrayList<>();
		for (AlgorithmEnum ae : algorithmEnums) {
			for (Double p : proximities) {
				for (Double b : beacons) {
					for (Double[] m : messages) {
						result.add(new Object[]{ae, p, b, m});
					}
				}
			}
		}
		return result;
	}

	@Test
	public void testAlgorithm() {
		SensorInterface sensorInterface = mock(SensorInterface.class);
		when(sensorInterface.getFrontProximity()).thenReturn(frontProximity);
		when(sensorInterface.getBeacons()).thenReturn(beacons);

		DriveInterface driveInterface = mock(DriveInterface.class);
		NetworkInterface networkInterface = mock(NetworkInterface.class);
		BeaconInterface beaconInterface = mock(BeaconInterface.class);
		when(beaconInterface.getCurrentBeaconId()).thenReturn(0);
		when(beaconInterface.getBeacons()).thenReturn(beacons);

		Algorithm algorithm = Algorithm.createAlgorithm(algorithmEnum, driveInterface, sensorInterface, networkInterface, beaconInterface);

		CommsInterface mockCommsInterface = mock(CommsInterface.class);
		when(mockCommsInterface.isLeader()).thenReturn(false);
		when(mockCommsInterface.getPredecessorMessages()).thenReturn(predecessorMessages);

		algorithm.algorithmData.commsInterface = mockCommsInterface;
		algorithm.initialise();
		Time.useDefinedTime();
		Time.setTime(0);
		algorithm.update();
		Time.increaseTime(10000000);
		algorithm.update();
	}
}
