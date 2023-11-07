package chess;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
/**
 * Chess Board Panel Object Class
 * Steven Chen
 * 1/20/2021
 */
public class ChessBoard extends JPanel implements MouseListener {
	//Field
	static Image chessBoard, wp, bp, wr, br, wn, bn, wb, bb, wq, bq, wk, bk, toBoard;	//Variables which hold images for the game
	static int xPos, yPos, newX, newY, piece = 0, captured, cx, cy;	//Piece specific variables: coordinates, piece ID, captured piece
	static int turn = 1, empassant = -1, promoted = 0;	//Variables for determining whose turn it is, if empassant is possible, and the promoted piece
	static String promote, colour;	//Variables to store promotion input from user and the colour of the player
	static int[][] board = new int[8][8];	//2D Array for representing the chessboard
	static boolean wshort = true, bshort = true, wlong = true, blong = true, sCastle = false, lCastle = false;	//Variables for determining if certain moves are possible or have occured
	/**
	 * Constructor
	 */
	public ChessBoard() {
		super();	//Creates a JPanel
		this.setSize(600, 600);	//Sets Size to width = 600, height = 600
		addMouseListener(this);	//Adds a mouse listener
		resetBoard(board);	//Sets up the pieces on the board
		chessBoard = importImage("ChessBoard.png", 600, 600);	//Importing all images
		wp = importImage("WhitePawn.png", 75, 75);
		bp = importImage("BlackPawn.png", 75, 75);
		wr = importImage("WhiteRook.png", 75, 75);
		br = importImage("BlackRook.png", 75, 75);
		wn = importImage("WhiteKnight.png", 75, 75);
		bn = importImage("BlackKnight.png", 75, 75);
		wb = importImage("WhiteBishop.png", 75, 75);
		bb = importImage("BlackBishop.png", 75, 75);
		wq = importImage("WhiteQueen.png", 75, 75);
		bq = importImage("BlackQueen.png", 75, 75);
		wk = importImage("WhiteKing.png", 75, 75);
		bk = importImage("BlackKing.png", 75, 75);
	}
	/**
	 * Draws Graphic Images on the JFrame
	 * pre: java.awt.Graphics variable
	 * post: paints images
	 */
	public void paintComponent(Graphics comp) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.drawImage(chessBoard, 0, 0, this);
		if (piece != 0) {
			highlight(xPos*75, yPos*75, comp2D);
			highlight(newX*75, newY*75, comp2D);
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				switch (board[i][j]) {
				case 0: toBoard = null; break;	//Empty
				case 1: toBoard = wp; break;	//White Pawn
				case -1: toBoard = bp; break;	//Black Pawn
				case 5: toBoard = wr; break;	//White Rook
				case -5: toBoard = br; break;	//Black Rook
				case 3: toBoard = wn; break;	//White Knight
				case -3: toBoard = bn; break;	//Black Knight
				case 4: toBoard = wb; break;	//White Bishop
				case -4: toBoard = bb; break;	//Black Bishop
				case 9: toBoard = wq; break;	//White Queen
				case -9: toBoard = bq; break;	//Black Queen
				case 100: toBoard = wk; break;	//White King
				case -100: toBoard = bk; break;	//Black King
				}
				comp2D.drawImage(toBoard, j*75, i*75, this);	//Draws the selected image in the appropriate position
			}
		}
	}
	/**
	 * Imports an Image from outside the source folder
	 * pre: Image name, desired height and width of the image
	 * post: returns an Image variable
	 */
	public static Image importImage(String name, int height, int width) {
		  Image i = null;
		  try {
		   i = ImageIO.read(new File(name)).getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		  return i;
		 }
	/**
	 * Highlights a sqaure on the chessboard
	 * pre: x and y coordinates and java.awt.Graphics2D variable
	 * post: paints a transparent yellow square on a position on the board
	 */
	public void highlight(int x, int y, Graphics2D comp2D) {
		comp2D.setPaint(new Color(255, 255, 0, 128));
		comp2D.fillRect(x, y, 75, 75);
	}
	/**
	 * Resets the 2D Array
	 * pre: 2D Array Chessboard
	 */
	public static void resetBoard(int[][] board) {
		String order = "53490435";	//Order of Chess Pieces
		for (int i = 0; i < 8; i++) {
			board[0][i] = -1*Integer.parseInt(Character.toString(order.charAt(i)));	//Fills each index with the appropriate chess piece
			if (board[0][i] == 0) board[0][i] = -100;	//Replaces 0 with (+/-)100 for the king
			board[1][i] = -1;	//Pawns
			board[6][i] = 1;
			board[7][i] = Integer.parseInt(Character.toString(order.charAt(i)));
			if (board[7][i] == 0) board[7][i] = 100;
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 2; j < 6; j++) {
				board[j][i] = 0;	//Empty Squares
			}
		}
	}
	/**
	 * Determines if a move is physically possible
	 * pre: none
	 * post: A boolean variable true or false
	 */
	public boolean checkLegal() {
		boolean legal = false;	//Move is false until proven true
		if (piece != 0) {	//Selected piece must be an actual piece, not space
			if (board[yPos][xPos]/turn > 0) {	//Player cannot move the opponent's piece
				if (board[newY][newX]/turn <= 0) {	//Player cannot capture his own piece
					switch (piece) {
					case 1:	//White Pawn
						if (newY-yPos == -1 && newX-xPos == 0 && board[newY][newX] == 0) {	//Regular 1 square up
							legal = true;
							empassant = -1;
						}
						else if (newY-yPos == -2 && board[yPos-1][xPos] == 0 && yPos > 5) {	//2 Squares up (Must be on the starting rank)
							legal = true;
							empassant = xPos;
						}
						else if (newY-yPos == -1 && Math.abs(newX-xPos) == 1) {	//Regular Diagonal Captures
							if (board[newY][newX] < 0) {
								legal = true;
								empassant = -1;
							}
							else if (board[newY+1][newX] == -1 && empassant == newX) {	//Empassant (Must be instant)
								board[newY+1][newX] = 0;
								legal = true;
								captured = -1;
								empassant = -1;
							}
						}
						if (legal && newY == 0) {	//Promotion at the end of the board
							promote = JOptionPane.showInputDialog(this,"Queen(Q)\nRook(R)\nBishop(B)\nKnight(N)","Promote to:",JOptionPane.PLAIN_MESSAGE);
							switch (promote) {
							case "R":	//Promotes to a rook
							case "r":
								piece = 5;
								break;
							case "B":	//Promotes to a bishop
							case "b":
								piece = 4;
								break;
							case "N":	//Promotes to a knight
							case "n":
								piece = 3;
								break;
							default:	//Promotes to a queen
								piece = 9;
								break;
							}
							promoted = piece;	//Stores the promoted piece in a variable
						}
						break;
					case -1:	//Black Pawn (Same logic as White Pawn)
						if (newY-yPos == 1 && newX-xPos == 0 && board[newY][newX] == 0) {
							legal = true;
							empassant = -1;
						}
						else if (newY-yPos == 2 && yPos < 3) {
							legal = true;
							empassant = xPos;
						}
						else if (newY-yPos == 1 && Math.abs(newX-xPos) == 1) { 
							if (board[newY][newX] > 0) {
								legal = true;
								captured = 1;
								empassant = -1;
							}
							else if (board[newY-1][newX] == 1 && empassant == newX) {
								board[newY-1][newX] = 0;
								legal = true;
								empassant = -1;
							}
						}
						if (legal && newY == 7) {
							promote = JOptionPane.showInputDialog(this,"Queen(Q)\nRook(R)\nBishop(B)\nKnight(N)","Promote to:",JOptionPane.PLAIN_MESSAGE);
							switch (promote) {
							case "R":
							case "r":
								piece = -5;
								break;
							case "B":
							case "b":
								piece = -4;
								break;
							case "N":
							case "n":
								piece = -3;
								break;
							default:
								piece = -9;
								break;
							}
							promoted = piece;
						}
						break;
						
					case 5:	//White Rook
					case -5:	//Black Rook
						if (checkStraight()) {	//Calls checkStraight method to check if legal (explained below)
							legal = true; 
							empassant = -1;
						}
						break;
						
					case 3:	//White Knight
					case -3:	//Black Knight
						if ((Math.abs(newY-yPos) == 2 && Math.abs(newX-xPos) == 1) || (Math.abs(newY-yPos) == 1 && Math.abs(newX-xPos) == 2)) {	//Checks if Knight moved 2 spaces in one direction and 1 space in a perpendicular direction
							legal = true;
							empassant = -1;
						}
						break;
						
					case 4:	//White Bishop
					case -4:	//Black Bishop
						if (checkDiagonal()) {	//Calls checkDiagonal method to check if legal (explained below)
							legal = true;
							empassant = -1;
						}
						break;
						
					case 9:	//White Queen
					case -9:	//Black Queen
						if (checkStraight() || checkDiagonal()) {	//Calls checkStraight and checkDiagonal method to check if legal (explained below)
							legal = true;
							empassant = -1;
						}
						break;
					
					case 100:	//White King
						if (Math.abs(newX-xPos) < 2 && Math.abs(newY-yPos) < 2) {	//King can only move 1 space in any direction
							legal = true;
							empassant = -1;
							wshort = false;	//King cannot castle after moving
							wlong = false;
						}
						else if (newX-xPos == 2 && newY-yPos == 0) {	//Short Castle
							if (board[yPos][xPos+1] == 0 && board[yPos][xPos+2] == 0) {	//Space between king and rook must be empty
								if (wshort) {	//Must be allowed (rook and king cannot have moved prior)
									if (!checkCheck(100, xPos, yPos)) {	//King cannot be in check when castling
										board[yPos][xPos+1] = 100;
										board[yPos][xPos] = 0;
										if (!checkCheck(100, xPos+1, yPos)) {
											legal = true;
											empassant = -1;
											wshort = false;	//Cannot castle again
											wlong = false;
											board[7][7] = 0;	//Switches rook position
											board[7][5] = 5;
											sCastle = true;
										}
										else {	//If castle is illegal
											board[yPos][xPos] = 100;
											board[yPos][xPos+1] = 0;
										}
									}
								}
							}
						}
						else if (newX-xPos == -2 && newY-yPos == 0) {	//long castle (same logic as short castle)
							if (board[yPos][xPos-1] == 0 && board[yPos][xPos-2] == 0 && board[yPos][xPos-3] == 0) {
								if (wlong) {
									if (!checkCheck(100, xPos, yPos)) {
										board[yPos][xPos-1] = 100;
										board[yPos][xPos] = 0;
										if (!checkCheck(100, xPos-1, yPos)) {
											board[yPos][xPos-2] = 100;
											board[yPos][xPos-1] = 0;
											if (!checkCheck(100, xPos-2, yPos)) {
												legal = true;
												empassant = -1;
												wshort = false;
												wlong = false;
												board[7][0] = 0;
												board[7][3] = 5;
												lCastle = true;
											}
											else {
												board[yPos][xPos] = 100;
												board[yPos][xPos-2] = 0;
											}
										}
									}
								}
							}
						}
						break;
					case -100:	//Black King (same logic as White King)
						if (Math.abs(newX-xPos) < 2 && Math.abs(newY-yPos) < 2) {
							legal = true;
							empassant = -1;
							bshort = false;
							blong = false;
						}
						else if (newX-xPos == 2 && newY-yPos == 0) {
							if (board[yPos][xPos+1] == 0 && board[yPos][xPos+2] == 0) {
								if (bshort) {
									if (!checkCheck(-100, xPos, yPos)) {
										board[yPos][xPos+1] = -100;
										board[yPos][xPos] = 0;
										if (!checkCheck(-100, xPos+1, yPos)) {
											legal = true;
											empassant = -1;
											bshort = false;
											blong = false;
											board[0][7] = 0;
											board[0][5] = -5;
											sCastle = true;
										}
										else {
											board[yPos][xPos] = 100;
											board[yPos][xPos+1] = 0;
										}
									}
								}
							}
						}
						else if (newX-xPos == -2 && newY-yPos == 0) {
							if (board[yPos][xPos-1] == 0 && board[yPos][xPos-2] == 0 && board[yPos][xPos-3] == 0) {
								if (blong) {
									if (!checkCheck(-100, xPos, yPos)) {
										board[yPos][xPos-1] = -100;
										board[yPos][xPos-0] = 0;
										if (!checkCheck(-100, xPos-1, yPos)) {
											board[yPos][xPos-2] = -100;
											board[yPos][xPos-1] = 0;
											if (!checkCheck(-100, xPos-2, yPos)) {
												legal = true;
												empassant = -1;
												bshort = false;
												blong = false;
												board[0][0] = 0;
												board[0][3] = -5;
												lCastle = true;
											}
											else {
												board[yPos][xPos] = -100;
												board[yPos][xPos-2] = 0;
											}
										}
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		return legal;	//Returns the final value of the boolean
	}
	/**
	 * Checks if a piece move is in a straight direction and is legal
	 * pre: none
	 * post: Boolean variable true or false
	 */
	public boolean checkStraight() {
		boolean legal = false;	//Legal is false until proven true
		if (xPos == newX) {	//If moving vertically
			if (yPos > newY) {	//Move Up
				for (int i = yPos-1; i > newY-1; i--) {	//Checks each square between movement
					if (i == newY) {	//If no obstruction, move is legal
						if (piece == 5) {
							if (xPos == 0 && yPos == 7) wlong = false;	//If the piece is a rook, castling is illegal
							else if (xPos == 7 && yPos == 7) wshort = false;
						}
						legal = true;
					}
					if (board[i][xPos] != 0) break;	//If piece is in the way, move is illegal
				}
			}
			else if (yPos < newY) {	//Move Down (Same logic as move up)
				for (int i = yPos+1; i < newY+1; i++) {
					if (i == newY) {
						if (piece == -5) {
							if (xPos == 0 && yPos == 0) blong = false;
							else if (xPos == 7 && yPos == 0) bshort = false;
						}
						legal = true;
					}
					if (board[i][xPos] != 0) break;
				}
			}
		}
		else if (yPos == newY) {	//If piece moves horizontally
			if (xPos > newX) {	//Move Left (Same logic as above)
				for (int i = xPos-1; i > newX-1; i--) {
					if (i == newX) {
						if (yPos == 7) {
							if (xPos == 7 && piece == 5) wshort = false;
							else if (xPos == 0 && piece == -5) bshort = false;
						}
						legal = true;
					}
					if (board[yPos][i] != 0) break;
				}
			}
			else if (xPos < newX) {	//Move Right (Same logic as above)
				for (int i = xPos+1; i < newX+1; i++) {
					if (i == newX) {
						if (yPos == 0) {
							if (xPos == 7 && piece == 5) wlong = false;
							else if (xPos == 0 && piece == -5) blong = false;
						}
						legal = true;
					}
					if (board[yPos][i] != 0) break;
				}
			}
		}
		return legal;	//Returns final variable true or false
	}
	/**
	 * Determines if a piece move is in a diagonal direction and is legal
	 * pre: none
	 * post: Boolean variable true or false
	 */
	public boolean checkDiagonal() {
		boolean legal = false;	//legal is false until proven true
		if (Math.abs(newY-yPos) == Math.abs(newX-xPos)) {	//Only legal if change in x is equal to the change in y
			if (yPos > newY) {	//Move Up...
				if (xPos < newX) {	//...And Right
					for (int i = 1; i <= newX-xPos; i++) {	//Checks each sqaure between movement
						if (xPos+i == newX) legal = true;
						if (board[yPos-i][xPos+i] != 0) break;	//Checks for obstructions
					}
				}
				else if (xPos > newX) {	//...And Left (same logic as above)
					for (int i = 1; i <= xPos-newX; i++) {
						if (xPos-i == newX) legal = true;
						if (board[yPos-i][xPos-i] != 0) break;
					}
				}
			}
			else if (yPos < newY) {	//Move Down...
				if (xPos < newX) {	//...And Right (same logic as above)
					for (int i = 1; i <= newX-xPos; i++) {
						if (xPos+i == newX) legal = true;
						if (board[yPos+i][xPos+i] != 0) break;
					}
				}
				else if (xPos > newX) {	//...And Left (same logic as above)
					for (int i = 1; i <= xPos-newX; i++) {
						if (xPos-i == newX) legal = true;
						if (board[yPos+i][xPos-i] != 0) break;
					}
				}
			}
		}
		return legal;	//Returns final variable true or false
	}
	/**
	 * Checks if a square is being checked
	 * pre: specific king, x and y coordinate
	 * post: Boolean variable true or false
	 */
	public boolean checkCheck(int king, int x, int y) {
		boolean check = false;	//check is false until proven true
		//Horizontal from rook and queen
		//Left side
		for (int i = x-1; i >= 0; i--) {
			if (board[y][i] == -5*king/100 || board[y][i] == -9*king/100) {check = true; break;}	//Checks for rook and queen
			else if (board[y][i] != 0) break;	//If other piece, no check
		}
		//Right side (same logic as above)
		for (int i = x+1; i <= 7; i++) {
			if (board[y][i] == -5*king/100 || board[y][i] == -9*king/100) {check = true; break;}
			else if (board[y][i] != 0) break;
		}
		//Top side (same logic as above)
		for (int i = y-1; i >= 0; i--) {
			if (board[i][x] == -5*king/100 || board[i][x] == -9*king/100) {check = true; break;}
			else if (board[i][x] != 0) break;
		}
		//Bottom side (same logic as above)
		for (int i = y+1; i <= 7; i++) {
			if (board[i][x] == -5*king/100 || board[i][x] == -9*king/100) {check = true; break;}
			else if (board[i][x] != 0) break;
		}
		//Diagonals from queen and bishop
		//Upper left
		for (int i = 1; x-i >= 0 && y-i >= 0; i++) {
			if (board[y-i][x-i] != 0) {	//If its a piece, the loop stops
				if (board[y-i][x-i] == -4*king/100 || board[y-i][x-i] == -9*king/100) check = true;	//If the piece is a bishop or a queen, check is true
				break;
			}
		}
		//Lower left (same logic as above)
		for (int i = 1; x-i >= 0 && y+i <= 7; i++) {
			if (board[y+i][x-i] != 0) {
				if (board[y+i][x-i] == -4*king/100 || board[y+i][x-i] == -9*king/100) check = true;
				break;
			}
		}
		//Upper right (same logic as above)
		for (int i = 1; x+i <= 7 && y-i >= 0; i++) {
			if (board[y-i][x+i] != 0) {
				if (board[y-i][x+i] == -4*king/100 || board[y-i][x+i] == -9*king/100) check = true;
				break;
			}
		}
		//Lower right (same logic as above)
		for (int i = 1; x+i <= 7 && y+i <= 7; i++) {
			if (board[y+i][x+i] != 0) {
				if (board[y+i][x+i] == -4*king/100 || board[y+i][x+i] == -9*king/100) check = true;
				break;
			}
		}
		
		//Knight moves (checks all eight possible squares if they exist)
		if (y-1 >= 0 && x-2 >= 0) if (board[y-1][x-2] == -3*king/100) check = true;
		if (y-2 >= 0 && x-1 >= 0) if (board[y-2][x-1] == -3*king/100) check = true;
		if (y+1 <= 7 && x-2 >= 0) if (board[y+1][x-2] == -3*king/100) check = true;
		if (y-2 >= 0 && x+1 <= 7) if (board[y-2][x+1] == -3*king/100) check = true;
		if (y-1 >= 0 && x+2 <= 7) if (board[y-1][x+2] == -3*king/100) check = true;
		if (y+2 <= 7 && x-1 >= 0) if (board[y+2][x-1] == -3*king/100) check = true;
		if (y+1 <= 7 && x+2 <= 7) if (board[y+1][x+2] == -3*king/100) check = true;
		if (y+2 <= 7 && x+1 <= 7) if (board[y+2][x+1] == -3*king/100) check = true;
		
		//Pawns
		//Black Pawns and White King
		if (x-1 >= 0 && y-1 >= 0) {
			if (king == 100 && board[y-1][x-1] == -king/100) check = true;
		}
		if (x+1 <= 7 && y-1 >= 0) {
			if (king == 100 && board[y-1][x+1] == -king/100) check = true;
		}
		//White Pawns and Black King
		if (x-1 >= 0 && y+1 <= 7) {
			if (king == -100 && board[y+1][x-1] == -king/100) check = true;
		}
		if (x+1 <= 7 && y+1 <= 7) {
			if (king == -100 && board[y+1][x+1] == -king/100) check = true;
		}
		
		return check;	//Returns final variable true or false
	}
	/**
	 * Checks if a square is being defended
	 * pre: specific king, x and y coordinates
	 * post: Boolean variable true or false
	 */
	public boolean checkDefends(int king, int x, int y) {
		boolean defends = false;	//Defends is false until proven true
		
		if (checkCheck(king, x, y)) defends = true;	//Checking a square is also defending it
		
		//King defending diagonally (1 square only)
		if (x-1 >= 0 && y-1 >= 0) {
			if (board[y-1][x-1] == -king) defends = true;
		}
		if (x+1 <= 7 && y-1 >= 0) {
			if (board[y-1][x+1] == -king) defends = true;
		}
		if (x-1 >= 0 && y+1 <= 7) {
			if (board[y+1][x-1] == -king) defends = true;
		}
		if (x+1 <= 7 && y+1 <= 7) {
			if (board[x+1][y+1] == -king) defends = true;
		}
		
		//King defending horizontally and vertically (1 square only)
		if (x-1 >= 0) if (board[y][x-1] == -king) defends = true;
		if (x+1 <= 7) if (board[y][x+1] == -king) defends = true;
		if (y-1 >= 0) if (board[y-1][x] == -king) defends = true;
		if (y+1 <= 7) if (board[y+1][x] == -king) defends = true;
		
		return defends;	//Returns final variable true or false
	}
	/**
	 * Checks if a king has a square to run to
	 * pre: specific king, x and y coordinates of the king
	 * post: Boolean variable true or false
	 */
	public boolean checkRun(int king, int x, int y) {
		boolean run = false;	//Run is false until proven true
		
		//Upper left
		if (x-1 >= 0 && y-1 >= 0) if (!checkDefends(king, x-1, y-1) && board[y-1][x-1]*king <= 0) {	//If the diagonal exists and the king can run to it safely, checkmate is false
			run = true;
		}
		//Left (same logic as above)
		if (x-1 >= 0) if (!checkDefends(king, x-1, y) && board[y][x-1]*king <= 0) {
			run = true;
		}
		//Lower left (same logic as above)
		if (x-1 >= 0 && y+1 <= 7) if (!checkDefends(king, x-1, y+1) && board[y+1][x-1]*king <= 0) {
			run = true;
		}
		//Up (same logic as above)
		if (y-1 >= 0) if (!checkDefends(king, x, y-1) && board[y-1][x]*king <= 0) {
			run = true;
		}
		//Down (same logic as Up)
		if (y+1 <= 7) if (!checkDefends(king, x, y+1) && board[y+1][x]*king <= 0) {
			run = true;
		}
		//Upper right (same logic as Up)
		if (x+1 <= 7 && y-1 >= 0) if (!checkDefends(king, x+1, y-1) && board[y-1][x+1]*king <= 0) {
			run = true; 
		}
		//Right (same logic as Up)
		if (x+1 <= 7) if (!checkDefends(king, x+1, y) && board[y][x+1]*king <= 0) {
			run = true;
		}
		//Lower right (same logic as Up)
		if (x+1 <= 7 && y+1 <= 7) if (!checkDefends(king, x+1, y+1) && board[y+1][x+1]*king <= 0) {
			run = true;
		}
		
		return run;
	}
	/**
	 * Checks if a king is checkmated
	 * pre: specific king, x and y coordinates of the king
	 * post: Boolean variable true or false
	 */
	public boolean checkCheckmate(int king, int x, int y) {
		boolean checkmate = true;	//Checkmate is true until proven false
		if (!checkDefends(king, x, y)) {	//If the king is not in check, there is no checkmate
			checkmate = false;
		}
		if (checkRun(king, x, y)) checkmate = false;	//If the king can run, there is no checkmate
		
		return checkmate;	//Returns final variable true or false
	}
	/**
	 * Checks if a position is stalemate
	 * pre: specific king, x and y coordinates of king
	 * post: Boolean variable true or false
	 */
	public boolean checkStalemate(int king, int x, int y) {
		boolean stalemate = true;	//stalemate is true until proven false
		if (checkDefends(king, x, y)) {	//If the king is in check, there is no stalemate
			stalemate = false;
		}
		if (checkRun(king, x, y)) stalemate = false;	//if the king can run, there is no stalemate
		
		boolean left = true, right = true;	//Variables for checking if pawn can capture
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == king/100) {
					if (board[i-(king/100)][j] != 0) {
						if (j > 0) {
							if (board[i-(king/100)][j-1] == 0) {
								left = false;
							}
						}
						if (j < 7) {
							if (board[i-(king/100)][j+1] == 0) {
								right = false;
							}
						}
						if (left || right) stalemate = false; 	//If pawns can move, stalemate is false
					}
					else stalemate = false;	//If there are other pieces, there is no stalemate
				}
			}
		}
		return stalemate;	//Returns final variable true or false
	}
	/**
	 * Executes when a game is won
	 * pre: colour of winner and method of winning
	 * post: sets up the board for the next game
	 */
	public void win(String colour, String method) {
		ChessMain.stopClock();	//Stops the clock
		int win = JOptionPane.showConfirmDialog(this,"Rematch?", colour + " wins by " + method, JOptionPane.YES_NO_OPTION);	//Reports win and allows user to select rematch or not
		resetBoard(board);	//Resets the board
		repaint();	//Resets the visuals
		turn = 1;	//Resets turn
		piece = 0;	//Resets piece
		empassant = -1;	//Resets empassant
		wshort = true;	//Resets castling
		wlong = true;
		bshort = true;
		blong = true;
		ChessScore.fullReset();	//Resets ChessScore JPanel
		switch (win) {
		case 0:
			ChessMain.startClock();	//Restarst clock for next game
			ChessMain.resetClock();
			break;
		case 1:
			ChessMain.hideBoard();	//Returns to menu
			ChessMain.hideScore();
			ChessMain.showMenu();
			break;
		}
	}
	/**
	 * Executes when a game is drawn
	 * pre: method of drawing
	 * post: sets up the board for the next game
	 */
	public void draw(String method) {
		//Same logic as win
		int draw = JOptionPane.showConfirmDialog(this,"Rematch?", "Draw by " + method, JOptionPane.YES_NO_OPTION);
		resetBoard(board);
		repaint();
		turn = 1;
		piece = 0;
		empassant = -1;
		ChessScore.fullReset();
		ChessMain.stopClock();
		switch (draw) {
		case 0:
			ChessMain.startClock();
			ChessMain.resetClock();
			break;
		case 1:
			ChessMain.hideBoard();
			ChessMain.hideScore();
			ChessMain.showMenu();
			break;
		}
	}
	/**
	 * Records the coordinates of mouse pressed and the piece on the square
	 * pre: MouseEvent variable
	 * post: records variables
	 */
	public void mousePressed(MouseEvent event) {
		xPos = event.getX()/75;	//Gets x coordinate on the chessboard
		yPos = event.getY()/75;	//Gets y coordinate on the chessboard
		if (xPos >= 0 && xPos <= 7 && yPos >= 0 && yPos <= 7) piece = board[yPos][xPos];	//Gets the piece on that position
	}
	/**
	 * Records the coordinates of mouse released and determines if the move is legal
	 * pre: MouseEvent variable
	 * post: void
	 */
	public void mouseReleased(MouseEvent event) {
		newX = event.getX()/75;	//Gets the x coordinate on the chessboard
		newY = event.getY()/75;	//Gets the y coordinate on the chessboard
		if (newX >= 0 && newX <= 7 && newY >= 0 && newY <= 7) {	//Determines if the position is actually on the chessboard
			captured = board[newY][newX];	//Records the piece on the square
			if (checkLegal()) {	//Checks if the move is legal
				board[yPos][xPos] = 0;	//Changes the position of the piece
				board[newY][newX] = piece;
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if (board[i][j] == 100*turn) {
							cx = j;	//Stores king's x coordinate
							cy = i;	//Stores king's y coordinate
							break;
						}
					}
				}
				if (!checkDefends(100*turn, cx, cy)) {	//Checks if the player's king is in check before deciding if the move is legal
					repaint();	//Repaints the visuals
					ChessMain.increment(turn);	//Increases time if increment > 0
					turn *= -1;	//Switches turns
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							if (board[i][j] == 100*turn) {
								cx = j;
								cy = i;
								break;
							}
						}
					}
					ChessScore.recordMove(-turn, piece, captured, newX, newY, xPos, yPos, promoted, checkCheck(100*turn, cx, cy), checkCheckmate(100*turn, cx, cy), sCastle, lCastle);	//Records Chessmove on the score
					sCastle = false;	//Resets variables after recording the move
					lCastle = false;
					promoted = 0;
					if (checkCheckmate(100*turn, cx, cy)) {	//Checks if a player is checkmated
						switch (turn) {
						case -1:
							colour = "White";
							break;
						case 1:
							colour = "Black";
							break;
						}
						win(colour, "checkmate");	//A player wins if checkmate is true and the game ends
					}
					else if (checkStalemate(100*turn, cx, cy)) draw("stalemate");	//Checks if stalemate is true
				}
				else {	//If the move is illegal because the king is in check, the piece and captured piece values and returned to their original values
					board[yPos][xPos] = piece;
					board[newY][newX] = captured;
				}
			}
		}
	}
	//Necessary Methods to Implement MouseListener
	public void mouseClicked(MouseEvent event) {
	}
	public void mouseEntered(MouseEvent event) {
	}
	public void mouseExited(MouseEvent event) {
	}
}