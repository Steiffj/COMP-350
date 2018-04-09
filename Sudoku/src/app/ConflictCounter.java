package app;

public class ConflictCounter {
	
	/*
	 * Amount to weight values that are the same as predefined values from the original puzzle
	 * A higher number means the value is more likely to be changed when attempting to minimize conflicts
	 * 
	 * This should be set between 2 and 24 
	 * 		- Setting penaltyVal = 1 places no additional penalty for this case
	 * 		- Any penaltyVal > 24 will function no differently than 24 itself
	 * 		  (and more realistically, values above ~10-16 shouldn't make a huge difference)
	 */
	private final int penaltyVal = 1;
	
	public ConflictCounter() {
		// Empty constructor
	}
	
	/*
	 * Returns the total number of broken constraints for the entire board
	 */
	public int countTotalConflicts(Puzzle board) {
		int conflicts = 0;
		
		for (int i = 0; i < board.size(); i++) {
			conflicts += countConflicts(board, i);
		}
		
		return conflicts;
	}
	
	/*
	 * Returns the number of broken constraints for the value at the provided position 
	 */
	public int countConflicts(Puzzle board, int index) {
		int conflicts = 0;
		
		Tuple checkVal = board.get(index);
		
		int row = index / board.width();
		int col = index % board.width();
		int cell = (row / board.cellWidth()) * board.cellWidth() + (col / board.cellWidth());	// calculate cell number based on row and column
		
		//System.out.println("row: " + row + ", col: " + col + ", cell: " + cell);
		
		// Check rows
		for (Tuple rowVal : board.groupRows()[row]) {
			
			if (checkVal.getValue() == rowVal.getValue() && checkVal.getIndex() != rowVal.getIndex() && rowVal.isOriginal()) {
				// The value being checked conflicts with another value in the row, and that value is from the original puzzle
				conflicts += penaltyVal;
			} else if (checkVal.getValue() == rowVal.getValue() && checkVal.getIndex() != rowVal.getIndex()) {
				// the value being checked conflicts with another value in the row
				conflicts++;
			}
		}
		
		// Check columns
		for (Tuple colVal : board.groupColumns()[col]) {
			
			if (checkVal.getValue() == colVal.getValue() && checkVal.getIndex() != colVal.getIndex() && colVal.isOriginal()) {
				// The value being checked conflicts with another value in the column, and that value is from the original puzzle
				conflicts += penaltyVal;
			} else if (checkVal.getValue() == colVal.getValue() && checkVal.getIndex() != colVal.getIndex()) {
				// the value being checked conflicts with another value in the column
				conflicts++;
			}
		}
		
		// Check cells
		for (Tuple cellVal : board.groupCells()[cell]) {
			if (checkVal.getValue() == cellVal.getValue() && checkVal.getIndex() != cellVal.getIndex() && cellVal.isOriginal()) {
				// The value being checked conflicts with another value in the cell, and that value is from the original puzzle
				conflicts += penaltyVal;
			} else if (checkVal.getValue() == cellVal.getValue() && checkVal.getIndex() != cellVal.getIndex()) {
				// the value being checked conflicts with another value in the cell 
				conflicts++;
			}
		}
		
		return conflicts;
	}
}
