package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

/**
 * A wrapper class around {@link Spinner}, that improves the editable
 * behaviour and provides both constructors and a static method for creating
 * improved spinners. This class is a drop-in replacement for {@link Spinner}.
 * @see <a href="https://stackoverflow.com/questions/32340476/manually-typing-in-text-in-javafx-spinner-is-not-updating-the-value-unless-user">StackOverflow</a>
 */
public class EditableSpinner<T> extends Spinner<T> {

	/**
	 * Setup the editor of the provided spinner to ensure all user modification
	 * is reflected in the value of the spinner.
	 * @param spinner    Spinner to setup.
	 * @param <E>        Type of the spinner value.
	 */
	public static <E> void setup(EditableSpinner<E> spinner) {

		spinner.setEditable(true);

		SpinnerValueFactory<E> factory = spinner.getValueFactory();

		StringConverter<E> converter = new SafeStringConverter<>(factory.getConverter());
		factory.setConverter(converter);

		TextFormatter<E> formatter = new TextFormatter<>(converter, factory.getValue());
		spinner.getEditor().setTextFormatter(formatter);
		factory.valueProperty().bindBidirectional(formatter.valueProperty());

		spinner.getEditor().setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case UP:
					spinner.increment();
					break;
				case DOWN:
					spinner.decrement();
					break;
			}
		});

	}

	// Reworked implementations of `Spinner` constructors follow, which call the
	// super-constructor and then `setup(this)`.

	/**
	 * Constructs a default Spinner instance, with the default 'spinner' style
	 * class and an editable editor.
	 *
	 * The editor is set to ensure all user modification is reflected in the
	 * value of the spinner.
	 *
	 * @see Spinner#Spinner()
	 */
	public EditableSpinner() {
		super();
		setup(this);
	}

	/**
	 * Creates a Spinner instance with the value factory set to be an instance
	 * of {@link SpinnerValueFactory.IntegerSpinnerValueFactory}. Note that if
	 * this constructor is called, the only valid generic type for the Spinner
	 * instance is Integer, i.e. Spinner<Integer>.
	 *
	 * The editor is set to ensure all user modification is reflected in the
	 * value of the spinner.
	 *
	 * @param min             The minimum allowed integer value for the Spinner.
	 * @param max             The maximum allowed integer value for the Spinner.
	 * @param initialValue    The value of the Spinner when first instantiated,
	 *                           must be within the bounds of the min and max
	 *                           arguments, or else the min value will be used.
	 *
	 * @see Spinner#Spinner(int, int, int)
	 */
	public EditableSpinner(
		@NamedArg("min") int min,
		@NamedArg("max") int max,
		@NamedArg("initialValue") int initialValue
	) {
		super(min, max, initialValue);
		setup(this);
	}

	/**
	 * Creates a Spinner instance with the value factory set to be an instance
	 * of {@link SpinnerValueFactory.IntegerSpinnerValueFactory}. Note that if
	 * this constructor is called, the only valid generic type for the Spinner
	 * instance is Integer, i.e. Spinner<Integer>.
	 *
	 * The editor is set to ensure all user modification is reflected in the
	 * value of the spinner.
	 *
	 * @param min             The minimum allowed integer value for the Spinner.
	 * @param max             The maximum allowed integer value for the Spinner.
	 * @param initialValue    The value of the Spinner when first instantiated,
	 *                           must be within the bounds of the min and max
	 *                           arguments, or else the min value will be used.
	 * @param amountToStepBy  The amount to increment or decrement by, per step.
	 *
	 * @see Spinner#Spinner(int, int, int, int)
	 */
	public EditableSpinner(
		@NamedArg("min") int min,
		@NamedArg("max") int max,
		@NamedArg("initialValue") int initialValue,
		@NamedArg("amountToStepBy") int amountToStepBy
	) {
		super(min, max, initialValue, amountToStepBy);
		setup(this);
	}

	/**
	 * Creates a Spinner instance with the value factory set to be an instance
	 * of {@link SpinnerValueFactory.DoubleSpinnerValueFactory}. Note that if
	 * this constructor is called, the only valid generic type for the Spinner
	 * instance is Double, i.e. Spinner<Double>.
	 *
	 * The editor is set to ensure all user modification is reflected in the
	 * value of the spinner.
	 *
	 * @param min             The minimum allowed integer value for the Spinner.
	 * @param max             The maximum allowed integer value for the Spinner.
	 * @param initialValue    The value of the Spinner when first instantiated,
	 *                           must be within the bounds of the min and max
	 *                           arguments, or else the min value will be used.
	 *
	 * @see Spinner#Spinner(double, double, double)
	 */
	public EditableSpinner(
		@NamedArg("min") double min,
		@NamedArg("max") double max,
		@NamedArg("initialValue") double initialValue
	) {
		super(min, max, initialValue);
		setup(this);
	}

	/**
	 * Creates a Spinner instance with the value factory set to be an instance
	 * of {@link SpinnerValueFactory.DoubleSpinnerValueFactory}. Note that if
	 * this constructor is called, the only valid generic type for the Spinner
	 * instance is Double, i.e. Spinner<Double>.
	 *
	 * The editor is set to ensure all user modification is reflected in the
	 * value of the spinner.
	 *
	 * @param min             The minimum allowed integer value for the Spinner.
	 * @param max             The maximum allowed integer value for the Spinner.
	 * @param initialValue    The value of the Spinner when first instantiated,
	 *                           must be within the bounds of the min and max
	 *                           arguments, or else the min value will be used.
	 * @param amountToStepBy  The amount to increment or decrement by, per step.
	 *
	 * @see Spinner#Spinner(double, double, double, double)
	 */
	public EditableSpinner(
		@NamedArg("min") double min,
		@NamedArg("max") double max,
		@NamedArg("initialValue") double initialValue,
		@NamedArg("amountToStepBy") double amountToStepBy
	) {
		super(min, max, initialValue, amountToStepBy);
		setup(this);
	}

	/**
	 * Creates a Spinner instance with the value factory set to be an instance
	 * of {@link SpinnerValueFactory.ListSpinnerValueFactory}. The Spinner
	 * value property will be set to the first element of the list, if an
	 * element exists, or null otherwise.
	 *
	 * The editor is set to ensure all user modification is reflected in the
	 * value of the spinner.
	 *
	 * @param items    A list of items that will be stepped through in the
	 *                    Spinner.
	 *
	 * @see Spinner#Spinner(ObservableList)
	 */
	public EditableSpinner(@NamedArg("items") ObservableList<T> items) {
		super(items);
		setup(this);
	}

	/**
	 * Creates a Spinner instance with the given value factory set.
	 *
	 * The editor is set to ensure all user modification is reflected in the
	 * value of the spinner.
	 *
	 * @param valueFactory    The value factory to use.
	 *
	 * @see Spinner#Spinner(SpinnerValueFactory)
	 */
	public EditableSpinner(@NamedArg("valueFactory") SpinnerValueFactory<T> valueFactory) {
		super(valueFactory);
		setup(this);
	}

}
