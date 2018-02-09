package heuristics;

import main.Board;

public class HeuristicMisplacedTiles implements Heuristic {
	
	public HeuristicMisplacedTiles() {
		// Empty constructor
	}
	
	@Override
	public int setValue(Board board) {
		
		// Don't set the board's heuristic within the method, since A* uses the sum of multiple heuristics to form a composite heuristic
		
		int[] boardArray = board.getBoard1D();
		int costSum = 0;
		for (int i = 0; i < boardArray.length - 1; i++) {
			if (boardArray[i] != i + 1) {
				costSum++;		// check if a value (i + 1) is misplaced from its proper location (boardArray[i])
			}
		}
		if (boardArray[boardArray.length - 1] != 0) {
			costSum++;			// check if the blank (0) is misplaced
		}
		return costSum;
	}
	
	@Override
	public void setReference(Board board) {
		// Empty method - only HeuristicWeightedCost uses a reference Board
		
	}
	
}
