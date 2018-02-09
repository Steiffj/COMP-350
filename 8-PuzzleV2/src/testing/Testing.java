package testing;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import heuristics.HeuristicManhattanDistance;
import heuristics.HeuristicMisplacedTiles;
import heuristics.HeuristicMovesMade;
import heuristics.HeuristicWeightedCost;
import main.Board;
import solvers.Solver;
import solvers.SolverAStar;
import solvers.SolverBreadthFirst;
import solvers.SolverDepthFirst;
import solvers.SolverWeightedCost;

public class Testing {
	
	// Folders for test output //
	public static final String FILE_PATH_HOME = "L:\\Repositories\\eclipse_workspace\\8-PuzzleV2\\src\\output\\";
	public static final String FILE_PATH_SCHOOL = "E:\\Repositories\\eclipse_workspace\\8-PuzzleV2\\src\\output\\";
	
	// File name shortcuts //
	public static final String FILE_DFS = "DFS-Output_";
	public static final String FILE_BFS = "BFS-Output_";
	public static final String FILE_WC = "Weighted-Cost-Output_";
	public static final String FILE_ASTAR_MT = "A-Star-Misplaced-Tiles-Output_";
	public static final String FILE_ASTAR_MD = "A-Star-Manhattan-Distance-Output_";
	
	public static final int NUM_TESTS = 3;	// Number of tests to run for each search method
	
	public static Board startBoard;
	public static FileWriter fw;
	public static PrintWriter pw;
	
	public static void main(String[] args) {
		
		// Run all searches for the 8 puzzle
		int n = 3;
		runBFS(n);
		runDFS(n);
		runWCS(n);
		runAStarMD(n);
		runAStarMT(n);
		
		// Run all searches for seeded 15 puzzles
		runSeededAStarMD(4);	// Both A* implementations run much faster than the other 3 options
		runSeededAStarMT(4);	// since they're make optimal moves for the seeded boards
		runSeededBFS(4);
		runSeededDFS(4);
		runSeededWCS(4);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Run a given search implementation the specified number of times with random starting states //
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void runBFS(int size) {
		runSearch(size, new SolverBreadthFirst(), FILE_BFS);
	}
	
	public static void runDFS(int size) {
		runSearch(size, new SolverDepthFirst(), FILE_DFS);
	}
	
	public static void runWCS(int size) {
		runSearch(size, new SolverWeightedCost(new HeuristicWeightedCost()), FILE_WC);
	}
	
	public static void runAStarMT(int size) {
		runSearch(size, new SolverAStar(new HeuristicMovesMade(), new HeuristicMisplacedTiles()), FILE_ASTAR_MT);
	}
	
	public static void runAStarMD(int size) {
		runSearch(size, new SolverAStar(new HeuristicMovesMade(), new HeuristicManhattanDistance()), FILE_ASTAR_MD);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Run a given search implementation the specified number of times with predetermined starting states //
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// *** Only use seeded run methods to test 15 puzzles *** //
	
	public static void runSeededBFS(int size) {
		runSeededSearch(size, new SolverBreadthFirst(), FILE_BFS);
	}
	
	public static void runSeededDFS(int size) {
		runSeededSearch(size, new SolverDepthFirst(), FILE_DFS);
	}
	
	public static void runSeededWCS(int size) {
		runSeededSearch(size, new SolverWeightedCost(new HeuristicWeightedCost()), FILE_WC);
	}
	
	public static void runSeededAStarMT(int size) {
		runSeededSearch(size, new SolverAStar(new HeuristicMovesMade(), new HeuristicMisplacedTiles()), FILE_ASTAR_MT);
	}
	
	public static void runSeededAStarMD(int size) {
		runSeededSearch(size, new SolverAStar(new HeuristicMovesMade(), new HeuristicManhattanDistance()),
				FILE_ASTAR_MD);
	}
	
	// Run Search Base Implementation //
	public static void runSearch(int size, Solver solver, String fileName) {
		for (int c = 0; c < NUM_TESTS; c++) {
			try {
				fw = new FileWriter(FILE_PATH_HOME + (size * size - 1) + "-Puzzle_" + fileName + (c + 1) + ".txt");
				pw = new PrintWriter(fw);
				
				pw.println(generateValidBoard(size));
				solver.setStartBoard(startBoard);
				
				long startTime = System.nanoTime();
				solver.getSolution();
				long duration = System.nanoTime() - startTime;
				
				SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Date now = new Date();
				String dateTime = sdfDate.format(now);
				
				pw.println("\n////////////////////\n// Search Results //\n////////////////////\n");
				pw.println(dateTime);
				pw.println("Search duration: " + TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS) + " seconds");
				pw.println("Total number of nodes in search path: " + solver.getSolutionPathLength());
				pw.print("Duplicate nodes in search path: " + solver.getDuplicateCount());
				pw.println(solver.solutionToString());
				
				pw.close();
				fw.close();
				
				solver.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Run Search Seeded Implementation //
	public static void runSeededSearch(int size, Solver solver, String fileName) {
		ArrayList<Board> easyBoards = getSeeded15Boards();
		for (int c = 0; c < NUM_TESTS; c++) {
			try {
				fw = new FileWriter(FILE_PATH_HOME + (size * size - 1) + "-Puzzle_" + fileName + (c + 1) + ".txt");
				pw = new PrintWriter(fw);
				
				startBoard = easyBoards.get(c);
				pw.println("Solving for board:" + startBoard.toString());
				solver.setStartBoard(startBoard);
				
				long startTime = System.nanoTime();
				solver.getSolution();
				long duration = System.nanoTime() - startTime;
				
				SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Date now = new Date();
				String dateTime = sdfDate.format(now);
				
				pw.println("\n////////////////////\n// Search Results //\n////////////////////\n");
				pw.println(dateTime);
				pw.println("Search duration: " + TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS) + " seconds");
				pw.println("Total number of nodes in search path: " + solver.getSolutionPathLength());
				pw.print("Duplicate nodes in search path: " + solver.getDuplicateCount());
				pw.println(solver.solutionToString());
				
				pw.close();
				fw.close();
				
				solver.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Initialize startBoard field to a valid, random board a return a string logging actions taken //
	public static String generateValidBoard(int size) {
		String logStr = "";
		
		startBoard = new Board(size, true);
		while (!startBoard.isValid()) {
			logStr += "Unsolvable board" + startBoard.toString() + "\nRetrying...\n\n";
			startBoard = new Board(size, true);
		}
		
		logStr += "Solving for board:" + startBoard.toString();
		
		return logStr;
	}
	
	// Return list of seeded 4x4 boards
	public static ArrayList<Board> getSeeded15Boards() {
		ArrayList<Board> easy15Puzzles = new ArrayList<Board>();
		
		// Some easy boards to solve so the 15 puzzle doesn't take an eternity and a half
		int[] easy1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 0, 15 };
		int[] easy2 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 0, 14, 15 };
		int[] easy3 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 0, 13, 14, 15 };
		int[][] easyArrays = { easy1, easy2, easy3 };
		
		Board b;
		for (int[] array : easyArrays) {
			b = new Board(4, array, true);
			easy15Puzzles.add(b);
		}
		return easy15Puzzles;
	}
}
