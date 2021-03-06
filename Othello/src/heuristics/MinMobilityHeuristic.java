package heuristics;

import components.Board;
import components.Color;

/**
 * 
 * Use this to grade boards resulting from your ply.
 *
 */
public class MinMobilityHeuristic extends Heuristic {
	
	public MinMobilityHeuristic() {
		super();
		name = "Minimum Mobility";
	}
	
	public MinMobilityHeuristic(int weight) {
		super(weight);
		name = "Minimum Mobility";
	}
	
	@Override
	public long gradeBoard(Color piece, Board board) {
		return -board.countValidMoves(piece.flip()) * weight * equalizer;
	}

	@Override
	public long gradeBoardRaw(Color piece, Board board) {
		// TODO Auto-generated method stub
		return -board.countValidMoves(piece.flip()) * weight * equalizer;
	}
	
}
