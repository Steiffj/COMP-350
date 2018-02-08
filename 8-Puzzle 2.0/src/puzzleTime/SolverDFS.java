package puzzleTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class SolverDFS extends Solver {

	public SolverDFS(Board ogBoard) {
		super(ogBoard);
	}

	@Override
	public void getSolution() {
		originalBoard.setRoot(true);
		searchStateSpace(originalBoard);
	}

	@Override
	public void searchStateSpace(Board currentBoard) {
		Stack<Board> frontier = new Stack<Board>();
		ArrayList<Board> searchPath = new ArrayList<Board>();
		Board temp;
		
		frontier.push(currentBoard);
		while(!frontier.isEmpty()) {
			temp = frontier.pop();
			searchPath.add(temp);
			
			//Base Case
			if(temp.equals(goalState)) {
				populateSolutionPath(temp);
				break;
			}
			
			ArrayList<Board> moves = validMoves(temp);
			for(Board move : moves) {
				if(!frontier.contains(move) && (!searchPath.contains(move))) {
					frontier.push(move);
				}
			}
		}

	}
	
	public void populateSolutionPath(Board node) {
		while (!node.getRoot()) {
			solutionPath.add(node);
			node = node.getParent();
		}
		solutionPath.add(node);
		Collections.reverse(solutionPath);
	}

}
