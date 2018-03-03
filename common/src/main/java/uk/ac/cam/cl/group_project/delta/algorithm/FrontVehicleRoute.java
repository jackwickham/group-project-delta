package uk.ac.cam.cl.group_project.delta.algorithm;

import java.util.ArrayList;
import java.util.List;

public class FrontVehicleRoute {

	/**
	 * The number of times nextStep() has been called
	 */
	private int currentStep = 0;

	/**
	 * The next value of currentStep at which an action takes place
	 */
	private int nextActionStep;

	/**
	 * True if there are any actions left to do
	 */
	private boolean stepsRemaining = true;

	/**
	 * List of remaining moves to do
	 */
	private List<Move> moves;

	/**
	 * The algorithmData object for this vehicle
	 */
	private AlgorithmData algorithmData;

	/**
	 * The number of seconds per algorithm loop
	 */
	private final double LOOP_LENGTH;

	/**
	 * Possible different types of move
	 */
	enum MoveType {ACCELERATION, TURN_RATE}

	/**
	 * Possible different routes for the front vehicle to follow
	 */
	public enum RouteNumber {ROUTE_ZERO, ROUTE_ONE, ROUTE_TWO, ROUTE_THREE}

	public FrontVehicleRoute(AlgorithmData algorithmData, double algoLoopLength, RouteNumber routeNumber) {
		this.algorithmData = algorithmData;
		this.LOOP_LENGTH = algoLoopLength / 1000000000;
		switch (routeNumber) {
			case ROUTE_ZERO:
				this.moves = routeZero();
				break;
			case ROUTE_ONE:
				this.moves = routeOne();
				break;
			case ROUTE_TWO:
				this.moves = routeTwo();
				break;
			case ROUTE_THREE:
				this.moves = routeThree();
				break;
		}
		updateNextActionStep();
	}

	/**
	 * Called in each algorithm loop, modifies algorithmData if necessary
	 */
	public boolean nextStep() {
		if (!stepsRemaining) return false;
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
			updateNextActionStep();
			return true;
		}
		currentStep++;
		return false;
	}

	private void updateNextActionStep() {
		if (moves.size() > 0) {
			nextActionStep = (int) (moves.get(0).seconds / LOOP_LENGTH);
		} else {
			stepsRemaining = false;
		}
	}

	/**
	 * @return empty list, i.e. car does nothing
	 */
	private static List<Move> routeZero() {
		return new ArrayList<>();
	}

	/**
	 * @return list of moves which makes the car accelerate at 0.05m/s/s for 3s,
	 * then decelerate at the same speed for 3s, then repeat.
	 */
	private static List<Move> routeOne() {
		List<Move> moves = new ArrayList<>();
		moves.add(new Move(0, MoveType.ACCELERATION, 0.1));
		moves.add(new Move(3, MoveType.ACCELERATION, -0.1));
		moves.add(new Move(6, MoveType.ACCELERATION, 0.1));
		moves.add(new Move(9, MoveType.ACCELERATION, -0.1));
		return moves;
	}

	/**
	 * @return list of moves which makes the car accelerate at 0.01m/m/m for 3s,
	 * make a S shape (right first), then decelerate to a stop.
	 */
	private static List<Move> routeTwo() {
		List<Move> moves = new ArrayList<>();
		moves.add(new Move(0, MoveType.ACCELERATION, 0.01));
		moves.add(new Move(3, MoveType.ACCELERATION, 0));
		moves.add(new Move(4, MoveType.TURN_RATE, 0.3));
		moves.add(new Move(6, MoveType.TURN_RATE, -0.3));
		moves.add(new Move(10, MoveType.TURN_RATE, 0.3));
		moves.add(new Move(13, MoveType.TURN_RATE, 0));
		moves.add(new Move(14, MoveType.ACCELERATION, -0.01));
		return moves;
	}

	/**
	 * @return list of moves which makes the car accelerate forwards, then
	 * drive to the right, then to the left, then decelerate to a stop.
	 */
	private static List<Move> routeThree() {
		List<Move> moves = new ArrayList<>();
		moves.add(new Move(0, MoveType.ACCELERATION, 0.05));
		moves.add(new Move(3, MoveType.ACCELERATION, 0));
		moves.add(new Move(5, MoveType.TURN_RATE, 0.5));
		moves.add(new Move(7, MoveType.TURN_RATE, 0));
		moves.add(new Move(10, MoveType.TURN_RATE, -0.8));
		moves.add(new Move(13, MoveType.TURN_RATE, 0));
		moves.add(new Move(14, MoveType.ACCELERATION, -0.05));
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
		MoveType move;
		double amount;

		public Move(int seconds, MoveType move, double amount) {
			this.seconds = seconds;
			this.move = move;
			this.amount = amount;
		}
	}

}
