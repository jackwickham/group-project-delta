package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import uk.ac.cam.cl.group_project.delta.Log;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class encapsulates the data required for two platoons to merge
 *
 * @author Aaron
 *
 */
public class Merge {

	/**
	 * The maximum time in ns between updates to a merge before it is invalidated
	 */
	private final static long TIMEOUT = 1000000000L; // 1 second


	private enum MergeState {
		Requested, Accepted, Confirmed, Cancelled
	}

	/**
	 * The id of the platoon which will remain after the merge
	 */
	private int mainPlatoonId;

	/**
	 * The id of the current platoon
	 */
	private int platoonId;

	/**
	 * The id of the merging platoon
	 */
	private int mergingPlatoonId;

	/**
	 * The transaction id for this merge
	 */
	private int transactionId;

	/**
	 * The local time this merge was last updated, used to timeout merges
	 */
	private long lastUpdate;

	/**
	 * The change in position of the current vehicle with the planned merge
	 */
	private int changePosition;

	/**
	 * The new ids to be added if the merge is committed, in their platoon order
	 */
	private List<Integer> additionalIdLookups;

	/**
	 * The mapping from old ids to new ids.
	 */
	private Map<Integer, Integer> idClashReplacements;

	/**
	 * The state of the current merge
	 */
	private MergeState state;

	/**
	 * A count of the number of vehicles still to confirm. A negative indicates
	 * this is not a leader vehicle, so the value doesn't matter
	 */
	private int vehiclesToConfirm = 0;

	/**
	 * This is the constructor used by leader of the merging platoon.
	 * This generates the transaction id
	 *
	 * @param mainPlatoon - the platoon id of the main platoon
	 * @param currentPlatoon - the platoon id of the current platoon
	 * @param platoonSize - the size of the merging platoon
	 */
	public Merge(int mainPlatoon, int currentPlatoon, int platoonSize) {
		this.mainPlatoonId = mainPlatoon;
		this.platoonId = currentPlatoon;
		this.mergingPlatoonId = currentPlatoon;
		vehiclesToConfirm = platoonSize;

		Random r = new Random();
		transactionId = r.nextInt();

		lastUpdate = System.nanoTime();
		state = MergeState.Requested;
	}

	/**
	 * This is the constructor used by everyone else after they receive a request
	 *
	 * @param mainPlatoon - the platoon id of the main platoon
	 * @param currentPlatoon - the platoon id of the current platoon
	 * @param payload - the payload of a RequestToMerge packet
	 */
	public Merge(int mainPlatoon, int currentPlatoon, Message m) {
		this.mainPlatoonId = mainPlatoon;
		this.platoonId = currentPlatoon;
		vehiclesToConfirm = -1;

		if(!(m instanceof RequestToMergeMessage)) {
			state = MergeState.Cancelled;
			return;
		}

		state = MergeState.Requested;

		RequestToMergeMessage msg = (RequestToMergeMessage) m;
		transactionId = msg.getTransactionId();
		this.mergingPlatoonId = msg.getMergingPlatoonId();

		// New platoon merging into this one
		if(mainPlatoon == currentPlatoon) {
			additionalIdLookups = msg.getNewPlatoon();
			changePosition = additionalIdLookups.size();
		}
		lastUpdate = System.nanoTime();
	}

	/**
	 * Handle the specific type of message, if something is incorrect
	 * then the state is set to Cancelled
	 *
	 * @param m - the data to be added to this merge
	 */
	public void handleMessage(Message m) {
		if(!(m instanceof MergeMessage)) {
			// TODO: Log this, as it indicated the message has been routed
			// to the wrong place. Critical error.
			return;
		}
		MergeMessage message = (MergeMessage) m;
		if(message.getTransactionId() != this.transactionId) {
			// Log the multiple concurrent merges
			Log.warn("Multiple concurrent merges occurring");
			state = MergeState.Cancelled;
			return;
		}
		if(message instanceof AcceptToMergeMessage) {
			if(!state.equals(MergeState.Requested)) {
				Log.warn("Out or order merge messages received: AcceptToMerge received when not in Requested");
				state = MergeState.Cancelled;
				return;
			} else {
				handleAcceptMessage((AcceptToMergeMessage) message);
			}
		} else if(message instanceof ConfirmMergeMessage) {
			if(!state.equals(MergeState.Accepted)) {
				Log.warn("Out or order merge messages received: Accepted received when not in ConfirmMerge");
				state = MergeState.Cancelled;
				return;
			} else {
				handleConfirmMessage();
			}
		} else if(message instanceof MergeCompleteMessage) {
			state = MergeState.Confirmed;
		} else {
			// TODO: Log this, as it again indicated the message was
			// routed to the wrong place.
		}
	}

	/**
	 * Handle the payload of an AcceptToMerge message
	 * @param msg - the message to be handled
	 */
	private void handleAcceptMessage(AcceptToMergeMessage msg) {
		if(!msg.isAccepted()) {
			// Rejected by the leader of the main platoon
			state = MergeState.Cancelled;
		} else {
			state = MergeState.Accepted;
			if(mainPlatoonId != platoonId) {
				additionalIdLookups = msg.getMainPlatoon();
				if(vehiclesToConfirm > 0) {
					// This is a leader, so needs to track the number
					// of vehicles to confirm.
					vehiclesToConfirm += msg.getMainPlatoon().size();
				}
			}
			idClashReplacements = msg.getRenames();
		}
		lastUpdate = System.nanoTime();
	}

	/**
	 * Handle the payload of a ConfirmMerge message, this is only done by
	 * the leader of the merging platoon
	 */
	private void handleConfirmMessage() {
		if(vehiclesToConfirm > 0) {
			vehiclesToConfirm--;

			// If only the current vehicle remaining then commit
			if(vehiclesToConfirm == 1) {
				state = MergeState.Confirmed;
			}
		}
		lastUpdate = System.nanoTime();
	}

	/**
	 * Checks the state and whether the merge has timed out to ensure it is
	 * still valid
	 *
	 * @return whether the merge is still valid
	 */
	public boolean isValid() {
		return !state.equals(MergeState.Cancelled) && (System.nanoTime() - TIMEOUT) < lastUpdate;
	}

	public boolean doesAccept() {
		return state.equals(MergeState.Accepted);
	}
	public boolean isConfirmed() {
		return state.equals(MergeState.Confirmed);
	}

	public int getMainPlatoonId() {
		return mainPlatoonId;
	}

	public int getMergingPlatoonId() {
		return mergingPlatoonId;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public int getChangePosition() {
		return changePosition;
	}

	public List<Integer> getAdditionalIdLookups() {
		return additionalIdLookups;
	}

	public Map<Integer, Integer> getIdClashReplacements() {
		return idClashReplacements;
	}

}
