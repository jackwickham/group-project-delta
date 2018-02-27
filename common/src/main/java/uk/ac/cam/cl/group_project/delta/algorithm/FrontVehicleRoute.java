package uk.ac.cam.cl.group_project.delta.algorithm;

import java.util.ArrayList;
import java.util.List;

public class FrontVehicleRoute {

	/**
	 * currentStep - number of times nextStep() has been called
	 * nextActionStep - next value of currentStep at which an action takes place
	 * stepsRemaining - are there any actions left to do
	 * moves - list of moves to do
	 * algorithmData - the algorithmData object for this vehicle
	 * LOOP_LENGTH - number of milliseconds per algorithm loop
	 */
	private int currentStep = 0;
	private int nextActionStep;
	private boolean stepsRemaining = true;
	private List<Move> moves;
	private AlgorithmData algorithmData;
	private final int LOOP_LENGTH;

	/**
	 * Possible different types of move
	 */
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

	/**
	 * Called in each algorithm loop, modifies algorithmData if necessary
	 */
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

	/**
	 * @return empty list, i.e. car does nothing
	 */
	private List<Move> routeZero() {
		return new ArrayList<>();
	}

	/**
	 * @return list of moves which makes the car accelerate at 0.05m/s/s for 3s,
	 * then decelerate at the same speed for 3s, then repeat.
	 */
	private List<Move> routeOne() {
		List<Move> moves = new ArrayList<>();
		moves.add(new Move(0, moveType.ACCELERATION, 0.05));
		moves.add(new Move(3, moveType.ACCELERATION, -0.05));
		moves.add(new Move(6, moveType.ACCELERATION, 0.05));
		moves.add(new Move(9, moveType.ACCELERATION, -0.05));
		return moves;
	}

	/**
	 * @return list of moves which makes the car accelerate at 0.01m/m/m for 3s,
	 * then follow an S shape (right first), then decelerate to a stop.
	 */
	private List<Move> routeTwo() {
		List<Move> moves = new ArrayList<>();
		moves.add(new Move(0, moveType.ACCELERATION, 0.01));
		moves.add(new Move(3, moveType.ACCELERATION, 0));
		moves.add(new Move(4, moveType.TURN_RATE, 0.3));
		moves.add(new Move(6, moveType.TURN_RATE, -0.3));
		moves.add(new Move(10, moveType.TURN_RATE, 0.3));
		moves.add(new Move(13, moveType.TURN_RATE, 0));
		moves.add(new Move(14, moveType.ACCELERATION, -0.01));
		return moves;
	}

	/**
	 * A Move represents an action to be taken by the car
	 */
	private static class Move {
		/**
		 * seconds - the number of seconds after start at which the action happens
		 * move - the type of action, e.g. ACCELERATION or TURN_RATE
		 * amount - the amount relevant to the move, e.g. the acceleration in m/s/s
		 */
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
