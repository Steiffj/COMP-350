package heuristics;

import components.Board;
import components.Color;

public class PositionHeuristic implements Heuristic {
	
	private int width;
	private int[][] weightTable;
	private int minWeight;
	private int maxWeight;
	
	public PositionHeuristic(int width, int minWeight, int maxWeight) {
		this.width = width;
		weightTable = new int[width][width];
		this.maxWeight = minWeight;
		this.maxWeight = maxWeight;
		
		for (int row = 0; row < width; row++) {
			for (int col = 0; col <width; col++) {
				
			}
		}
	}
	
	@Override
	public int gradeBoard(Color c, Board board) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
