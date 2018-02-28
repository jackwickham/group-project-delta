package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.beans.property.*;
import javafx.scene.control.TableColumn;
import uk.ac.cam.cl.group_project.delta.algorithm.VehicleData;
import uk.ac.cam.cl.group_project.delta.algorithm.communications.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX structure for tree view.
 */
public class NetworkLogMessage {

	/**
	 * Message receipt time.
	 */
	private LongProperty time = new SimpleLongProperty();

	/**
	 * Message sender ID.
	 */
	private IntegerProperty senderId = new SimpleIntegerProperty();

	/**
	 * Sender's platoon ID
	 */
	private IntegerProperty platoonId = new SimpleIntegerProperty();

	/**
	 * Type of the message.
	 */
	private ObjectProperty<MessageType> messageType = new SimpleObjectProperty<>();

	/**
	 * String body of the message.
	 */
	private StringProperty message = new SimpleStringProperty();

	/**
	 * Construct a log message from a packet.
	 * @param t         The receipt time of the message.
	 * @param packet    The received packet.
	 */
	public NetworkLogMessage(long t, Packet packet) {

		time.set(t);
		senderId.set(packet.vehicleId);
		platoonId.set(packet.platoonId);
		messageType.set(packet.message.getType());

		String msg = "UNKNOWN";

		switch (packet.message.getType()) {
			case Emergency:
				msg = "N/A";
				break;
			case Data:
				VehicleData vd = (VehicleData) packet.message;
				msg = String.format(
					"%f (%f) m/s, %f (%f) m/s², %f (%f) rad/s",
					vd.getSpeed(),
					vd.getChosenSpeed(),
					vd.getAcceleration(),
					vd.getChosenAcceleration(),
					vd.getTurnRate(),
					vd.getChosenTurnRate()
				);
				break;
			case RequestToMerge:
				RequestToMergeMessage rtmm = (RequestToMergeMessage) packet.message;
				msg = String.format(
					"Requesting merge of platoon %d (Transaction %d)",
					rtmm.getMergingPlatoonId(),
					rtmm.getTransactionId()
				);
				break;
			case AcceptToMerge:
				AcceptToMergeMessage atmm = (AcceptToMergeMessage) packet.message;
				String status = atmm.isAccepted() ? "Accepting" : "Rejecting";
				msg = String.format(
					"%s merge (Transaction %d)",
					status,
					atmm.getTransactionId()
				);
				break;
			case ConfirmMerge:
				ConfirmMergeMessage cmm = (ConfirmMergeMessage) packet.message;
				msg = String.format(
					"Merge approved (Transaction %d)",
					cmm.getTransactionId()
				);
				break;
			case MergeComplete:
				MergeCompleteMessage mcm = (MergeCompleteMessage) packet.message;
				msg = String.format(
					"Merge complete (Transaction %d)",
					mcm.getTransactionId()
				);
				break;
			case BeaconIdQuestion:
				BeaconIdQuestion biq = (BeaconIdQuestion) packet.message;
				msg = String.format(
					"Where is Vehicle %d? Tell Platoon %d",
					biq.getBeaconId(),
					biq.getReturnPlatoonId()
				);
				break;
			case BeaconIdAnswer:
				BeaconIdAnswer bia = (BeaconIdAnswer) packet.message;
				msg = String.format(
					"Platoon %d contains Vehicle %d",
					bia.getAskedPlatoonId(),
					bia.getBeaconId()
				);
				break;
		}

		message.set(msg);

	}

	// Getters

	public long getTime() {
		return time.get();
	}

	public LongProperty timeProperty() {
		return time;
	}

	public int getSenderId() {
		return senderId.get();
	}

	public IntegerProperty senderIdProperty() {
		return senderId;
	}

	public int getPlatoonId() {
		return platoonId.get();
	}

	public IntegerProperty platoonIdProperty() {
		return platoonId;
	}

	public MessageType getMessageType() {
		return messageType.get();
	}

	public ObjectProperty<MessageType> messageTypeProperty() {
		return messageType;
	}

	public String getMessage() {
		return message.get();
	}

	public StringProperty messageProperty() {
		return message;
	}

}
