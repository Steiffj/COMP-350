package heuristics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import components.Board;
import components.Color;
import components.Coordinate;

public class HeuristicSearch implements Runnable {
	/*
	 * It would be cool to implement some kind of learning where the search approximates the opponent's strategy
	 * based on the actual opponent's ply. Insert whatever heuristic (or combination of heuristics) that would lead to
	 * the opponent making the move it did
	 * 
	 * On the other hand, the cost of approximating the opponent's strategy might outweigh the benefit of spending that computation power
	 * to search further down the tree 
	 */
	private Heuristic[] oppHeuristics;
	
	private double aggregateScore;
	private int nodesVisited;
	private final int winValue = 100;	// TODO adjust this after testing
	
	private long timeLimit;
	private Heuristic heuristic;
	private Board ogBoard;
	private Color ogColor;
	private Color oppColor;
	
	private Random rand;
	
	public HeuristicSearch(Board ogBoard, Color ogColor, Heuristic h, long timeLimit) {
		this.ogBoard = ogBoard;
		this.ogColor = ogColor;
		oppColor = ogColor.flip();
		heuristic = h;
		oppHeuristics = new Heuristic[] { new MaxPiecesHeuristic(75), new MaxMobilityHeuristic(35), new PieceTableHeuristic(40) };	// TODO tune heuristic weights
		
		this.timeLimit = timeLimit;
		aggregateScore = 0.0;
		nodesVisited = 0;
		
		rand = new Random();
	}
	
	public double getFinalScore() {
		return aggregateScore / nodesVisited;
	}

	@Override
	public void run() {
		//System.out.println("Thread running for single heuristic!");
		aggregateScore = 0;
		nodesVisited = 0;
		gradeBoardRecursive(ogBoard, timeLimit);
		System.out.println("Nodes expanded: " + nodesVisited);
	}
	
	/**
	 * 
	 * All the work is done here!
	 * It's a long method, but we're trying to minimize adding method calls to the call stack as much as possible
	 * 
	 * @param board
	 * @param localTimeLimit
	 */
	private void gradeBoardRecursive(Board board, long localTimeLimit) {
		nodesVisited++;	// update total number of nodes explored in the overall recursive search
		if(System.nanoTime() >= localTimeLimit || board.isGameOver()) {
			/*
			 * TODO add/subtract value from aggregateScore if the game is over, and you won/lost
			 * 
			 * Base cases:
			 * 	The search has reached a game over state, or
			 * 	the current branch exceeds its time limit
			 */
			
			// Add/subtract the value of reaching a win state from aggregateScore if you won/lost
			if (board.winner() == ogColor) {
				aggregateScore += winValue * heuristic.getWeight();
			} else {
				aggregateScore -= winValue * heuristic.getWeight();
			}
			return;
		} else {
			/*
			 * Recursive case
			 */
			
			
			Board copy;
			List<Coordinate> moves = board.getValidMoves(ogColor);
			double currentScore;
			if (moves.size() > 0) {
				Coordinate bestMove = moves.get(0);
				double maxScore = 0.0;
				
				// Determine the best move to make for the current ply
				for (Coordinate move : moves) {
					copy = board.clone();
					copy.set(ogColor, move);
					currentScore = heuristic.gradeBoard(ogColor, board);
					
					if (currentScore > maxScore) {
						maxScore = currentScore;
						bestMove = move;
					}
				}
				
				// Grade the resulting board after applying the optimal move
				copy = board.clone();
				copy.set(ogColor, bestMove);	// apply the best move to a copy of the current board
				aggregateScore += maxScore;		// update aggregate heuristic value
			} else {
				copy = board.clone();
			}
			
			
			HashMap<Double, Coordinate> oppMoves = new HashMap<Double, Coordinate>();
			
			/*
			 * A similar process to finding the optimal move for the current player,
			 * the following for-each loop scores all the opponent's moves, storing the coordinate and resulting heuristic value
			 * as a key-value pair
			 */
			Board oppCopy;
			for (Coordinate oppMove : copy.getValidMoves(oppColor)) {
				
				oppCopy = copy.clone();
				oppCopy.set(oppColor, oppMove);
				
				/*
				 * The opponent can make moves based on more than just the current heuristic, it's not too expensive,
				 * and it should further discourage the search from going down deterministic paths
				 * (that occur when assuming perfect play with no look-ahead)
				 * 
				 * Since we don't know what the opponent will prioritize, 
				 * using a mix of heuristics will hopefully result in exploring a more balanced spread of potential plies.
				 * BUT WHO EVEN KNOWS! Maybe it's complete trash, and picking randomly works better
				 */
				currentScore = 0.0;
				for (Heuristic h : oppHeuristics) {
					currentScore += h.gradeBoard(oppColor, oppCopy);
				}
				oppMoves.put(currentScore, oppMove);
			}
			
			// Limit nodes to expand based on what might be a decent-to-optimal move for the opponent
			List<Double> oppScores = new ArrayList<Double>();
			for (Double val : oppMoves.keySet()) {
				oppScores.add(val);
			}
			/*
			 * Using Double objects is preferable here.
			 * 
			 * Arrays.sort() uses TimSort for objects, which runs in O(nlog(n)),
			 * vs QuickSort for primitive types, which can run in O(n^2) for nearly-sorted arrays 
			 * (which could happen pretty frequently here)
			 * 
			 * The array is always really small, so it doesn't matter that much....
			 * Also toArray() requires a cast to an array of Double objects anyway
			 */
			Collections.sort(oppScores);	 
			
			// Determine how many nodes to expand from the frontier
			int sampleCount;
			double sampleFloor = 0.3;	// TODO consider adjusting the sample floor and ceiling
			double sampleCeil = 0.6;
			if (oppScores.size() <= 2) {
				sampleCount = oppScores.size();
			} else {
				sampleCount = rand.nextInt(oppScores.size());
				if (sampleCount < Math.ceil(oppScores.size() * sampleFloor)) {
					sampleCount = (int) Math.ceil(oppScores.size() * sampleFloor);
				} else if (sampleCount > Math.ceil(oppScores.size() * sampleCeil)) {
					sampleCount = (int) Math.ceil(oppScores.size() * sampleCeil);
				}
			}
			
			// Take a chunk of the opponent's better potential moves to expand (skips the loop if there are no moves)
			List<Coordinate> oppDecentMoves = new ArrayList<Coordinate>();
			for (int i = 0; i < sampleCount; i++) {
				oppDecentMoves.add(oppMoves.get(oppScores.get(i)));
			}
			
			
			if (oppScores.size() > 0) {
				/*
				 * The opponent has a valid move (doesn't pass)
				 * Find the next optimal move based on opponent's potential move
				 * (and so forth, until the search reaches a base case)
				 */
				
				// Split time limit for the current node into equal parts for each of its children
				long newTimeLimit = localTimeLimit / oppDecentMoves.size();
				
				for(Coordinate move : oppDecentMoves) {
					// Apply opponent's move for each move to expand
					oppCopy = copy.clone();
					oppCopy.set(oppColor, move);
					gradeBoardRecursive(oppCopy, System.nanoTime() + newTimeLimit);
				}
			} else {
				/*
				 * The opponent has no valid moves (passes)
				 * Find the next optimal move, taking a second ply on the board you just placed a piece on
				 */
				gradeBoardRecursive(copy, System.nanoTime() + timeLimit);
			}
		}
	}
	
	private void gradeBoardBFS(Board board) {
		Queue<Board> frontier = new LinkedList<Board>();
		frontier.add(board);
		Board copy = board.clone();
		while (!frontier.isEmpty()) {
			if (copy.isGameOver()) {
				/*
				 * Base case - update heuristic based on whether you won or lost
				 */
				if (board.winner() == ogColor) {
					aggregateScore += winValue * heuristic.getWeight();
				} else {
					aggregateScore -= winValue * heuristic.getWeight();
				}
			} else {
				List<Coordinate> moves = board.getValidMoves(ogColor);
				double currentScore;
				if (moves.size() > 0) {
					Coordinate bestMove = moves.get(0);
					double maxScore = 0.0;
					
					// Determine the best move to make for the current ply
					for (Coordinate move : moves) {
						copy = board.clone();
						copy.set(ogColor, move);
						currentScore = heuristic.gradeBoard(ogColor, board);
						
						if (currentScore > maxScore) {
							maxScore = currentScore;
							bestMove = move;
						}
					}
					
					// Grade the resulting board after applying the optimal move
					copy = board.clone();
					copy.set(ogColor, bestMove);	// apply the best move to a copy of the current board
					aggregateScore += maxScore;		// update aggregate heuristic value
				}
				// TODO more stuff to transition
			}
		}
	}
	
	private double gradeBoardOG(Board board) {
		return heuristic.gradeBoard(ogColor, board);
	}
	
	private double gradeBoardOPP(Board board) {
		double oppScore = 0.0;
		for (Heuristic h : oppHeuristics) {
			oppScore += h.gradeBoard(oppColor, board);
		}
		return oppScore / oppHeuristics.length;
	}
}
