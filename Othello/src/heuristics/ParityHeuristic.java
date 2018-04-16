package heuristics;

import components.Board;
import components.Color;

public class ParityHeuristic extends Heuristic {
	
	public ParityHeuristic() {
		super();
		name = "Parity";
	}
	
	public ParityHeuristic(long weight) {
		super(weight);
		name = "Parity";
	}
	
	@Override
	public long gradeBoard(Color c, Board board) {
		if (board.countValidMoves(c.flip()) == 0 && board.getSize() - board.countPieces(Color.EMPTY) % 2 != 0) {
			return weight;
		} else if (board.countValidMoves(c.flip()) == 0 && board.getSize() - board.countPieces(Color.EMPTY) % 2 == 0) {
			return -weight;
		} else {
			return 0;
		}
	}
	
	@Override
	public long gradeBoardRaw(Color c, Board board) {
		return gradeBoard(c, board);
	}
	
}
