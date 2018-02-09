package solvers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import main.Board;

public class SolverBreadthFirst extends Solver {
	
	public SolverBreadthFirst() {
		super();
	}
	
	public SolverBreadthFirst(Board b) {
		super(b);
		bOriginal.setRoot(true);
		solutionPath = search(bOriginal);
	}
	
	@Override
	public void getSolution() {
		bOriginal.setRoot(true);
		solutionPath = search(bOriginal);
	}
	
	@Override
	public ArrayList<Board> search(Board current) {
		Queue<Board> frontier = new LinkedList<Board>();
		ArrayList<Board> explored = new ArrayList<Board>();
		Board b;
		
		frontier.add(current);
		while (!frontier.isEmpty()) {
			b = frontier.remove();		// Dequeue node to expand
			explored.add(b);
			
			// Base case - goal state reached
			if (b.equals(bSolution)) {
				return buildSolutionPath(b);	// build path here because nodes are all local to the search method
			}
			
			ArrayList<Board> moves = b.getNeighbors();	// Expand current node
			Collections.shuffle(moves);				// Prevent search from repeating stupid moves on multiple runs with the same start state
			for (Board move : moves) {
				if (!frontier.contains(move) && (!explored.contains(move))) {
					frontier.add(move);				// Enqueue node from the frontier
				}
			}
		}
		return new ArrayList<Board>();		// No solution found
	}
}
