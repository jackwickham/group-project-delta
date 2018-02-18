package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class Controller {

	/**
	 * Thread running this application's simulation.
	 */
	private SimulationThread simulation;

	@FXML
	private Pane viewPane;

	/**
	 * Construct the application's simulation thread.
	 */
	public Controller() {
		simulation = new SimulationThread();
	}

	/**
	 * Start the simulation.
	 */
	@FXML
	public void initialize() {
		simulation.start();
	}

}
