package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import uk.ac.cam.cl.group_project.delta.simulation.SimulatedCar;

/**
 * Represents dialog for creation of a new simulated car.
 */
public class SimulatedCarFormDialog extends FormDialog {

	private Callback callback;

	private ObservableValue<Double> wheelBaseInput;

	private ObservableValue<Double> positionX;

	private ObservableValue<Double> positionY;

	//private StringProperty algorithmController;

	//private static final HashMap<String, Algorithm> ALGORITHM_HASH_MAP;

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

	@Override
	public void confirm() {
		callback.call(
			wheelBaseInput.getValue(),
			positionX.getValue(),
			positionY.getValue()
		);
	}

	@FunctionalInterface
	public interface Callback {
		void call(double wheelBase, double posX, double posY);
	}

}
