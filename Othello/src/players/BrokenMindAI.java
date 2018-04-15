package players;

import java.util.List;

import components.Board;
import components.Color;
import components.Coordinate;
import heuristics.Heuristic;
import heuristics.HeuristicSearch;
/**
 * 
 * Time limit - 25 seconds (automatically searches as many plies as it can, such as during opening and end game)
 * Randomly decide whether to use the MaxPiecesHeuristic or the MaxMobilityHeuristic to order and prune the possible moves
 * 	Additionally randomly decide to prune the possible moves list to at least the top 20% but no more than the top 40%
 *
 */
public class BrokenMindAI extends OthelloAI {
	
	private final long secondsPerPly = 25;
	
	public BrokenMindAI(String name, Color color, Heuristic... heuristics) {
		super(name, color, heuristics);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Coordinate makeMove(Board board) {
		
//		for (Heuristic h : heuristics) {
//			curveWeight(h, board);
//		}
		List<Coordinate> moves = board.getValidMoves(color); //valid moves list
		Coordinate bestMove = moves.get(0);
		
		Board copy;
		double maxScore = 0.0;
		double currentScore;
		
		long timePerMove = secondsPerPly * 1_000_000_000 / moves.size();
		double[] heuristicScores = new double[heuristics.length];
		for (Coordinate coord : moves) { //walks thru valid moves and applies/decides which is best score
			currentScore = 0.0;
			copy = board.clone();
			copy.set(color, coord);
			
			/*
			 * Each heuristic the AI uses runs on its own thread, since they evaluate independently of each other
			 */
			for(Heuristic h : heuristics) {
				HeuristicSearch search = new HeuristicSearch(board, color, h, System.nanoTime() + timePerMove);
				Thread searchThread = new Thread(search);
				searchThread.start();
				currentScore += search.getFinalScore();
			}
			// TODO wait to synchronize all the heuristic threads 
			
			if (currentScore > maxScore) {
				maxScore = currentScore;
				bestMove = coord;
			}
		}
		
		return bestMove;
	}
	
	/**
	 * 
	 * Updates the weight of a heuristic based on the game's progress, and potentially other features of the board's current state.
	 * </br>We used this <a href="https://www.desmos.com/calculator/kreo2ssqj8">graphing website</a> to visualize weight curves that made sense for the various heuristics.
	 * 
	 * </br></br>
	 * Pieces heuristic curve: y = 0.000028x^3 + 0.0045x^2 - 0.1x + 2.75
	 * </br>
	 * Mobility heuristic curve: abs(-0.024*(x-35)^2 + 16)
	 * </br>
	 * Piece table heuristic curve: y = 24*log(-x + 70) - 10
	 * 
	 * @param h  the {@link Heuristic} to adjust
	 * @param board the {@link Board} off of which to adjust the heuristic's weight
	 * @return the updated weight
	 */
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
