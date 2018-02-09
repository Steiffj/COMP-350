package heuristics;

import main.Board;

public interface Heuristic {
	
	public int setValue(Board board);
	
	public void setReference(Board board);
}
