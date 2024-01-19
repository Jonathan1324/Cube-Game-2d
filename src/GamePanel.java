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

public class GamePanel extends JPanel implements ActionListener{

    private final Set<Integer> activeKeys;
    private Set<Integer> nextKeys;
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
    SettingsFrame settingsFrame;

    private List<String> tileTypes;
    private Map<String, Color> tileColorMap;

    int Option = 0;
    int PaddingTop = 100;
    int maxOption = PaddingTop*2;
    String currentState = "";
    public String worldName = "world.wrd";

    GamePanel(int width, int height, int unit_size, int delay, List<String> tileTypes, int player_size, GameFrame frame, int seed, SettingsFrame Settingsframe, int playerMultiplier) {
        System.setProperty("sun.java2d.opengl", "true");

        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;

        settingsFrame = Settingsframe;

        SEED = seed;

        gameFrame = frame;

        activeKeys = new HashSet<>();
        nextKeys = new HashSet<>();

        DELAY = delay;

        UNIT_SIZE = unit_size;
        PLAYER_SIZE = player_size;

        PLAYER_MULTIPLIER = playerMultiplier;

        GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;

        this.tileTypes = tileTypes;

        /* Create File at start
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

    void createFile(String name) {
        try {
            File myObj = new File(name);
            if (myObj.createNewFile()) {
                currentState = "saved the world '" + myObj.getName() + "'";
            } else {
                currentState = "updated the world '" + myObj.getName() + "'";
            }
            try {
                modifyFileContent(".\\" + name, Integer.toBinaryString(x) + "," + Integer.toBinaryString(y) + "\n" + convertListToString(tileTypes).replace("floor", "0").replace("wall", "1"));
            } catch (IOException e) {
                currentState = "couldn't save world";
                e.fillInStackTrace();
            }
        } catch (IOException e) {
            currentState = "couldn't save world";
            e.fillInStackTrace();
        }
    }

    public void drawMenu(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        String SaveButton = "Save as '" + worldName + "'";
        String LoadButton = "Load '" + worldName + "'";
        String ExitButton = "Leave";

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());

        int option = g.getFont().getSize() + Option;

        g.drawString(SaveButton, (SCREEN_WIDTH - metrics.stringWidth(SaveButton))/2, g.getFont().getSize() + 20);
        g.drawString(LoadButton, (SCREEN_WIDTH - metrics.stringWidth(LoadButton))/2, g.getFont().getSize() + PaddingTop + 20);
        g.drawString(ExitButton, (SCREEN_WIDTH - metrics.stringWidth(ExitButton))/2, g.getFont().getSize() + PaddingTop*2 + 20);

        g.drawString(currentState, (SCREEN_WIDTH - metrics.stringWidth(currentState))/2, g.getFont().getSize() + PaddingTop*4 + 20);

        g.drawOval((int) (SCREEN_WIDTH/2-metrics.stringWidth(SaveButton)/1.5), option - 5, 20, 20);
        g.drawOval((int) (SCREEN_WIDTH/2+metrics.stringWidth(SaveButton)/1.5), option - 5, 20, 20);
    }

    void MenuOption(int option) {
        option /= 100;

        switch (option){
            case 0 -> createFile(worldName);
            case 1 -> {
                String[] values;
                try{

                    values = getWorldFromFile(".\\world.wrd").split("\\r?\\n|\\r");
                    this.tileTypes = convertStringToList(values[1].replace("0", "floor").replace("1", "wall"));

                    spawnPlayer(Integer.parseInt(convertStringToList(values[0]).get(0), 2), Integer.parseInt(convertStringToList(values[0]).get(1), 2));

                    currentState = "loaded the world '" + worldName + "'";
                } catch (Exception e){
                    e.fillInStackTrace();
                }
            }
            case 2 -> gameFrame.dispatchEvent(new WindowEvent(gameFrame, WindowEvent.WINDOW_CLOSING));
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

    public void spawnPlayer(int X, int Y){
        x = X;
        y = Y;
    }

    public void paintComponent(Graphics g){
        //super.paintComponent(g);
        draw(g);
    }

    void drawBackground(Graphics g){
        for (int row = 0; row < SCREEN_HEIGHT / UNIT_SIZE; row++) {
            for (int col = 0; col < SCREEN_WIDTH / UNIT_SIZE; col++) {
                int index = row * (SCREEN_WIDTH / UNIT_SIZE) + col;
                String currentTileType = tileTypes.get(index);
                Color currentTileColor = tileColorMap.getOrDefault(currentTileType, Color.WHITE);

                g.setColor(currentTileColor);
                g.fillRect(col * UNIT_SIZE, row * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    public void draw(Graphics g){
        if(!menu) {
            if (living) {

                drawBackground(g);

                g.setColor(Color.GREEN);
                g.fillRect(x * UNIT_SIZE, y * UNIT_SIZE, PLAYER_SIZE, PLAYER_SIZE);
            } else {
                dead(g);
            }
        } else {
            drawMenu(g);
        }
    }

    public void dead(Graphics g){

    }

    public void move(int keyCode){
        int nextX = x;
        int nextY = y;
        switch (keyCode) {
            case KeyEvent.VK_W -> nextY = y - 1;
            case KeyEvent.VK_S -> nextY = y + 1;
            case KeyEvent.VK_D -> nextX = x + 1;
            case KeyEvent.VK_A -> nextX = x - 1;
        }


        // Check if the next position is within bounds
        if (nextX >= 0 && (nextX+PLAYER_MULTIPLIER-1) < SCREEN_WIDTH / UNIT_SIZE && nextY >= 0 && (nextY + PLAYER_MULTIPLIER - 1) < SCREEN_HEIGHT / UNIT_SIZE) {
            // Check for walls at all four corners of the player's next position
            boolean canMove = isCanMove(nextY, nextX);

            // If there are no walls, update the player's position
            if (canMove) {
                x = nextX;
                y = nextY;
            }
        }
    }

    private boolean isCanMove(int nextY, int nextX) {
        boolean canMove = tileTypes.get((nextY * (SCREEN_WIDTH / UNIT_SIZE)) + nextX).equals("floor") &&
                tileTypes.get((nextY * (SCREEN_WIDTH / UNIT_SIZE)) + (nextX + PLAYER_MULTIPLIER - 1)).equals("floor") &&
                tileTypes.get(((nextY + PLAYER_MULTIPLIER - 1) * (SCREEN_WIDTH / UNIT_SIZE)) + nextX).equals("floor") &&
                tileTypes.get(((nextY + PLAYER_MULTIPLIER - 1) * (SCREEN_WIDTH / UNIT_SIZE)) + (nextX + PLAYER_MULTIPLIER - 1)).equals("floor");

        int temporalY = nextY;
        for(int i = 0; i < PLAYER_MULTIPLIER; i++){
            for(int j = 0; j < PLAYER_MULTIPLIER; j++){
                if(!canMove){
                    break;
                }
                canMove = tileTypes.get(temporalY * (SCREEN_WIDTH / UNIT_SIZE) + nextX + j).equals("floor");
            }
            temporalY++;
        }
        return canMove;
    }

    public void moveWithoutCollision(int X, int Y){
        x += X;
        y += Y;

        int temporalY = y;

        if (x >= 0 && (x+PLAYER_MULTIPLIER-1) < SCREEN_WIDTH / UNIT_SIZE && y >= 0 && (y + PLAYER_MULTIPLIER - 1) < SCREEN_HEIGHT / UNIT_SIZE){
            for(int i = 0; i < PLAYER_MULTIPLIER; i++){
                for(int j = 0; j < PLAYER_MULTIPLIER; j++){
                    tileTypes.set(temporalY * (SCREEN_WIDTH / UNIT_SIZE) + x + j, "floor");
                }
                temporalY++;
            }
        } else {
            x -= X;
            y -= Y;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FRAME++;

        repaint();

        boolean name;
        try{
            name = FRAME % (DELAY/PLAYER_MULTIPLIER) == 0;
        } catch (Exception E){
            name = true;
        }

        if(name){
            handleMoveKeys();
            nextKeys = activeKeys;
        }
    }

    public void keyPressedAction(int keyCode) {
        if(keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_A){
            activeKeys.add(keyCode);
            nextKeys.add(keyCode);
        }
        switch (keyCode) {
            case KeyEvent.VK_E -> {
                if (!menu) {
                    spawnPlayer();
                }
            }
            case KeyEvent.VK_ESCAPE -> menu = !menu;
            case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                if(Option > 0 && menu){
                    Option -= PaddingTop;
                }
            }
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                if(Option < maxOption && menu){
                    Option += PaddingTop;
                }
            }
            case KeyEvent.VK_SPACE-> {
                if(menu){
                    MenuOption(Option);
                }
            }
        }

        if(keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT){
            switch (keyCode){
                case KeyEvent.VK_UP -> moveWithoutCollision(0, -1);
                case KeyEvent.VK_DOWN -> moveWithoutCollision(0, 1);
                case KeyEvent.VK_LEFT -> moveWithoutCollision(-1, 0);
                case KeyEvent.VK_RIGHT -> moveWithoutCollision(1, 0);
            }
        }
    }

    public void keyReleasedAction(int keyCode) {activeKeys.remove(keyCode);}

    private void handleMoveKeys() {
        if(menu){
            return;
        }
        for (Integer keyCode : nextKeys) {
            move(keyCode);
        }
    }

   static class MyKeyAdapter extends KeyAdapter{
        private final GamePanel gamePanel;

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

