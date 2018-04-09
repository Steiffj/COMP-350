package app;

public class FailReport {
	private String message;
	private int steps;
	
	// Simple class to keep track of relevant data if a search fails
	public FailReport(String message, int steps) {
		this.message = message;
		this.steps = steps;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
}
