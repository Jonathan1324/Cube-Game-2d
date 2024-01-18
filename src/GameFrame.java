import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

public class GameFrame extends JFrame {

    Cursor cursor;
    BufferedImage Icon;
    JTextArea seedText = new JTextArea();
    JTextArea sclText = new JTextArea();
    JTextArea unitSizeText = new JTextArea();
    JTextArea playerSizeText = new JTextArea();
    String Seed;
    String Scl;
    String unitSize;
    String playerSize;

    GameFrame(int width, int height, float scl, int unit_size, int delay, List<String> tileTypes, int player_size, int seed, SettingsFrame settingsFrame) {
        this.add(new GamePanel(width, height, unit_size, delay, tileTypes, player_size, this, seed, settingsFrame));
        this.setTitle("Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        seedText.setText(String.valueOf(seed));
        sclText.setText(String.valueOf(scl));
        unitSizeText.setText(String.valueOf(unit_size));
        playerSizeText.setText(String.valueOf(player_size));

        Seed = String.valueOf(seed);
        Scl = String.valueOf(scl);
        unitSize = String.valueOf(unit_size);
        playerSize = String.valueOf(player_size/unit_size);


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