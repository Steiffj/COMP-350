package heuristics;

import components.Board;
import components.Color;

public class PieceTableHeuristic extends Heuristic {

	private static final double[][] PIECE_TABLE = new double[8][8];
	
	public PieceTableHeuristic() {
		super();
		buildPieceTable();
		name = "Piece Table";
	}
	
	public PieceTableHeuristic(int weight) {
		super(weight);
		buildPieceTable();
		name = "Piece Table";
	}
	
	@Override
	public double gradeBoard(Color piece, Board board) {
		double score = 0.0;
		Color[][] boardInnards = board.getContents();
		
		for (int row = 0; row < boardInnards.length; row++) {
			for (int col = 0; col < boardInnards[row].length; col++) {
				if (boardInnards[row][col] == piece) {
					score += PIECE_TABLE[row][col];
				}
			}
		}
		return score * weight;
	}
	
	@Override
	public double gradeBoardRaw(Color piece, Board board) {
		double score = 0.0;
		Color[][] boardInnards = board.getContents();
		
		for (int row = 0; row < boardInnards.length; row++) {
			for (int col = 0; col < boardInnards[row].length; col++) {
				if (boardInnards[row][col] == piece) {
					score += PIECE_TABLE[row][col];
				}
			}
		}
		return score;
	}
	
	/**
	 * Initializes the piece table with the tuned values discussed in:
	 * <a href="https://web.stanford.edu/class/cs221/2017/restricted/p-final/man4/final.pdf">Programming an Othello AI</a>
	 */
	private void buildPieceTable() {
		
		/*
		 * Take advantage of the assignment operator's right-associativity :) 
		 * There is a mathy way to do this, but, we decided to assign the values manually, since they aren't getting updated in this program
		 */
		
		// Weight corners
		PIECE_TABLE[0][0] =
		PIECE_TABLE[0][7] = 
		PIECE_TABLE[7][0] = 
		PIECE_TABLE[7][7] = 16.16;
		
		// Weight corner buffers 1/3
		PIECE_TABLE[0][1] = 
		PIECE_TABLE[0][6] = 
		PIECE_TABLE[7][1] = 
		PIECE_TABLE[7][6] = -3.03;
		
		// Weight corner buffers 2/3
		PIECE_TABLE[1][0] =
		PIECE_TABLE[1][7] =
		PIECE_TABLE[6][0] = 
		PIECE_TABLE[6][7] = -4.12; 
		
		// Weight corner buffers 3/3
		PIECE_TABLE[1][1] = 
		PIECE_TABLE[1][6] = 
		PIECE_TABLE[6][1] = 
		PIECE_TABLE[6][6] = -1.81;
		
		// Weight edges 1/4
		PIECE_TABLE[0][2] = 
		PIECE_TABLE[0][5] = 
		PIECE_TABLE[7][2] = 
		PIECE_TABLE[7][5] = 0.99;
		
		// Weight edges 2/4
		PIECE_TABLE[0][3] = 
		PIECE_TABLE[0][4] = 
		PIECE_TABLE[7][3] = 
		PIECE_TABLE[7][4] = 0.43; 
		
		// Weight edges 3/4
		PIECE_TABLE[2][0] = 
		PIECE_TABLE[2][7] = 
		PIECE_TABLE[5][0] = 
		PIECE_TABLE[5][7] = 1.33;
		
		// Weight edges 4/4
		PIECE_TABLE[3][0] = 
		PIECE_TABLE[3][7] = 
		PIECE_TABLE[4][0] = 
		PIECE_TABLE[4][7] = 0.63;
		
		// Weight center 1/8
		PIECE_TABLE[2][1] = 
		PIECE_TABLE[2][6] = 
		PIECE_TABLE[5][1] = 
		PIECE_TABLE[5][6] = -0.04;
		
		// Weight center 2/8
		PIECE_TABLE[3][1] = 
		PIECE_TABLE[3][6] = 
		PIECE_TABLE[4][1] = 
		PIECE_TABLE[4][6] = -0.18;
		
		// Weight center 3/8
		PIECE_TABLE[1][2] = 
		PIECE_TABLE[1][5] = 
		PIECE_TABLE[6][2] = 
		PIECE_TABLE[6][5] = -0.08;
		
		// Weight center 4/8
		PIECE_TABLE[2][2] = 
		PIECE_TABLE[2][5] = 
		PIECE_TABLE[5][2] = 
		PIECE_TABLE[5][5] = 0.51; 
		
		// Weight center 5/8
		PIECE_TABLE[3][2] = 
		PIECE_TABLE[3][5] = 
		PIECE_TABLE[4][2] = 
		PIECE_TABLE[4][5] = -0.04;
		
		// Weight center 6/8
		PIECE_TABLE[1][3] = 
		PIECE_TABLE[1][4] = 
		PIECE_TABLE[6][3] = 
		PIECE_TABLE[6][4] = -0.27; 
		
		// Weight center 7/8
		PIECE_TABLE[2][3] = 
		PIECE_TABLE[2][5] = 
		PIECE_TABLE[5][3] = 
		PIECE_TABLE[5][5] = 0.07;
		
		// Weight center 8/8
		PIECE_TABLE[3][3] = 
		PIECE_TABLE[3][4] = 
		PIECE_TABLE[4][3] = 
		PIECE_TABLE[4][4] = -0.01;
	}
}
