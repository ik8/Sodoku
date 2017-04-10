import sun.java2d.pipe.SpanShapeRenderer;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


/**
 * Created by ikosteniov on 3/4/2017.
 */
public class Board extends JFrame implements Runnable, Serializable{

    public enum Levels{
        EASY(30),
        INTERMIDIATE(41),
        HARD(60),
        SOLVER(81);

        private int number;
        Levels(int i) { number = i; }

        public int getNumber() {
            return number;
        }
    }

    private int board_size = 9;
    private int window_x = 800;
    private int window_y = 800;
    private int level;
    private Levels level_description;
    private JPanel sections[][];
    private JPanel main;
    private JPanel header;
    private JPanel board_panel;
    private JButton clue;
    private Icon numbers[][];
    private Thread thread;
    private int question_marks = 0;
    private Counter timer  = new Counter();
    private int clue_left;
    private Timer SimpleTimer;
    private Sudok sudoku = new Sudok();
    private int[][] players_board = new int[board_size][board_size];
    private boolean board_flag = false;
    private boolean gameloaded = false;
    private int[][] loaded_puzzle_board = new int[9][9];
    private JMenuItem save_button;





    public Board() {

        createMenuBar();
        setSize(window_x, window_y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void createMenuBar() {
        JMenuBar menuBar;
        // Create & set the menu bar.
        menuBar = new JMenuBar();
        // Sets menuBar to JFrame
        setJMenuBar(menuBar);

        //Create a menu.

        JMenu menu = new JMenu("File");
        // set shortcut using setMnemonic method, uses the ALT mask
        menu.setMnemonic(KeyEvent.VK_F);// Load with Alt+F
        // add menu to menuBar
        menuBar.add(menu);

        JMenuItem menuItem;

        save_button = new JMenuItem("  Save game",new ImageIcon("Star Trek.JPG"));
        save_button.setToolTipText("Click here to save your game");
        save_button.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        save_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(SimpleTimer!=null && level_description != Levels.SOLVER)
                    SimpleTimer.stop();
                saveGame();
                if(SimpleTimer!=null && level_description != Levels.SOLVER)
                    SimpleTimer.start();

            }
        });
        save_button.setEnabled(false);
        menu.add(save_button);
        menu.addSeparator();
        menuItem = new JMenuItem("  Load game",new ImageIcon("Star Trek.JPG"));
        menuItem.setToolTipText("Click here to load your game");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(SimpleTimer!=null && level_description != Levels.SOLVER)
                    SimpleTimer.stop();
                loadGame();
                if(SimpleTimer!=null && level_description != Levels.SOLVER)
                    SimpleTimer.start();
            }
        });
        menu.add(menuItem);
        menu.addSeparator();

		/* Create some JMenuItems */

        menu = new JMenu("Play levels");
        // set shortcut using setMnemonic method, uses the ALT mask
        menu.setMnemonic(KeyEvent.VK_P);// Load with Alt+F
        // add menu to menuBar
        menuBar.add(menu);

        menuItem = new JMenuItem("  Easy",new ImageIcon("Star Trek.JPG"));
        menuItem.setToolTipText("Click For a New Game");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                if(board_flag)
                    newGUIBoardDisplay(Levels.EASY);
                else {
                    level = Levels.EASY.getNumber();
                    clue_left = level / 9;
                    level_description = Levels.EASY;
                    initBoard();
                    board_flag = true;
                }

            }
        });
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("  Intermidiate",new ImageIcon("Star Trek.JPG"));
        menuItem.setToolTipText("Click For a New Game");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(board_flag)
                    newGUIBoardDisplay(Levels.INTERMIDIATE);
                else {
                    level = Levels.INTERMIDIATE.getNumber();
                    clue_left = level / 9;
                    level_description = Levels.INTERMIDIATE;
                    initBoard();
                    board_flag = true;
                }
            }
        });
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("  Hard",new ImageIcon("Star Trek.JPG"));
        menuItem.setToolTipText("Click For a New Game");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(board_flag)
                    newGUIBoardDisplay(Levels.HARD);
                else {
                    level = Levels.HARD.getNumber();
                    clue_left = level / 9;
                    level_description = Levels.HARD;
                    initBoard();
                    board_flag = true;
                }
            }
        });
        menu.add(menuItem);
        menu.addSeparator();

        menuItem = new JMenuItem("  Solver",new ImageIcon("Star Trek.JPG"));
        menuItem.setToolTipText("Click to check the solver");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                level = Levels.SOLVER.getNumber();
                clue_left = level / 9;
                level_description = Levels.SOLVER;
                if(board_flag) {
                    gameFinished();
                    newGUIBoardDisplay(Levels.SOLVER);


                }

                else {

                    initBoard();
                    board_flag = true;
                    newGUIBoardDisplay(Levels.SOLVER);
                }
            }
        });
        menu.add(menuItem);
        menu.addSeparator();


    }

    public void initBoard() { // Builds the graphical board
        save_button.setEnabled(true);
        if(!board_flag) {
            main = new JPanel();
            main.setLayout(new BorderLayout());
        }
        header = new JPanel();
        JLabel time = new JLabel(timer.FormattedTime());
        time.setFont(new Font("Tahoma", Font.BOLD, 24));
        header.setLayout(new FlowLayout(0, window_x / 20, 0));
        header.add(time);
        JLabel level = new JLabel("Level: " + this.level_description);
        level.setFont(new Font("Tahoma", Font.PLAIN, 24));
        header.add(level);
        clue = new JButton();
        clue.setFont(new Font("Tahoma", Font.BOLD, 24));
        clue.setText("Click for a clue (" + clue_left + ")");
        clue.setForeground(Color.WHITE);
        clue.setFocusPainted(false);
        clue.setBackground(Color.BLUE);
        if(this.level == Levels.SOLVER.getNumber()) {
            clue.setText("Solve Sudoku");
            clue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SimpleTimer = new Timer(1000, new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            timer.AddSecond();
                            time.setText(timer.FormattedTime());

                        }
                    });
                    SimpleTimer.start();

                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            if(players_board[i][j] != 0)
                                sudoku.getPuzzle_board()[i][j] = players_board[i][j];
                            else
                                sudoku.getPuzzle_board()[i][j] = 0;
                        }
                    }
                    if(sudoku.validPuzzle()==0) {
                        SimpleTimer.stop();

                        JOptionPane.showMessageDialog(null, "Board can not be solved.");
                    }
                    else {
                        sudoku.solveSudokuPazzle();
                        SimpleTimer.stop();
                        start();
                        sudoku.printBoard(sudoku.getSolve_board());
                        JOptionPane.showMessageDialog(null, "Board was solved in " + timer.FormattedTime() + " \uD83D\uDE03 ");
                        sudoku = new Sudok();
                        gameFinished();
                    }



                }
            });
        }
        else {
            if(!gameloaded) {
                sudoku = new Sudok();
                sudoku.createFullBoard(0,0);
                sudoku.createSudokuPazzle(this.level);
                sudoku.solveSudokuPazzle();
                sudoku.printBoard(sudoku.getFull_board());
                initPlayersBoard();
            }

            SimpleTimer = new Timer(1000, new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    timer.AddSecond();
                    time.setText(timer.FormattedTime());

                }
            });
            SimpleTimer.start();
            clue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(clue_left > 0) {
                        giveClue(clue);
                    }
                    clue.setText("Click for a clue ( " + clue_left + " )");

                }
            });
        }

        header.add(clue);
        main.add(header, BorderLayout.NORTH);

        sections = new JPanel[board_size / 3][board_size / 3];
        ImageIcon icon;
        board_panel = new JPanel();
        board_panel.setLayout(new GridLayout(board_size / 3, board_size / 3, 5, 5));
        board_panel.setBackground(Color.black);
        numbers = new Icon[board_size][board_size];
        for (int i = 0; i < board_size; i++) {
            numbers[i] = new Icon[board_size];
        }
        for (int i = 0; i < board_size; i++) {
            numbers[i] = new Icon[board_size];
            for (int j = 0; j < board_size; j++) {
                if(sudoku.getPuzzle_board()[i][j]!=0)
                    icon = new ImageIcon(Integer.toString(players_board[i][j]) + "-black.png");
                else
                    icon = new ImageIcon("question_mark.jpg");
                Image img = icon.getImage();
                numbers[i][j] = new Icon(img);
                numbers[i][j].setBorder(new LineBorder(Color.WHITE));
                numbers[i][j].addActionListener(new AL(i,j));
                if(sudoku.getPuzzle_board()[i][j]==0)
                    numbers[i][j].addKeyListener(new KL(i, j));


            }
        }

        for (int i = 0; i < board_size; i+=3) {
            sections[i/3] = new JPanel[board_size / 3];
            for (int j = 0; j < board_size; j+=3) {
                sections[i/3][j/3] = new JPanel();
                sections[i/3][j/3].setLayout(new GridLayout(3,3));
                putInGrid(sections[i/3][j/3], i, j);
                board_panel.add(sections[i/3][j/3]);
            }
        }
        main.add(board_panel, BorderLayout.CENTER);
        add(main);
    }

    public void newGUIBoardDisplay(Levels x) {

        level = x.getNumber();
        level_description = x;
        clue_left = level / 9;
        main.remove(header);
        main.remove(board_panel);
        board_panel = new JPanel();
        header = new JPanel();
        if(SimpleTimer != null) {
            SimpleTimer.stop();
            timer = new Counter();
        }
        if(level_description == Levels.SOLVER) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    players_board[i][j] = 0;
                    ImageIcon icon;
                    icon = new ImageIcon("question_mark.jpg");
                    Image img = icon.getImage();
                    numbers[i][j] = new Icon(img);
                    board_panel.revalidate();
                    board_panel.repaint();
                }
            }
        }
        sudoku = new Sudok();
        initBoard();
        repaint();

    }

    public void putInGrid(JPanel panel, int i, int j) {

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 3; l++) {
                panel.add(numbers[i+k][j+l]);
            }
        }

    }
    class AL implements ActionListener {

        private int row;
        private int col;

        public AL(int row, int col) {

            this.row = row;
            this.col = col;

        }

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < board_size; i++) {
                for (int j = 0; j < board_size; j++) {

                    numbers[i][j].setBorder(new LineBorder(Color.WHITE));
                }
            }

            numbers[row][col].setBorder(new LineBorder(Color.ORANGE, 5));
            setFocusable(true);

        }

    }

    class KL extends KeyAdapter {

        private int row;
        private int col;

        public KL(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_I)
                newGUIBoardDisplay(Levels.HARD);
            if(e.getKeyCode() == KeyEvent.VK_D && sudoku.getPuzzle_board()[row][col] == 0) {
                players_board[row][col] = 0;
                Image img;
                ImageIcon icon = new ImageIcon("question_mark.jpg");
                img = icon.getImage();
                numbers[row][col].setImg(img);
                repaint();
            }
            if(level_description == Levels.SOLVER) {
                if(e.getKeyCode() <= KeyEvent.VK_9 && e.getKeyCode() >= KeyEvent.VK_0) {
                    Image img;
                    ImageIcon icon = new ImageIcon(Integer.toString(e.getKeyCode() - '0') + "-black.png");
                    img = icon.getImage();
                    numbers[row][col].setImg(img);
                    players_board[row][col] = e.getKeyCode() - '0';
                    repaint();

                }

            }
            else if(e.getKeyCode() <= KeyEvent.VK_9 && e.getKeyCode() >= KeyEvent.VK_0) {
                if(sudoku.getPuzzle_board()[row][col] == 0) {
                    Image img;
                    ImageIcon icon = new ImageIcon(Integer.toString(e.getKeyCode() - '0') + ".png");
                    img = icon.getImage();
                    numbers[row][col].setImg(img);
                    players_board[row][col] = e.getKeyCode() - '0';
                    repaint();
                    if(CheckWinner()) {
                        SimpleTimer.stop();
                        JOptionPane.showMessageDialog(null, "You won in: " + timer.FormattedTime());
                        gameFinished();
                    }
                    else if(sumBoard()){
                        JOptionPane.showMessageDialog(null, "Wrong Solution");

                    }

                }

            }

        }
    }




    public void giveClue(JButton clue) {
        boolean clue_given = false;
        Image img;
        do {
            Random random = new Random();
            int number1 = random.nextInt(board_size);
            int number2 = random.nextInt(board_size);
            if(sudoku.getPuzzle_board()[number1][number2] == 0 && players_board[number1][number2]==0) {
                ImageIcon icon = new ImageIcon(Integer.toString(sudoku.getFull_board()[number1][number2]) + "-black.png");
                img = icon.getImage();
                numbers[number1][number2].setImg(img);
                repaint();
                clue_given = true;
                clue_left--;
                sudoku.getPuzzle_board()[number1][number2] = sudoku.getFull_board()[number1][number2];
                players_board[number1][number2] = sudoku.getPuzzle_board()[number1][number2];
                question_marks--;
            }
        } while(!clue_given);
        if(question_marks == 0)
            clue.setEnabled(false);
    }

    public void saveGame() {
        try{  // Catch errors in I/O if necessary.
            JFileChooser c = new JFileChooser();
            int r = c.showDialog(Board.this, "Create file to save object");
            if(r == JFileChooser.APPROVE_OPTION)
            {
                File f = c.getSelectedFile();
                // Open a file to write to, named line.sav.
                FileOutputStream saveFile=new FileOutputStream(f);

                // Create an ObjectOutputStream to put objects into save file.
                ObjectOutputStream save = new ObjectOutputStream(saveFile);

                // Now we do the save.
                save.writeObject(level_description);
                save.writeObject(timer.getSeconds());
                save.writeObject(timer.getMinutes());
                save.writeObject(sudoku.getPuzzle_board());
                save.writeObject(players_board);
                save.writeObject(board_flag);
                // Close the file.
                save.close(); // This also closes saveFile.
            }
        }catch(Exception exc){
            exc.printStackTrace(); // If there was an error, print the info.
        }


    }

    public void loadGame() {
        try{
            // Open file to read from, named SavedObj.sav.
            JFileChooser c = new JFileChooser();
            int r = c.showDialog(Board.this, "Open file to set object");
            if(r == JFileChooser.APPROVE_OPTION){
                File f = c.getSelectedFile();
                FileInputStream loadFile = new FileInputStream(f);

                // Create an ObjectInputStream to get objects from save file.
                ObjectInputStream load = new ObjectInputStream(loadFile);

                // Now we do the restore.
                // readObject() returns a generic Object, we cast those back
                // into their original class type.
                // For primitive types, use the corresponding reference class.
                initBoard();
                SimpleTimer.stop();
                level_description = (Board.Levels) load.readObject();
                timer = new Counter();
                timer.setSeconds((int) load.readObject());
                timer.setMinutes((int) load.readObject());
                loaded_puzzle_board = (int[][]) load.readObject();
                players_board = (int[][]) load.readObject();
                board_flag = (boolean) load.readObject();
                loadGUIGame();

                // Close the file.
                load.close(); // This also closes saveFile.
            }
        } catch(Exception exc){
            exc.printStackTrace(); // If there was an error, print the info.
        }
    }

    public void loadGUIGame() {
        gameFinished();
        sudoku = new Sudok();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sudoku.getPuzzle_board()[i][j] = loaded_puzzle_board[i][j];
            }
        }
        sudoku.solveSudokuPazzle();
        gameloaded = true;
        level = level_description.getNumber();
        initBoard();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(sudoku.getPuzzle_board()[i][j] == 0 && players_board[i][j]!=0) {
                    Image img;
                    ImageIcon icon;
                    if(level_description == Levels.SOLVER)
                         icon = new ImageIcon(Integer.toString(players_board[i][j]) + "-black.png");
                    else
                        icon = new ImageIcon(Integer.toString(players_board[i][j]) + ".png");
                    img = icon.getImage();
                    numbers[i][j].setImg(img);
                    repaint();
                }
                sudoku.getFull_board()[i][j] = sudoku.getSolve_board()[i][j];
            }
        }
        repaint();
        gameloaded = false;
    }
    public void gameFinished() {
        save_button.setEnabled(false);
        main.removeAll();
        numbers = new Icon[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ImageIcon icon;
                icon = new ImageIcon("question_mark.jpg");
                Image img = icon.getImage();
                numbers[i][j] = new Icon(img);
                numbers[i][j].setBorder(new LineBorder(Color.WHITE));
                numbers[i][j].addActionListener(new AL(i,j));
                if(sudoku.getPuzzle_board()[i][j]==0)
                    numbers[i][j].addKeyListener(new KL(i, j));
            }
        }
        repaint();
    }


    public void initPlayersBoard() {
        for (int i = 0; i < board_size; i++) {
            players_board[i] = new int[board_size];
            for (int j = 0; j < board_size; j++) {
                players_board[i][j] = sudoku.getPuzzle_board()[i][j];

            }
        }
    }

    public boolean CheckWinner() {
        for (int i = 0; i < board_size; i++) {
            for (int j = 0; j < board_size; j++) {
                if(players_board[i][j] != sudoku.getFull_board()[i][j])
                    return false;
            }
        }
        return true;
    }

    public boolean sumBoard() {
        for (int i = 0; i < board_size; i++) {
            for (int j = 0; j < board_size; j++) {
                if(players_board[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    public void run() {
        try {
            Image img;
            for (int i = 0; i < board_size; i++) {
                for (int j = 0; j < board_size; j++) {
                    if(players_board[i][j] == 0) {
                        ImageIcon icon = new ImageIcon(Integer.toString(sudoku.getSolve_board()[i][j]) + ".png");
                        img = icon.getImage();
                        numbers[i][j].setImg(img);
                        repaint();
                        Thread.sleep(4);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start () {
        if (true) {
            thread = new Thread (this);
            thread.start ();
        }
    }

    public void paint(Graphics g) {
        super.paintComponents(g);
    }

    public static void main(String[] args) {
        new Board();
    }
}
