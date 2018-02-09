package solvers;

import java.util.Comparator;

import main.Board;

public class BoardComparator implements Comparator<Board> {
	
	public BoardComparator() {
		// Empty constructor
	}
	
	@Override
	public int compare(Board b1, Board b2) {
		// Comparator for A* priority queue 
		
		if (b1.getHeuristic() < b2.getHeuristic()) {
			return -1;
		} else if (b1.getHeuristic() > b2.getHeuristic()) {
			return 1;
		} else {
			return 0;
		}
	}
}
