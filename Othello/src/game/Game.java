package game;

import java.util.HashMap;
import java.util.Scanner;

import components.Board;
import components.Color;
import components.Coordinate;
import components.OthelloBoard;
import components.Player;
import heuristics.MaxPiecesHeuristic;
import heuristics.MinMobilityHeuristic;
import heuristics.ParityHeuristic;
import heuristics.PieceTableHeuristic;
import heuristics.StabilityHeuristic;
import players.Human;
import players.HybridAI;
import players.ShallowMindAI;

public class Game {
	public static HashMap<String, Player> avatars = new HashMap<String, Player>();
	public static Player p1;
	public static Player p2;
	
	public static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		initPlayerList();
		choosePlayers();
		playTournament(3);
		System.exit(0);
	}
	
	public static void initPlayerList() {
		avatars.put("p", new HybridAI("Pieces", Color.B, false, new MaxPiecesHeuristic(1)));
		avatars.put("m", new HybridAI("Mobility", Color.B, false, new MinMobilityHeuristic(1)));
		avatars.put("h1", new HybridAI("Hybrid", Color.B, false, new MaxPiecesHeuristic(75), new MinMobilityHeuristic(25)));
		avatars.put("h2", new HybridAI("Hybrid Curves", Color.B, false, new MaxPiecesHeuristic(40), new MinMobilityHeuristic(65), new PieceTableHeuristic(100)));
		
		avatars.put("shallow", new ShallowMindAI("Shallow Mind", Color.B, new PieceTableHeuristic(100), 
																		new MaxPiecesHeuristic(30), 
																		new MinMobilityHeuristic(50), 
																		new StabilityHeuristic(60), 
																		new ParityHeuristic(75)));
		
		avatars.put("shallow simple", new ShallowMindAI("Shallow Mind Simplified", Color.B, 
																							new MaxPiecesHeuristic(40)
																							));
		avatars.put("human", new Human("Opponent", Color.B));
	}
	
	public static void choosePlayers() {
		System.out.println("Player Options:");
		for (String name : avatars.keySet()) {
			System.out.print(name + "  ");
		}
		System.out.print("\n\n");
		
		System.out.print("Which avatar do you choose as player 1? ");
		String player1 = sc.nextLine();
		while(!avatars.containsKey(player1)) {
			System.out.print("Which avatar do you choose as player 1? ");
			player1 = sc.nextLine();
		}
		System.out.print("Which avatar do you choose as player 2? ");
		String player2 = sc.nextLine();
		while(!avatars.containsKey(player2)) {
			System.out.print("Which avatar do you choose as player 2? ");
			player2 = sc.nextLine();
		}
		
		p1 = avatars.get(player1);
		p2 = avatars.get(player2);
		p1.setColor(Color.B);
		p2.setColor(Color.W);
	}
	
	/**
	 * Plays a tournament of Othello.
	 * 
	 * @param numRounds the number of rounds to play in a tournament
	 */
	public static void playTournament(int numRounds) {
		int currentRound = 0;
		boolean swap = false;
		while (currentRound++ < numRounds) {
			
			System.out.println("\n\n====== Game " + currentRound + " of " + numRounds + " ======\n");
			playGame(swap);
			
			if (currentRound < numRounds) {
				System.out.print("\nSwitch who goes first for the next game? (y/n): ");
				if (sc.nextLine().matches("[Yy][Ee]?[Ss]?")) {
					swap = true;
				} else {
					swap = false;
				}
			}
		}
	}
	
	/**
	 * Plays one full game of Othello. 
	 * Set up the different Players here!
	 */
	public static Player playGame(boolean swap) {
		Board gameBoard = new OthelloBoard();
		
		if(swap) {
			p1.swapColor();
			p2.swapColor();
		}
		
		int currentTurn = 1;
		
		// Each iteration through the while-loop is one full turn
		while(!gameBoard.isGameOver()) {
			System.out.println("\n\n---== Turn " + currentTurn++ + " ==---");
			if(swap) {
				System.out.println("  " + Color.B + " total: " + gameBoard.countPieces(Color.B));
				System.out.println("  " + Color.W + " total: " + gameBoard.countPieces(Color.W));
				turn(p2, p1, gameBoard);
			} else {
				System.out.println("  " + Color.B + " total: " + gameBoard.countPieces(Color.B));
				System.out.println("  " + Color.W + " total: " + gameBoard.countPieces(Color.W));
				turn(p1, p2, gameBoard);
			}
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
		
		System.out.println("\n  " + Color.B + " total: " + gameBoard.countPieces(Color.B));
		System.out.println("  " + Color.W + " total: " + gameBoard.countPieces(Color.W));
		
		return p1.getColor() == gameBoard.winner() ? p1 : p2;
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
		System.out.println("\n  " + Color.B + " total: " + b.countPieces(Color.B));
		System.out.println("  " + Color.W + " total: " + b.countPieces(Color.W));
		
		System.out.print("\n");
		
		ply(p2, b);
		System.out.println("\n  " + Color.B + " total: " + b.countPieces(Color.B));
		System.out.println("  " + Color.W + " total: " + b.countPieces(Color.W));
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
		System.out.println("- Playing on board above; result shown below -");
		
		if (b.countValidMoves(p.getColor()) > 0) {
			System.out.print("Valid moves: ");
			int count = 0;
			for (Coordinate coord : b.getValidMoves(p.getColor())) {
				System.out.print(convertCoordinate(coord) + "  ");
				if (++count % 6 == 0) {
					System.out.print("\n             ");
				}
			}
			System.out.print("\n");
		}
		System.out.print(p.getName() + "\'s ply (" + p.getColor() + "): ");
		
		if (b.countValidMoves(p.getColor()) > 0) {
			
			// Call the current Player's makeMove() method, and attempt to update the game board with the results
			Coordinate playerMove;
			while (!b.set(p.getColor(), (playerMove = p.makeMove(b)))) {
				System.out.println("\n" + p.getName() + " attempted invalid move: " + convertCoordinate(playerMove) + "\nPress ENTER to continue");
				sc.nextLine();
				System.out.print(p.getName() + "\'s ply (" + p.getColor() + "): ");
			}
			if (!(p instanceof Human)) {
				System.out.println(convertCoordinate(playerMove));
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
		if (coord == null) {
			return "NULL COORDINATE";
		}
		
		String humanCoord = "";
		humanCoord += "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("")[coord.getCol()];
		humanCoord += (coord.getRow() + 1);
		
		return humanCoord;
	}
}
