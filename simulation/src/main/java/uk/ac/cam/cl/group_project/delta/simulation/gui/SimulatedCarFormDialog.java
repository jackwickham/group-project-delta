package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import uk.ac.cam.cl.group_project.delta.algorithm.AlgorithmEnum;
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

	/**
	 * Property for algorithm selection box.
	 */
	private ObjectProperty<AlgorithmEnum> algorithmController;

	/**
	 * Construct a dialog for inputting the values required to construct a
	 * {@link SimulatedCar}: wheel-base and position.
	 * @param x           Initial x-position (typically where the user clicked)
	 * @param y           Initial y-position (typically where the user clicked)
	 * @param callback    Function to call on confirmation of input.
	 */
	public SimulatedCarFormDialog(double x, double y, Callback callback) {

		super("Create object - Car");
		this.callback = callback;

		// TODO: validation of spinner input (avoid ParseException)

		GridPane grid = new GridPane();
		this.getForm().getChildren().add(grid);
		grid.setHgap(10.0);
		grid.setVgap(10.0);

		grid.add(new Text("Wheel base"), 0, 0);
		grid.add(new Text("Position"), 0, 1);
		grid.add(new Text("Algorithm"), 0, 2);

		Spinner<Double> wheelBaseSpinner = new EditableSpinner<>(0.0, 10.0, 0.15, 0.5);
		wheelBaseSpinner.setEditable(true);
		this.wheelBaseInput = wheelBaseSpinner.valueProperty();
		grid.add(wheelBaseSpinner, 1, 0, 2, 1);

		Spinner<Double> posXSpinner = new EditableSpinner<>(-Double.MAX_VALUE, Double.MAX_VALUE, x, 1.0);
		posXSpinner.setEditable(true);
		this.positionX = posXSpinner.valueProperty();
		grid.add(posXSpinner, 1, 1);

		Spinner<Double> posYSpinner = new EditableSpinner<>(-Double.MAX_VALUE, Double.MAX_VALUE, y, 1.0);
		posYSpinner.setEditable(true);
		this.positionY = posYSpinner.valueProperty();
		grid.add(posYSpinner, 2, 1);

		ChoiceBox<AlgorithmEnum> choiceBox = new ChoiceBox<>();
		for (int i = AlgorithmEnum.values().length - 1; i >= 0; --i) {
			choiceBox.getItems().add(AlgorithmEnum.values()[i]);
		}
		choiceBox.getSelectionModel().selectFirst();
		this.algorithmController = choiceBox.valueProperty();
		grid.add(choiceBox, 1, 2, 2, 1);

	}

	/**
	 * On confirmation of input, call the callback function.
	 */
	@Override
	protected void confirm() {
		callback.call(
			wheelBaseInput.getValue(),
			positionX.getValue(),
			positionY.getValue(),
			algorithmController.getValue()
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
		 * @param wheelBase     Wheel-base input to the dialog.
		 * @param posX          X-position input to the dialog.
		 * @param posY          Y-position input to the dialog.
		 * @param controller    The selected controller.
		 */
		void call(double wheelBase, double posX, double posY, AlgorithmEnum controller);
	}

}
