import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.logging.Level;


/**
 * Created by ikosteniov on 3/4/2017.
 */
public class Board extends JFrame implements Runnable{

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
    private int grids_succeded = 0;
    private int line_number = 0;
    private int window_x = 800;
    private int window_y = 800;
    private int level;
    private Levels level_description;
    private JPanel sections[][];
    private Icon numbers[][];
    private Thread thread;
    private int question_marks = 0;
    private Counter timer  = new Counter();
    private int clue_left;
    private Timer SimpleTimer;
    private Sudok sudoku = new Sudok();
    private int[][] players_board = new int[board_size][board_size];





    public Board(Levels x) {
        this.level = x.getNumber();
        clue_left = level / 9;
        this.level_description = x;
        initBoard();

        setSize(window_x, window_y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void initBoard() { // Builds the graphical board
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        JPanel header = new JPanel();
        JLabel time = new JLabel(timer.FormattedTime());
        time.setFont(new Font("Tahoma", Font.BOLD, 24));
        header.setLayout(new FlowLayout(0, window_x / 20, 0));
        header.add(time);
        JLabel level = new JLabel("Level: " + this.level_description);
        level.setFont(new Font("Tahoma", Font.PLAIN, 24));
        header.add(level);
        JButton clue = new JButton();
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
                        JOptionPane.showMessageDialog(null, "Board was solved in " + timer.FormattedTime() + " \uD83D\uDE03 ");
                        new Menu();
                        dispose();
                    }



                }
            });
        }
        else {
            sudoku.createFullBoard(0,0);
            sudoku.createSudokuPazzle(this.level);
            sudoku.solveSudokuPazzle();
            sudoku.printBoard(sudoku.getFull_board());
            initPlayersBoard();
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
        add(header, BorderLayout.NORTH);

        sections = new JPanel[board_size / 3][board_size / 3];
        ImageIcon icon;
        JPanel board_panel = new JPanel();
        board_panel.setLayout(new GridLayout(board_size / 3, board_size / 3, 5, 5));
        board_panel.setBackground(Color.black);
        numbers = new Icon[board_size][board_size];

        boolean question_mark;
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
        add(board_panel, BorderLayout.CENTER);

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
                        new Menu();
                        dispose();
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
        if (thread == null) {
            thread = new Thread (this);
            thread.start ();
        }
    }

    public void createSudoku() {

    }



    public static void main(String[] args) {
        new Board(Levels.SOLVER);
    }
}
