package components;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class OthelloBoard extends Board {
	
	/**
	 * The data structure containing the actual contents/configuration of the board
	 */
	//protected Color[][] contents;
	
	public OthelloBoard() {
		super(8);	// Ha, film
		contents = new Color[width][width];
		
		for (int i = 0; i < contents.length; i++) {
			for (int j = 0; j < contents[i].length; j++) {
				contents[i][j] = Color.EMPTY;
			}
		}
		
		initialize();
	}
	
	public OthelloBoard(int width) {
		super(width);
		contents = new Color[this.width][this.width];
		
		for (int i = 0; i < contents[i].length; i++) {
			for (int j = 0; i < contents[j].length; j++) {
				contents[i][j] = Color.EMPTY;
			}
		}
		
		initialize();
	}
	
	public OthelloBoard(Board that) {
		super(that.width);
		contents = new Color[width][width];
		
		for (int i = 0; i < contents.length; i++) {
			for (int j = 0; j < contents[i].length; j++) {
				contents[i][j] = that.contents[i][j];
			}
		}
	}
	
	@Override
	public boolean set(Color piece, Coordinate coord) {
		
		if (piece == null || coord == null) {
			return false;
		}
		
		if (!inBounds(coord) || contents[coord.getRow()][coord.getCol()] != Color.EMPTY) {
			// Position provided is already occupied - don't attempt to place piece on the board
			// Or the coordinate provided is invalid
			return false;
			
		} else if (checkValidMove(piece, coord)) {
			// Attempted move is valid - apply move to board
			applyMove(piece, coord);
			return true;
			
		} else {
			// Attempted move is not valid - don't apply move to board
			return false;
		}
	}
	
	private void applyMove(Color c, Coordinate coord) {
		
		contents[coord.getRow()][coord.getCol()] = c;
		
		int rowOffset;
		int colOffset;
		
		for (Direction dir : checkValidMoveDirection(c, coord)) {
			
			switch(dir) {
			case COL_DOWN:
				rowOffset = 1;
				colOffset = 0;
				break;
			case COL_UP:
				rowOffset = -1;
				colOffset = 0;
				break;
			case DIAG_LOWER_LEFT:
				rowOffset = 1;
				colOffset = -1;
				break;
			case DIAG_LOWER_RIGHT:
				rowOffset = 1;
				colOffset = 1;
				break;
			case DIAG_UPPER_LEFT:
				rowOffset = -1;
				colOffset = -1;
				break;
			case DIAG_UPPER_RIGHT:
				rowOffset = -1;
				colOffset = 1;
				break;
			case ROW_LEFT:
				rowOffset = 0;
				colOffset = -1;
				break;
			case ROW_RIGHT:
				rowOffset = 0;
				colOffset = 1;
				break;
			default:
				rowOffset = -999;
				colOffset = -999;
			}
			
			int row = coord.getRow();
			int col = coord.getCol();
			
			// (row + rowOffset * mult < width && row + rowOffset * mult >= 0) && (col + colOffset * mult < width && col + colOffset * mult >= 0) && 
			for (int mult = 1; inBounds(row + rowOffset * mult, col + colOffset * mult); mult++) {
				if(get(row + rowOffset * mult, col + colOffset * mult) != c) {
					flip(row + rowOffset * mult, col + colOffset * mult);
				} else {
					break;
				}
			}
		}
	}
	
	private boolean checkValidMove(Color c, Coordinate coord) {
		return checkValidMoveDirection(c, coord).size() > 0 && contents[coord.getRow()][coord.getCol()] == Color.EMPTY;
	}
	
	private List<Direction> checkValidMoveDirection(Color c, Coordinate coord) {
		
		List<Direction> validDirections = new ArrayList<Direction>();
		String vectorStr;
		for (Direction dir : Direction.values()) {
			vectorStr = "";
			for(Color piece : groupDirection(c, coord, dir)) {
				vectorStr += piece;
			}
			
			// for c == B, vectorStr matches "BW...B" in which there is at least one "W", but no more "W"s than two less than the width of the board 
			if (vectorStr.matches("[" + c + "]{1}" +  "[" + c.flip() + "]{1," + (width - 2) + "}" + "[" + c + "]{1}.*")) {
				validDirections.add(dir);
			}
		}
		
		return validDirections;
	}
	
	private List<Color> groupDirection(Color c, Coordinate coord, Direction dir) {
		int rowOffset;
		int colOffset;
		
		switch(dir) {
		case COL_DOWN:
			rowOffset = 1;
			colOffset = 0;
			break;
		case COL_UP:
			rowOffset = -1;
			colOffset = 0;
			break;
		case DIAG_LOWER_LEFT:
			rowOffset = 1;
			colOffset = -1;
			break;
		case DIAG_LOWER_RIGHT:
			rowOffset = 1;
			colOffset = 1;
			break;
		case DIAG_UPPER_LEFT:
			rowOffset = -1;
			colOffset = -1;
			break;
		case DIAG_UPPER_RIGHT:
			rowOffset = -1;
			colOffset = 1;
			break;
		case ROW_LEFT:
			rowOffset = 0;
			colOffset = -1;
			break;
		case ROW_RIGHT:
			rowOffset = 0;
			colOffset = 1;
			break;
		default:
			rowOffset = -999;
			colOffset = -999;
		}
		
		int row = coord.getRow();
		int col = coord.getCol();
		List<Color> group = new Vector<Color>();
		group.add(c);
		
		// (row + rowOffset * mult < width && row + rowOffset * mult >= 0) && (col + colOffset * mult < width && col + colOffset * mult >= 0) && 
		for (int mult = 1; inBounds(row + rowOffset * mult, col + colOffset * mult); mult++) {
			group.add(contents[row + rowOffset * mult][col + colOffset * mult]);			
		}
		
		return group;
	}
	
	@Override
	public int countValidMoves(Color c) {
		int moveCount = 0;
		
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < width; col++) {
				if (checkValidMove(c, new Coordinate(row, col))) {
					moveCount++;
				}
			}
		}
		
		return moveCount;
	}
	
	@Override
	public List<Coordinate> getValidMoves(Color c) {
		List<Coordinate> moveList = new ArrayList<Coordinate>();
		Coordinate currentPos;
		
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < width; col++) {
				currentPos = new Coordinate(row, col);
				if (checkValidMove(c, currentPos)) {
					moveList.add(currentPos);
				}
			}
		}
		
		return moveList;
	}
	
	@Override
	public Color get(Coordinate coord) {
		return contents[coord.getRow()][coord.getCol()];
	}
	
	private Color get(int row, int col) {
		return contents[row][col];
	}
	
	public Color[][] getContents() {
		return contents;
	}
	
	public void setContents(Color[][] contents) {
		this.contents = contents;
	}
	
	@Override
	public void initialize() {
		
		/*
		 * WB
		 * BW <- start at this W and work left and up
		 */
		
		int lowerRight = width / 2;
		// contents[row][column]
		contents[lowerRight][lowerRight] = Color.W;
		contents[lowerRight][lowerRight - 1] = Color.B;
		contents[lowerRight - 1][lowerRight] = Color.B;
		contents[lowerRight - 1][lowerRight - 1] = Color.W;
	}
	
	////////////////////
	// Helper Methods //
	////////////////////
	
	public boolean flip(Coordinate coord) {
		
		return flip(coord.getRow(), coord.getCol());
	}
	
	public boolean flip(int row, int col) {
		
		if (flippable(row, col)) {
			contents[row][col] = contents[row][col].flip();
			return true;
		} else {
			return false;
		}
	}
	
	private boolean flippable(Coordinate coord) {
		return inBounds(coord) && contents[coord.getRow()][coord.getCol()] != Color.EMPTY;
	}
	
	private boolean flippable(int row, int col) {
		return inBounds(row, col) && contents[row][col] != Color.EMPTY;
	}
	
	private boolean inBounds(Coordinate coord) {
		return coord.getRow() < width && coord.getCol() < width && coord.getRow() >= 0 && coord.getCol() >= 0;
	}
	
	private boolean inBounds(int row, int col) {
		return row < width && col < width && row >= 0 && col >= 0;
	}
	
	@Override
	public String toString() {
		String retString = "  A B C D E F G H \n";
		
		int rowNum = 1;
		for (Color[] row : contents) {
			retString += rowNum++ + " ";
			for (Color p : row) {
				switch(p) {
				case B:
					retString += "B ";
					break;
				case W:
					retString += "W ";
					break;
				case EMPTY:
					retString += ". ";
					break;
				}
			}
			retString += "\n";
		}
		
		return retString;
	}
	
	public String toString(Color c) {
		String retString = "  A B C D E F G H \n";
		List<Coordinate> validMoves = getValidMoves(c);		
		
		int rowNum = 1;
		for (int row = 0; row < width; row++) {
			retString += rowNum++ + " ";
			
			for (int col = 0; col < width; col++) {
				
				switch(contents[row][col]) {
				case B:
					retString += "B ";
					break;
				case W:
					retString += "W ";
					break;
				case EMPTY:
					retString += validMoves.contains(new Coordinate(row, col)) ? "* " : ". ";
					break;
				}
			}
			retString += "\n";
		}
		
		return retString;
	}
	
	@Override
	public Board clone() {
		return new OthelloBoard(this);
	}
}
