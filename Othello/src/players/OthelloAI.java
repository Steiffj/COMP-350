package players;

import components.Board;
import components.Color;
import components.Coordinate;
import components.Player;
import heuristics.Heuristic;

public abstract class OthelloAI extends Player {
	
	private Heuristic[] heuristics;
	
	public OthelloAI(String name, Color color, Heuristic...heuristics ) {
		super(name, color);
		this.heuristics = heuristics;
	}
	
	@Override
	public abstract Coordinate makeMove(Board board);
}
