import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;
import javax.sound.sampled.*;
import java.io.*;

class ChessGameSetting{
    private JFrame settingFrame;private JPanel settingPanel;
    private int whiteTimeLeft=300,blackTimeLeft=300;
    private int bonusTime = 0;private JLabel title;
    
    public ChessGameSetting(){
        initSettingWindow();
    }
    private void initSettingWindow(){
        settingFrame = new JFrame("CHESS BY HARIN THE GREAT");
        settingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settingFrame.setSize(400,300);
        settingFrame.setLayout(new BorderLayout());

        title = new JLabel("♕♔ CHESS ♚♛",SwingConstants.CENTER);
        title.setFont(new Font("Arial Unicode MS",Font.PLAIN,28));
        title.setOpaque(true);
        title.setBackground(Color.DARK_GRAY);
        title.setForeground(new Color(255, 215, 0)); // Gold color
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.DARK_GRAY));
        title.setAlignmentX(SwingConstants.CENTER);
        
        settingFrame.add(title,BorderLayout.PAGE_START);

        settingPanel = new JPanel(new GridLayout(3,1,10,10));
        settingPanel.setBackground(Color.BLACK);
        JButton setTime = new JButton("SET TIME DURATION");
        setTime.setBorderPainted(false);
        setTime.setOpaque(true);
        setTime.setFocusPainted(false);
        setTime.setBackground(new Color(30, 144, 255));setTime.setForeground(Color.WHITE);
        setTime.setAlignmentX(SwingConstants.CENTER);
        setTime.addActionListener(e-> showDurationMenu());

        JButton startButton = new JButton("START");
        startButton.setSize(100, 150);
        startButton.addActionListener(e->{
            settingFrame.dispose();
            new ChessGame(whiteTimeLeft,blackTimeLeft,bonusTime);
        });
        settingPanel.add(setTime);settingPanel.add(Box.createVerticalStrut(10));
        settingPanel.add(startButton);

        settingFrame.add(settingPanel,BorderLayout.CENTER);
        settingFrame.setLocationRelativeTo(null);
        settingFrame.setVisible(true);
    }
    private void showDurationMenu(){
        String[] opt = {"3 MIN RAPID","3 MIN RAPID (2s bonus)","5 MINUTES","10 MINUTES","15 MINUTES","15 MINUTES (10s bonus)","CUSTOM"};
        String c = (String)JOptionPane.showInputDialog(
            null,
            "SELECT GAME DURATION",
            "GAME DURATION",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opt,
            opt[1]
        );
        if(c!=null){
            switch(c){
                case "3 MIN RAPID":
                whiteTimeLeft=blackTimeLeft=180;
                break;
                case "3 MIN RAPID (2s bonus)":
                whiteTimeLeft=blackTimeLeft=180;
                bonusTime = 2;
                break;
                case "5 MINUTES":
                whiteTimeLeft=blackTimeLeft=300;
                break;
                case "10 MINUTES":
                whiteTimeLeft = blackTimeLeft = 600; // 10 minutes
                break;
                case "15 MINUTES":
                whiteTimeLeft = blackTimeLeft = 900; // 15 minutes
                break;
                case "15 MINUTES (10s bonus)":
                whiteTimeLeft=blackTimeLeft=900;
                bonusTime = 10;
                break;
                case "CUSTOM":
                showCustomDialogBox();
                break;
            }
            
        }
    }
    private void showCustomDialogBox(){
        JDialog d = new JDialog(settingFrame, "CUSTOM TIME SETTINGS", true);
        d.setLayout(new GridBagLayout());
        d.getContentPane().setBackground(Color.DARK_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel timeInMinLabel = createStyledLabel("GAME TIME (MINUTES):");
        gbc.gridx = 0;
        gbc.gridy = 0;
        d.add(timeInMinLabel, gbc);

        JSlider sliderInMin = createSlider(1, 60, 5);
        JTextField textInMin = createTextField(String.valueOf(sliderInMin.getValue()));
        textInMin.setPreferredSize(new Dimension(30, 35));
        gbc.gridx = 1;
        d.add(sliderInMin, gbc);
        gbc.gridx = 2;
        d.add(textInMin, gbc);

        sliderInMin.addChangeListener(e -> textInMin.setText(String.valueOf(sliderInMin.getValue())));
        textInMin.addActionListener(e -> syncSliderWithText(sliderInMin, textInMin, 1, 60));

        JLabel timeInSecLabel = createStyledLabel("GAME TIME (SECONDS):");
        gbc.gridx = 0;
        gbc.gridy = 1;
        d.add(timeInSecLabel, gbc);

        JSlider sliderInSec = createSlider(0, 59, 0);
        JTextField textInSec = createTextField(String.valueOf(sliderInSec.getValue()));
        textInSec.setPreferredSize(new Dimension(30,35));
        gbc.gridx = 1;
        d.add(sliderInSec, gbc);
        gbc.gridx = 2;
        d.add(textInSec, gbc);

        sliderInSec.addChangeListener(e -> textInSec.setText(String.valueOf(sliderInSec.getValue())));
        textInSec.addActionListener(e -> syncSliderWithText(sliderInSec, textInSec, 0, 59));

        JLabel bonusTimeLabel = createStyledLabel("BONUS TIME (SECONDS):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        d.add(bonusTimeLabel, gbc);

        JSlider bonusTimeSlider = createSlider(0, 30, 0);
        JTextField textBonusTime = createTextField(String.valueOf(bonusTimeSlider.getValue()));
        textBonusTime.setPreferredSize(new Dimension(30,35));
        gbc.gridx = 1;
        d.add(bonusTimeSlider, gbc);
        gbc.gridx = 2;
        d.add(textBonusTime, gbc);

        bonusTimeSlider.addChangeListener(e -> textBonusTime.setText(String.valueOf(bonusTimeSlider.getValue())));
        textBonusTime.addActionListener(e -> syncSliderWithText(bonusTimeSlider, textBonusTime, 0, 30));

        JButton confirm = createStyledButton("CONFIRM");
        confirm.addActionListener(e -> {
            whiteTimeLeft = blackTimeLeft = sliderInMin.getValue() * 60 + sliderInSec.getValue();
            bonusTime = bonusTimeSlider.getValue();
            d.dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        d.add(confirm, gbc);

        d.setSize(500, 300);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
        
    }
    private void syncSliderWithText(JSlider slider, JTextField textField, int min, int max) {
        try {
            int value = Integer.parseInt(textField.getText());
            if (value >= min && value <= max) {
                slider.setValue(value);
            } else {
                textField.setText(String.valueOf(slider.getValue()));
            }
        } catch (NumberFormatException e) {
            textField.setText(String.valueOf(slider.getValue())); // Reset on invalid input
        }
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(30, 144, 255)); // Dodger Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setOpaque(true);
        return button;
    }
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }
    private JSlider createSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing((max - min) / 5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(Color.DARK_GRAY);
        slider.setForeground(Color.WHITE);
        return slider;
    }
    private JTextField createTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Arial", Font.BOLD, 16));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        return textField;
    }
}

class CaptureHistoryBar extends JPanel{
    private JPanel whiteCaptures,blackCaptures;
    private JLabel whitePointsLabel,blackPointsLabel;
    public CaptureHistoryBar(){
        setLayout(new GridLayout(2, 1));
        
        whiteCaptures = new JPanel(new BorderLayout());
        whiteCaptures.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 4),
            "__WHITE_CAPTURES__",
            SwingConstants.CENTER,SwingConstants.TOP,
            new Font("SansSerif", Font.BOLD, 18),
            Color.WHITE
        ));
        whiteCaptures.setBackground(Color.DARK_GRAY);
        whiteCaptures.setForeground(Color.WHITE);
        whitePointsLabel = new JLabel("POINTS : 0", SwingConstants.CENTER);
        whitePointsLabel.setFont(new Font("Verdana", Font.PLAIN, 16));
        whitePointsLabel.setForeground(Color.WHITE);
        whiteCaptures.add(whitePointsLabel,BorderLayout.NORTH);

        JPanel whitePiecesPanel = new JPanel(new GridLayout(0, 8, 5, 5)); 
        whitePiecesPanel.setOpaque(false); 
        whiteCaptures.add(whitePiecesPanel, BorderLayout.CENTER);

        blackCaptures = new JPanel(new BorderLayout());
        blackCaptures.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK, 4), 
            "__BLACK_CAPTURES__",                           
            SwingConstants.CENTER, SwingConstants.TOP,      
            new Font("SansSerif", Font.BOLD, 18),           
            Color.BLACK
        ));
        blackPointsLabel = new JLabel("POINTS : 0",SwingConstants.CENTER);
        blackPointsLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));blackPointsLabel.setForeground(Color.BLACK);
        blackCaptures.add(blackPointsLabel,BorderLayout.NORTH);
        JPanel blackPiecesPanel = new JPanel(new GridLayout(0, 8, 5, 5)); 
        blackPiecesPanel.setOpaque(false); 
        blackCaptures.add(blackPiecesPanel, BorderLayout.CENTER);

        add(whiteCaptures);
        add(blackCaptures);
        setPreferredSize(new Dimension(300, 800));
    }
    public void updateCapturedPiece(String p,boolean isWhiteTurn){
        JPanel targetPanel = isWhiteTurn ? blackCaptures:whiteCaptures;
        JLabel pieceLabel = new JLabel(getSymbol(p));;
        pieceLabel.setFont(new Font("SansSerif", Font.PLAIN, 30));
        pieceLabel.setForeground(Character.isUpperCase(p.charAt(0))?Color.WHITE:Color.BLACK);
        JPanel targetPiecesPanel = (JPanel) targetPanel.getComponent(1); 
        targetPiecesPanel.add(pieceLabel);

        if(isWhiteTurn){
            int pt = Integer.parseInt(blackPointsLabel.getText().split(": ")[1]) + getPieceValue(p);
            blackPointsLabel.setText("POINTS : "+pt);
        }else{
            int pt = Integer.parseInt(whitePointsLabel.getText().split(": ")[1]) + getPieceValue(p);
            whitePointsLabel.setText("POINTS : "+pt);
        }
        targetPanel.revalidate();
        targetPanel.repaint();
    }
    private int getPieceValue(String p){
        switch (p.toLowerCase()) {
            case "p": return 1; 
            case "n": case "b": return 3; 
            case "r": return 5; 
            case "q": return 9; 
            default: return 0;
        }
    }
    public String getSymbol(String piece) {
        switch (piece) {
            case "P":return "♙";                
            case "p":return "♟";                
            case "R":return "♖";                
            case "r":return "♜";                
            case "N":return "♘";                
            case "n":return "♞";              
            case "B":return "♗";                
            case "b":return "♝";                
            case "Q":return "♕";                
            case "q":return "♛";                
            case "K":return "♔";                
            case "k":return "♚";                
            default:return "";                
        }      
    }
}

class MoveHistoryBar extends JPanel{
    private DefaultListModel<String> whiteMoves,blackMoves;
    private JList<String> whiteMoveHistory,blackMoveHistory;

    public MoveHistoryBar(){
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK , 4),
            "___MOVE_HISTORY___",
            SwingConstants.CENTER,SwingConstants.TOP,
            new Font("SansSerif", Font.BOLD, 10),
            Color.BLACK
        ));

        whiteMoves = new DefaultListModel<>();
        blackMoves = new DefaultListModel<>();
        whiteMoveHistory = new JList<>(whiteMoves);
        blackMoveHistory = new JList<>(blackMoves);
        JScrollPane whiteMoveScrollPane = new JScrollPane(whiteMoveHistory);
        whiteMoveScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
            "WHITE",
            SwingConstants.CENTER,SwingConstants.TOP,
            new Font("SansSerif",Font.PLAIN,12),
            Color.BLACK
        ));
        whiteMoveScrollPane.setForeground(Color.BLACK);
        JScrollPane blackMoveScrollPane = new JScrollPane(blackMoveHistory);
        blackMoveScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE,2),
            "BLACK",
            SwingConstants.CENTER,SwingConstants.TOP,
            new Font("SansSerif",Font.PLAIN,12),
            Color.BLACK
        ));
        blackMoveScrollPane.getViewport().setBackground(Color.DARK_GRAY);
        //blackMoveScrollPane.getViewport().setForeground(Color.WHITE);
        JPanel histPanel = new JPanel(new GridLayout(1, 2,5,0));
        histPanel.add(whiteMoveScrollPane);
        histPanel.add(blackMoveScrollPane);
        add(histPanel , BorderLayout.CENTER);

        setPreferredSize(new Dimension(180, 800));
    }
    public void addMove(String move,boolean isWhiteTurn){
        if(isWhiteTurn){
            whiteMoves.addElement(move);
        }else{
            blackMoves.addElement(move);
        }
    }
}

public class ChessGame {
    CaptureHistoryBar captureHistoryBar;MoveHistoryBar moveHistoryBar;
    private static final int BOARDSIZE = 8;
    private JButton[][] sq = new JButton[BOARDSIZE][BOARDSIZE];
    private JPanel boardPanel;
    private String[][] boardState = new String[BOARDSIZE][BOARDSIZE];
    private boolean isWhiteTurn = true;
    private int selectedRow = -1, selectedCol = -1;
    private int flag = 0;private int bonusTime;
    private boolean moveInProgress=false;
    private int[] move = new int[2];
    private int whiteTimeLeft ;private int blackTimeLeft ;    
    private JLabel wTimeLabel;private JLabel bTimeLabel;private JLabel turnLabel;       
    private boolean[][] possibleMoves = new boolean[8][8];
    private boolean whiteKingMoved=false;private boolean blackKingMoved=false;    
    private boolean whiteLeftRookMoved=false;private boolean whiteRightRookMoved=false;  
    private boolean blackLeftRookMoved=false;private boolean blackRightRookMoved=false;    
    private int enPassentRow = -1;private int enPassentCol = -1;private int kingInCheckCol=-1;private int kingInCheckRow=-1;
    private SwingWorker<Void,Integer> gameTimerWorker;
    private SwingWorker<Boolean, Void> checkmateWorker;
    private SwingWorker<Boolean, Void> stalemateWorker;
    private SwingWorker<Boolean, Void> isInCheckWorker;
    
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

    public ChessGame(int whiteTimeLeft,int blackTimeLeft,int bonusTime){
        JFrame frame = new JFrame("CHESS BY HARIN THE GREAT");
        this.whiteTimeLeft=whiteTimeLeft;
        
        this.blackTimeLeft = blackTimeLeft;
        
        this.bonusTime=bonusTime;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 900);
        boardPanel = new JPanel(new GridLayout(BOARDSIZE, BOARDSIZE)){
            @Override
            public void doLayout() {
                int size = Math.min(getWidth(), getHeight());
                int squareSize = size / BOARDSIZE;
                int offsetX = (getWidth() - size) / 2;
                int offsetY = (getHeight() - size) / 2;

                for (int row = 0; row < BOARDSIZE; row++) {
                    for (int col = 0; col < BOARDSIZE; col++) {
                        JButton button = sq[row][col];
                        int x = offsetX + col * squareSize;
                        int y = offsetY + row * squareSize;
                        button.setBounds(x, y, squareSize, squareSize);
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int size = Math.min(getParent().getWidth(), getParent().getHeight());
                return new Dimension(size, size);
            }
        };
        init();

        JPanel timePanel = new JPanel();

        timePanel.setLayout(new GridLayout(1,2));
        wTimeLabel = new JLabel("White : 05:00");
        wTimeLabel.setBackground(Color.WHITE);
        wTimeLabel.setFont(new Font("Arial",Font.BOLD,15));
        wTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bTimeLabel = new JLabel("Black : 05:00");
        bTimeLabel.setFont(new Font("Arial",Font.BOLD,15));
        bTimeLabel.setBackground(Color.BLACK);
        bTimeLabel.setOpaque(true);
        bTimeLabel.setForeground(Color.WHITE);
        bTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        timePanel.add(wTimeLabel);
        timePanel.add(bTimeLabel);
        updateTimer(bTimeLabel, blackTimeLeft);updateTimer(wTimeLabel, whiteTimeLeft);

        turnLabel = new JLabel("White's Turn");
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnLabel.setOpaque(true);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 20));
        turnLabel.setPreferredSize(new Dimension(0, 40));
        turnLabel.setBackground(Color.WHITE);
        turnLabel.setForeground(Color.BLACK);
        turnLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 4));

        captureHistoryBar = new CaptureHistoryBar();
        moveHistoryBar = new MoveHistoryBar();

        frame.add(timePanel, BorderLayout.SOUTH);
        frame.add(turnLabel, BorderLayout.NORTH);
        frame.add(captureHistoryBar, BorderLayout.EAST);
        frame.add(moveHistoryBar, BorderLayout.WEST);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    } 

    private void updateTurnLabel(){
        if(isWhiteTurn){
            turnLabel.setText("White's Turn");
            turnLabel.setBackground(Color.WHITE);
            turnLabel.setForeground(Color.BLACK);
        }else{
            turnLabel.setText("Black's Turn");
            turnLabel.setBackground(Color.BLACK);
            turnLabel.setForeground(Color.WHITE);
        }
    }
    private void flashTurnLabel(){
        javax.swing.Timer flashTimer = new javax.swing.Timer(200, new ActionListener() {
            int count = 0;
    
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count % 2 == 0) {
                    turnLabel.setBackground(isWhiteTurn ? Color.YELLOW : Color.DARK_GRAY);
                    turnLabel.setForeground(isWhiteTurn ? Color.BLACK : Color.WHITE);
                } else {
                    updateTurnLabel();
                }
                count++;
                if (count > 3) { 
                    ((javax.swing.Timer) e.getSource()).stop();
                }
            }
        });
        flashTimer.start();
    }

    private void startGame(){
        gameTimerWorker = new SwingWorker<Void,Integer>() {
            @Override
            protected Void doInBackground() throws Exception{
                while(whiteTimeLeft>0 && blackTimeLeft>0){
                    Thread.sleep(1000);
                    if(isWhiteTurn){
                        whiteTimeLeft--;
                        publish(whiteTimeLeft);
                    }else{
                        blackTimeLeft--;
                        publish(-blackTimeLeft);
                    }
                }
                return null;
            }
            protected void process(java.util.List<Integer> chunks){
                int lastVal = chunks.get(chunks.size()-1);
                if (lastVal > 0) {
                    updateTimer(wTimeLabel, lastVal);
                } else {
                    updateTimer(bTimeLabel, -lastVal);
                }
            }
            protected void done(){
                String winner = whiteTimeLeft > 0 ? "Black" : "White";
                endGame(winner + " wins by timeout.");
            }
        };
        gameTimerWorker.execute();
        startIsInCheckWorker(true);
        startCheckmateWorker(false);
        startStalemateWorker(false);
    }
    private void updateTimer(JLabel l,int timeLeft){
        int min = timeLeft / 60;
        int sec = timeLeft % 60;
        l.setText(String.format("%02d:%02d", min, sec));
    }

    private void init() {
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = 0; col < BOARDSIZE; col++) {
                JButton square = new JButton(){
                    @Override
                    public Dimension getPreferredSize() {
                        int size = Math.min(getParent().getWidth() / BOARDSIZE, getParent().getHeight() / BOARDSIZE);
                        return new Dimension(size, size);
                    }
                };
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
    public String getSymbol(String piece) {
        switch (piece) {
            case "P":return WHITE_PAWN;                
            case "p":return BLACK_PAWN;                
            case "R":return WHITE_ROOK;                
            case "r":return BLACK_ROOK;                
            case "N":return WHITE_KNIGHT;                
            case "n":return BLACK_KNIGHT;              
            case "B":return WHITE_BISHOP;                
            case "b":return BLACK_BISHOP;                
            case "Q":return WHITE_QUEEN;                
            case "q":return BLACK_QUEEN;                
            case "K":return WHITE_KING;                
            case "k":return BLACK_KING;                
            default:return "";                
        }      
    }

    private void squareClick(int row, int col) {
        if(flag==0){
            startGame();
            flag++;
        }

        if (selectedRow == -1 && selectedCol == -1) {
            if (isValidSelection(row, col)) {
                if(moveInProgress){
                    sq[move[0]][move[1]].setBackground(Color.WHITE);
                }
                selectedRow = row;
                selectedCol = col;
                sq[row][col].setBackground(Color.YELLOW);
                checkPossibleMoves(row, col);
                colorPossibleMoves(row,col);
                moveInProgress=true;
                move[0]=row;
                move[1]=col;
            }else{
                resetSelection();
            }
        } else {
            if(selectedCol==col && selectedRow ==row){
                resetSelection();
            }else if (isValidMove(selectedRow, selectedCol, row, col)) {
                System.out.println("Valid move: (" + selectedRow + ", " + selectedCol + ") -> (" + row + ", " + col + ")");
                movePiece(selectedRow, selectedCol, row, col);
                moveInProgress=false;
                isWhiteTurn = !isWhiteTurn;
                highlightKingInCheck();
                updateTurnLabel();
                flashTurnLabel();
                resetSelection();        
            } else {
                System.out.println("Invalid move: (" + selectedRow + ", " + selectedCol + ") -> (" + row + ", " + col + ")");
                flashInvalidMove(row, col);
                moveInProgress=false;
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(200); 
                        Thread.currentThread().interrupt();
                    }catch(Exception e){}
                        resetSelection(); 
                });
            }
                
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
                validPieceMove = isValidKingMove(fromRow, fromCol, toRow, toCol) || isValidCastling(fromRow,fromCol,toRow,toCol,true); 
                break;
            case "k": 
                validPieceMove = isValidKingMove(fromRow, fromCol, toRow, toCol) || isValidCastling(fromRow,fromCol,toRow,toCol,false);
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

    private boolean isValidCastling(int fromRow,int fromCol,int toRow,int toCol,boolean isWhiteTurn){
        if(Math.abs(fromCol-toCol)!=2 || fromRow!=toRow) return false;
        if(isWhiteTurn){
            if(whiteKingMoved || isInCheck(true))return false;              
            if(toCol == 6)return !whiteRightRookMoved && boardState[7][5].isEmpty() && boardState[7][6].isEmpty() && !isUnderAttack(7, 5, false);
            else if(toCol == 2)return !whiteLeftRookMoved && boardState[7][1].isEmpty() && boardState[7][2].isEmpty()&& boardState[7][3].isEmpty() && !isUnderAttack(7, 2, false) && !isUnderAttack(7, 3, false);    
        }else{
            if(blackKingMoved || isInCheck(false))return false;
            if(toCol == 6)return !blackRightRookMoved && boardState[0][5].isEmpty() && boardState[0][6].isEmpty() && !isUnderAttack(0, 5, true);
            else if(toCol == 2)return !blackLeftRookMoved && boardState[0][1].isEmpty() && boardState[0][2].isEmpty() && boardState[0][3].isEmpty() && !isUnderAttack(0, 2, true) && !isUnderAttack(0, 3, true);
        }
        return false;
    }

    private boolean isUnderAttack(int r,int c,boolean byWhite){
        if (r < 0 || r >= BOARDSIZE || c < 0 || c >= BOARDSIZE) {
            System.out.println("Invalid attack position: (" + r + ", " + c + ")");
            return false;
        }
        
        for(int i=0;i<BOARDSIZE;i++){
            for(int j=0;j<BOARDSIZE;j++){
                String a = boardState[r][c];
                if(!a.isEmpty() && (Character.isUpperCase(a.charAt(0))==byWhite)){
                    if(isValidMove(i, j, r, c)){
                        System.out.println("KING IS IN CHECK");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        if (fromCol == toCol && boardState[toRow][toCol].isEmpty()) {
            return toRow == fromRow + direction || (fromRow == (isWhite ? 6 : 1) && toRow == fromRow + 2 * direction);
        }
        if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction) {
    		if (!boardState[toRow][toCol].isEmpty() && (isWhite != Character.isUpperCase(boardState[toRow][toCol].charAt(0)))){
                return true;
            }
            if(toRow == enPassentRow && toCol==enPassentCol){
                return true;
            }
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
    	if (toRow < 0 || toRow >= BOARDSIZE || toCol < 0 || toCol >= BOARDSIZE) {
            System.out.println("King move out of bounds: toRow=" + toRow + ", toCol=" + toCol);
            return false;
        }
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
    
    private void playSound(String filePath){
        try{
            File soundFile = new File(filePath);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        }catch(UnsupportedAudioFileException|IOException|LineUnavailableException e){
            e.printStackTrace();
        }
    }
    
    private synchronized void movePiece (int fromRow, int fromCol, int toRow, int toCol) {
        System.out.println("Moving piece: (" + fromRow + ", " + fromCol + ") -> (" + toRow + ", " + toCol + ")");
        System.out.println("Piece being moved: " + boardState[fromRow][fromCol]);
        if (fromRow < 0 || fromRow >= BOARDSIZE || fromCol < 0 || fromCol >= BOARDSIZE ||toRow < 0 || toRow >= BOARDSIZE || toCol < 0 || toCol >= BOARDSIZE) {
            System.err.println("Invalid move: Out of bounds");
            return;
        }
        
        String piece = boardState[fromRow][fromCol];
        String captured = boardState[toRow][toCol];
        if(!captured.isEmpty()){
            captureHistoryBar.updateCapturedPiece(captured, isWhiteTurn);
        }
        boardState[fromRow][fromCol] = "";
        boardState[toRow][toCol] = piece;

        sq[fromRow][fromCol].setText("");
        sq[toRow][toCol].setText(getSymbol(piece));
        playSound("C:\\Users\\harin\\OneDrive\\Desktop\\ChessMove.wav");
        
        if (isWhiteTurn) {
            whiteTimeLeft += bonusTime;
            updateTimer(wTimeLabel, whiteTimeLeft);
        } else {
            blackTimeLeft += bonusTime;
            updateTimer(bTimeLabel, blackTimeLeft);
        }
        //EP start
        if(piece.equals("P")||piece.equals("p")){
            if(Math.abs(toRow-fromRow)==2){
                enPassentRow = fromRow + (toRow-fromRow)/2;
                enPassentCol=fromCol;
            }else if (toRow == enPassentRow && toCol == enPassentCol) {
                int capturedPawnRow = toRow + (piece.equals("P") ? 1 : -1);
                String  epCaptured = boardState[capturedPawnRow][toCol];
                if(!epCaptured.isEmpty()){
                    captureHistoryBar.updateCapturedPiece(epCaptured, isWhiteTurn);
                }
                boardState[capturedPawnRow][toCol] = "";
                sq[capturedPawnRow][toCol].setText("");

                playSound("C:\\Users\\harin\\OneDrive\\Desktop\\ChessMove.wav");
            }else{
                enPassentCol=-1;
                enPassentRow=-1;
            }
        }else{
            enPassentCol=-1;
            enPassentRow=-1;
        }
        //castling start
        if(piece.equals("K")){
            whiteKingMoved = true;
            if (Math.abs(toCol - fromCol) == 2) { 
                if (toCol == 6) {movePiece(7, 7, 7, 5);
                    playSound("C:\\Users\\harin\\OneDrive\\Desktop\\ChessMove.wav");} 
                else if (toCol == 2){ movePiece(7, 0, 7, 3); 
                    playSound("C:\\Users\\harin\\OneDrive\\Desktop\\ChessMove.wav");}
            }
        }else if(piece.equals("k")){
            blackKingMoved = true;
            if (Math.abs(toCol - fromCol) == 2) { 
                if (toCol == 6){ movePiece(0, 7, 0, 5);
                    playSound("C:\\Users\\harin\\OneDrive\\Desktop\\ChessMove.wav");} 
                else if (toCol == 2) {movePiece(0, 0, 0, 3); 
                    playSound("C:\\Users\\harin\\OneDrive\\Desktop\\ChessMove.wav");}
            }
        } else if (piece.equals("R")) {
                if (fromRow == 7 && fromCol == 0) whiteLeftRookMoved = true;
                if (fromRow == 7 && fromCol == 7) whiteRightRookMoved = true;
        } else if (piece.equals("r")) {
                if (fromRow == 0 && fromCol == 0) blackLeftRookMoved = true;
                if (fromRow == 0 && fromCol == 7) blackRightRookMoved = true;
        }   
        //castling end
		//pp start
		if ((piece.equals("P") && toRow == 0) || (piece.equals("p") && toRow == 7)) {
        	pawnPromotion(toRow, toCol, piece.equals("P"));
    	}
        String move = getSymbol(piece) +" "+String.format("%s%s -> %s%s", 
            (char)('a' + fromCol),8-fromRow,
            (char)('a' + toCol),8-toRow);
        moveHistoryBar.addMove(move, isWhiteTurn);
        //pp end
		startCheckmateWorker(!isWhiteTurn);
        startStalemateWorker(!isWhiteTurn);
        startIsInCheckWorker(isWhiteTurn);

        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                if(i == kingInCheckRow && j == kingInCheckCol){continue;}
                sq[i][j].setBackground((i+ j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                sq[i][j].setBorder(null);
                sq[i][j].setBorderPainted(false);
            }
        }      
                  
        //highlightKingInCheck();
        
        if (!isInCheck(true) && !isInCheck(false)) {
            if (kingInCheckRow != -1 && kingInCheckCol != -1) {
                sq[kingInCheckRow][kingInCheckCol].setBackground((kingInCheckRow + kingInCheckCol) % 2 == 0 ? Color.WHITE : Color.GRAY);
                sq[kingInCheckRow][kingInCheckCol].setBorder(null);
                sq[kingInCheckRow][kingInCheckCol].setBorderPainted(false);
            }
            kingInCheckRow = -1;
            kingInCheckCol = -1;
        }
        
    }

    private void startCheckmateWorker(boolean isWhite){
        checkmateWorker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return isCheckmate(isWhite);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        endGame((!isWhite ? "White" : "Black") + " wins by checkmate!");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        checkmateWorker.execute();
    }

    private void startStalemateWorker(boolean isWhite){
        stalemateWorker = new SwingWorker<Boolean,Void>() {
            @Override
            protected Boolean doInBackground(){
                return isStalemate(isWhite);
            }
            @Override
            protected void done(){
                try{
                    if(get()){
                        endGame("Stalemate! It's a draw.");
                    }
                }catch(InterruptedException |ExecutionException e){
                    e.printStackTrace();
                }
            }
        };
        stalemateWorker.execute();
    }

    private void startIsInCheckWorker(boolean isWhite) {
        isInCheckWorker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return isInCheck(isWhite);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        highlightKingInCheck();
                        System.out.println("IN CHECK");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        isInCheckWorker.execute();
    }

    private void flashInvalidMove(int row, int col) {
        Color originalColor = sq[row][col].getBackground();
        sq[row][col].setBackground(Color.RED);
        javax.swing.Timer flashTimer = new javax.swing.Timer(200, e -> {
            sq[row][col].setBackground(originalColor);
            ((javax.swing.Timer) e.getSource()).stop(); 
        });
        flashTimer.setRepeats(false); 
        flashTimer.start();
    }
	
	private void pawnPromotion(int toRow,int toCol,boolean isWhite){
		JDialog pd = new JDialog((JFrame)null,"PAWN PROMOTION TO:",true);
		pd.setLayout(new GridLayout(1,4));
		pd.setSize(400,100);
		
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
        queen.setFocusPainted(false);
    	JButton rook = new JButton(isWhite ? WHITE_ROOK + " Rook" : BLACK_ROOK + " Rook");
        rook.setFocusPainted(false);
    	JButton bishop = new JButton(isWhite ? WHITE_BISHOP + " Bishop" : BLACK_BISHOP + " Bishop");
        bishop.setFocusPainted(false);
    	JButton knight = new JButton(isWhite ? WHITE_KNIGHT + " Knight" : BLACK_KNIGHT + " Knight");
		knight.setFocusPainted(false);
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

    private void highlightKingInCheck(){
        //SwingUtilities.invokeLater(()->{
            kingInCheckRow = -1;
            kingInCheckCol = -1;
            for(int i=0;i<BOARDSIZE;i++){
                for(int j=0;j<BOARDSIZE;j++){
                    String p = boardState[i][j];
                    if (p.equals("K") && isUnderAttack(i, j, true)) {
                        System.out.println("White king is in check at: " + i + ", " + j);
                        kingInCheckRow = i;kingInCheckCol = j;
                        sq[i][j].setOpaque(true);
                        sq[i][j].setBorderPainted(true);
                        sq[i][j].setBackground(Color.RED);
                        sq[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); 
                    } else if (p.equals("k") && isUnderAttack(i, j, false)) {
                        System.out.println("Black king is in check at: " + i + ", " + j);
                        kingInCheckRow = i;kingInCheckCol = j;
                        sq[i][j].setOpaque(true);
                        sq[i][j].setBorderPainted(true);
                        sq[i][j].setBackground(Color.RED);
                        sq[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); 
                    } else if (!p.isEmpty() &&(p.equals("K") || p.equals("k"))) {
                        sq[i][j].setBorderPainted(false);
                        sq[i][j].setBorder(null);
                        sq[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                    }
                }
            }     
    }
        
	private boolean isInCheck(boolean isWhite) {
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
                    	return true;
                	}
            	}
        	}
    	}
    	return false;
	}

	private boolean isCheckmate(boolean isWhite) {
		if (!isInCheck(isWhite)) {
        	return false;
    	}
    	for (int row = 0; row < BOARDSIZE; row++) {
        	for (int col = 0; col < BOARDSIZE; col++) {
            	String piece = boardState[row][col];
            	if (!piece.isEmpty() && (isWhite == Character.isUpperCase(piece.charAt(0)))) {
                	for (int targetRow = 0; targetRow < BOARDSIZE; targetRow++) {
                    	for (int targetCol = 0; targetCol < BOARDSIZE; targetCol++) {
                        	if (isValidMove(row, col, targetRow, targetCol)) {
                            	String originalPiece = boardState[targetRow][targetCol];
                            	boardState[targetRow][targetCol] = piece;
                            	boardState[row][col] = "";

                            	boolean stillInCheck = isInCheck(isWhite);

                            	boardState[row][col] = piece;
                            	boardState[targetRow][targetCol] = originalPiece;

                            	if (!stillInCheck) {
                                	return false;
                            	}
                        	}
                    	}
                	}
            	}
        	}
    	}
    	return true;
	}
	
	private boolean isStalemate(boolean isWhite) {
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
	
    private void checkPossibleMoves(int fromRow,int fromCol){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                possibleMoves[i][j]=false;
            }
        }

        for(int i=0;i<8;i++){
            for(int j = 0; j< 8; j++){
                if(isValidMove(fromRow, fromCol, i, j)){
                    possibleMoves[i][j] = true;
                }
            }
        }
    }

    private void colorPossibleMoves(int fromRow,int fromCol){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(possibleMoves[i][j]){
                    if(!boardState[i][j].isEmpty() && Character.isUpperCase(boardState[fromRow][fromCol].charAt(0)) != Character.isUpperCase(boardState[i][j].charAt(0))){
                        sq[i][j].setBackground(Color.PINK);
                        sq[i][j].setBorderPainted(true);
                        sq[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }else{
                        sq[i][j].setBackground(new Color(204, 255, 102));
                        sq[i][j].setBorderPainted(true);
                        sq[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }    
                }else{
                    sq[i][j].setBackground((i+ j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                }
            }
        }
    }
	
	private void endGame(String message) {
        JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    	System.exit(0);
	}

    private void resetSelection() {
        if (selectedRow != -1 && selectedCol != -1) {
            sq[selectedRow][selectedCol].setBackground((selectedRow + selectedCol) % 2 == 0 ? Color.WHITE : Color.GRAY);
            sq[selectedRow][selectedCol].setBorder(null);
            sq[selectedRow][selectedCol].setBorderPainted(false);
        }
        selectedRow = -1;
        selectedCol = -1;

        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                if (i == kingInCheckRow && j == kingInCheckCol) {
                    continue;
                }
                sq[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                sq[i][j].setBorder(null);
                sq[i][j].setBorderPainted(false);
                possibleMoves[i][j] = false;
            }
        }
    }
    
    private boolean isOpponentPiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < 0 || toRow >= boardState.length || toCol < 0 || toCol >= boardState[0].length) {
            return false; 
        }
        String targetPiece = boardState[toRow][toCol];
        if (targetPiece.isEmpty()) {
            return true; 
        }
        return Character.isUpperCase(boardState[fromRow][fromCol].charAt(0)) != Character.isUpperCase(targetPiece.charAt(0));
    }
    public static void main(String[] args) {
        new ChessGameSetting();
    }
}
