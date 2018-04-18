package heuristics;

import components.Board;
import components.Color;

public class MaxPiecesHeuristic extends Heuristic {
	
	public MaxPiecesHeuristic() {
		super();
		name = "Maximum Pieces";
	}
	
	public MaxPiecesHeuristic(int weight) {
		super(weight);
		name = "Maximum Pieces";
	}
	
	@Override
	public long gradeBoard(Color piece, Board board) {
		return board.countPieces(piece) * weight * equalizer;
	}

	@Override
	public long gradeBoardRaw(Color piece, Board board) {
		// TODO Auto-generated method stub
		return board.countPieces(piece);
	}
}
