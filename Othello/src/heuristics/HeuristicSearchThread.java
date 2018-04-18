package heuristics;

import java.util.ArrayList;
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

public class HeuristicSearchThread implements Callable<Long> {
	
	// Othello Data //
	private List<Heuristic> oppHeuristics;
	private Heuristic heuristic;
	
	/*
	 * winValue is used to weight moves especially high/low if that move leads to a win/loss 
	 * (they'll cancel each other out if a move leads to equal numbers of wins and losses)
	 */
	private long winValue = 1000;
	private Board currentBoard;
	
	private Color ogColor;
	private Color oppColor;
	
	// Othello and Multithreading //
	private long heuristicTotal;	// the sum of scores for all boards of your ply explored in a search
	private long nodesExplored;		// the number of nodes (boards) of your ply explored in a search
	
	// Multithreading Data //
	private long timeLimit;
	public String threadName;
	
	// Miscellaneous //
	private Random rand;
	
	/////////////////
	// Constructor //
	/////////////////
	public HeuristicSearchThread(Heuristic heuristic, List<Heuristic> oppHeuristics) {
		this.heuristic = heuristic;
		threadName = heuristic.getName() + " Thread";
		//oppHeuristics = Arrays.asList(new MaxPiecesHeuristic(50), new MinMobilityHeuristic(50), new PieceTableHeuristic(50));
		this.oppHeuristics = oppHeuristics;
		rand = new Random();
		initialize();
	}
	
	/////////////////////////////
	// Othello-Related Methods //
	/////////////////////////////
	public void setColor(Color c) {
		ogColor = c;
		oppColor = ogColor.flip();
	}
	
	public void setCurrentBoard(Board b) {
		currentBoard = b.clone();
	}
	
	private long gradeBoardOg(Board board) {
		return heuristic.gradeBoard(ogColor, board);
	}
	
	private long gradeBoardOpp(Board board) {
		long oppScore = 0L;
		for (Heuristic h : oppHeuristics) {
			oppScore += h.gradeBoard(oppColor, board);
		}
		return oppScore / oppHeuristics.size();
	}
	
	///////////////////////////////////////////
	// Where Othello and Multithreading Meet //
	///////////////////////////////////////////
	private long search() {
		initialize();
		List<Coordinate> ogMoves;
		HashMap<Long, Coordinate> oppMoves;
		List<Long> oppScores;
		
		Board localBoard;
		Board futureBoard;
		long currentScore;
		long maxScore;
		long numWins = 0L;
		long numLosses = 0L;
		Coordinate bestMove;
		
		Queue<Board> frontier = new LinkedList<Board>();
		frontier.add(currentBoard.clone());
		while (System.nanoTime() < timeLimit && !frontier.isEmpty()) {
			//long startTime = System.nanoTime();
			
			localBoard = frontier.remove();	// dequeue node to expand
			nodesExplored++;
			if (localBoard.isGameOver()) {
				/*
				 * Current board in the queue has reached a game over state
				 * Award/Penalize extra for reaching a win/loss in the search
				 */
				if (localBoard.winner() == ogColor) {
					heuristicTotal += winValue * heuristic.getWeight();
					numWins++;
				} else {
					heuristicTotal -= winValue * heuristic.getWeight();
					numLosses++;
				}
			} else {
				ogMoves = localBoard.getValidMoves(ogColor);
				if (ogMoves.size() > 0) {
					maxScore = Long.MIN_VALUE;
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
					heuristicTotal += maxScore;	// keep score of all nodes visited for piece table and parity heuristics
					
					// Grade all the opponent's possible moves (just one ply down for them)
					int oppMoveCount = 0;	// quick, kind of messy way to check if the opponent has any valid moves later on
					oppMoves = new HashMap<Long, Coordinate>();
					for (Coordinate oppMove : localBoard.getValidMoves(oppColor)) {
						oppMoveCount++;
						futureBoard = localBoard.clone();
						futureBoard.set(oppColor, oppMove);
						currentScore = gradeBoardOpp(futureBoard);
						oppMoves.put(currentScore, oppMove);	
					}
					
					if(oppMoveCount > 0) {
						oppScores = new ArrayList<Long>();	// scores go in a list because they're easy to sort that way (and the size will always be small)
						for (Long val : oppMoves.keySet()) {
							oppScores.add(val);
						}
						/*
						 * Randomly choose somewhere in the top 35% - 65% of valid moves.
						 * Choosing exactly 35% or 65% is more likely than in between because I don't feel like doing better math right now.
						 * 
						 * UPDATE: just takes the top half of the opponent's possible moves to expand upon
						 */
						Collections.sort(oppScores);
						int sampleCount;
						if (oppScores.size() <= 3) {
							sampleCount = oppScores.size();
						} else {
//							double sampleFloor = 0.35;	// TODO consider adjusting the sample floor and ceiling
//							double sampleCeil = 0.65;
//							sampleCount = rand.nextInt(oppScores.size());
//							if (sampleCount < Math.ceil(oppScores.size() * sampleFloor)) {
//								sampleCount = (int) Math.ceil(oppScores.size() * sampleFloor);
//							} else if (sampleCount > Math.ceil(oppScores.size() * sampleCeil)) {
//								sampleCount = (int) Math.ceil(oppScores.size() * sampleCeil);
//							}
							sampleCount = oppScores.size() / 2;
						}
						
						// Take a sample of the opponent's better potential moves to expand further
						for (int i = 0; i < sampleCount; i++) {
							futureBoard = localBoard.clone();
							futureBoard.set(oppColor, oppMoves.get(oppScores.get(i)));
							frontier.add(futureBoard);	// enqueue board (that your opponent just played on) for future expansion
							
							// TODO not sure if this will help yet with the piece table heuristic
							if (heuristic instanceof PieceTableHeuristic) {
								heuristicTotal -= oppScores.get(i);
								nodesExplored++;
							}
						}
					}  else {
						frontier.add(localBoard);	// enqueue board (that you just played on) for future expansion
					}
				}
			}
//			long elapsedTime = System.nanoTime() - startTime;
//			System.out.println("Node " + nodesExplored + " time: " + TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS) + "ms");
		}
		
		/*
		 * Time's Up! Do closing calculations
		 * Set the time limit a bit lower than the max alloted time; 
		 * this part of isn't timed since it's no longer searching, but it does grade 100s or 1000s of boards, depending on the initial time limit.
		 */
//		System.out.println(threadName + " Size of frontier after while loop: " + frontier.size());
		if (!frontier.isEmpty()) {
			// Incomplete search: get aggregate heuristic for all nodes left in the frontier
			long aggregateScore;
			aggregateScore = 0L;
			for (Board b : frontier) {
				aggregateScore += heuristic.gradeBoard(ogColor, b);
			}
			return aggregateScore / frontier.size();
		} else if (numWins + numLosses > 0) {
			// Complete search: count number of wins vs losses and average them
			return (numWins - numLosses) * winValue * heuristic.getWeight() / (numWins + numLosses);
		} else {
			/*
			 * This should no longer happen (as often...) after fixing an issue with the search where it didn't enqueue the board you just moved on 
			 * if your opponent has no valid moves, or if the random selection somehow decides not to expand any of your opponent's options
			 */
//			System.out.println("!!!!!! SEARCH GETTING WEIRD RESULTS !!!!!!!");	
			return heuristic.gradeBoard(ogColor, currentBoard);
		}
		// END SEARCH METHOD
	}
	
	////////////////////////////////////
	// Multithreading-Related Methods //
	////////////////////////////////////
	@Override
	public Long call() throws Exception {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
//		System.out.println("Search started in: " + threadName + " (" + Thread.currentThread().getName() + ")");
		long result = search();
//		System.out.println(threadName + " (" + Thread.currentThread().getName() + ")" + " search completed!\n\tNodes explored: " + nodesExplored + 
//				"\n\tAggregate heuristic: " + heuristicTotal + 
//				"\n\tAveraged heuristic: " + (heuristicTotal / nodesExplored));
		if (heuristic instanceof PieceTableHeuristic || heuristic instanceof ParityHeuristic || heuristic instanceof StabilityHeuristic) {
			return heuristicTotal / nodesExplored;
		} else {
			return result;
		}
	}
	
	public void initialize() {
		heuristicTotal = 0L;
		nodesExplored = 0L;
	}
	
	public void setTimeLimit(long nanoseconds) {
		timeLimit = System.nanoTime() + nanoseconds;
	}
}
