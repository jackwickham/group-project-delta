package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class CarPropertiesController {

	@FXML
	public Label uuid;

	@FXML
	public Label controller;

	@FXML
	public Label positionX;

	@FXML
	public Label positionY;

	@FXML
	public Label heading;

	@FXML
	public Label wheelAngle;

	@FXML
	public Label enginePower;

	@FXML
	public Label velocityX;

	@FXML
	public Label velocityY;

	@FXML
	public Label vehicleId;

	@FXML
	public Label platoonId;

	@FXML
	public Label platoonPosition;

	@FXML
	public Label isLeader;

	@FXML
	public Label platoonLeaderId;

	@FXML
	public Rectangle platoonColour;

}
