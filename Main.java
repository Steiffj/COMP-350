package puzzleTime;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import heuristicTime.*;
import solverTime.*;

public class Main {

	public static void main(String[] args) {
//		int[] test = {1,2,5,3,4,0,6,7,8};
//		Board b = new Board(3, test);
		Board b = new Board(3);
		while(!b.isValid()) {
			System.out.println("Unsolvable Board:\n" + b.toString() + "Creating new board...\n");
			b = new Board(3);
		}
		System.out.println("Solvable Board:\n" + b.toString() + "Searching...\n");
//		Solver df = new SolverDFS(b);
//		df.getSolution();		
//		printSolution(df);
		
		Solver bf = new SolverBFS(b);
		bf.getSolution();
		printSolution(bf);
		
//		Heuristic h = new HeuristicWC();
//		Solver wc = new SolverWC(b, h);
//		wc.getSolution();
//		printSolution(wc);
		
//		Heuristic hMM = new HeuristicMM();
//		Heuristic hMT = new HeuristicMT();
//		Solver AMMMT = new SolverAStar(b, hMM, hMT);
//		AMMMT.getSolution();
//		printSolution(AMMMT);
		
//		Heuristic hMD = new HeuristicMD();
//		Solver AMMMD = new SolverAStar(b, hMM, hMD);
//		AMMMD.getSolution();
//		printSolution(AMMMD);
	}
	
	public static void printSolution(Solver search) {
		//This is a mess, all it does it print the results to a text file cuz its crazy long
		try {
			FileWriter fw = new FileWriter("E:\\350 PPT\\8-PuzzleBFS.txt");
			PrintWriter pw = new PrintWriter(fw);
			int steps = 1;
			if(search.solutionPath.isEmpty()) {
				pw.println("No solution found.");
			} else {
				pw.println("Solution: ");
				for (Board b : search.solutionPath) {
					pw.println("Step " + steps++ + " of " + search.solutionPath.size());
					//System.out.println("Heuristic: " + b.getHeuristic());
					pw.println(b.toString() + "\n");
				}
			}
			pw.close();
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		int steps = 1;
		if(search.solutionPath.isEmpty()) {
			System.out.println("No solution found.");
		} else {
			System.out.println("Solution: ");
			for (Board b : search.solutionPath) {
				System.out.println("Step " + steps++ + " of " + search.solutionPath.size());
				System.out.println("Heuristic: " + b.getHeuristic());
				System.out.println(b.toString() + "\n");
			}
		}
	}

}
