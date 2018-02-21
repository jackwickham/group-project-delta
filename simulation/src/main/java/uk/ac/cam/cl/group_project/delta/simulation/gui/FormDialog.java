package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Abstract base class for pop-up dialogs that request user input for a set of
 * fields, giving the option to confirm their input or cancel the form.
 */
public abstract class FormDialog extends Stage {

	/**
	 * GUI form body, where the form fields should be placed.
	 */
	private VBox form;

	/**
	 * Was this dialog closed with the cancel button?
	 */
	private boolean cancelled = false;

	/**
	 * Construct empty form with cancel and confirm buttons.
	 */
	public FormDialog(String title) {

		this.setTitle(title);

		form = new VBox();
		Button cancel = new Button("Cancel");
		Button confirm = new Button("Confirm");
		HBox buttons = new HBox(cancel, confirm);
		VBox root = new VBox(form, buttons);
		this.setScene(new Scene(root));

		cancel.setCancelButton(true);
		confirm.setDefaultButton(true);

		this.setOnCloseRequest(e -> cancel.fire());

		cancel.setOnMouseClicked(e -> {
			this.cancelled = true;
			this.cancel();
			this.close();
		});
		confirm.setOnMouseClicked(e -> {
			this.confirm();
			this.close();
		});

	}

	/**
	 * Get the form layout box.
	 * @return    Form layout container.
	 */
	protected VBox getForm() {
		return form;
	}

	/**
	 * Was this dialog closed with the cancel button? Note that this will be
	 * false if the form is still open.
	 * @return    Boolean representing exit state.
	 */
	public boolean wasCancelled() {
		return cancelled;
	}

	/**
	 * Called when cancel button is used to exit the dialog or the dialog was
	 * forcefully quit.
	 */
	protected void cancel() {
		// No default behaviour
	}

	/**
	 * Called when the confirmation button is used to exit the dialog.
	 */
	protected void confirm() {
		// No default behaviour
	}

}
