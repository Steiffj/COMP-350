package heuristics;

import components.Board;
import components.Color;
import components.Coordinate;

public class PiecesHeuristic implements Heuristic {
	
	public PiecesHeuristic() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int gradeBoard(Color c, Board board) {
		int count = 0;
		
		for (int row = 0; row < board.getWidth(); row++) {
			for (int col = 0; row < board.getWidth(); col++) {
				if(board.get(new Coordinate(row, col)) == c) {
					count++;
				}
			}
		}
		
		return count;
	}
	
}
