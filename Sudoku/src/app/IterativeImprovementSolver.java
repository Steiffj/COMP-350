package app;

import java.util.Random;

public class IterativeImprovementSolver extends Solver {
	
	public IterativeImprovementSolver(Puzzle board) {
		super(board);
		name = "Modified Iterative Improvement";
	}
	
	@Override
	public Puzzle getSolution() {
		randomFill();
		
		Random rand = new Random();
		boolean randomSpike = false;
		
		int thresholdMax = 9;	// try changing this to see if it makes a noticeable difference
		int thresholdCurrent = 0;
		
		int previousConflictCount = 0;
		int currentConflictCount = 0;
		
		int index;
		Puzzle boardCopy;
		// TODO change while loop to use !board.isSolution() for better efficiency 
		// counter.countTotalConflicts(board) > 0
		while (conflicts() > 0) {
			
			//System.out.println(board.toString());
			
			index = rand.nextInt(board.size());
			
			if (counter.countConflicts(board, index) > 0 && !board.get(index).isOriginal()) {
				// count the total number of conflicts on the board
				currentConflictCount = conflicts();
				//currentConflictCount = counter.countTotalConflicts(board);
				
				if(previousConflictCount == currentConflictCount) {
					thresholdCurrent++;
				} else {
					thresholdCurrent = 0;
				}
				
				previousConflictCount = currentConflictCount;
				
				if (thresholdCurrent > thresholdMax) {
					randomSpike = true;
				}
				
				if (randomSpike) {
					board.set(rand.nextInt(board.width()) + 1, index);
					randomSpike = false;
					thresholdCurrent = 0;
				} else {
					
					int localPreviousConflictCount = 99999;
					int localCurrentConflictCount;
					int newValue = 0;
					
					for (int i = 1; i <= board.width(); i++) {
						
						boardCopy = new Puzzle(board);
						boardCopy.set(i, index);
						
						if ((localCurrentConflictCount = counter.countConflicts(boardCopy, index)) < localPreviousConflictCount) {
							localPreviousConflictCount =  localCurrentConflictCount;
							newValue = i;
						}
					}
					
					board.set(newValue, index);
				}
			}			
		}
		
		board.setSolved(true);
		return board;
		
	}
}
