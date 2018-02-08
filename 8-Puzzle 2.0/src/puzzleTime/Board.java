package puzzleTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Board {
	private Board parent;
	private boolean isRoot = false;
	protected int[] board;
	protected int size;
	protected HashMap<Tuple, Integer> map;

	//Constructor to create root board
	public Board(int size) {
		this.size = size;
		board = new int[size*size];
		initializeMap();
		
		int[][] randomBoard = generateBoard();
		while(!isSolvable(randomBoard)) {
			randomBoard = generateBoard();
		}
		setValues(Arrays.stream(randomBoard).flatMapToInt(Arrays::stream).toArray());
	}
	//Constructor to create a board thats a copy of the board given
	public Board(Board b) {
		this.size = b.getSize();
		setValues(Arrays.copyOf(b.get1D(), b.get1D().length));
		initializeMap();
	}
	
	//Constructor to feed specific values
	public Board(int size, int[] values) {
		this.size = size;
		setValues(values);
		initializeMap();
	}
	
	///////////////
	//BOARD SETUP//
	///////////////

	private void initializeMap() {
		map = new HashMap<Tuple, Integer>();
		int index = 0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				map.put(new Tuple(row, col), index);
				index++;
			}
		}
	}
	
	public int[][] generateBoard() {
    	int[][] randomState = new int[size][size];
    	ArrayList<Integer> usedTiles = new ArrayList<Integer>();
    	Random rand = new Random();
    	int numTiles = size * size;
    	int tile;
    	
    	for (int row = 0; row < size; row++) {
    		for (int col = 0; col < size; col++) {
    			tile = rand.nextInt(numTiles);
    			
    			while (usedTiles.contains(tile)) {
    				tile = rand.nextInt(numTiles);
    			}
    			usedTiles.add(tile);
    			randomState[row][col] = tile;
    		}
    	}
    	return randomState;
    }
	
	protected void setValues (int[] values) {
		if (values.length == size * size) {
			this.board = values;
		} else {
			System.err.println("Board is of invalid size.");
			return;
		}		
	}
	
	/////////////////////
	//GETTERS & SETTERS//
    /////////////////////
	
	public Tuple getBlank() {
 		for(Tuple coord : map.keySet()){
 			if (board[map.get(coord)] == 0) {
 				return coord;
 			}
 		}
 		return new Tuple(-100, -100);
 	}
	public int getSize() {
	   	return size;
	}
	
	public void setRoot(boolean root) {
    	isRoot = root;
    }

    public boolean getRoot() {
    	return this.isRoot;
    }
    
    public void setParent(Board child, Board parent) {
    	child.parent = parent;
    }
    
    public Board getParent() {
    	return this.parent;
    }
    
    public int getValueAt(int index) {
		return board[index];
	}
	
	public int getValueAt(int row, int col) {
		return board[map.get(new Tuple(row, col))];
	}
	
    public int[] get1D() {
    	return board;
    }
	
    ////////////////////
    //HELPER FUNCTIONS//
    ////////////////////
    
    public void swap(Tuple coord1, Tuple coord2) {
    	int first, second;
    	first = board[map.get(coord1)];
    	second = board[map.get(coord2)];
    	
    	board[map.get(coord1)] = second;
    	board[map.get(coord2)] = first;    	
    }
    
    @Override
    public String toString() {
    	String boardStr = "";
    	for (int row = 0; row < size; row++) {
    		for (int col = 0; col < size; col++) {
    			boardStr += getValueAt(row, col) + "  ";
    		}
    		boardStr += "\n";
    	}
    	return boardStr;
    }
    
	////////////////////
	//IS SOLVABLE AREA//
	////////////////////
	
	public boolean isSolvable(int[][] puzzle) {
    	// Count inversion in given puzzle
    	int invCount = getInvCount(Arrays.stream(puzzle).flatMapToInt(Arrays::stream).toArray());
    	
    	//If grid is odd, return true if inversion
    	//count is even.
    	if (size % 2 != 0) {
    		return (invCount % 2 == 0); 
    	} else { //Grid is even
    		int pos = findXPosition(puzzle);
    		if (pos % 2 == 0) {
    			return (invCount % 2 != 0);
    		} else { //pos is odd
    			return (invCount % 2 == 0);
    		}
    	}
    }
    
    protected int getInvCount (int arr[]) {
    	int invCount = 0;
    	for (int i = 0; i < size * size - 1; i++) {
    		for (int j = i + 1; j < size * size; j++) {
    			if (arr[i] > arr[j]) {
    				invCount++;
    			}
    		}
    	}
    	return invCount;
    }
    
    protected int findXPosition (int[][] puzzle) {
    	for (int i = size - 1; i >= 0; i--) {
    		for (int j = size - 1; j >= 0; j--) {
    			if (puzzle[i][j] == 0) {
    				return size - i;
    			}
    		}
    	}
    	return -1;
    }
    /////////////////////////////////////////////////////
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
    	for ( int i = 0; i < board.length; i++) {
    		if(!(this.board[i] == b.board[i])) {
    			sameValues = false;
    			break;
    		}
    	}
    	return this.size == b.size && sameValues;
    }

}
