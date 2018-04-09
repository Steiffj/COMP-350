package game;

import java.util.Scanner;

import components.Board;
import components.Color;
import components.Coordinate;
import components.OthelloBoard;
import components.Player;
import players.StupidAI;

public class Game {
	
	public static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		playTournament(3);
	}
	
	/**
	 * Plays a tournament of Othello.
	 * 
	 * @param numRounds the number of rounds to play in a tournament
	 */
	public static void playTournament(int numRounds) {
		int currentRound = 0;
		while (currentRound++ < numRounds) {
			System.out.println("\n\n====== Game " + currentRound + " of " + numRounds + " ======\n");
			playGame();
		}
	}
	
	/**
	 * Plays one full game of Othello. 
	 * Set up the different Players here!
	 */
	public static Color playGame() {
		Board gameBoard = new OthelloBoard();
		
		// Instantiate Player AIs from desired classes
		Player p1 = new StupidAI("Player 1", Color.B);
		Player p2 = new StupidAI("The Artificial Unintelligent", Color.W);
		
		int currentTurn = 1;
		
		// Each iteration through the while-loop is one full turn
		while(!gameBoard.isGameOver()) {
			System.out.println("\n\n--- Turn " + currentTurn++ + " ---");
			turn(p1, p2, gameBoard);
		}
		
		System.out.println("\n\n====== Game Over! ======\n\n" + gameBoard.toString());
		
		if (gameBoard.winner() == Color.EMPTY) {
			System.out.println("It's a draw!");
		} else {
			System.out.println("The Winner is " + 
				(gameBoard.winner() == p1.getColor() ? 
					p1.getName() + ", playing as " + p1.getColor() : 
					p2.getName() + ", playing as " + p2.getColor()));
		}
		
		System.out.println(p1.getColor() + " total: " + gameBoard.countPieces(p1.getColor()));
		System.out.println(p2.getColor() + " total: " + gameBoard.countPieces(p2.getColor()));
		
		return gameBoard.winner();
	}
	
	/**
	 * 
	 * One turn in a game of Othello (one ply for each Player)
	 * 
	 * @param p1 Player 1
	 * @param p2 Player 2
	 * @param b The game Board
	 */
	public static void turn(Player p1, Player p2, Board b) {
		ply(p1, b);
		System.out.print("\n");
		ply(p2, b);
	}
	
	/**
	 * 
	 * A ply for a single Player in a game of Othello. The Player will either place a piece on the Board, or pass it there are no valid moves available.
	 * 
	 * @param p the Player whose ply it is
	 * @param b the game Board
	 */
	public static void ply(Player p, Board b) {
		
		System.out.println("\n" + b.toString(p.getColor()));
		System.out.print(p.getName() + "\'s move (" + p.getColor() + ") ");
		
		if (b.countValidMoves(p.getColor()) > 0) {
			
			// Call the current Player's makeMove() method, and attempt to update the game board with the results
			Coordinate playerMove;
			while (!b.set(p.getColor(), (playerMove = p.makeMove(b)))) {
				System.out.println("\n" + p.getName() + " attempted invalid move: " + convertCoordinate(playerMove) + "\nPress ENTER to continue");
				sc.nextLine();
				System.out.print(p.getName() + "\'s move (" + p.getColor() + "): ");
			}
		} else {
			System.out.println("\n" + p.getName() + " passes (no moves available).");
			return;
		}
	}
	
	/**
	 * Makes Coordinates easier to read for debugging
	 * @param coord the Coordinate to convert
	 * @return
	 */
	public static String convertCoordinate(Coordinate coord) {
		String humanCoord = "";
		
		humanCoord += "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("")[coord.getCol()];
		humanCoord += (coord.getRow() - 1);
		
		return humanCoord;
	}
}
