import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SettingsFrame extends JFrame {
    private final JTextField seedTextField;
    private final JTextField sclTextField;
    private final JTextField unitSizeTextField;
    private final JTextField playerSizeTextField;
    private final JCheckBox fullscreen;

    private int screenWidth = 1500;
    private int screenHeight = 750;
    private int unitSize;

    private int playerSize;
    SettingsFrame settingsFrame = this;

    public SettingsFrame() {
        setTitle("Game Settings");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        seedTextField = new JTextField();
        sclTextField = new JTextField();
        unitSizeTextField = new JTextField();
        playerSizeTextField = new JTextField();
        fullscreen = new JCheckBox("Fullscreen");

        seedTextField.setText(Integer.toString((int) (Math.random() * 1000000000)));
        sclTextField.setText("0.01");
        unitSizeTextField.setText("5");
        playerSizeTextField.setText("10");

        add(new JLabel("Seed:"));
        add(seedTextField);
        add(new JLabel("SCL:"));
        add(sclTextField);
        add(new JLabel("Unit Size:"));
        add(unitSizeTextField);
        add(new JLabel("Player Size:"));
        add(playerSizeTextField);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            int seed;
            float scl;
            try{
                seed = Integer.parseInt(seedTextField.getText());
            } catch (Exception E){
                seed = (int) (Math.random() * 1000);
            }
            try {
                scl = Float.parseFloat(sclTextField.getText());
            } catch (Exception E){
                scl = (float) 0.01;
            }
            try {
                unitSize = Integer.parseInt(unitSizeTextField.getText());
            } catch (Exception E){
                unitSize = 5;
            }
            try {
                playerSize = Integer.parseInt(playerSizeTextField.getText());
            } catch (Exception E){
                playerSize = 10;
            }

            List<String> tileTypes = new ArrayList<>();

            boolean fullScreen = false;
            if(fullscreen.isSelected()){
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                screenWidth = (int) screen.getWidth();
                screenHeight = (int) screen.getHeight();
                fullScreen = true;
            }

            PerlinNoise perlinNoise = new PerlinNoise(seed);

            for (int i = 0; i < screenHeight/unitSize; i++) {
                for(int j = 0; j < screenWidth/unitSize; j++) {
                    double noiseValue = perlinNoise.noise((float) j*scl, (float) i*scl, 0);

                    if(noiseValue < 0){
                        noiseValue = 0-noiseValue;
                    }

                    if (noiseValue < 0.2) {
                        tileTypes.add("wall");
                    } else {
                        tileTypes.add("floor");
                    }
                }
            }

            new GameFrame(screenWidth, screenHeight, scl, unitSize, 10, tileTypes, unitSize*playerSize, seed, settingsFrame, playerSize, fullScreen);

            dispose();
        });

        add(confirmButton);
        add(fullscreen);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
