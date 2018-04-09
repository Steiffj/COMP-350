package players;

import java.util.Scanner;

import components.Board;
import components.Color;
import components.Coordinate;
import components.Player;

public class Human extends Player {
	
	private Scanner sc;
	
	public Human(String name, Color color) {
		super(name, color);
		sc = new Scanner(System.in);
	}
	
	@Override
	public Coordinate makeMove(Board board) {
		int row = -1;
		int col = -1;
		String command;
		String[] parts;
		
		while (row < 0 || col < 0 || row >= board.getWidth() || col >= board.getWidth()) {
			
			// Get input
			command = sc.nextLine();
			parts = command.replaceAll("\\s+|,|(|)", "").toUpperCase().split("");
			
			// Input uses A-H for column name, and 1-8 for row name (Ex: upper-left corner is "A1", and lower-right corner is "H8")
			if (command.matches("[a-zA-Z][1-" + board.getWidth() + "]")) {
				
				String[] colLabels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(0, board.getWidth()).split("");
				for (int c = 0; c < colLabels.length; c++) {
					if (parts[0].equals(colLabels[c])) {
						col = c;
					}
				}
				
				row = Integer.parseInt(parts[1]) - 1;
				
			} else {
				break;
			}
		} 
		
		return new Coordinate(row, col);
	}
}
