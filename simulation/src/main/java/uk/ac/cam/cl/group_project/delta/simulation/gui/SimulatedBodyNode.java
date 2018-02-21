package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.scene.Group;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import uk.ac.cam.cl.group_project.delta.simulation.PhysicsBody;

/**
 * Encapsulates the GUI representation of a simulated object in the world.
 */
public class SimulatedBodyNode extends Group implements Treeable {

	/**
	 * The physics body that this node represents.
	 */
	private final PhysicsBody body;

	/**
	 * Construct representation of the given body.
	 * @param body    The body that this represents.
	 */
	public SimulatedBodyNode(PhysicsBody body) {
		this.body = body;
		this.setVisible(false);
	}

	/**
	 * Get the body that this node represents.
	 * @return    The body represented by this node.
	 */
	public PhysicsBody getBody() {
		return body;
	}

	/**
	 * Update the positions of this representation based on the physics
	 * simulation.
	 */
	public void update() {
		this.setVisible(true);
		synchronized (body) {
			this.setTranslateX(body.getPosition().getX() * Controller.UNITS_PER_METRE);
			this.setTranslateY(body.getPosition().getY() * Controller.UNITS_PER_METRE);
		}
	}

	/**
	 * Convert this object to a {@link TreeItem<String>} for display in a
	 * {@link TreeView}.
	 * @return    Hierarchical representation of this object.
	 */
	public TreeItem<String> toTree() {
		synchronized (body) {
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

}
