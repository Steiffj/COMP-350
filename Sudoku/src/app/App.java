package app;

public class App {
	
	public static final String EASY_PUZZLE   = "..3.2.6..9..3.5..1..18.64....81.29..7.......8..67.82....26.95..8..2.3..9..5.1.3..";
	public static final String MEDIUM_PUZZLE = "4173698.5.3..........7......2.....6.....8.4......1.......6.3.7.5..2.....1.4......";
	public static final String HARD_PUZZLE   = "1....7.9..3..2...8..96..5....53..9...1..8...26....4...3......1..4......7..7...3..";
	
	// (Previously used for testing - ignore)
	public static final String UNRELATED_SOLVED_PUZZLE = "827154396965327148341689752593468271472513689618972435786235914154796823239841567";
	
	public static final boolean AT_SCHOOL = false;
	
	public static void main(String[] args) {
		
		Puzzle[] puzzles = {new Puzzle(EASY_PUZZLE, 9, "Easy"), new Puzzle(MEDIUM_PUZZLE, 9, "Medium"), new Puzzle(HARD_PUZZLE, 9, "Hard")};
		
		for (Puzzle p : puzzles) {
			
			/*
			 * Multithreaded implementation 
			 * (Unfortunately doesn't improve on the algorithm itself; it just allows all the tests run in parallel)
			 */
			Tester hcTester = new Tester(new HillClimbingSolver(new Puzzle(p)), AT_SCHOOL);
			Thread hcThread = new Thread(hcTester);
			hcThread.start();
			
			Tester iiTester = new Tester(new IterativeImprovementSolver(new Puzzle(p)), AT_SCHOOL);
			Thread iiThread = new Thread(iiTester);
			iiThread.start();
			
			/*
			 * Single-threaded implementation
			 * (Possibly slightly faster when comparing each test's individual runtime to its multithreaded counterpart,
			 *  depending on how the JVM feels like managing its threads, but overall faster to run all tests)
			 */
//			Tester hcTester = new Tester(new HillClimbingSolver(new Puzzle(p)), AT_SCHOOL);
//			hcTester.runTest();
//			
//			Tester iiTester = new Tester(new IterativeImprovementSolver(new Puzzle(p)), AT_SCHOOL);
//			iiTester.runTest();
			
		}
	}
	
	// (Previously used for testing - ignore)
	public static void print2DArray(Tuple[][] arr) {
		for (Tuple[] row : arr) {
			System.out.print("[ ");
			
			for (Tuple i : row) {
				System.out.print(i.getValue() + " ");
			}
			
			System.out.print("]\n");
		}
	}
}
