package players;

import components.Color;
import components.Player;
import heuristics.Heuristic;

public abstract class OthelloAI extends Player {
	
	/***
	 * The weights of all the {@link Heuristic}s in the array should add up to 1. 
	 */
	protected Heuristic[] heuristics;
	
	protected Player opponent;
	
	public OthelloAI(String name, Color color, Heuristic... heuristics) {
		super(name, color);
		this.heuristics = heuristics;
	}
}
