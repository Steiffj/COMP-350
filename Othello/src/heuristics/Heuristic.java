package heuristics;

import components.Board;
import components.Color;

public interface Heuristic {
	
	public int gradeBoard(Color c, Board board);
}
