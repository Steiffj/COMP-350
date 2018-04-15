package heuristics;

import components.Board;
import components.Color;

/**
 * 
 * Rephrase that: use this to grade boards resulting from your opponent's ply
 * 
 * Only use this to grade boards after a theoretical opponent makes their ply. 
 * (This is not very useful unless you search at least one turn beyond your current move.) 
 * You want to maximize your moves, not your opponent's!
 */
public class MaxMobilityHeuristic extends Heuristic {
	
	public MaxMobilityHeuristic() {
		super();
		name = "Maximum Mobility";
	}
	
	public MaxMobilityHeuristic(int weight) {
		super(weight);
		name = "Maximum Mobility";
	}
	
	@Override
	public double gradeBoard(Color piece, Board board) {
		return board.countValidMoves(piece.flip()) * weight;
	}

	@Override
	public double gradeBoardRaw(Color piece, Board board) {
		return board.countValidMoves(piece.flip());
	}
}
