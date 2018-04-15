package heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;

import components.Board;
import components.Color;
import components.Coordinate;

public class HeuristicSearchThread implements Callable<Double> {
	
	// Othello Data //
	private List<Heuristic> oppHeuristics;
	private Heuristic heuristic;
	
	private int oppCurveOffset = 2;	// used for updating opponent's heuristic weights
	/*
	 * Used to weight moves especially high/low if that move leads to a win/loss 
	 * (they'll cancel each other out if a move leads to equal numbers of wins and losses)
	 */
	private double winValue = 100;
	
	private Board currentBoard;
	private Board previousBoard;
	
	private Color ogColor;
	private Color oppColor;
	
	// Othello and Multithreading //
	private double heuristicTotal;
	private long nodesExplored;
	
	// Multithreading Data //
	private long timeLimit;
	public String threadName;
	
	// Miscellaneous //
	private Random rand;
	
	/////////////////
	// Constructor //
	/////////////////
	public HeuristicSearchThread(Heuristic heuristic) {
		this.heuristic = heuristic;
		threadName = heuristic.getName() + " Thread";
		oppHeuristics = Arrays.asList(new MaxPiecesHeuristic(50), new MinMobilityHeuristic(50), new PieceTableHeuristic(50));
		previousBoard = currentBoard = null;
		rand = new Random();
		initialize();
	}
	
	/////////////////////////////
	// Othello-Related Methods //
	/////////////////////////////
	public void setColor(Color c) {
		ogColor = c;
		oppColor = c.flip();
	}
	
	public void setCurrentBoard(Board b) {
		previousBoard = currentBoard;
		currentBoard = b.clone();
	}
	
	private double gradeBoardOg(Board board) {
		return heuristic.gradeBoard(ogColor, board);
	}
	
	private double gradeBoardOpp(Board board) {
		double oppScore = 0.0d;
		for (Heuristic h : oppHeuristics) {
			oppScore += h.gradeBoard(oppColor, board);
		}
		return oppScore / oppHeuristics.size();
	}
	
	private void learnOppHeuristics() {
		try {
			if (previousBoard != null) {
				double prevScore;
				double currScore;
				double percentError;
				System.out.println("\nUpdating opponent's estimated heuristics\n");
				for (Heuristic h : oppHeuristics) {
					prevScore = h.gradeBoard(oppColor, previousBoard);
					currScore = h.gradeBoard(oppColor, currentBoard);
					percentError = Math.abs((currScore - prevScore) / prevScore) * 100;
					System.out.println("% Error for " + h.getName() + ": " + percentError);
					// TODO maybe tune these ranges, they're just semi-randomly thrown in there for now
					if (percentError < 3) {
						h.updateWeight(5 * oppCurveOffset, true);
					} else if (percentError < 6) {
						h.updateWeight(4 * oppCurveOffset, true);
					} else if (percentError < 12) {
						h.updateWeight(3 * oppCurveOffset, true);
					} else if (percentError < 19) {
						h.updateWeight(2 * oppCurveOffset, true);
					} else if (percentError < 26) {
						h.updateWeight(oppCurveOffset, true);
					} else if (percentError < 35) {
						// Don't adjust weight
					} else if (percentError < 42) {
						h.updateWeight(-oppCurveOffset, true);
					} else if (percentError < 52) {
						h.updateWeight(-2 * oppCurveOffset, true);
					} else if (percentError < 59) {
						h.updateWeight(-3 * oppCurveOffset, true);
					} else {
						h.updateWeight(-5 * oppCurveOffset, true);
					}
					System.out.println(h.getName() + " updated weight: " + h.getWeight());
				}
			}
		} catch (NullPointerException e) {
			return;
		}	
	}
	
	///////////////////////////////////////////
	// Where Othello and Multithreading Meet //
	///////////////////////////////////////////
	private void search() {
		initialize();
		learnOppHeuristics();
		List<Coordinate> ogMoves;
		HashMap<Double, Coordinate> oppMoves;
		List<Double> oppScores;
		Queue<Board> frontier = new LinkedList<Board>();
		
		frontier.add(currentBoard.clone());
		Board localBoard;
		Board futureBoard;
		double currentScore;
		double maxScore;
		Coordinate bestMove;
		
		while (System.nanoTime() < timeLimit && !frontier.isEmpty()) {
			nodesExplored++;
			localBoard = frontier.remove();
			if (localBoard.isGameOver()) {
				if (localBoard.winner() == ogColor) {
					heuristicTotal += winValue;
				} else {
					heuristicTotal -= winValue;
				}
			} else {
				ogMoves = localBoard.getValidMoves(ogColor);
				if (ogMoves.size() > 0) {
					maxScore = 0.0d;
					bestMove = ogMoves.get(0);
					
					// Determine the best move to make for the current ply
					for (Coordinate ogMove : ogMoves) {
						futureBoard = localBoard.clone();
						futureBoard.set(ogColor, ogMove);
						currentScore = gradeBoardOg(futureBoard);
						if (currentScore > maxScore) {
							maxScore = currentScore;
							bestMove = ogMove;
						} else if (currentScore == maxScore) {
							if (rand.nextBoolean()) {
								bestMove = ogMove;	// the current move has a 50% chance of being selected as the best move if it scores the same as the previous highest
							}
						}
					}
					// Grade the resulting board after applying the optimal move
					localBoard.set(ogColor, bestMove);
					heuristicTotal += maxScore;
					
					// Find some reasonable moves for the opponent
					oppMoves = new HashMap<Double, Coordinate>();
					for (Coordinate oppMove : localBoard.getValidMoves(oppColor)) {
						futureBoard = localBoard.clone();
						futureBoard.set(oppColor, oppMove);
						currentScore = 0.0;
						for (Heuristic h : oppHeuristics) {
							currentScore += gradeBoardOpp(futureBoard);
						}
						oppMoves.put(currentScore, oppMove);
						oppScores = new ArrayList<Double>();
						for (Double val : oppMoves.keySet()) {
							oppScores.add(val);
						}
						Collections.sort(oppScores);
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
						
						// Take a sample of the opponent's better potential moves to expand further
						for (int i = 0; i < sampleCount; i++) {
							futureBoard = localBoard.clone();
							futureBoard.set(oppColor, oppMoves.get(oppScores.get(i)));
							frontier.add(futureBoard);	// place the resulting board at the back of the queue
						}	
					}
				}
			}
		}
	}
	
	////////////////////////////////////
	// Multithreading-Related Methods //
	////////////////////////////////////
	@Override
	public Double call() throws Exception {
		// TODO set thread priority based on heuristic weight
		System.out.println("Search started in: " + threadName + " (" + Thread.currentThread().getName() + ")");
		search();
		System.out.println(threadName + " (" + Thread.currentThread().getName() + ")" + " search completed!\n\tNodes explored: " + nodesExplored + 
				"\n\tAggregate heuristic: " + heuristicTotal + 
				"\n\tAveraged heuristic: " + (heuristicTotal / nodesExplored));
		return heuristicTotal / nodesExplored;
	}
	
	public void initialize() {
		heuristicTotal = 0.0d;
		nodesExplored = 0L;
	}
	
	public void setTimeLimit(long nanoseconds) {
		timeLimit = System.nanoTime() + nanoseconds;
	}
}
