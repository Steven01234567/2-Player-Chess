package chess;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
/**
 * Chess Score Panel Object Class
 * Steven Chen
 * 1/20/2021
 */
public class ChessScore extends JPanel implements ActionListener, Runnable {
	//Field
	static JLabel wClock = new JLabel("White");	//Displays White's Colour and Clock
	static JButton wDraw = new JButton("Draw");	//White's Offer Draw Button
	static JButton wResign = new JButton("Resign");	//White's Resign Button
	static JTextArea moves = new JTextArea(20, 20);	//Score to keep track of moves
	static JButton bDraw = new JButton ("Draw");	//Black's Offer Draw Button
	static JButton bResign = new JButton("Resign");	//Black's Resign Button
	static JLabel bClock = new JLabel("Black");	//Display's Black's Colour and Clock
	Thread clock;	//Thread to use Runnable
	static String allMoves = "";	//Records all moves played
	static int moveNumber = 0;	//Movenumber
	int minutes, seconds = 0, wmin, wsec = 0, bmin, bsec = 0, inc = 0;	//Original Minutes, Seconds, Increment, and both colours' current minutes and seconds
	boolean time = false;	//Allows the program to control when the clock runs
	/**
	 * Constructor
	 */
	public ChessScore() {
		super();	//Creates a JPanel
		setLayout(new GridLayout(0, 1));	//Format in GridLayout
		setSize(600, 600);	//Sets Size to 600 width and 600 height
		add(bClock);	//Adds the components
		add(bDraw);
		bDraw.addActionListener(this);	//Adds action listener to the buttons
		add(bResign);
		bResign.addActionListener(this);
		moves.setVisible(true);
		add(moves);
		add(wDraw);
		wResign.addActionListener(this);
		add(wResign);
		add(wClock);
		wDraw.addActionListener(this);
		clock = new Thread(this);
		clock.start();
	}
	/**
	 * Run method which runs while the program runs
	 */
	public void run() {
		Thread thisThread = Thread.currentThread();

		while (clock == thisThread) {
			System.out.print("");	//I don't know why, but the clocks do not display without these so I included them in the final
			if (time) {	//Does not execute when the clock is not active
				System.out.print("");
				if ((wmin != 0 || wsec != 0) && (bmin != 0 || bsec != 0)) {	//If the minutes and seconds of both players are not 0
					try { Thread.sleep(1000); }	//1 second delay
					catch (InterruptedException e) {} 

						switch (ChessBoard.turn) {
						case 1:	//White's Turn
							if (wsec == 0) {	//1 minute -> 60 seconds
								wsec = 59;
								wmin--;
							}
							else if (wsec >= 60) {	//60 seconds -> 1 minute
								wsec -= 60;
								wmin++;
							}
							wsec--;	//-1 seconds each time
							break;
						case -1:	//Black's Turn (same logic as White)
							if (bsec == 0) {
								bsec = 59;
								bmin--;
							}
							else if (bsec >= 60) {
								bsec -= 60;
								bmin++;
							}
							bsec--;
							break;
						}
						wClock.setText("White  " + wmin + ":" + wsec);	//Displays the times
						bClock.setText("Black  " + bmin + ":" + bsec);
					if (wmin == 0 && wsec == 0) ChessMain.toWin("Black", "timeout");	//Timeout wins when a player's minutes and seconds both reach 0
					else if (bmin == 0 && bsec == 0) ChessMain.toWin("White", "timeout");
				}
			}
		}
	}
	/**
	 * Records the move played
	 * pre: Colour of player, piece moved, piece captured, x and y coordinates, initial x and y coordinates, promoted piece, check t/f, checkmate t/f, castlet/f
	 * post: Sets the textfield to add the move played
	 */
	public static void recordMove(int colour, int piece, int captured, int x, int y, int ix, int iy, int promote, boolean check, boolean checkmate, boolean sCastle, boolean lCastle) {
		String move = "";	//Resets the String
		if (colour == 1) {	//White's moves add the move number
			moveNumber++;	//Increases move number
			if (moveNumber%7 == 0) move = "\n";	//Keeps the text inside the JPanel
			move = move + moveNumber + ". ";
		}
		else move = " ";	//Black's move
		
			switch (Math.abs(piece)) {
			case 100: //King
				if (sCastle) move = move + "O-O";	//Short Castle
				else if (lCastle) move = move + "O-O-O";	//Long Castle
				else move = move + "K";
				break;
			case 9: move = move + "Q"; break;	//Queen
			case 5: move = move + "R"; break;	//Rook
			case 4: move = move + "B"; break;	//Bishop
			case 3: move = move + "N"; break;	//Knight
			}
		
		if (captured != 0) {
			if (Math.abs(piece) == 1 || promote != 0) move = move + getFile(ix);	//Pawn notation does not have a specific symbol. Instead, its the original file of the pawn
			move = move + "x";	//Symbolizes capture
		}
		
		if (!sCastle && !lCastle) {	//Other than castle, other moves include coordinates in the location
			move = move + getFile(x);	//Converts x coordinate into file
		
			switch (y) {	//Converts y coordinate into rank
			case 0: move = move + "8"; break;
			case 1: move = move + "7"; break;
			case 2: move = move + "6"; break;
			case 3: move = move + "5"; break;
			case 4: move = move + "4"; break;
			case 5: move = move + "3"; break;
			case 6: move = move + "2"; break;
			case 7: move = move + "1"; break;
			}
		}
		if (promote != 0) {	//If a pawn promoted...
			switch (Math.abs(promote)) {
			case 9: move = move + "=Q"; break;	//To Queen
			case 5: move = move + "=R"; break;	//To Rook
			case 4: move = move + "=B"; break;	//To Bishop
			case 3: move = move + "=N"; break;	//To Knight
			}
		}
		
		if (checkmate) move = move + "#";	//if Checkmate
		else if (check) move = move + "+";	//if check
		
		move = move + "  ";	//Space between the next move
		
		allMoves = allMoves + move;	//Adds the move to allMoves String
		
		moves.setText(allMoves);	//Sets text to allMoves
	}
	/**
	 * Converts x coordinate into rank
	 * pre: x coordinate
	 * post: Letter from a-h
	 */
	public static String getFile(int x) {
		String toReturn = null;
		switch (x) {
		case 0: toReturn = "a"; break;
		case 1: toReturn = "b"; break;
		case 2: toReturn = "c"; break;
		case 3: toReturn = "d"; break;
		case 4: toReturn = "e"; break;
		case 5: toReturn = "f"; break;
		case 6: toReturn = "g"; break;
		case 7: toReturn = "h"; break;
		}
		return toReturn;
	}
	/**
	 * Fully Resets the JPanel
	 */
	public static void fullReset() {
		wDraw.setEnabled(true);
		bDraw.setEnabled(true);
		moveNumber = 0;
		moves.setText(null);
		allMoves = "";
	}
	/**
	 * Action Listener
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == wDraw) {	//Offering Draws
			int draw = JOptionPane.showConfirmDialog(this,"Accept?", "White offers a Draw", JOptionPane.YES_NO_OPTION);	//Other Player Must Agree
			switch (draw) {
			case 0:
				ChessMain.toDraw("agreement");	//Game Draws
				break;
			case 1:
				wDraw.setEnabled(false);	//Cannot Offer Draw again after a decline
			}
		}
		else if (event.getSource() == wResign) {	//Black Resigning
			ChessMain.toWin("Black", "resignation");	//White Wins
		}
		
		//Same Logic as above
		else if (event.getSource() == bDraw) {
			int draw = JOptionPane.showConfirmDialog(this,"Accept?", "Black offers a Draw", JOptionPane.YES_NO_OPTION);
			switch (draw) {
			case 0:
				ChessMain.toDraw("agreement");
				break;
			case 1:
				bDraw.setEnabled(false);
			}
		}
		else if (event.getSource() == bResign) {
			ChessMain.toWin("White", "resignation");
		}
	}
}