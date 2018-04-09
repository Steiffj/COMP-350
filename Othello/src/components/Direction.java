package components;

/**
 * 
 * Representation of the 8 directions to check for pieces to flip, relative to the most recent piece placed on the board.
 *
 */
public enum Direction {
	
	/**
	 * Increment column
	 */
	ROW_RIGHT, 
	
	/**
	 * Decrement column
	 */
	ROW_LEFT,
	
	/**
	 * Increment row
	 */
	COL_DOWN, 
	
	/**
	 * Decrement row
	 */
	COL_UP, 
	
	/**
	 * Increment row, increment column
	 */
	DIAG_LOWER_RIGHT, 
	
	/**
	 * Decrement row, decrement column
	 */
	DIAG_UPPER_LEFT,
	
	/**
	 * Decrement row, increment column
	 */
	DIAG_UPPER_RIGHT, 
	
	/**
	 * Increment row, decrement column
	 */
	DIAG_LOWER_LEFT;
}
