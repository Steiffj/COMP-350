package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Board implements Comparable<Board> {
	private int size;
	private int[] board1D;
	private boolean isRoot;
	private Board parent;
	private int heuristic;
	
	///////////////////////////////////////////////////
	// Internal Helper Interface and Lambda Function //
	///////////////////////////////////////////////////
	
	private interface CoordinateConverter {
		public int convert(Tuple t);
	}
	
	private final CoordinateConverter toIndex = (t) -> {
		// Lambda function implementing CoordinateConverter interface
		return ((t.getRow() * size) + t.getCol());	// converts a tuple coordinate to an index to use with board1D 
	};
	
	//////////////////
	// Constructors //
	//////////////////
	
	public Board(int size, boolean isRoot) {
		// Initializes a new random board, specifying whether or not it's the root node
		this.size = size;
		board1D = generateBoard();
		this.isRoot = isRoot;
		heuristic = -1;	// Invalid heuristic value
	}
	
	public Board(int size, int[] values, boolean isRoot) {
		// Initializes a new board with provided values, specifying whether or not it's the root node
		this.size = size;
		if (values.length == size * size) {
			board1D = Arrays.copyOf(values, values.length);
		}
		this.isRoot = isRoot;
		heuristic = -1;	// Invalid heuristic value
	}
	
	/////////////////////////////////////////////////////////////////////////
	// Internal methods to check if a randomly-generated board is solvable //
	/////////////////////////////////////////////////////////////////////////
	
	private boolean isSolvable(int[][] board) {
		int[] board1D = Arrays.stream(board).flatMapToInt(Arrays::stream).toArray();
		int invCount = countInversions(board1D);
		
		if (size % 2 != 0) {	// Size of board is odd
			return (invCount % 2 == 0);
			
		} else {				// Size of board is even
			int blankPos = findBlank(board);
			
			if (blankPos % 2 == 0) {
				return (invCount % 2 != 0);
			} else {
				return (invCount % 2 == 0);
			}
		}
	}
	
	private int countInversions(int[] flatBoard) {
		int invCount = 0;
		for (int i = 0; i < size * size - 1; i++) {
			for (int j = i + 1; j < size * size; j++) {
				if (flatBoard[i] != 0 && flatBoard[j] != 0 && flatBoard[i] > flatBoard[j]) {
					invCount++;
				}
			}
		}
		return invCount;
	}
	
	private int findBlank(int[][] state) {
		for (int i = size - 1; i >= 0; i--) {
			for (int j = size - 1; j >= 0; j--) {
				if (state[i][j] == 0) {
					return size - i;
				}
			}
		}
		return -1;
	}
	
	private int[][] buildBoard2D() {
		int[][] board2D = new int[size][size];
		int index1D = 0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				board2D[row][col] = board1D[index1D];
				index1D++;
			}
		}
		return board2D;
	}
	
	/////////////////////////////////////////////
	// Getters, Setters, and Auxiliary Methods //
	/////////////////////////////////////////////
	
	public int get(Tuple coord) {
		return board1D[toIndex.convert(coord)];
	}
	
	public void swap(Tuple coord1, Tuple coord2) {
		// Switch the contents of two valid coordinates in the current board 
		
		if (coord1.getRow() < size || coord1.getCol() < size || coord2.getRow() < size || coord2.getCol() < size) {
			int index1 = toIndex.convert(coord1);
			int index2 = toIndex.convert(coord2);
			
			int firstVal = board1D[index1];
			board1D[index1] = board1D[index2];
			board1D[index2] = firstVal;
		} else {
			return;
		}
	}
	
	public Tuple findBlankCoord() {
		// Return the location of the blank (0) tile in the form of an ordered pair, where (0,0) corresponds to the upper-left corner of the board
		
		int[][] board2D = buildBoard2D();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board2D[row][col] == 0) {
					return new Tuple(row, col);
				}
			}
		}
		return new Tuple(-1, -1);	// failure - invalid coordinate
	}
	
	public ArrayList<Board> getNeighbors() {
		// Generate the frontier for the current board
		
		Tuple blankCoord = findBlankCoord();
		int bRow = blankCoord.getRow();
		int bCol = blankCoord.getCol();
		
		ArrayList<Tuple> validMoves = new ArrayList<Tuple>();
		if (bRow + 1 < size) {
			validMoves.add(new Tuple(bRow + 1, bCol));
		}
		if (bRow - 1 >= 0) {
			validMoves.add(new Tuple(bRow - 1, bCol));
		}
		if (bCol + 1 < size) {
			validMoves.add(new Tuple(bRow, bCol + 1));
		}
		if (bCol - 1 >= 0) {
			validMoves.add(new Tuple(bRow, bCol - 1));
		}
		
		ArrayList<Board> neighbors = new ArrayList<Board>();
		Board neighbor;
		for (Tuple move : validMoves) {
			neighbor = Board.copyOf(this);		// Return a new board with the current board's properties
			neighbor.swap(blankCoord, move);	// Perform a valid move on the copy
			neighbor.parent = this;				// Neighbors' parents should be the current board
			neighbor.isRoot = false;
			neighbors.add(neighbor);
		}
		return neighbors;
	}
	
	private int[] generateBoard() {
		// Build a random board based on the current object's size field (size 3 for an 8 puzzle, 4 for a 15 puzzle, etc.)
		// This method does not guarantee a solvable starting state
		
		int[] randomBoard = new int[size * size];
		ArrayList<Integer> usedTiles = new ArrayList<Integer>();
		Random rand = new Random();
		int numTiles = size * size;
		int tile;
		
		for (int i = 0; i < numTiles; i++) {
			tile = rand.nextInt(numTiles);
			
			while (usedTiles.contains(tile)) {
				tile = rand.nextInt(numTiles);
			}
			usedTiles.add(tile);
			randomBoard[i] = tile;
		}
		return randomBoard;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public boolean isValid() {
		return isSolvable(buildBoard2D());
	}
	
	public int[] getBoard1D() {
		return board1D;
	}
	
	public boolean isRoot() {
		return isRoot;
	}
	
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	
	public Board getParent() {
		return parent;
	}
	
	public void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
	}
	
	public int getHeuristic() {
		return heuristic;
	}
	
	public static Board copyOf(Board b) {
		return new Board(b.size, Arrays.copyOf(b.board1D, b.board1D.length), b.isRoot);
	}
	
	@Override
	public String toString() {
		String boardStr = "";
		
		for (int i = 0; i < board1D.length; i++) {
			if (i % size == 0) {
				boardStr += "\n";
			}
			boardStr += board1D[i] + "  ";
		}
		return boardStr;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Board)) {
			return false;
		}
		
		Board b = (Board) o;
		
		boolean sameValues = true;
		for (int i = 0; i < board1D.length; i++) {
			if (!(this.board1D[i] == b.board1D[i])) {
				sameValues = false;
				break;
			}
		}
		
		return this.size == b.size && sameValues;
	}
	
	@Override
	public int compareTo(Board b) {
		// Implements Comparable interface to allow for priority queue sorting
		
		if (this.heuristic > b.heuristic) {
			return -1;		// current board is less than b, since a lower heuristic value is more favorable
		} else if (this.heuristic < b.heuristic) {
			return 1;
		} else {
			return 0;
		}
	}
}
