import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GameFrame extends JFrame {

    Cursor cursor;
    BufferedImage Icon;

    GameFrame(int width, int height, int unit_size, int delay, List<String> tileTypes, int player_size) {
        this.add(new GamePanel(width, height, unit_size, delay, tileTypes, player_size, this));
        this.setTitle("Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try{
            Icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Textures/Icon.png")));
        } catch (Exception e){
            e.fillInStackTrace();
        }

        this.setIconImage(Icon);

        try{
            cursor = Toolkit.getDefaultToolkit().createCustomCursor(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Textures/Cursor.png"))), new Point(0, 0), "Cursor");
        } catch (Exception e){
            e.fillInStackTrace();
        }

        /* FULL SCREEN
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
         */

        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}