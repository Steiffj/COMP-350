package heuristics;

import components.Board;
import components.Color;

public abstract class Heuristic implements Comparable<Heuristic> {
	protected String name;
	protected long weight;
	protected long equalizer = 100;
	
	public Heuristic() {
		weight = 1;
	}
	
	public Heuristic(long weight) {
		this.weight = weight;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract long gradeBoard(Color c, Board board);
	
	public abstract long gradeBoardRaw(Color c, Board board);

	public long getWeight() {
		return weight;
	}

	public void updateWeight(long weight) {
		this.weight = weight;
	}
	
	public void updateWeight(long offset, boolean isOffset) {
		if (isOffset) {
			weight += offset;
		} else {
			weight = offset;
		}
	}
	
	@Override
	public int compareTo(Heuristic h) {
		if (weight < h.weight) {
			return -1;
		} else if (weight == h.weight) {
			return 0;
		} else {
			return 1;
		}
	}
}
