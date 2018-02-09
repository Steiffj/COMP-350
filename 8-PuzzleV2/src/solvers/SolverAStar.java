package solvers;

import java.util.ArrayList;
import java.util.PriorityQueue;

import heuristics.Heuristic;
import main.Board;

public class SolverAStar extends Solver {
	protected Heuristic heuristic2;
	
	public SolverAStar(Heuristic h1, Heuristic h2) {
		super();
		heuristic = h1;
		heuristic2 = h2;
	}
	
	public SolverAStar(Board b, Heuristic h1, Heuristic h2) {
		super(b);
		heuristic = h1;
		heuristic2 = h2;
	}
	
	@Override
	public void getSolution() {
		bOriginal.setRoot(true);	// Ensure initial state is marked as the root
		solutionPath = search(bOriginal);
	}
	
	@Override
	public ArrayList<Board> search(Board current) {
		BoardComparator bCompare = new BoardComparator();
		PriorityQueue<Board> frontier = new PriorityQueue<Board>(12, bCompare);
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
			for (Board move : moves) {
				setCompositeHeuristic(move);	// Set heuristics for each node in the frontier
			}
			
			for (Board move : moves) {
				if (!frontier.contains(move) && (!explored.contains(move))) {
					frontier.add(move);
				}
			}
		}
		return new ArrayList<Board>();		// No solution found
	}
	
	public int setCompositeHeuristic(Board b) {
		int cost = heuristic.setValue(b) + heuristic2.setValue(b);
		b.setHeuristic(cost);
		return b.getHeuristic();
	}
}
