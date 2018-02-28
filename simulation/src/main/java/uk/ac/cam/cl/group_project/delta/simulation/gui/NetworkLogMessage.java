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
					"Transaction %d: Requesting merge of platoon %d",
					rtmm.getTransactionId(),
					rtmm.getMergingPlatoonId()
				);
				break;
			case AcceptToMerge:
				AcceptToMergeMessage atmm = (AcceptToMergeMessage) packet.message;
				String status = atmm.isAccepted() ? "Accepting" : "Rejecting";
				msg = String.format(
					"Transaction %d: %s merge",
					atmm.getTransactionId(),
					status
				);
				break;
			case ConfirmMerge:
				ConfirmMergeMessage cmm = (ConfirmMergeMessage) packet.message;
				msg = String.format(
					"Transaction %d: Merge approved",
					cmm.getTransactionId()
				);
				break;
			case MergeComplete:
				MergeCompleteMessage mcm = (MergeCompleteMessage) packet.message;
				msg = String.format(
					"Transaction %d: Merge complete",
					mcm.getTransactionId()
				);
				break;
			case BeaconIdQuestion:
				BeaconIdQuestion biq = (BeaconIdQuestion) packet.message;
				msg = String.format(
					"Who is Vehicle %d? Tell Platoon %d",
					biq.getBeaconId(),
					biq.getReturnPlatoonId()
				);
				break;
			case BeaconIdAnswer:
				BeaconIdAnswer bia = (BeaconIdAnswer) packet.message;
				msg = String.format(
					"Tell Platoon %d that I have beacon %d",
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
