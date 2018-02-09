package heuristics;

import main.Board;

public class HeuristicManhattanDistance implements Heuristic {
	
	public HeuristicManhattanDistance() {
		// Empty constructor
	}
	
	@Override
	public int setValue(Board board) {
		
		// Don't set the board's heuristic within the method, since A* uses the sum of multiple heuristics to form a composite heuristic
		
		int[] boardArray = board.getBoard1D();
		int costSum = 0;
		for (int i = 1; i < boardArray.length; i++) {
			if (boardArray[i] == 0) {
				costSum += Math.abs(i - boardArray.length - 1);		// Calculate distance between blank (0) position and the end of the board
			} else {
				costSum += Math.abs(boardArray[i - 1] - i);			// Calculate the distance between a value (i) and its proper location (boardArray[i-1])
			}
		}
		return costSum;
	}
	
	@Override
	public void setReference(Board board) {
		// Empty method - only HeuristicWeightedCost uses a reference Board
	}
	
}
