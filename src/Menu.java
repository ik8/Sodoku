import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ikosteniov on 3/7/2017.
 */
public class Menu extends JFrame {

    private int window_x = 800;
    private int window_y = 200;
    private Board.Levels levels_arr[] = {Board.Levels.EASY, Board.Levels.INTERMIDIATE, Board.Levels.HARD, Board.Levels.SOLVER};
    private JPanel main;
    private JButton levels[] = new JButton[levels_arr.length];

    public Menu() {

        setSize(window_x, window_y);
        InitMenu();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void InitMenu() {
        main = new JPanel();
        main.setLayout(new GridLayout(1, 4, 0, window_x / 7));
        for (int i = 0; i < levels_arr.length; i++) {
            levels[i] = new JButton();
            levels[i].setText(levels_arr[i].toString());
            levels[i].addActionListener(new AL(i));
            main.add(levels[i]);
        }
        add(main, BorderLayout.CENTER);
    }

    class AL implements ActionListener {

        private int index;


        public AL(int i) {

            this.index = i;

        }

        public void actionPerformed(ActionEvent e) {
            new Board(levels_arr[index]);
            dispose();
        }
    }

    public static void main(String[] args) {
        new Menu();
    }
}
