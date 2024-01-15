import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class GamePanel extends JPanel implements ActionListener{

    private final Set<Integer> activeKeys;
    int SCREEN_WIDTH;
    int SCREEN_HEIGHT;

    int PLAYER_SIZE;

    int PLAYER_MULTIPLIER;

    int UNIT_SIZE;
    int GAME_UNITS;
    int DELAY;
    int LIMIT = 1000;
    int FRAME = 0;
    int SEED;

    int x;
    int y;

    boolean living = false;
    boolean menu = false;
    Timer timer;
    Random random;
    GameFrame gameFrame;

    private List<String> tileTypes;
    private Map<String, Color> tileColorMap;

    GamePanel(int width, int height, int unit_size, int delay, List<String> tileTypes, int player_size, GameFrame frame, int seed) {

        System.setProperty("sun.java2d.opengl", "true");

        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;

        SEED = seed;

        gameFrame = frame;

        activeKeys = new HashSet<>();

        DELAY = delay;

        UNIT_SIZE = unit_size;
        PLAYER_SIZE = player_size;

        PLAYER_MULTIPLIER = player_size/unit_size;

        GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;

        this.tileTypes = tileTypes;

        /*
        try{
            createFile();
            this.tileTypes = convertStringToList(getWorldFromFile(".\\world.wrd"));
            System.out.println("Loaded File from File.");
        } catch (Exception ignored) {}
         */
        initializeColorMap();

        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        this.addKeyListener(new MyKeyAdapter(this));
        startGame();
    }
    private void initializeColorMap() {
        // Initialize the color map with colors associated with each tile type
        tileColorMap = new HashMap<>();
        tileColorMap.put("floor", Color.LIGHT_GRAY);
        tileColorMap.put("wall", Color.DARK_GRAY);
    }

    public void startGame(){
        spawnPlayer();
        living = true;
        timer = new Timer(1,this);
        timer.start();
    }

    public static String convertListToString(List<String> list) {
        return String.join(",", list);
    }

    public static List<String> convertStringToList(String str) {
        return Arrays.asList(str.split(","));
    }

    private static void modifyFileContent(String filePath, String newContent) throws IOException {
        Path path = Paths.get(filePath);
        Files.writeString(path, newContent);
    }

    public String getWorldFromFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    void createFile() {
        try {
            File myObj = new File("world.wrd");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            try {
                modifyFileContent(".\\world.wrd", convertListToString(tileTypes));
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.fillInStackTrace();
        }
    }

    public void spawnPlayer(){
        int tries = 0;
        while(true) {
            if(tries > LIMIT){
                break;
            }
            x = (int) (Math.random() * SCREEN_WIDTH / UNIT_SIZE);
            y = (int) (Math.random() * SCREEN_HEIGHT / UNIT_SIZE);

            boolean canSpawn = true;

            try{
                for (int i = 0; i < PLAYER_MULTIPLIER; i++) {
                    for (int j = 0; j < PLAYER_MULTIPLIER; j++) {
                        int currentX = x + j;
                        int currentY = y + i;

                        if (currentX < 0 || currentX >= SCREEN_WIDTH / UNIT_SIZE ||
                                currentY < 0 || currentY >= SCREEN_HEIGHT / UNIT_SIZE ||
                                !tileTypes.get((currentY * (SCREEN_WIDTH / UNIT_SIZE)) + currentX).equals("floor")) {
                            canSpawn = false;
                            tries++;
                            break;
                        }
                    }
                }
            } catch (Exception e){
                canSpawn = false;
            }

            if (canSpawn) {
                break;
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){

        if(living) {

            for (int row = 0; row < SCREEN_HEIGHT / UNIT_SIZE; row++) {
                for (int col = 0; col < SCREEN_WIDTH / UNIT_SIZE; col++) {
                    int index = row * (SCREEN_WIDTH / UNIT_SIZE) + col;
                    String currentTileType = tileTypes.get(index);
                    Color currentTileColor = tileColorMap.getOrDefault(currentTileType, Color.WHITE);

                    g.setColor(currentTileColor);
                    g.fillRect(col*UNIT_SIZE, row*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.GREEN);
            g.fillRect(x*UNIT_SIZE, y*UNIT_SIZE, PLAYER_SIZE, PLAYER_SIZE);

            /* GRID FOR THE MAP
            g.setColor(Color.WHITE);
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }w
             */
        }
        else {
            dead(g);
        }
    }

    public void checkCollisions(){

    }

    public void dead(Graphics g){

    }

    public void move(int keyCode){
        int nextX = x;
        int nextY = y;
        switch (keyCode) {
            case KeyEvent.VK_W:
                nextY = y - 1;
                break;
            case KeyEvent.VK_S:
                nextY = y + 1;
                break;
            case KeyEvent.VK_D:
                nextX = x + 1;
                break;
            case KeyEvent.VK_A:
                nextX = x - 1;
                break;
        }


        // Check if the next position is within bounds
        if (nextX >= 0 && (nextX+PLAYER_MULTIPLIER-1) < SCREEN_WIDTH / UNIT_SIZE && nextY >= 0 && (nextY + PLAYER_MULTIPLIER - 1) < SCREEN_HEIGHT / UNIT_SIZE) {
            // Check for walls at all four corners of the player's next position
            boolean canMove = tileTypes.get((nextY * (SCREEN_WIDTH / UNIT_SIZE)) + nextX).equals("floor") &&
                    tileTypes.get((nextY * (SCREEN_WIDTH / UNIT_SIZE)) + (nextX + PLAYER_MULTIPLIER - 1)).equals("floor") &&
                    tileTypes.get(((nextY + PLAYER_MULTIPLIER - 1) * (SCREEN_WIDTH / UNIT_SIZE)) + nextX).equals("floor") &&
                    tileTypes.get(((nextY + PLAYER_MULTIPLIER - 1) * (SCREEN_WIDTH / UNIT_SIZE)) + (nextX + PLAYER_MULTIPLIER - 1)).equals("floor");

            // If there are no walls, update the player's position
            if (canMove) {
                x = nextX;
                y = nextY;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FRAME++;

        repaint();

        if(FRAME % (DELAY/PLAYER_MULTIPLIER) == 0){
            handleMoveKeys();
        }
    }

    public void keyPressedAction(int keyCode) {
        if(keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_A){
            activeKeys.add(keyCode);
        } else {
            switch (keyCode) {

                case KeyEvent.VK_E:
                    spawnPlayer();
                    break;

                case KeyEvent.VK_ESCAPE:
                    SettingsFrame frame = new SettingsFrame(SEED);
                    gameFrame.dispose();

            }
        }
    }

    public void keyReleasedAction(int keyCode) {activeKeys.remove(keyCode);}

    private void handleMoveKeys() {
        int steps = 1;
        for (Integer keyCode : activeKeys) {
            move(keyCode);
        }
    }

   class MyKeyAdapter extends KeyAdapter{
        private GamePanel gamePanel;

        public MyKeyAdapter(GamePanel gamePanel){
            this.gamePanel = gamePanel;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            gamePanel.keyPressedAction(e.getKeyCode());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            gamePanel.keyReleasedAction(e.getKeyCode());
        }
    }
}

