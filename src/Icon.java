import javax.swing.*;
import java.awt.*;

/**
 * Created by ikosteniov on 3/7/2017.
 */
public class Icon extends JButton {

    private Image img;

    public Icon(Image img) {
        this.img = img;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
    }
}
