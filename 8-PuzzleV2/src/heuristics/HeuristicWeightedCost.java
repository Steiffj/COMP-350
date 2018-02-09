package heuristics;

import main.Board;

/*
 * The weighted cost heuristic measures the cost of a board by the numerical value of the tile
 * the tile moved to reach that state
 * 
 * (This is not really the best heuristic...)
 */
public class HeuristicWeightedCost implements Heuristic {
	private Board reference;
	
	public HeuristicWeightedCost() {
		// Empty constructor
	}
	
	@Override
	public int setValue(Board board) {
		// Find the value of board at the reference's blank position
		board.setHeuristic(board.get(reference.findBlankCoord()));		// set the heuristic value of a board on the frontier in the position at which its parent's blank tile was  
		return board.getHeuristic();
	}
	
	public void setReference(Board b) {
		reference = b;		// Weighted Cost checks which number is in the position of current board's parent's blank
	}
}
