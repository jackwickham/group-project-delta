package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
	Emergency(0), Data(1), RequestToMerge(2), AcceptToMerge(3), ConfirmMerge(4), MergeComplete(5),
	BeaconIdQuestion(6), BeaconIdAnswer(7);

	private int value;
	private static Map<Integer, MessageType> lookup = new HashMap<>();

	private MessageType(int value) {
		this.value = value;
	}

	/**
	 * Create the mapping from integer values to MessageTypes. Slightly hacky but
	 * not much choice
	 */
	static {
		for (MessageType type : MessageType.values()) {
			lookup.put(type.value, type);
		}
	}

	/**
	 * Lookup which MessageType a particular value corresponds to.
	 *
	 * @param value
	 *            - the value of the MessageType to be returned
	 * @return the associated MessageType
	 */
	public static MessageType valueOf(int value) {
		return lookup.get(value);
	}

	public int getValue() {
		return value;
	}
}
