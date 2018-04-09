package app;

/*
 * Tuples are always useful
 */
public class Tuple {
	
	private int value;
	private int index;
	private boolean original;
	
	public Tuple(int value, int index, boolean original) {
		this.value = value;
		this.index = index;
		this.original = original;
	}
	
	public Tuple(Tuple that) {
		
		// create a deep copy of the given Tuple
		this.value = that.value;
		this.index = that.index;
		this.original = that.original;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getIndex() {
		return index;
	}
	
	public boolean isOriginal() {
		return original;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Tuple)) {
			return false;
		}
		
		Tuple t = (Tuple) o;
		
		return value == t.value && index == t.index && original == t.original;
	}
}
