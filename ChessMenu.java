package chess;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
/**
 * Chess Menu Panel Object Class
 * Steven Chen
 * 1/20/2021
 */
public class ChessMenu extends JPanel implements ActionListener, ItemListener {
	//Field
	JButton play = new JButton("Play");	//Play button
	String[] times = {"Unlimited", "1|0", "2|1", "3|0", "3|2", "5|0", "5|3", "10|0", "10|5", "15|10", "30|0", "30|20"};	//Time selection
	JComboBox time = new JComboBox(times);	//Combobox to store the times
	Image background, wKing, bQueen, wBishop, bKnight, wRook, bPawn;	//Decorative images
	/**
	 * constructor
	 */
	public ChessMenu() {
		super();	//Creates a JPanel
		this.setSize(1200, 600);	//Sets size to 1200 width and 600 height
		background = ChessBoard.importImage("ChessBoard.png", 1200, 1200);	//imports images
		wKing = ChessBoard.importImage("WhiteKing.png", 175, 175);
		bQueen = ChessBoard.importImage("BlackQueen.png", 175, 175);
		wBishop = ChessBoard.importImage("WhiteBishop.png", 175, 175);
		bKnight = ChessBoard.importImage("BlackKnight.png", 175, 175);
		wRook = ChessBoard.importImage("WhiteRook.png", 175, 175);
		bPawn = ChessBoard.importImage("BlackPawn.png", 175, 175);
		play.addActionListener(this);	//Adds action listener to play
		add(play);	//Adds button to the JPanel
		time.addItemListener(this);	//Adds item listener to the combobox
		add(time);	//Adds combobox to the JPanel
	}
	/**
	 * Paints the images on the JPanel
	 * pre: java.awt.Graphics variable
	 */
	public void paintComponent(Graphics comp) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.drawImage(background, 0, 0, this);
		comp2D.drawImage(wKing, 200, 0, this);
		comp2D.drawImage(bQueen, 800, 0, this);
		comp2D.drawImage(wBishop, 200, 200, this);
		comp2D.drawImage(bKnight, 800, 200, this);
		comp2D.drawImage(wRook, 200, 400, this);
		comp2D.drawImage(bPawn, 800, 400, this);
	}
	/**
	 * Action Listener
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == play) {
			ChessMain.hideMenu();	//Hides the menu and displays the board and score
			ChessMain.showBoard();
			ChessMain.showScore();
			ChessMain.startClock();	//Starts the clock
		}
	}
	/**
	 * Item Listener
	 */
	public void itemStateChanged(ItemEvent event) {
		if (event.getItemSelectable() == time && event.getStateChange() == ItemEvent.SELECTED) {
			if ((event.getItem().toString()).equals("Unlimited")) {	//No time limit
				int tomins = 0;
				int tosecs = 0;
				int toinc = 0;
				ChessMain.setClock(tomins, tosecs, toinc);
			}
			else {
				String toMins = "";
				int tomins = 0;
				int toinc = 0;
				for (int i = 0; i < event.getItem().toString().length(); i++) {
					if (event.getItem().toString().charAt(i) == '|') {	//Before the '|' is the minutes, after is the incremment
						for (int j = 0; j < i; j++) {
							toMins = toMins + event.getItem().toString().charAt(j);
						}
						tomins = Integer.parseInt(toMins);
						toinc = Integer.parseInt(event.getItem().toString().substring(i+1));
					}
				}
				int tosecs = 0;
				ChessMain.setClock(tomins, tosecs, toinc);	//Sets the clock
			}
		}
	}
}