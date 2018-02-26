package uk.ac.cam.cl.group_project.delta.algorithm;

import java.util.ArrayList;
import java.util.List;

public class FrontVehicleRoute {

	private int currentStep = 0;
	private int nextActionStep;
	private boolean stepsRemaining = true;
	private List<Move> moves = new ArrayList<>();
	private AlgorithmData algorithmData;

	enum moveType {ACCELERATION, TURN_RATE}

	public FrontVehicleRoute(AlgorithmData algorithmData) {
		this.algorithmData = algorithmData;
		initialiseRouteTwo();
		nextActionStep = moves.get(0).stepNum;
	}

	public void nextStep() {
		if (!stepsRemaining) return;
		if (currentStep == nextActionStep) {
			Move thisMove = moves.get(0);
			switch (thisMove.move) {
				case ACCELERATION:
					algorithmData.chosenAcceleration = thisMove.amount;
					break;
				case TURN_RATE:
					algorithmData.chosenTurnRate = thisMove.amount;
					break;
			}
			moves.remove(0);
			if (moves.size() > 0) {
				nextActionStep = moves.get(0).stepNum;
			} else {
				stepsRemaining = false;
			}
		}
		currentStep++;
	}

	private void initialiseRouteOne() {
		moves.add(new Move(0, moveType.ACCELERATION, 0.05));
		moves.add(new Move(250, moveType.ACCELERATION, -0.05));
		moves.add(new Move(500, moveType.ACCELERATION, 0.05));
		moves.add(new Move(750, moveType.ACCELERATION, -0.05));
	}

	private void initialiseRouteTwo() {
		moves.add(new Move(0, moveType.ACCELERATION, 0.01));
		moves.add(new Move(400, moveType.ACCELERATION, 0));
		moves.add(new Move(500, moveType.TURN_RATE, 0.3));
		moves.add(new Move(700, moveType.TURN_RATE, -0.3));
		moves.add(new Move(1100, moveType.TURN_RATE, 0.3));
		moves.add(new Move(1400, moveType.TURN_RATE, 0));
		moves.add(new Move(1500, moveType.ACCELERATION, -0.01));
	}

	private class Move {
		int stepNum;
		moveType move;
		double amount;

		public Move(int stepNum, moveType move, double amount) {
			this.stepNum = stepNum;
			this.move = move;
			this.amount = amount;
		}
	}

}
