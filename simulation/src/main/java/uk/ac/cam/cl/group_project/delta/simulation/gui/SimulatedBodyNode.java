package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import uk.ac.cam.cl.group_project.delta.simulation.PhysicsBody;

/**
 * Encapsulates the GUI representation of a simulated object in the world.
 */
public class SimulatedBodyNode extends Group {

	/**
	 * The physics body that this node represents.
	 */
	private final PhysicsBody body;

	/**
	 * Property binding for the current x-coordinate.
	 */
	private final DoubleProperty posX;

	/**
	 * Property binding for the current y-coordinate.
	 */
	private final DoubleProperty posY;

	/**
	 * Construct representation of the given body.
	 * @param body    The body that this represents.
	 */
	public SimulatedBodyNode(PhysicsBody body) {
		this.body = body;
		posX = new SimpleDoubleProperty(body.getPosition().getX());
		posY = new SimpleDoubleProperty(body.getPosition().getY());
		translateXProperty().bind(posX);
		translateYProperty().bind(posY);
	}

	/**
	 * Update the positions of this representation based on the physics
	 * simulation.
	 */
	public void update() {
		synchronized (body) {
			posX.set(body.getPosition().getX() * Controller.UNITS_PER_METRE);
			posY.set(body.getPosition().getY() * Controller.UNITS_PER_METRE);
		}
	}

	/**
	 * Get the body that this node represents.
	 * @return    The body represented by this node.
	 */
	public PhysicsBody getBody() {
		return body;
	}

	/**
	 * Get the property for the x-position of the simulated object that this
	 * GUI element represents.
	 * @return    A {@link DoubleProperty}
	 */
	public DoubleProperty posXProperty() {
		return posX;
	}

	/**
	 * Get the property for the y-position of the simulated object that this
	 * GUI element represents.
	 * @return    A {@link DoubleProperty}
	 */
	public DoubleProperty posYProperty() {
		return posY;
	}

}
