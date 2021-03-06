import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


//------------------------------------
//             Sudoku Game
//             -----------
//
// General : A Sudoku game application.
//
//  Input : Mouse and keyboard clicks.
//
// Process : Enables the user to play the sudoku game.
//
// Output : Graphical User Interface that enables to play.
//
//-------------------------------------
// Programmer : Ilan Kosteniov
// Date : 13/04/17
//-------------------------------------

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

    private int             board_size = 9;
    private int             window_x = 800;
    private int             window_y = 800;
    private int             level;
    private Levels          level_description;
    private JPanel          sections[][];
    private JPanel          main;
    private JPanel          header;
    private JPanel          board_panel;
    private JButton         clue;
    private Icon            numbers[][];
    private Thread          thread;
    private int             question_marks = 0;
    private Counter         timer  = new Counter();
    private int             clue_left;
    private Timer           SimpleTimer;
    private Sudok           sudoku = new Sudok();
    private int[][]         players_board = new int[board_size][board_size];
    private boolean         board_flag = false;
    private boolean         gameloaded = false;
    private int[][]         loaded_puzzle_board = new int[9][9];
    private JMenuItem       save_button;
    private JMenu           game_actions;
    private int[][]         undo_board;
    private int             move_count=0;
    private int             human_count = 1;



    public Board() {
        // Builds the board
        createMenuBar();
        setSize(window_x, window_y);
        setTitle("Sudoku");
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
        // Create a MenuItem
        JMenuItem menuItem;

        save_button = new JMenuItem("  Save game");
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

        menuItem = new JMenuItem("  Easy");
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
        menuItem = new JMenuItem("  Intermidiate");
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
        menuItem = new JMenuItem("  Hard");
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

        menuItem = new JMenuItem("  Solver");
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

        game_actions = new JMenu("Game actions");
        // set shortcut using setMnemonic method, uses the ALT mask
        game_actions.setMnemonic(KeyEvent.VK_P);// Load with Alt+F
        // add menu to menuBar
        menuBar.add(game_actions);
        game_actions.setEnabled(false);

        menuItem = new JMenuItem("  Reset game");
        menuItem.setToolTipText("Click to reset game");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetBoard();
            }
        });

        game_actions.add(menuItem);
        game_actions.addSeparator();

        menuItem = new JMenuItem("  Undo move");
        menuItem.setToolTipText("Click to undo last move");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                undoMove();
            }
        });
        game_actions.add(menuItem);
        game_actions.addSeparator();
        menuItem = new JMenuItem("  Keys");
        menuItem.setToolTipText("Click to undo last move");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JOptionPane.showMessageDialog(null, "Only when cell is clicked!!\nd - Delete cell\ni - Human solver hint\n");

            }
        });
        game_actions.add(menuItem);
        game_actions.addSeparator();

    }

    public void initBoard() { // Builds the graphical board

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
                    sudoku.solveSudokuPazzle();
                    if(sudoku.validPuzzle()==0) {
                        SimpleTimer.stop();

                        JOptionPane.showMessageDialog(null, "Board can not be solved.");
                    }
                    else if(sudoku.canBeSolved(sudoku.getPuzzle_board()) == 2) {
                        SimpleTimer.stop();
                        JOptionPane.showMessageDialog(null, "Board has too many solutions.");

                    }
                    else {
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
                initPlayersBoard();
                sudoku.human_solution();


                while(!sudoku.humanSolver(this.level)) {
                    initPlayersBoard();
                    sudoku.createFullBoard(0,0);
                    sudoku.createSudokuPazzle(this.level);
                    sudoku.solveSudokuPazzle();
                    sudoku.createSudokuPazzle(this.level);
                    sudoku.human_solution();
                    sudoku.getHuman_solve_board();
                }
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
        undo_board = new int[9][9];
        for (int i = 0; i < board_size; i++) {
            undo_board[i] = new int[9];
            numbers[i] = new Icon[board_size];
            for (int j = 0; j < board_size; j++) {
                undo_board[i][j] = 0;
                if(sudoku.getPuzzle_board()[i][j]!=0)
                    icon = new ImageIcon(Integer.toString(sudoku.getPuzzle_board()[i][j]) + "-black.png");
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
        game_actions.setEnabled(true);
        save_button.setEnabled(true);

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
            sudoku.printOptions(row, col);

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
            boolean human_flag = false;
            if(e.getKeyCode() == KeyEvent.VK_I) {
                int value = 0;
                int x=0, y=0;
                while(!human_flag) {
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            if(human_count == sudoku.getStep_board()[i][j] && players_board[i][j] == 0) {
                                value = sudoku.getSolve_board()[i][j];
                                x=i;
                                y=j;
                                human_flag = true;
                                break;
                            }
                        }
                    }
                    if(human_count==81)
                        human_count = 0;
                    human_count++;
                }
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            numbers[i][j].setBorder(new LineBorder(Color.WHITE));
                            if(players_board[i][j] == value) {
                                numbers[i][j].setBorder(new LineBorder(Color.RED, 5));
                                setFocusable(true);
                                repaint();
                            }

                        }


                    }
                for (int i = 0; i < 9; i++) {
                    if(players_board[x][i] != 0)
                        numbers[x][i].setBorder(new LineBorder(Color.ORANGE, 5));
                    if(players_board[i][y] != 0)
                        numbers[i][y].setBorder(new LineBorder(Color.ORANGE, 5));
                    repaint();
                }
                    numbers[x][y].setBorder(new LineBorder(Color.BLUE, 5));
                    this.row = x;
                    this.col = y;

                    sudoku.printBoard(players_board);

            }
            if(e.getKeyCode() == KeyEvent.VK_D && sudoku.getPuzzle_board()[row][col] == 0) {
                players_board[row][col] = 0;
                Image img;
                ImageIcon icon = new ImageIcon("question_mark.jpg");
                img = icon.getImage();
                numbers[row][col].setImg(img);
                clue.setEnabled(true);
                undo_board[row][col] = 0;
                repaint();
            }
            if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        numbers[i][j].setBorder(new LineBorder(Color.WHITE));
                    }
                }
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        col = (col == 8)?0:col+1;
                        break;
                    case KeyEvent.VK_LEFT:
                        col = (col == 0)?8:col-1;
                        break;
                    case KeyEvent.VK_UP:
                        row = (row==0)?8:row-1;
                        break;
                    case KeyEvent.VK_DOWN:
                        row = (row==8)?0:row+1;
                        break;
                }
                numbers[row][col].setBorder(new LineBorder(Color.ORANGE, 5));
                setFocusable(true);

            }

            if(level_description == Levels.SOLVER) {
                if(e.getKeyCode() <= KeyEvent.VK_9 && e.getKeyCode() >= KeyEvent.VK_0) {
                    Image img;
                    ImageIcon icon = new ImageIcon(Integer.toString(e.getKeyCode() - '0') + "-black.png");
                    img = icon.getImage();
                    numbers[row][col].setImg(img);
                    players_board[row][col] = e.getKeyCode() - '0';
                    undo_board[row][col] = ++move_count;
                    repaint();

                }

            }
            else if(e.getKeyCode() <= KeyEvent.VK_9 && e.getKeyCode() > KeyEvent.VK_0) {
                if(sudoku.getPuzzle_board()[row][col] == 0) {
                    Image img;
                    ImageIcon icon = new ImageIcon(Integer.toString(e.getKeyCode() - '0') + ".png");
                    img = icon.getImage();
                    numbers[row][col].setImg(img);
                    players_board[row][col] = e.getKeyCode() - '0';
                    undo_board[row][col] = ++move_count;
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
        int tries = 100;
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
            tries--;
        } while(!clue_given && tries!= 0);
        if(question_marks == 0)
            clue.setEnabled(false);
                        boolean clue_flag = true;
                        if(tries == 0) {
                            for (int i = 0; i < 9; i++) {
                                for (int j = 0; j < 9; j++) {
                                    if (players_board[i][j] == 0) {
                                        players_board[i][j] = sudoku.getSolve_board()[i][j];
                        ImageIcon icon = new ImageIcon(sudoku.getSolve_board()[i][j] + "-black.png");
                        img = icon.getImage();
                        numbers[i][j].setImg(img);
                        repaint();
                        clue_flag = false;
                        clue_left--;
                    }
                }
            }
        }

        if(clue_flag && tries == 0)
            JOptionPane.showMessageDialog(null, "Board full, can't help :(");

        if(CheckWinner()) {
            SimpleTimer.stop();
            JOptionPane.showMessageDialog(null, "You won in: " + timer.FormattedTime());
            gameFinished();
        }
        else if(sumBoard()){
            JOptionPane.showMessageDialog(null, "Wrong Solution");

        }


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
                save.writeObject(clue_left);
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
                clue_left = (int) load.readObject();
                loadGUIGame();

                // Close the file.
                load.close(); // This also closes saveFile.
            }
        } catch(Exception exc){
            JOptionPane.showMessageDialog(null, "Sorry, there was an error loading the file.");
            //exc.printStackTrace(); // If there was an error, print the info.
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
        game_actions.setEnabled(false);
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

    public void resetBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                players_board[i][j] = 0;
                ImageIcon icon;
                if(sudoku.getPuzzle_board()[i][j] != 0) {
                    icon = new ImageIcon(Integer.toString(sudoku.getPuzzle_board()[i][j])+"-black.png");

                }
                else {
                    icon = new ImageIcon("question_mark.jpg");

                }
                Image img = icon.getImage();
                numbers[i][j].setImg(img);
            }
        }
        timer.setMinutes(0);
        timer.setSeconds(0);
        repaint();
    }

    public void undoMove() {
        int max , x , y;
        max = x = y = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                numbers[i][j].setBorder(new LineBorder(Color.WHITE));
                if(max < undo_board[i][j]) {
                    max = undo_board[i][j];
                    x = i;
                    y = j;
                }

            }
        }
        repaint();
        if(max != 0) {
            ImageIcon icon;
            icon = new ImageIcon("question_mark.jpg");
            Image img = icon.getImage();
            players_board[x][y] = 0;
            undo_board[x][y] = 0;
            numbers[x][y].setBorder(new LineBorder(Color.ORANGE, 5));
            numbers[x][y].setImg(img);
        }


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
