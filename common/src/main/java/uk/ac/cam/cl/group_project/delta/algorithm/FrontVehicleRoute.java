package uk.ac.cam.cl.group_project.delta.algorithm;

import java.util.ArrayList;
import java.util.List;

public class FrontVehicleRoute {

	private int currentStep = 0;
	private int nextActionStep;
	private boolean stepsRemaining = true;
	private List<Move> moves;
	private AlgorithmData algorithmData;
	private final int LOOP_LENGTH;

	enum moveType {ACCELERATION, TURN_RATE}

	public FrontVehicleRoute(AlgorithmData algorithmData, int loopLength) {
		this.algorithmData = algorithmData;
		this.LOOP_LENGTH = loopLength;
		this.moves = routeTwo();
		if (moves.size() > 0) {
			nextActionStep = moves.get(0).seconds * LOOP_LENGTH;
		} else {
			stepsRemaining = false;
		}
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
				nextActionStep = moves.get(0).seconds * LOOP_LENGTH;
			} else {
				stepsRemaining = false;
			}
		}
		currentStep++;
	}

	private List<Move> routeOne() {
		List<Move> moves = new ArrayList<>();
		moves.add(new Move(0, moveType.ACCELERATION, 0.05));
		moves.add(new Move(3, moveType.ACCELERATION, -0.05));
		moves.add(new Move(6, moveType.ACCELERATION, 0.05));
		moves.add(new Move(9, moveType.ACCELERATION, -0.05));
		return moves;
	}

	private List<Move> routeTwo() {
		List<Move> moves = new ArrayList<>();
		moves.add(new Move(0, moveType.ACCELERATION, 0.01));
		moves.add(new Move(4, moveType.ACCELERATION, 0));
		moves.add(new Move(5, moveType.TURN_RATE, 0.3));
		moves.add(new Move(7, moveType.TURN_RATE, -0.3));
		moves.add(new Move(11, moveType.TURN_RATE, 0.3));
		moves.add(new Move(14, moveType.TURN_RATE, 0));
		moves.add(new Move(15, moveType.ACCELERATION, -0.01));
		return moves;
	}

	private static class Move {
		int seconds;
		moveType move;
		double amount;

		public Move(int seconds, moveType move, double amount) {
			this.seconds = seconds;
			this.move = move;
			this.amount = amount;
		}
	}

}
