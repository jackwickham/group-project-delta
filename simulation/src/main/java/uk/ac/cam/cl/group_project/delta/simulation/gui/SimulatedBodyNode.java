package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.scene.Group;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import uk.ac.cam.cl.group_project.delta.simulation.PhysicsBody;

public class SimulatedBodyNode extends Group implements Treeable {

	private final PhysicsBody body;

	public SimulatedBodyNode(PhysicsBody body) {

		this.body = body;

		Circle c = new Circle(10.0);
		c.setFill(Color.TRANSPARENT);
		c.setStroke(Color.BLACK);
		getChildren().add(c);

	}

	public PhysicsBody getBody() {
		return body;
	}

	public void update() {
		this.setTranslateX(body.getPosition().getX());
		this.setTranslateY(body.getPosition().getY());
	}

	public TreeItem<String> toTree() {
		TreeItem<String> root = new TreeItem<>("Body #" + body.getUuid());

		TreeItem<String> position = new TreeItem<>("position");
		root.getChildren().add(position);
		position.getChildren().add(
			new TreeItem<>("x = " + body.getPosition().getX())
		);
		position.getChildren().add(
			new TreeItem<>("y = " + body.getPosition().getY())
		);

		return root;
	}

}
