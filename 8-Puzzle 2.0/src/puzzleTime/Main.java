package puzzleTime;

public class Main {

	public static void main(String[] args) {
		Board bValid = new Board(4);
		System.out.println(bValid.toString());
		Solver df = new SolverDFS(bValid);
		df.getSolution();
		
		printSolution(df);
	}
	
	public static void printSolution(Solver df) {
		if(df.solutionPath.isEmpty()) {
			System.out.println("No solution found.");
		} else {
			System.out.println("Solution: ");
			for (Board b : df.solutionPath) {
				System.out.println(b.toString() + "\n");
			}
		}
	}

}
