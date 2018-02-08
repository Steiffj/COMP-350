package puzzleTime;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Solver {
	protected Board originalBoard;
	protected Board goalState;
    protected ArrayList<Board> solutionPath = new ArrayList<Board>();
    
    //Create a solver object to pass each solver class the board and
    // create its goalstate.
	public Solver(Board ogBoard) {
		originalBoard = ogBoard;
		setGoalState();
	}
	////////////////////
	//ABSTRACT METHODS//
	////////////////////
	public abstract void getSolution();
	public abstract void searchStateSpace(Board currentBoard);
	
	////////////////////
	//HELPER FUNCTIONS//
	////////////////////
	
	public void setGoalState() {
    	int[] goal = new int[originalBoard.size * originalBoard.size];
    	for(int i = 1; i < goal.length; i++) {
    		goal[i-1] = i;
    	}
    	goal[goal.length-1] = 0;
    	goalState = new Board(originalBoard.size, goal);
    }
	
	public ArrayList<Board> validMoves(Board currentBoard){
		ArrayList<Board> moves = new ArrayList<Board>();
		Tuple blank = currentBoard.getBlank();
		int row = blank.getRow();
		int col = blank.getCol();
				
		if(row + 1 < currentBoard.size) {
			Board child = new Board(currentBoard);
			child.swap(blank, new Tuple(row + 1, col));
			child.setParent(child, currentBoard);
			moves.add(child);
		}
		if(row - 1 >= 0) {
			Board child = new Board(currentBoard);
			child.swap(blank, new Tuple(row - 1, col));
			child.setParent(child, currentBoard);
			moves.add(child);
		}
		if(col + 1 < currentBoard.size) {
			Board child = new Board(currentBoard);
			child.swap(blank, new Tuple(row, col + 1));
			child.setParent(child, currentBoard);
			moves.add(child);
		}
		if(col - 1 >= 0) {
			Board child = new Board(currentBoard);
			child.swap(blank, new Tuple(row, col - 1));
			child.setParent(child, currentBoard);
			moves.add(child);
		}
		return moves;
	}
	

}
