import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test extends JFrame {

    int tileSize = 5;
    float scl = (float) 0.1;

    int width = 1000;
    int height = 1000;

    int seed = 100;

    public static void main(String[] args) {
        Test test = new Test();
        test.setup();
    }

    void setup() {
        TFrame tFrame = new TFrame(width, height);
        // Set layout manager for the frame
        setLayout(new BorderLayout());
        add(tFrame, BorderLayout.CENTER);

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Function to generate noise (you might want to replace it with your noise function)
    float noise(int x, int y) {
        PerlinNoise perlinNoise = new PerlinNoise(seed);

        float result = (float) perlinNoise.noise((float) x*scl, (float) y*scl, 0);

        if(result<0){
            result = 0-result;
        }

        System.out.println(x);
        return result;
    }

    class TFrame extends JPanel implements ActionListener {

        TFrame(int width, int height) {
            this.setPreferredSize(new Dimension(width, height));
            this.setBackground(Color.BLACK);
            this.setFocusable(true);
            this.setDoubleBuffered(true);
            // Start a timer for animations (if needed)
            Timer timer = new Timer(100, this);
            timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle timer events or other periodic updates here
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the grid of rectangles
            for (int i = 0; i < width / tileSize; i++) {
                for (int j = 0; j < height / tileSize; j++) {
                    // Calculate noise value (you might want to adjust the parameters)
                    float n = noise(i, j);
                    int grayValue = (int) (n * 255);
                    // Set the color of the rectangle based on the noise value
                    g.setColor(new Color(grayValue, grayValue, grayValue));
                    // Draw the rectangle
                    g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
                }
            }
        }
    }
}
