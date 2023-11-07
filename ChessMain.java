package chess;
import java.awt.FlowLayout;

import javax.swing.*;
/*
 * 2-Player Chess Main Class
 * Steven Chen
 * 1/20/2021
 */
public class ChessMain extends JFrame {
	//Field
	static ChessBoard CBoard = new ChessBoard();	//new ChessBoard object
	static ChessMenu CMenu = new ChessMenu();	//new ChessMenu object
	static ChessScore CScore = new ChessScore();	//new ChessScore object
	/**
	 * Constructor
	 */
	public ChessMain() {
		super();	//Creates a JFrame
		setTitle("2 Player Chess");	//Sets game title
		setSize(1200, 640);	//Sets size to 1200 width and 640 height
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(CMenu);	//Adds the JPanel objects
		CMenu.setVisible(true);
		add(CScore);
		CScore.setVisible(false);
		CScore.setBounds(600, 0, 600, 600);	//Sets bounds of CScore
		add(CBoard);
		CBoard.setBounds(0, 0, 600, 600);	//Sets bounds of CBoard
		CBoard.setVisible(false);
		
		setVisible(true);
	}

	public static void main(String[] args) {
		ChessMain CMain = new ChessMain();	//new ChessMain object
	}
	/*
	 * Sets visibility of CBoard to true
	 */
	public static void showBoard() {
		CBoard.setVisible(true);
	}
	/*
	 * Sets visibility of CBoard to false
	 */
	public static void hideBoard() {
		CBoard.setVisible(false);
	}
	/*
	 * Sets visibility of CMenu to true
	 */
	public static void showMenu() {
		CMenu.setVisible(true);
	}
	/*
	 * Sets visibility of CMenu to false
	 */
	public static void hideMenu() {
		CMenu.setVisible(false);
	}
	/*
	 * Sets visibility of CScore to true
	 */
	public static void showScore() {
		CScore.setVisible(true);
	}
	/*
	 * Sets visibility of CScore to false
	 */
	public static void hideScore() {
		CScore.setVisible(false);
	}
	/*
	 * Allows other Jpanels to access the win method of CBoard
	 * pre: winning colour and method of winning
	 */
	public static void toWin(String colour, String method) {
		CBoard.win(colour, method);
	}
	/*
	 * Allows othe rJPanels to access the draw method of CBoard
	 * pre: method of drawing
	 */
	public static void toDraw(String method) {
		CBoard.draw(method);
	}
	/*
	 * Increases time of appropriate player
	 * pre: turn of the player
	 * post: adds time to clock of that player
	 */
	public static void increment(int turn) {
		switch (turn) {
		case 1:	//White moved
			CScore.wsec += CScore.inc;
			break;
		case -1:	//Black moved
			CScore.bsec += CScore.inc;
			break;
		}
	}
	/*
	 * Sets the clocks of ChessScore
	 * pre: minutes, seconds, and increment
	 */
	public static void setClock(int mins, int secs, int incr) {
		CScore.minutes = mins;
		CScore.seconds = secs;
		CScore.inc = incr;
		resetClock();
	}
	/*
	 * Resets the clocks of ChessScore
	 */
	public static void resetClock() {
		CScore.wmin = CScore.minutes;
		CScore.bmin = CScore.minutes;
		CScore.wsec = 0;
		CScore.bsec = 0;
	}
	/*
	 * Starts the clocks
	 */
	public static void startClock() {
		CScore.time = true;
	}
	/*
	 * Stops the clocks
	 */
	public static void stopClock() {
		CScore.time = false;
	}
}