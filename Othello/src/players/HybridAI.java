package players;

import java.util.List;

import components.Board;
import components.Color;
import components.Coordinate;
import heuristics.Heuristic;

public class HybridAI extends OthelloAI {
	private boolean curve;
	
	public HybridAI(String name, Color color, boolean curve, Heuristic... heuristics) {
		super(name, color, heuristics);
		this.curve = curve;
	}
	
	@Override
	public Coordinate makeMove(Board board) {
//		if (curve) {
//			for (Heuristic h : heuristics) {
//				curveWeight(h, board);
//			}
//		}
		
		List<Coordinate> moves = board.getValidMoves(color); //gets valid moves
		if (moves.size() > 0) {
			Coordinate bestMove = moves.get(0); 				 
			Board copy;
			double maxScore = 0.0;
			double currentScore;
			
			for (Coordinate coord : moves) { 
				currentScore = 0.0;	 
				copy = board.clone();
				copy.set(color, coord);
				
				for(Heuristic h : heuristics) {
					currentScore += h.gradeBoard(color, copy);
				}
				
				if (currentScore > maxScore) {
					maxScore = currentScore;
					bestMove = coord;
				}
			}
			
			return bestMove;
		} else {
			return null;
		}
	}
	
//	private void curveWeight(Heuristic h, Board board) {
//		int count = board.countPieces(Color.B) + board.countPieces(Color.W);	// indicator of game progress
//		
//		if (h instanceof MaxPiecesHeuristic) {
//			h.updateWeight(0.000028*(Math.pow(count, 3) + 0.0045*Math.pow(count, 2) - 0.1*count + 4.75));
//		} else if (h instanceof MaxMobilityHeuristic) {
//			h.updateWeight(Math.abs(-0.024*Math.pow(count-35, 2) + 20));
//		} else if (h instanceof PieceTableHeuristic) {
//			h.updateWeight(32*Math.log(-count+70) - 5);
//		} else {
//			h.updateWeight(8);
//		}
//	}
}
