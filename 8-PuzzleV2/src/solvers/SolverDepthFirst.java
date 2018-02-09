package solvers;

import java.util.ArrayList;
import java.util.Stack;

import main.Board;

public class SolverDepthFirst extends Solver {
	
	public SolverDepthFirst() {
		super();
	}
	
	public SolverDepthFirst(Board b) {
		super(b);
	}
	
	@Override
	public void getSolution() {
		bOriginal.setRoot(true);
		solutionPath = search(bOriginal);
	}
	
	@Override
	public ArrayList<Board> search(Board current) {
		Stack<Board> frontier = new Stack<Board>();
		ArrayList<Board> explored = new ArrayList<Board>();
		Board b;
		
		frontier.push(current);
		while (!frontier.isEmpty()) {
			b = frontier.pop();		// Pop node to expand
			explored.add(b);
			
			// Base case - goal state reached
			if (b.equals(bSolution)) {
				return buildSolutionPath(b);	// build path here because nodes are all local to the search method
			}
			
			ArrayList<Board> moves = b.getNeighbors();	// Expand current node
			
			// TODO Comment the shuffle when testing the 15 puzzle, or otherwise-easy starting states will take forever to run
			//Collections.shuffle(moves);	// Prevent search from repeating stupid moves on multiple runs with the same start state
			for (Board move : moves) {
				if (!frontier.contains(move) && (!explored.contains(move))) {
					frontier.push(move);
				}
			}
		}
		return new ArrayList<Board>();		// No solution found
	}
}
