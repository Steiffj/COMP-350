package players;

import java.util.List;
import java.util.Random;

import components.Board;
import components.Color;
import components.Coordinate;
import components.Player;

public class StupidAI extends Player {
	
	public StupidAI(String name, Color color) {
		super(name, color);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Coordinate makeMove(Board board) {
		Random rand = new Random();
		List<Coordinate> moves = board.getValidMoves(color);
		
		try {
			return moves.get(rand.nextInt(moves.size()));
		} catch(IndexOutOfBoundsException e) {
			return new Coordinate(-1, -1);
		}	
	}
}
