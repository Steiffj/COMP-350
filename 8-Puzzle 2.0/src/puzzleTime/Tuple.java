package puzzleTime;

public class Tuple {
	private int row;
	private int col;
	
	public Tuple(int row, int col) {
		this.row = row;
		this.col = col;
	}

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
	public boolean equals(Object o) {
		if (o == this) {
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
		return 31 * (col * 7) + (row * 19);
	}
	
	public String toString() {
		return ("(" + row + ", " + col + ")");
	}

}
