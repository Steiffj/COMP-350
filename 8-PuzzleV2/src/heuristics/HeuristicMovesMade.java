package heuristics;

import main.Board;

public class HeuristicMovesMade implements Heuristic {
	
	public HeuristicMovesMade() {
		// Empty constructor
	}
	
	@Override
	public int setValue(Board board) {
		
		// Don't set the board's heuristic within the method, since A* uses the sum of multiple heuristics to form a composite heuristic
		
		int moveCount = 0;
		while (!board.isRoot()) {		// Count number of states from the current state back to the root state
			moveCount++;
			board = board.getParent();
		}
		return moveCount;
	}
	
	@Override
	public void setReference(Board board) {
		// Empty method - only HeuristicWeightedCost uses a reference Board
	}
}
