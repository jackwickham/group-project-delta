package uk.ac.cam.cl.group_project.delta.simulation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests for SimulatedNetworkModule's failure injection functionality
 */
public class SimulatedNetworkFailureTest {
	private SimulatedNetworkModule mockReceiver;
	private SimulatedNetworkModule mockSender;
	private SimulatedNetwork classUnderTest;

	private int messagesReceived = 0;

	@Before
	public void init() {
		classUnderTest = new SimulatedNetwork();
		mockReceiver = mock(SimulatedNetworkModule.class);
		mockSender = mock(SimulatedNetworkModule.class);

		when(mockSender.getPosition()).thenReturn(new Vector2D());
		when(mockReceiver.getPosition()).thenReturn(new Vector2D(1, 0));
		doAnswer(new Answer() {
			@Override
			public Object answer (InvocationOnMock invocation) throws Throwable {
				messagesReceived++;
				return null;
			}
		}).when(mockReceiver).handleMessage(Mockito.any(byte[].class));

		classUnderTest.register(mockReceiver);
	}

	@After
	public void resetMessageDeliveryModifier() {
		SimulatedNetwork.setMessageDeliveryModifier(0);
	}

	@Test
	public void testAllMessagesReceivedWhenNoLoss() {
		for (int i = 0; i < 50; i++) {
			classUnderTest.broadcast(mockSender, new byte[10]);
		}
		assertEquals("Wrong number of packets received", 50, messagesReceived);
	}

	@Test
	public void testHalfPacketsLostWhenLossEnabled() {
		// Get 50% loss at 1m: https://www.wolframalpha.com/input/?i=0.5+%3D+0.55705+-+0.35463+*+arctan+(c++-+3)
		SimulatedNetwork.setMessageDeliveryModifier(3.16);
		for (int i = 0; i < 200; i++) {
			classUnderTest.broadcast(mockSender, new byte[10]);
		}
		// It will almost always fall within the range 75..125
		// https://www.wolframalpha.com/input/?i=2+*+P(X+%3E+125)+where+X+~+Binom(200,+0.5)
		assertThat(messagesReceived, allOf(greaterThanOrEqualTo(75), lessThanOrEqualTo(125)));
	}

	@Test
	public void testMostPacketsLostWhenFarAway() {
		SimulatedNetwork.setMessageDeliveryModifier(20);
		for (int i = 0; i < 200; i++) {
			classUnderTest.broadcast(mockSender, new byte[10]);
		}
		assertThat(messagesReceived, lessThanOrEqualTo(15));
	}
}
