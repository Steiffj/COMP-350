package solvers;

import java.util.ArrayList;
import java.util.Collections;

import heuristics.Heuristic;
import main.Board;

public abstract class Solver {
	protected int size;
	protected Heuristic heuristic;
	protected Board bOriginal;
	protected Board bSolution;
	protected ArrayList<Board> solutionPath;
	protected int duplicateCount;
	
	public Solver() {
		duplicateCount = 0;
	}
	
	public Solver(Board b) {
		bOriginal = b;
		bSolution = buildGoalState();
		duplicateCount = 0;
	}
	
	protected Board buildGoalState() {
		// Creates an appropriate goal state for a Solver object based on the size of the original state
		
		int[] solutionArray = new int[bOriginal.getSize() * bOriginal.getSize()];
		
		for (int i = 1; i < solutionArray.length; i++) {
			solutionArray[i - 1] = i;
		}
		
		solutionArray[solutionArray.length - 1] = 0;
		return new Board(bOriginal.getSize(), solutionArray, false);
	}
	
	public String solutionToString() {
		// Return a string containing all the states in the path from the start to the goal
		
		String pathStr = "";
		if (solutionPath.isEmpty()) {
			return "No Solution Found";
		} else {
			int count = 1;
			int length = solutionPath.size();
			
			for (Board b : solutionPath) {
				pathStr += "\n\nState " + count++ + " of " + length + b.toString();
			}
			return pathStr;
		}
	}
	
	public ArrayList<Board> buildSolutionPath(Board node) {
		// Builds a solution path from the original node to the goal state, using the boards' parent-child relationships
		// (The parameter, node, should be the goal state found within the search method)
		
		ArrayList<Board> path = new ArrayList<Board>();
		while (!node.isRoot()) {
			if (path.contains(node)) {
				duplicateCount++;
			}
			path.add(node);
			node = node.getParent();
		}
		path.add(node);
		Collections.reverse(path);
		return path;
	}
	
	public void setStartBoard(Board b) {
		// Sets the start state to the provided board, and updates related methods
		// Typically used after resetting a solver object
		
		bOriginal = b;
		size = bOriginal.getSize();
		bSolution = buildGoalState();
	}
	
	public void reset() {
		// Reset the current solver object
		
		solutionPath.clear();
		bOriginal = null;
		bSolution = null;
		duplicateCount = 0;
	}
	
	public int getSolutionPathLength() {
		return solutionPath.size();
	}
	
	public int getDuplicateCount() {
		return duplicateCount;
	}
	
	public void setDuplicateCount(int duplicateCount) {
		this.duplicateCount = duplicateCount;
	}
	
	public abstract void getSolution();
	
	public abstract ArrayList<Board> search(Board b);
}
