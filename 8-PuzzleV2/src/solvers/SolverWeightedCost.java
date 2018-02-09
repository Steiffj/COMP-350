package solvers;

import java.util.ArrayList;
import java.util.PriorityQueue;

import heuristics.Heuristic;
import main.Board;

public class SolverWeightedCost extends Solver {
	
	public SolverWeightedCost(Heuristic h) {
		super();
		heuristic = h;
	}
	
	public SolverWeightedCost(Board b, Heuristic h) {
		super(b);
		heuristic = h;
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
		PriorityQueue<Board> frontier = new PriorityQueue<Board>();
		ArrayList<Board> explored = new ArrayList<Board>();
		Board b;
		
		frontier.add(current);
		while (!frontier.isEmpty()) {
			b = frontier.remove();		// Dequeue node to expand
			explored.add(b);
			
			// Base case - goal state reached
			if (b.equals(bSolution)) {
				return buildSolutionPath(b);	// Build path here because nodes (board objects) are all local to the search method
			}
			
			ArrayList<Board> moves = b.getNeighbors();	// Expand current node
			heuristic.setReference(b);
			for (Board move : moves) {
				heuristic.setValue(move);		// Set heuristics for each node in the frontier
			}
			
			for (Board move : moves) {
				if (!frontier.contains(move) && (!explored.contains(move))) {
					frontier.add(move);			// Enqueue node from frontier
				}
			}
		}
		return new ArrayList<Board>();		// No solution found
	}
}
