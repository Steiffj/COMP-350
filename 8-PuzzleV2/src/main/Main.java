package main;

import heuristics.Heuristic;
import heuristics.HeuristicManhattanDistance;
import heuristics.HeuristicMisplacedTiles;
import heuristics.HeuristicMovesMade;
import heuristics.HeuristicWeightedCost;
import solvers.Solver;
import solvers.SolverAStar;
import solvers.SolverBreadthFirst;
import solvers.SolverDepthFirst;
import solvers.SolverWeightedCost;

public class Main {
	
	public static void main(String[] args) {
		
		/*
		 * Full testing is in Testing class
		 */
		
		Board startBoard = new Board(3, true);
		while (!startBoard.isValid()) {
			System.out.println("Unsolvable board" + startBoard.toString() + "\nRetrying...\n");
			startBoard = new Board(3, true);
		}
		
		System.out.println("Solving for board:" + startBoard.toString());
		
		// Depth-First Search //
		Solver solverDFS = new SolverDepthFirst(startBoard);
		solverDFS.getSolution();
		System.out.println(solverDFS.solutionToString());
		System.out.println("Duplicate nodes in search path: " + solverDFS.getDuplicateCount());
		
		// Breadth-First Search //
		Solver solverBFS = new SolverBreadthFirst(startBoard);
		solverBFS.getSolution();
		System.out.println(solverBFS.solutionToString());
		System.out.println("Duplicate nodes in search path: " + solverBFS.getDuplicateCount());
		
		// Weighted-Cost Search //
		Heuristic hWC = new HeuristicWeightedCost();
		Solver solverWC = new SolverWeightedCost(startBoard, hWC);
		solverWC.getSolution();
		System.out.println(solverWC.solutionToString());
		System.out.println("Duplicate nodes in search path: " + solverWC.getDuplicateCount());
		
		// A* Search with Moves Made and Misplaced Tiles //	
		Heuristic hMM = new HeuristicMovesMade();
		Heuristic hMT = new HeuristicMisplacedTiles();
		Solver solverAStarMT = new SolverAStar(startBoard, hMM, hMT);
		solverAStarMT.getSolution();
		System.out.println(solverAStarMT.solutionToString());
		System.out.println("Duplicate nodes in search path: " + solverAStarMT.getDuplicateCount());
		
		// A* Search with Moves Made and Manhattan Distance //
		Heuristic hMD = new HeuristicManhattanDistance();
		Solver solverAStarMD = new SolverAStar(startBoard, hMM, hMD);
		solverAStarMD.getSolution();
		System.out.println(solverAStarMD.solutionToString());
		System.out.println("Duplicate nodes in search path: " + solverAStarMD.getDuplicateCount());
	}
}
