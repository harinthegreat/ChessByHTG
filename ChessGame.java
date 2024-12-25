import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.TimerTask;


public class ChessGame {
    private static final int BOARDSIZE = 8;
    private JButton[][] sq = new JButton[BOARDSIZE][BOARDSIZE];
    private JPanel boardPanel;
    private String[][] boardState = new String[BOARDSIZE][BOARDSIZE];
    private boolean isWhiteTurn = true;
    private int selectedRow = -1, selectedCol = -1;
    private int flag = 0;
    private java.util.Timer gameTimer;
    //private Timer bTimer;
    private int whiteTimeLeft = 300;
    private int blackTimeLeft = 300;
    //private Timer currTimer;
    private JLabel wTimeLabel;
    private JLabel bTimeLabel;

    private static final String WHITE_PAWN = "♙";
    private static final String BLACK_PAWN = "♟";
    private static final String WHITE_ROOK = "♖";
    private static final String BLACK_ROOK = "♜";
    private static final String WHITE_KNIGHT = "♘";
    private static final String BLACK_KNIGHT = "♞";
    private static final String WHITE_BISHOP = "♗";
    private static final String BLACK_BISHOP = "♝";
    private static final String WHITE_QUEEN = "♕";
    private static final String BLACK_QUEEN = "♛";
    private static final String WHITE_KING = "♔";
    private static final String BLACK_KING = "♚";

    public ChessGame() {
        JFrame frame = new JFrame("CHESS BY HARIN THE GREAT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        boardPanel = new JPanel(new GridLayout(BOARDSIZE, BOARDSIZE));
        init();

        JPanel timePanel = new JPanel();
        timePanel.setLayout(new GridLayout(1,2));
        wTimeLabel = new JLabel("White : 05:00");
        wTimeLabel.setBackground(Color.WHITE);
        wTimeLabel.setFont(new Font("Arial",Font.BOLD,15));
        
        bTimeLabel = new JLabel("Black : 05:00");
        bTimeLabel.setFont(new Font("Arial",Font.BOLD,15));
        bTimeLabel.setBackground(Color.BLACK);
        bTimeLabel.setOpaque(true);
        bTimeLabel.setForeground(Color.WHITE);

        timePanel.add(wTimeLabel);
        timePanel.add(bTimeLabel);

        JLabel turnLabel = new JLabel("White's Turn");
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);

        frame.add(timePanel, BorderLayout.SOUTH);
        frame.add(turnLabel, BorderLayout.NORTH);
		
		javax.swing.Timer timer = new Timer(100, e -> turnLabel.setText(isWhiteTurn ? "White's turn" : "Black's turn"));
        timer.start();
		
        gameTimer = new java.util.Timer();
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void startGame(){
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                SwingUtilities.invokeLater(()->{
                    if(isWhiteTurn){
                        whiteTimeLeft--;
                        updateTimer(wTimeLabel,whiteTimeLeft);
                        if(whiteTimeLeft==0){
                            endGame("BLACK WINS BY TIMEOUT...");
                        }
                    }else{
                        blackTimeLeft--;
                        updateTimer(bTimeLabel,blackTimeLeft);
                        if(blackTimeLeft==0){
                            endGame("WHITE WINS BY TIMEOUT...");
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void updateTimer(JLabel l,int timeLeft){
        int min = timeLeft / 60;
        int sec = timeLeft % 60;
        l.setText(String.format("%02d:%02d", min, sec));
    }

    private void init() {
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = 0; col < BOARDSIZE; col++) {
                JButton square = new JButton();
                square.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                square.setOpaque(true);
                square.setBorderPainted(false);

                square.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
                final int r = row, c = col;
                square.addActionListener(e -> squareClick(r, c));

                sq[row][col] = square;
                boardPanel.add(square);
                boardState[row][col] = "";
				sq[row][col].setFocusPainted(false);
            }
        }
		
        setPieces();
    }

    private void setPieces() {
        for (int i = 0; i < BOARDSIZE; i++) {
            boardState[6][i] = "P"; 
            boardState[1][i] = "p";
            sq[6][i].setText(WHITE_PAWN);
            sq[1][i].setText(BLACK_PAWN);
        }

        String[] pieces = {"R", "N", "B", "Q", "K", "B", "N", "R"};
        for (int i = 0; i < pieces.length; i++) {
            boardState[7][i] = pieces[i]; 
            boardState[0][i] = pieces[i].toLowerCase(); 
            sq[7][i].setText(getSymbol(pieces[i]));
            sq[0][i].setText(getSymbol(pieces[i].toLowerCase()));
        }
    }

    private String getSymbol(String piece) {
        switch (piece) {
            case "P":
                return WHITE_PAWN;
            case "p":
                return BLACK_PAWN;
            case "R":
                return WHITE_ROOK;
            case "r":
                return BLACK_ROOK;
            case "N":
                return WHITE_KNIGHT;
            case "n":
                return BLACK_KNIGHT;
            case "B":
                return WHITE_BISHOP;
            case "b":
                return BLACK_BISHOP;
            case "Q":
                return WHITE_QUEEN;
            case "q":
                return BLACK_QUEEN;
            case "K":
                return WHITE_KING;
            case "k":
                return BLACK_KING;
            default:
                return "";
        }
        
        
    }

    private void squareClick(int row, int col) {
        if(flag==0){
            startGame();
            flag++;
        }

        if (selectedRow == -1 && selectedCol == -1) {
            if (isValidSelection(row, col)) {
                selectedRow = row;
                selectedCol = col;
                sq[row][col].setBackground(Color.YELLOW);
            }
        } else {
            if (isValidMove(selectedRow, selectedCol, row, col)) {
                movePiece(selectedRow, selectedCol, row, col);
                isWhiteTurn = !isWhiteTurn;

                
            } else {
                flashInvalidMove(row, col);
            }
            resetSelection();
        }
    }

    private boolean isValidSelection(int row, int col) {
        String piece = boardState[row][col];
        return !piece.isEmpty() && (isWhiteTurn == Character.isUpperCase(piece.charAt(0)));
    }

    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        String piece = boardState[fromRow][fromCol];

        boolean validPieceMove;
        switch (piece) {
            case "P": 
                validPieceMove = isValidPawnMove(fromRow, fromCol, toRow, toCol, true);
                break;
            case "p": 
                validPieceMove = isValidPawnMove(fromRow, fromCol, toRow, toCol, false);
                break;
            case "R": 
            case "r": 
                validPieceMove = isValidRookMove(fromRow, fromCol, toRow, toCol);
                break;
            case "N": 
            case "n": 
                validPieceMove = isValidKnightMove(fromRow, fromCol, toRow, toCol);
                break;
            case "B": 
            case "b": 
                validPieceMove = isValidBishopMove(fromRow, fromCol, toRow, toCol);
                break;
            case "Q": 
            case "q": 
                validPieceMove = isValidQueenMove(fromRow, fromCol, toRow, toCol);
                break;
            case "K": 
            case "k": 
                validPieceMove = isValidKingMove(fromRow, fromCol, toRow, toCol);
                break;
            default:
                validPieceMove = false;
    }

        if (!validPieceMove) {
            return false;
        }

        String originalPiece = boardState[toRow][toCol];
        boardState[toRow][toCol] = boardState[fromRow][fromCol];
        boardState[fromRow][fromCol] = "";

        boolean stillInCheck = isInCheck(isWhiteTurn);

        boardState[fromRow][fromCol] = boardState[toRow][toCol];
        boardState[toRow][toCol] = originalPiece;

        return !stillInCheck;
    }


    private boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        if (fromCol == toCol && boardState[toRow][toCol].isEmpty()) {
            return toRow == fromRow + direction || (fromRow == (isWhite ? 6 : 1) && toRow == fromRow + 2 * direction);
        }
        if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction) {
    		return !boardState[toRow][toCol].isEmpty() && (isWhite != Character.isUpperCase(boardState[toRow][toCol].charAt(0)));
		}

        return false;
    }

    private boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow != toRow && fromCol != toCol) {
            return false;
        }
        if (!isPathClear(fromRow, fromCol, toRow, toCol)) {
            return false; 
        }
        return isOpponentPiece(fromRow, fromCol, toRow, toCol);
    }

    private boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false; 
        }
        return isOpponentPiece(fromRow, fromCol, toRow, toCol);
    }

    private boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) {
            return false; 
        }
        
        if (!isPathClear(fromRow, fromCol, toRow, toCol)) {
            return false; 
        }
    
        String destinationPiece = boardState[toRow][toCol];
        return destinationPiece.isEmpty() || (Character.isUpperCase(destinationPiece.charAt(0)) != Character.isUpperCase(boardState[fromRow][fromCol].charAt(0)));
    }

    private boolean isValidQueenMove(int fromRow, int fromCol, int toRow, int toCol) {
        boolean validRookMove = (fromRow == toRow || fromCol == toCol) && isPathClear(fromRow, fromCol, toRow, toCol);
        boolean validBishopMove = (Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol)) && isPathClear(fromRow, fromCol, toRow, toCol);
        if (!(validRookMove || validBishopMove)) {
            return false; 
        }
        return isOpponentPiece(fromRow, fromCol, toRow, toCol);
    }

    private boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
    	if (Math.abs(toRow - fromRow) > 1 || Math.abs(toCol - fromCol) > 1) {
            return false; 
        }
        return isOpponentPiece(fromRow, fromCol, toRow, toCol);
	}

    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int stepRow = Integer.compare(toRow, fromRow);
        int stepCol = Integer.compare(toCol, fromCol);

        for (int r = fromRow + stepRow, c = fromCol + stepCol; r != toRow || c != toCol; r += stepRow, c += stepCol) {
            if (!boardState[r][c].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void movePiece (int fromRow, int fromCol, int toRow, int toCol) {
        String piece = boardState[fromRow][fromCol];
        boardState[fromRow][fromCol] = "";
        boardState[toRow][toCol] = piece;

        sq[fromRow][fromCol].setText("");
        sq[toRow][toCol].setText(getSymbol(piece));
		
		if ((piece.equals("P") && toRow == 0) || (piece.equals("p") && toRow == 7)) {
        	pawnPromotion(toRow, toCol, piece.equals("P"));
    	}
		
    	if (isCheckmate(!isWhiteTurn)) {
            System.out.println("Checkmate detected! Ending the game...");
        	endGame((isWhiteTurn ? "White" : "Black") + " wins by checkmate!");
    	} else if (isStalemate(isWhiteTurn)) {
        	endGame("Stalemate! It's a draw.");
    	}
    }

    private void flashInvalidMove(int row, int col) {
        Color originalColor = sq[row][col].getBackground();
        sq[row][col].setBackground(Color.RED);
        Timer t = new Timer(100, e -> sq[row][col].setBackground(originalColor));
        t.setRepeats(false);
        t.start();
    }
	
	private void pawnPromotion(int toRow,int toCol,boolean isWhite){
		JDialog pd = new JDialog((JFrame)null,"PAWN PROMOTION TO:",true);
		pd.setLayout(new GridLayout(1,4));
		pd.setSize(400,100);
		Font promotionFont = new Font("Segoe UI Symbol", Font.BOLD, 24);
		
		ActionListener promoListener = e->{
			String newPiece;
			String newSymbol;
			JButton src = (JButton)e.getSource();
			String c=src.getText();
			
			if (c.contains("Queen")) {
            	newPiece = isWhite ? "Q" : "q";
            	newSymbol = isWhite ? WHITE_QUEEN : BLACK_QUEEN;
        	} else if (c.contains("Rook")) {
            	newPiece = isWhite ? "R" : "r";
            	newSymbol = isWhite ? WHITE_ROOK : BLACK_ROOK;
        	} else if (c.contains("Bishop")) {
            	newPiece = isWhite ? "B" : "b";
            	newSymbol = isWhite ? WHITE_BISHOP : BLACK_BISHOP;
        	} else { 
            	newPiece = isWhite ? "N" : "n";
            	newSymbol = isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
        	}
			
			boardState[toRow][toCol]=newPiece;
			sq[toRow][toCol].setText(newSymbol);
			
			pd.dispose();
		};
		
		JButton queen = new JButton(isWhite ? WHITE_QUEEN + " Queen" : BLACK_QUEEN + " Queen");
    	JButton rook = new JButton(isWhite ? WHITE_ROOK + " Rook" : BLACK_ROOK + " Rook");
    	JButton bishop = new JButton(isWhite ? WHITE_BISHOP + " Bishop" : BLACK_BISHOP + " Bishop");
    	JButton knight = new JButton(isWhite ? WHITE_KNIGHT + " Knight" : BLACK_KNIGHT + " Knight");
		  
		queen.addActionListener(promoListener);
		rook.addActionListener(promoListener);
		bishop.addActionListener(promoListener);
		knight.addActionListener(promoListener);
		
		pd.add(queen);
		pd.add(rook);
		pd.add(bishop);
		pd.add(knight);
		
		pd.setLocationRelativeTo(null); 
    	pd.setVisible(true);
	}
	
	private boolean isInCheck(boolean isWhite) {
		System.out.println("in check");
    	int kingRow = -1, kingCol = -1;

    	for (int row = 0; row < BOARDSIZE; row++) {
        	for (int col = 0; col < BOARDSIZE; col++) {
            	String piece = boardState[row][col];
            	if ((isWhite && "K".equals(piece)) || (!isWhite && "k".equals(piece))) {
                	kingRow = row;
                	kingCol = col;
                	break;
            	}
        	}
    	}

    	for (int row = 0; row < BOARDSIZE; row++) {
        	for (int col = 0; col < BOARDSIZE; col++) {
            	String piece = boardState[row][col];
            	if (!piece.isEmpty() && (isWhite != Character.isUpperCase(piece.charAt(0)))) {
                	if (isValidMove(row, col, kingRow, kingCol)) {
						System.out.println("King is under attack by piece at " + row + "," + col);
                    	return true;
                	}
            	}
        	}
    	}
    	return false;
	}

	private boolean isCheckmate(boolean isWhite) {
		if (!isInCheck(isWhite)) {
        	System.out.println("King is not in check, so no checkmate.");
        	return false;
    	}

    	System.out.println("King is in check. Checking for possible escape...");

    	for (int row = 0; row < BOARDSIZE; row++) {
        	for (int col = 0; col < BOARDSIZE; col++) {
            	String piece = boardState[row][col];
            	if (!piece.isEmpty() && (isWhite == Character.isUpperCase(piece.charAt(0)))) {
                	for (int targetRow = 0; targetRow < BOARDSIZE; targetRow++) {
                    	for (int targetCol = 0; targetCol < BOARDSIZE; targetCol++) {
                        	if (isValidMove(row, col, targetRow, targetCol)) {
                            	System.out.println("Testing move: " + piece + " from " + row + "," + col + " to " + targetRow + "," + targetCol);

                            	String originalPiece = boardState[targetRow][targetCol];
                            	boardState[targetRow][targetCol] = piece;
                            	boardState[row][col] = "";

                            	boolean stillInCheck = isInCheck(isWhite);

                            	boardState[row][col] = piece;
                            	boardState[targetRow][targetCol] = originalPiece;

                            	if (!stillInCheck) {
                                	System.out.println("Move escapes check: " + piece + " to " + targetRow + "," + targetCol);
                                	return false;
                            	}
                        	}
                    	}
                	}
            	}
        	}
    	}

    	System.out.println("No valid moves found. It's checkmate.");
    	return true;
	}
	
	private boolean isStalemate(boolean isWhite) {
		System.out.println("in stalemate");
    	if (isInCheck(isWhite)) return false;

    	for (int row = 0; row < BOARDSIZE; row++) {
        	for (int col = 0; col < BOARDSIZE; col++) {
            	if (!boardState[row][col].isEmpty() && (isWhite == Character.isUpperCase(boardState[row][col].charAt(0)))) {
                	for (int targetRow = 0; targetRow < BOARDSIZE; targetRow++) {
                    	for (int targetCol = 0; targetCol < BOARDSIZE; targetCol++) {
                        	String originalPiece = boardState[targetRow][targetCol];
                        	String movingPiece = boardState[row][col];

                        	boardState[targetRow][targetCol] = movingPiece;
                        	boardState[row][col] = "";

                        	boolean validMove = !isInCheck(isWhite);

                        	boardState[row][col] = movingPiece;
                        	boardState[targetRow][targetCol] = originalPiece;

                        	if (validMove) {
                            	return false;
                        	}
                    	}
                	}
            	}
        	}
    	}
    	return true;
	}
	
	
	private void endGame(String message) {
    	JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    	System.exit(0);
	}

    private void resetSelection() {
        if (selectedRow != -1 && selectedCol != -1) {
            sq[selectedRow][selectedCol].setBackground((selectedRow + selectedCol) % 2 == 0 ? Color.WHITE : Color.GRAY);
        }
        selectedRow = -1;
        selectedCol = -1;
    }

    private boolean isOpponentPiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (boardState[toRow][toCol].isEmpty()) {
            return true; 
        }
        return Character.isUpperCase(boardState[fromRow][fromCol].charAt(0)) != Character.isUpperCase(boardState[toRow][toCol].charAt(0));
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGame::new);
    }
}
