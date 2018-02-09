package main;

public class Tuple {
	private int row;
	private int col;
	
	/////////////////
	// Constructor //
	/////////////////
	
	public Tuple(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	/////////////////////////////////////////////
	// Setters, Getters, and Auxiliary Methods //
	/////////////////////////////////////////////
	
	public int getRow() {
		return row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public int getCol() {
		return col;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	
	@Override
	public String toString() {
		return "(" + row + ", " + col + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof Tuple)) {
			return false;
		}
		
		Tuple t = (Tuple) o;
		return this.row == t.row && this.col == t.col;
	}
	
	@Override
	public int hashCode() {
		return 31 * (col * 7) + (row * 19);		// Provide a consistent hash code for Tuples based on values of row and col to allow hash sets etc to function properly
	}
}
