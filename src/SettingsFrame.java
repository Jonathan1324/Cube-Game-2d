import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SettingsFrame extends JFrame {
    private JTextField seedTextField;
    private JTextField sclTextField;
    private JTextField unitSizeTextField;
    private JTextField playerSizeTextField;

    private int screenWidth = 2000;
    private int screenHeight = 1000;
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

        seedTextField.setText(Integer.toString((int) (Math.random() * 1000)));
        sclTextField.setText("0.1");
        unitSizeTextField.setText("50");
        playerSizeTextField.setText("1");

        add(new JLabel("Seed:"));
        add(seedTextField);
        add(new JLabel("SCL:"));
        add(sclTextField);
        add(new JLabel("Unit Size:"));
        add(unitSizeTextField);
        add(new JLabel("Player Size:"));
        add(playerSizeTextField);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                    scl = (float) 0.1;
                }
                try {
                    unitSize = Integer.parseInt(unitSizeTextField.getText());
                } catch (Exception E){
                    unitSize = 50;
                }
                try {
                    playerSize = Integer.parseInt(playerSizeTextField.getText());
                } catch (Exception E){
                    playerSize = 1;
                }

                List<String> tileTypes = new ArrayList<>();

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

                new GameFrame(screenWidth, screenHeight, scl, unitSize, 10, tileTypes, unitSize*playerSize, seed, settingsFrame);

                dispose();
            }
        });

        add(confirmButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public SettingsFrame(int seed) {
        setTitle("Game Settings");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        sclTextField = new JTextField();
        unitSizeTextField = new JTextField();
        playerSizeTextField = new JTextField();

        sclTextField.setText("0.1");
        unitSizeTextField.setText("50");
        playerSizeTextField.setText("1");

        add(new JLabel("SCL:"));
        add(sclTextField);
        add(new JLabel("Unit Size:"));
        add(unitSizeTextField);
        add(new JLabel("Player Size:"));
        add(playerSizeTextField);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float scl;
                try {
                    scl = Float.parseFloat(sclTextField.getText());
                } catch (Exception E){
                    scl = (float) 0.1;
                }
                try {
                    unitSize = Integer.parseInt(unitSizeTextField.getText());
                } catch (Exception E){
                    unitSize = 50;
                }
                try {
                    playerSize = Integer.parseInt(playerSizeTextField.getText());
                } catch (Exception E){
                    playerSize = 1;
                }

                List<String> tileTypes = new ArrayList<>();

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

                new GameFrame(screenWidth, screenHeight, scl, unitSize, 10, tileTypes, unitSize*playerSize, seed, settingsFrame);

                dispose();
            }
        });

        add(confirmButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
