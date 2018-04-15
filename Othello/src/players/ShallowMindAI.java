package players;

import java.util.ArrayList;
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

public class ShallowMindAI extends OthelloAI {
	
	// Othello stuff
	private final Board startBoard;
	
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
		
		for (Heuristic h : heuristics) {
			searchThreads.add(new HeuristicSearchThread(h));
		}
		startBoard = new OthelloBoard();
	}
	
	@Override
	public Coordinate makeMove(Board board) {
		List<Coordinate> moves = board.getValidMoves(color);
		if (moves.size() == 0) {
			System.out.println("No moves");
			return null;
			
		} else if (board.equals(startBoard)) {
			System.out.println("Random choice");
			Random rand = new Random();
			return moves.get(rand.nextInt(moves.size()));
			
		} else {
			System.out.println("\nIt should be searching");
			Coordinate bestMove = moves.get(0);
			long timeLimitPerMove = plyTimeLimit / moves.size();
			Board copy;
			double highestScore = 0.0d;
			double currentScore = 0.0d;
			for (Coordinate move : moves) {
				copy = board.clone();
				copy.set(color, move);
				currentScore = getAggregateHeuristic(copy, timeLimitPerMove);
				if (currentScore > highestScore) {
					highestScore = currentScore;
					bestMove = move;
				}
			}
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
		List<Future<Double>> heuristicValues;
		
		// Set time limit for each thread (limited by the number of potential moves for the current ply)
		for (HeuristicSearchThread hsThread : searchThreads) {
			hsThread.setCurrentBoard(board);
			hsThread.setColor(color);
			hsThread.setTimeLimit(timeLimit);
		}
		
		try {
			heuristicValues = threadService.invokeAll(searchThreads);
			for (Future<Double> result : heuristicValues) {
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
