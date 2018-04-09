package app;

import java.util.Random;

public abstract class Solver {
	
	protected Puzzle board;
	protected ConflictCounter counter;
	
	// Metadata for additional functionality with the Tester class
	protected String name;
	
	public Solver(Puzzle board) {
		this.board = board;
		counter = new ConflictCounter();
	}
	
	public abstract Puzzle getSolution();
	
	/* 
	 * Replaces all blanks or previously-filled-in numbers with random numbers between 1 and 9
	 * This method runs in O(n)! Hooray! (Where n is the size of the board), and excluding inconsistent random inefficiencies
	 */
	protected void randomFill() {
		
		Random rand = new Random();
		int randVal;	// number to place in an empty spot on the board
		boolean[] used;	// keeps track of which numbers have been placed in a cell
		
		Tuple[][] cellGroup = board.groupCells();
		
		for (Tuple[] cell : cellGroup) {
			
			/* 
			 * Pre-fill used array with the original numbers 
			 * For example:
			 * 		if there was a 7 given in the original puzzle within the current working cell, mark 7 as already placed in that cell
			 */
			used = new boolean[board.width()];
			for (Tuple originalVal : cell) {
				if (originalVal.getValue() != board.blank()) {
					used[originalVal.getValue() - 1] = true;
				}
			}
			
			/*
			 * Each iteration of the inner for-loop fills one 3x3 cell with random numbers 1-9
			 * It doesn't change values that were part of the original puzzle
			 */
			for (Tuple cellVal : cell) {
				// Only add a value at the current position within the cell if it was a blank in the original puzzle
				if(!cellVal.isOriginal()) {
					randVal = rand.nextInt(board.width()) + 1;
					
					// Find a valid random number to add
					while (used[randVal - 1]) {
						randVal = rand.nextInt(board.width()) + 1;
					}
					
					board.set(randVal, cellVal.getIndex());	// Add the randVal to the board in the correct position
					used[randVal - 1] = true;				// Mark that randomly-generated number as used
				}
			}
		}
	}
	
	/* 
	 * Returns true if the board field is a solution in its current configuration
	 * Yay this check runs in O(n) too!
	 * 
	 * TODO Deprecated - algorithms use countTotalConflicts() from ConflictCounter instead 
	 */
	protected boolean isSolution() {
		Tuple[][] rows = board.groupRows();
		Tuple[][] columns = board.groupColumns();
		Tuple[][] cells = board.groupCells();
		
		for (int i = 0; i < board.width(); i++) {
			
			if (!(checkGroup(rows[i]) && checkGroup(columns[i]) && checkGroup(cells[i]))) {
				return false;
			}
		}

		return true;
	}
	
	/*
	 * Returns true if a group (row, column, or cell) contains values 1 - 9 (assuming a standard 9x9 Sudoku board)
	 * 
	 * TODO Deprecated - algorithms use countTotalConflicts() from ConflictCounter instead 
	 */
	public boolean checkGroup(Tuple[] group) {
		
		boolean[] used = new boolean[group.length];
		
		for (int index = 0; index < group.length; index++) {

			if (used[group[index].getValue() - 1]) {
				return false;
			} else {
				used[group[index].getValue() - 1] = true;
			}
		}
		
		return true;
	}
	
	public Puzzle getBoard() {
		return board;
	}
	
	public int conflicts() {
		return counter.countTotalConflicts(board);
	}
	
	// Allows for retrieval of metadata
	public String getName() {
		return name;
	}
	
}
