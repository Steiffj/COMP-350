package app;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Tester implements Runnable {
	
	private Solver s;
	private boolean atSchool;
	private FileWriter fw;
	private PrintWriter pw;
	private final SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	// Folders for test output
	public final String FILE_PATH_HOME = "L:\\Repositories\\eclipse_workspace\\Sudoku\\src\\output\\";
	public final String FILE_PATH_SCHOOL = "E:\\Repositories\\eclipse_workspace\\Sudoku\\src\\output\\";
	
	public Tester(Solver s, boolean atSchool) {
		this.s = s;
		this.atSchool = atSchool;
	}
	
	public void runTest() {
		try {
			SimpleDateFormat sdfFileDate = new SimpleDateFormat("MM_dd_yyyy_HH_mm");
			Date fileDate = new Date();
			String fileStr = sdfFileDate.format(fileDate);
			
			if (atSchool) {
				fw = new FileWriter(FILE_PATH_SCHOOL + s.getName() + " - " + s.getBoard().getName() + " (" + fileStr + ")" + ".txt");
			} else {
				fw = new FileWriter(FILE_PATH_HOME + s.getName() + " - " + s.getBoard().getName() + " (" + fileStr + ")" + ".txt");
			}
			
			pw = new PrintWriter(fw);
			
			String blankBoard = s.getBoard().toString();
			pw.println("Solving " + s.getBoard().getName() + " using " + s.getName() + "\n");
			
			// Track time it takes to solve the puzzle (or to fail)
			Date start = new Date();
			String dateTimeStart = sdfDate.format(start);
			long startTime = System.nanoTime();
			
			s.getSolution();	// attempt to solve puzzle
			
			long duration = System.nanoTime() - startTime;
			Date stop = new Date();
			String dateTimeFinish = sdfDate.format(stop);
			
			// Write results to file
			pw.println("Search ran from " + dateTimeStart + " to " + dateTimeFinish);
			pw.println("Duration: " + TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS) + " seconds\n");
			pw.println("Starting Puzzle\n" + blankBoard);
			
			if (s.getBoard().isSolved()) {
				pw.println("Completed Puzzle\n" + s.getBoard().toString());
			} else {
				pw.println("Search failed: " + s.getBoard().getReport().getMessage());
				pw.println("Puzzle's state after " + s.getBoard().getReport().getSteps() + " swaps");
				pw.println(s.getBoard().toString());
			}
			
			pw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 1);
		System.out.println("Thread running: " + s.getBoard().getName() + " - " + s.getName());
		runTest();
	}
}
