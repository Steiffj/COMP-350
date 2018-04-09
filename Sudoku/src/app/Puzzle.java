package app;

import java.util.Arrays;

public class Puzzle {
	
	private final int width;
	private final int size;
	private final int cellWidth;
	private final int blankIndicator;
	
	private Tuple[] contents;
	
	// Metadata for additional functionality with the Tester class
	private String name;
	private FailReport report;
	private boolean solved;
	
	public Puzzle(String contentsStr, int width, String name) {	
		
		this.name = name;
		
		this.width = width;
		size = width * width;
		cellWidth = (int) Math.sqrt(this.width);
		blankIndicator = 0;
		
		// Replace any given blank indicator (non-numeric characters) in the string with the internal blank indicator
		// Truncates the string if it is longer than the accepted puzzle size	
		int[] contentsArr = Arrays.stream(
							contentsStr.substring(0,  size)
							.replaceAll("[^1-9]{1}", Integer.toString(blankIndicator))
							.split("")
						)
						.mapToInt(Integer::parseInt).toArray();
		
		this.contents = new Tuple[size];
		
		for (int i = 0; i < contentsArr.length; i++) {
			this.contents[i] = new Tuple(contentsArr[i], i, contentsArr[i] != blankIndicator);
		}
	}
	
	// Copy Constructor
	public Puzzle(Puzzle that) {
		
		this.width = that.width;
		this.size = that.size;
		this.cellWidth = that.cellWidth;
		this.blankIndicator = that.blankIndicator;
		
		// create a deep copy of the contents array
		this.contents = new Tuple[that.contents.length];
		for (int i = 0; i < this.contents.length; i++) {
			this.contents[i] = new Tuple(that.contents[i]);
		}
		
		this.name = that.name;
	}
	
	// Returns a 2D array in which the inner arrays contain the values of the puzzle's rows
	public Tuple[][] groupRows() {
		Tuple[][] grouped = new Tuple[width][width];
		int iGroup = -1;
		
		for (int iContents = 0; iContents < size; iContents++) {
			if (iContents % 9 == 0) {
				iGroup++;
			}
			grouped[iGroup][iContents % 9] = contents[iContents];
		}
		return grouped;
	}
	
	// Returns a 2D array in which the inner arrays contain the values of the puzzle's columns
	public Tuple[][] groupColumns() {
		Tuple[][] grouped = new Tuple[width][width];
		int iGroup = -1;
		
		for (int iContents = 0; iContents < size; iContents++) {
			if (iContents % 9 == 0) {
				iGroup++;
			}
			grouped[iContents % 9][iGroup] = contents[iContents];
		}
		return grouped;
	}
	
	// Returns a 2D array in which the inner arrays contain the values of the puzzle's cells (3x3 square for a regular Sudoku board)
	public Tuple[][] groupCells() {
		Tuple[][] grouped = new Tuple[width][width];
		
		// row and col indicate the position within the current working cell
		int col = 0;
		int row = 0;
		
		// cellRow and cellCol indicate the upper-left corner of the current working cell
		int cellRow = 0;
		int cellCol = 0;
		
		int valuesAddedCurrentCell = 0;
		
		for(Tuple val : contents) {
			grouped[row][col] = val;
			valuesAddedCurrentCell++;
			
			// Handle rows and columns inside a cell
			col++;
			if (col % cellWidth == 0) {
				row++;
				col = cellRow;
			}
			
			// Handle the pointers to the current working cell
			if (valuesAddedCurrentCell == width) {
				cellRow += cellWidth;
				if (cellRow >= width) {
					cellCol += cellWidth;
					cellRow = 0;
				}
				valuesAddedCurrentCell = 0;
				col = cellRow;
				row = cellCol;
			}	
		}
		
		return grouped;
	}
	
	// Returns true if the Puzzle contains 81 digits between 1 and 9
	public boolean isFull() {			
		for(int i = 0; i < contents.length; i++) {
			
			if (contents[i].getValue() == blankIndicator) {
				return false;
			}
		}
		
		return true;
	}
	
	// Resets all numbers back to blanks (0), except for the original numbers in the puzzle
	public void reset() {
		for (int index = 0; index < size; index++) {
			if (!contents[index].isOriginal()) {
				contents[index].setValue(blankIndicator);;
			}
		}
	}
	
	public int size() {
		return size;
	}
	
	public int width() {
		return width;
	}
	
	public int cellWidth() {
		return cellWidth;
	}
	
	// Returns the value at the index specified
	public Tuple get(int index) {
		return contents[index];
	}
	
	public Tuple get(int row, int col) {
		return contents[width * row + col];
	}
	
	// Inserts the provided value at the index specified
	public void set(int value, int index) {
		if (!contents[index].isOriginal()) {
			contents[index].setValue(value);;
		}	
	}
	
	public void set(int value, int row, int col) {
		if (!contents[width * row + col].isOriginal()) {
			contents[width * row + col].setValue(value);
		}
	}	
	
//	// Returns true if the specified position is/was a blank in the original puzzle
//	public boolean isMutableAt(int index) {
//		return !contents[index].isOriginal();
//	}
//	
//	public boolean isMutableAt(int row, int col) {
//		return !contents[width * row + col].isOriginal();
//	}
	
	public int blank() {
		return blankIndicator;
	}
	
	// Swaps the values at the two specified indices 
	public void swap(int index1, int index2) {
		
		if (index1 == index2) {
			return;
		}
		
		int temp = contents[index2].getValue();
		contents[index2].setValue(contents[index1].getValue());
		contents[index1].setValue(temp);
		
	}
	
	@Override
	public String toString() {
		String retStr = "";
		
		// Build separating line to place between groups of rows
		String columnDelimiter = "";
		
		int dashCount;
		for (int i = 0; i < cellWidth; i++) {
			if (i != 0) {
				columnDelimiter += "+";
			}
			
			
			if(i != 0 && i != cellWidth - 1) {
				dashCount = cellWidth * 2 + 1;
			} else {
				dashCount = cellWidth * 2;
			}
			
			for (int j = 0; j < dashCount; j++) {
				columnDelimiter += "-";
			}
		}
		
		columnDelimiter += "\n";
		
		// Build string representation of the Sudoku board
		int row = 0;
		int col = 0;
		for (int index = 0; index < size; index++) {
			retStr += (contents[index].getValue() == blankIndicator) ? ". " : contents[index].getValue() + " ";
			
			col++;
			if (col % cellWidth == 0 && col != width) {
				retStr += "| ";
			}
			
			if (col % width == 0) {
				retStr += "\n";
				row++;
				col = 0;
				if (row % cellWidth == 0 && row != width) {
					retStr += columnDelimiter;
				}
			}
		}
		
		return retStr;
	}
	
	public String getName() {
		return name;
	}
	
	public FailReport getReport() {
		return report;
	}

	public void setReport(FailReport report) {
		this.report = report;
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Puzzle)) {
			return false;
		}
		
		Puzzle p = (Puzzle) o;
		if (width != p.width) {
			return false;
		}
		
		for (int i = 0; i < size; i++) {
			if (!contents[i].equals(p.contents[i])) {
				return false;
			}
		}
		
		return true;
	}
}
