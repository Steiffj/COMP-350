package heuristics;

import components.Board;
import components.Color;

public class MobilityHeuristic implements Heuristic {
	
	public MobilityHeuristic() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int gradeBoard(Color c, Board board) {
		// TODO Auto-generated method stub
		return board.countValidMoves(c);
	}
}
