package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a dialog for filling out the fields of a constructor and initialising
 * an object with the user-input values.
 * @param <T>    Type of the object to create.
 */
public class ConstructorDialog<T> extends Stage {

	/**
	 * Constructor that this dialog will employ.
	 */
	private Constructor<T> constructor;

	/**
	 * List of field values from the input.
	 */
	private List<ObservableValue> values;

	/**
	 * The object constructed.
	 */
	private T value;

	/**
	 * Construct dialog for provided constructor.
	 * @param constructor    Constructor to use when creating object.
	 */
	public ConstructorDialog(Constructor<T> constructor) {

		this.constructor = constructor;
		this.values = new ArrayList<>();

		VBox root = new VBox(5);
		for (Parameter parameter : constructor.getParameters()) {
			Text text = new Text(parameter.getName());
			Control control = getInputField(parameter.getType());
			HBox box = new HBox(text, control);
			root.getChildren().add(box);
		}

		Button cancel = new Button("Cancel");
		Button confirm = new Button("Confirm");
		HBox box = new HBox(cancel, confirm);
		root.getChildren().add(box);

		cancel.setOnMouseClicked(e -> this.close());
		confirm.setOnMouseClicked(e -> {
			this.value = this.construct();
			this.close();
		});

		this.setScene(new Scene(root));

	}

	/**
	 * Construct an object from the values found.
	 * @return    The constructed object.
	 */
	private T construct() {
		try {
			Object[] args = new Object[values.size()];
			int i = 0;
			for (ObservableValue v : values) {
				args[i++] = v.getValue();
			}
			return constructor.newInstance(args);
		}
		catch (InstantiationError|IllegalAccessException|InstantiationException|InvocationTargetException e) {
			return null;
		}
	}

	/**
	 * Convert the input class to a UI control and add the value of the field to
	 * the list of observed value for the contructor.
	 * @param clss    Class of input.
	 * @return        Control for input of this class.
	 */
	private Control getInputField(Class<?> clss) {
		if (clss.equals(Integer.class) || clss.equals(Integer.TYPE)) {
			Spinner<Integer> spinner = new Spinner<>(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 5);
			spinner.setEditable(true);
			values.add(spinner.valueProperty());
			return spinner;
		}
		else if (clss.equals(Double.class) || clss.equals(Double.TYPE)) {
			Spinner<Double> spinner  = new Spinner<>(-Double.MAX_VALUE, Double.MAX_EXPONENT, 0.0, 0.5);
			spinner.setEditable(true);
			values.add(spinner.valueProperty());
			return spinner;
		}
		else {
			TextField field = new TextField();
			values.add(field.textProperty());
			return field;
		}
	}

	/**
	 * Gets the constructed value.
	 * @return
	 */
	public T getValue() {
		return value;
	}
}
