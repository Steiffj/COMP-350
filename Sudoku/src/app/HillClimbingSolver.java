package app;

import java.util.Random;

public class HillClimbingSolver extends Solver {
	
	public HillClimbingSolver(Puzzle board) {
		super(board);
		name = "Hill Climbing";
	}
	
	@Override
	public Puzzle getSolution() {
		
		randomFill();
		Tuple[][] cells = board.groupCells();		
		
		int thresholdMax = 9;
		int thresholdCurrent = 0;
		int[] boardIndices;
		int conflicts;
		
		Puzzle boardPreviousCycle = new Puzzle(board);
		int cycleCounter = 0;
		int totalStepCount = 0;
		
		while (conflicts() > 0) {
			
			// Update the reference board to test for cycles every other iteration in the while-loop
			if (cycleCounter % 2 == 0) {
				boardPreviousCycle = new Puzzle(board);
			}
			
			thresholdCurrent = 0;
			
			for (Tuple[] cellGroup : cells) {
				
				conflicts = conflicts();
				//boardIndices = findSwap(cellGroup);
				boardIndices = findSwapRandom(cellGroup);
				board.swap(boardIndices[0], boardIndices[1]);
				
				thresholdCurrent = conflicts() >= conflicts ? thresholdCurrent + 1 : 0;
				
				totalStepCount++;
			}
			
			cycleCounter++;
			
			/*
			 *  Search has gotten stuck in a cycle (reaches the same board state every other iteration of the while-loop)
			 *  This could be remedied by instead forcing a suboptimal random swap, similar to the modified min-conflicts solver
			 */
			if (cycleCounter % 2 == 0 && board.equals(boardPreviousCycle)) {
				System.err.println("Caught in cycle, no solution found");
				board.setReport(new FailReport("caught in cycle, no solution found", totalStepCount));
				board.setSolved(false);
				return board;
			}
			
			/*
			 *  Search has gotten stuck in a local minimum (can no longer make swaps that reduce conflicts in any cell)
			 *  Again, forcing a random swap could keep the search from failing
			 */
			if (thresholdCurrent >= thresholdMax) {
				System.err.println("Caught in local minumum, no solution found");
				board.setReport(new FailReport("caught in local minimum, no solution found", totalStepCount));
				board.setSolved(false);
				return board;
			}
		}
		
		board.setSolved(true);
		return board;
	}
	
	// Returns the the first set of indices it finds that reduces conflicts for the board
	// This approach tends to get stuck in local minima/cycles very quickly - use random version instead
	private int[] findSwap(Tuple[] cellGroup) {
		int[] indices = new int[2];
		
		Puzzle boardCopy = new Puzzle(board);
		int lowestConflicts = 99999;
		int currentConflicts;
		
		for (int i = 0; i < cellGroup.length; i++) {
			for (int j = 0; j < cellGroup.length; j++) {
				
				if (i == j || boardCopy.get(i).isOriginal() || boardCopy.get(j).isOriginal()) {
					continue;
				} else {
					boardCopy = new Puzzle(board);
					boardCopy.swap(cellGroup[i].getIndex(), cellGroup[j].getIndex());
					
					// Update lowest conflicts if the result of the swap reduces total conflicts
					lowestConflicts = (currentConflicts = counter.countTotalConflicts(boardCopy)) < lowestConflicts ? currentConflicts : lowestConflicts;
					
					// Return indices if lowestConflicts has changed
					if (lowestConflicts == currentConflicts) {
						indices[0] = cellGroup[i].getIndex();
						indices[1] = cellGroup[j].getIndex();
						return indices;
					}
				}
			}
		}
		
		return indices;
	}
	
	// Returns a random set of indices that reduces conflicts
	private int[] findSwapRandom(Tuple[] cellGroup) {
		int[] indices = new int[2];
		int conflicts = counter.countTotalConflicts(board);
		Puzzle boardCopy = new Puzzle(board);
		
		Random rand = new Random();
		int i = 0;
		int j = 0;
		int failThreshold = 800;	// increasing this value will allow the algorithm to search longer, but it ultimately doesn't result in finding a solution
		int failCount = 0;
		while (counter.countTotalConflicts(boardCopy) >= conflicts) {
			boardCopy = new Puzzle(board);
			i = rand.nextInt(cellGroup.length);
			j = rand.nextInt(cellGroup.length);
			boardCopy.swap(cellGroup[i].getIndex(), cellGroup[j].getIndex());
			
			if (failCount > failThreshold) {
				indices[0] = 0;
				indices[1] = 0;
				return indices;
			}
			failCount++;
		}
		
		indices[0] = cellGroup[i].getIndex();
		indices[1] = cellGroup[j].getIndex();
		return indices;
	}
}
