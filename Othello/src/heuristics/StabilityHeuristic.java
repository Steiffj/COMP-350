package heuristics;

import components.Board;
import components.Color;
import components.Coordinate;

public class StabilityHeuristic extends Heuristic {
	
	public StabilityHeuristic() {
		// TODO Auto-generated constructor stub
	}
	
	public StabilityHeuristic(long weight) {
		super(weight);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public long gradeBoard(Color c, Board board) {
		Board copy = board.clone();
		Coordinate check;
		long score = 0;
		int numOppMoves = 0;
		for (Coordinate move : copy.getValidMoves(c.flip())) {
			copy.set(c.flip(), move);
			
			for (int row = 0; row < board.getWidth(); row++) {
				for (int col = 0; col < board.getWidth(); col++) {
					check = new Coordinate(row, col);
					if (board.get(check) == copy.get(check) && board.get(check) != Color.EMPTY) {
						score += 1;
					} else if (board.get(check) == copy.get(check) && board.get(check) != Color.EMPTY) {
						score -= 1;
					}
				}
			}
			numOppMoves++;
		}
		return numOppMoves > 0 ? score * weight * equalizer / numOppMoves : 0;
	}
	
	@Override
	public long gradeBoardRaw(Color c, Board board) {
		// TODO Auto-generated method stub
		return 0;
	}	
}
