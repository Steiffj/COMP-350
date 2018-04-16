package players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import components.Board;
import components.Color;
import components.Coordinate;
import components.OthelloBoard;
import heuristics.Heuristic;
import heuristics.HeuristicSearchThread;
import heuristics.MaxPiecesHeuristic;
import heuristics.MinMobilityHeuristic;
import heuristics.PieceTableHeuristic;

public class ShallowMindAI extends OthelloAI {
	
	// Othello stuff
	private final Board startBoard;
	private final Color oppColor = color.flip();
	
	// Othello stuff for heuristic threads
	private List<Heuristic> oppHeuristics;
	private Board previousBoard;
	private int oppCurveOffset = 2;	// used for updating opponent's heuristic weights
	
	// Multithreading stuff
	private final List<HeuristicSearchThread> searchThreads;
	private final ExecutorService threadService;
	private final long plyTimeLimit = TimeUnit.NANOSECONDS.convert(5, TimeUnit.SECONDS);
	
	/////////////////
	// Constructor //
	/////////////////
	public ShallowMindAI(String name, Color color, Heuristic... heuristics) {
		super(name, color, heuristics);
		
		threadService = Executors.newFixedThreadPool(heuristics.length);
		searchThreads = new ArrayList<HeuristicSearchThread>();
		
		oppHeuristics = Arrays.asList(new MaxPiecesHeuristic(50), new MinMobilityHeuristic(50), new PieceTableHeuristic(50));
		for (Heuristic h : heuristics) {
			/*
			 * All threads point to the same list of opponent's heuristics, 
			 * so it can just be updated once at the beginning of each ply, before the threads go off and do their work. 
			 */
			searchThreads.add(new HeuristicSearchThread(h, oppHeuristics));
		}
		startBoard = new OthelloBoard();
	}
	
	private void learnOppHeuristics(Board currentBoard) {
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
					} else if (percentError < 15) {
						h.updateWeight(2 * oppCurveOffset, true);
					} else if (percentError < 25) {
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
	
	private void curveHeuristics(Board currentBoard) {
		int boardSize = currentBoard.getSize();
		int piecesOnBoard = boardSize - currentBoard.countPieces(Color.EMPTY);
		long newWeight;
		int lowVar = 3;
		int medVar = 5;
		int highVar = 9;
		
		// TODO not the greatest solution... very bespoke
		for (Heuristic h : heuristics) {
			newWeight = h.getWeight();
			if (h instanceof MaxPiecesHeuristic) {
				if (piecesOnBoard < 32) {
					// Early game - low priority, low variance
					newWeight += addRandomVariance(lowVar);
				} else if (piecesOnBoard < 48) {
					// Mid game - low priority, medium variance
					newWeight += addRandomVariance(medVar);
				} else if (piecesOnBoard < 58) {
					// Late game - medium priority, high variance
					newWeight += newWeight * 0.08 * (boardSize - piecesOnBoard);
					newWeight += addRandomVariance(highVar);
				} else {
					// End game - high priority, low variance
					newWeight += newWeight * 0.17 * (boardSize - piecesOnBoard);
					newWeight += addRandomVariance(lowVar);
				}
				
			} else if (h instanceof MinMobilityHeuristic) {
				if (piecesOnBoard < 40) {
					// Early game - high priority, low variance
					newWeight += addRandomVariance(lowVar);
				} else if (piecesOnBoard < 48) {
					// Mid game - high priority, medium variance
					newWeight += addRandomVariance(medVar);
				} else if (piecesOnBoard < 56) {
					// Late game - medium priority, high variance
					newWeight -= newWeight * 0.1 * (boardSize - piecesOnBoard);
					newWeight += addRandomVariance(highVar);
				} else {
					// End game - low priority, high variance
					newWeight -= newWeight * 0.15 * (boardSize - piecesOnBoard);
					newWeight += addRandomVariance(lowVar);
				}
				
			} else if (h instanceof PieceTableHeuristic) {
				if (piecesOnBoard < 42) {
					// Early game - high priority, low variance
					newWeight += addRandomVariance(lowVar);
				} else if (piecesOnBoard < 52) {
					// Mid game - high priority, medium variance
					newWeight += addRandomVariance(medVar);
				} else if (piecesOnBoard < 60) {
					// Late game - medium priority, medium variance
					newWeight -= newWeight * 0.04 * (boardSize - piecesOnBoard);
					newWeight += addRandomVariance(medVar);
				} else {
					// End game - low priority, high variance
					newWeight -= newWeight * 0.13 * (boardSize - piecesOnBoard);
					newWeight += addRandomVariance(highVar);
				}
			}
			h.updateWeight(newWeight);
		}
	}
	
	private int addRandomVariance(int varianceAmount) {
		Random rand = new Random();
		int variance = 0;
		if (rand.nextBoolean() && rand.nextBoolean()) {
			variance += rand.nextBoolean() ? varianceAmount : -varianceAmount;
		}
		return variance;
	}
	
	@Override
	public Coordinate makeMove(Board board) {
		List<Coordinate> moves = board.getValidMoves(color);
		if (moves.size() == 0) {
			System.out.println("No moves");
			return null;
			
		} else if (board.equals(startBoard)) {
			System.out.println("Random choice");
			previousBoard = board.clone();
			Random rand = new Random();
			return moves.get(rand.nextInt(moves.size()));
			
		} else {
			System.out.println("\n\n*** Search started in makeMove ***\n\n");
			learnOppHeuristics(board);	// update opponent's estimated heuristics for all threads based on opponent's previous ply
			curveHeuristics(board);		// weight Shallow Mind's heuristics based on the number of pieces on the board
			Coordinate bestMove = moves.get(0);
			long timeLimitPerMove = plyTimeLimit / moves.size();
			Board copy;
			double highestScore = 0.0d;
			double currentScore = 0.0d;
			for (Coordinate move : moves) {
				System.out.println("\n==== Move Search ====");
				copy = board.clone();
				copy.set(color, move);
				currentScore = getAggregateHeuristic(copy, timeLimitPerMove);
				if (currentScore > highestScore) {
					highestScore = currentScore;
					bestMove = move;
				}
			}
			previousBoard = board.clone();
			return bestMove;
		}
	}
	
	/**
	 * 
	 * Grades the specified board, based on a single heuristic. 
	 * This spawns a thread for each the AI uses, so grading the board based on multiple heuristics can run in parallel.
	 * 
	 * @param board - the board to grade
	 * @param timeLimit - how long a thread can take to search down the tree, based on a single heuristic
	 * @return the value of the specified board, based on itself and on potential future board states
	 */
	private double getAggregateHeuristic(Board board, long timeLimit) {
		//Board localCopy = board.clone();
		double aggregateHeuristic = 0.0d;
		double singleHeuristic = 0.0d;
		List<Future<Long>> heuristicValues;
		
		// Set time limit for each thread (limited by the number of potential moves for the current ply)
		for (HeuristicSearchThread hsThread : searchThreads) {
			hsThread.setCurrentBoard(board);
			hsThread.setColor(color);
			hsThread.setTimeLimit(timeLimit);
		}
		
		try {
			heuristicValues = threadService.invokeAll(searchThreads);
			for (Future<Long> result : heuristicValues) {
				try {
					singleHeuristic = result.get();
				} catch (ExecutionException e) {
					System.err.println("\n!!! There was a problem within the search !!!\n");
					e.printStackTrace();
				}  catch (CancellationException e) {
					System.err.println("The search was cancelled.");
				}
				aggregateHeuristic += singleHeuristic;
			}
			
		} catch (InterruptedException e) {
			System.err.println("\nShallow Mind's thead manager was interrupted\n");
		}
		
		return aggregateHeuristic;
	}
}
