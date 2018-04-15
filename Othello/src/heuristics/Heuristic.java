package heuristics;

import components.Board;
import components.Color;

public abstract class Heuristic implements Comparable<Heuristic> {
	protected String name;
	protected int weight;
	
	public Heuristic() {
		weight = 1;
	}
	
	public Heuristic(int weight) {
		this.weight = weight;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract double gradeBoard(Color c, Board board);
	
	public abstract double gradeBoardRaw(Color c, Board board);

	public double getWeight() {
		return weight;
	}

	public void updateWeight(int weight) {
		this.weight = weight;
	}
	
	public void updateWeight(int offset, boolean isOffset) {
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
