package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;

/**
 * Represents dialog for creation of a new simulated car.
 */
public class SimulatedCarFormDialog extends FormDialog {

	/**
	 * Function to call when we exit the dialog by clicking the confirmation
	 * button.
	 */
	private Callback callback;

	/**
	 * Value property for wheel base input spinner.
	 */
	private ObservableValue<Double> wheelBaseInput;

	/**
	 * Value property for x-position input spinner.
	 */
	private ObservableValue<Double> positionX;

	/**
	 * Value property for y-position input spinner.
	 */
	private ObservableValue<Double> positionY;

	//private StringProperty algorithmController;

	//private static final HashMap<String, Algorithm> ALGORITHM_HASH_MAP;

	/**
	 * Construct a dialog for inputting the values required to construct a
	 * {@link SimulatedCar}: wheel-base and position.
	 * @param callback    Function to call on confirmation of input.
	 */
	public SimulatedCarFormDialog(Callback callback) {

		super();
		this.callback = callback;

		Spinner<Double> wheelBaseSpinner = new Spinner<>(0.0, 10.0, 2.5, 0.5);
		this.wheelBaseInput = wheelBaseSpinner.valueProperty();
		this.getForm().getChildren().add(wheelBaseSpinner);

		Spinner<Double> posXSpinner = new Spinner<>(-100.0, 100.0, 0.0, 1.0);
		this.positionX = posXSpinner.valueProperty();
		this.getForm().getChildren().add(posXSpinner);

		Spinner<Double> posYSpinner = new Spinner<>(-100.0, 100.0, 0.0, 1.0);
		this.positionY = posYSpinner.valueProperty();
		this.getForm().getChildren().add(posYSpinner);

	}

	/**
	 * On confirmation of input, call the callback function.
	 */
	@Override
	protected void confirm() {
		callback.call(
			wheelBaseInput.getValue(),
			positionX.getValue(),
			positionY.getValue()
		);
	}

	/**
	 * An interface for the confirmation callback. This is a
	 * {@link FunctionalInterface}, and hence lambda functions are permitted in
	 * place of implementing classes.
	 */
	@FunctionalInterface
	public interface Callback {
		/**
		 * Perform the callback operation.
		 * @param wheelBase    Wheel-base input to the dialog.
		 * @param posX         X-position input to the dialog.
		 * @param posY         Y-position input to the dialog.
		 */
		void call(double wheelBase, double posX, double posY);
	}

}
