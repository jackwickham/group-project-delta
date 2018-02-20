package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
	public FormDialog() {

		form = new VBox();
		Button cancel = new Button("Cancel");
		Button confirm = new Button("Confirm");
		HBox buttons = new HBox(cancel, confirm);
		VBox root = new VBox(form, buttons);
		this.setScene(new Scene(root));

		cancel.setCancelButton(true);
		confirm.setDefaultButton(true);

		cancel.setOnMouseClicked(e -> {
			this.cancelled = true;
			this.close();
		});
		confirm.setOnMouseClicked(e -> this.close());

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
}
