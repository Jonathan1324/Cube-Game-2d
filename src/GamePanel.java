import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GamePanel extends JPanel implements ActionListener{

    private Set<Integer> activeKeys;
    int SCREEN_WIDTH;
    int SCREEN_HEIGHT;

    int PLAYER_SIZE;

    int PLAYER_MULTIPLIER;

    int UNIT_SIZE;
    int GAME_UNITS;
    int DELAY;
    int LIMIT = 1000;
    int FRAME = 0;

    int x;
    int y;

    int dir = 303;

    int dirX;

    int dirY;

    boolean living = false;
    boolean menu = false;
    Timer timer;
    Random random;
    GameFrame gameFrame;

    private List<String> tileTypes;
    private Map<String, Color> tileColorMap;

    GamePanel(int width, int height, int unit_size, int delay, List<String> tileTypes, int player_size, GameFrame frame){

        System.setProperty("sun.java2d.opengl", "true");

        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;

        gameFrame = frame;

        activeKeys = new HashSet<>();

        DELAY = delay;

        UNIT_SIZE = unit_size;
        PLAYER_SIZE = player_size;

        PLAYER_MULTIPLIER = player_size/unit_size;

        GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;

        this.tileTypes = tileTypes;
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
        moveMouse(new Point((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
        living = true;
        timer = new Timer(1,this);
        timer.start();
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
                    g.fillRect(col * UNIT_SIZE, row * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.GREEN);
            g.fillRect(x*UNIT_SIZE, y*UNIT_SIZE, PLAYER_SIZE, PLAYER_SIZE);
            g.setColor(Color.red);
            g.fillRect((x + dirX) * UNIT_SIZE, (y+dirY) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

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
                nextX = x + dirX;
                nextY = y + dirY;
                break;
            case KeyEvent.VK_S:
                nextX = x + -dirX;
                nextY = y + -dirY;
                break;
            case KeyEvent.VK_D:
                nextX = x + -dirY;
                nextY = y + -dirX;
                break;
            case KeyEvent.VK_A:
                nextX = x + dirY;
                nextY = y + dirX;
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

        if(dir > 360){
            dir -= 360;
        } else if(dir < 0){
            dir += 360;
        }

        if(dir < 45){
            dirY = 0;
            dirX = 1;
        } else if(dir < 90){
            dirY = 1;
            dirX = 1;
        } else if(dir < 135){
            dirY = 1;
            dirX = 0;
        } else if(dir < 180){
            dirY = 1;
            dirX = -1;
        } else if(dir < 225){
            dirY = 0;
            dirX = -1;
        } else if(dir < 290){
            dirY = -1;
            dirX = -1;
        } else if(dir < 315){
            dirY = -1;
            dirX = 0;
        } else if(dir > 315){
            dirY = -1;
            dirX = 1;
        }

        repaint();

        if(FRAME % (DELAY/PLAYER_MULTIPLIER) == 0){
            handleMoveKeys();
        }

        //Cursor Lock

        if(gameFrame.isActive() && !menu){
            dir -= (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - (MouseInfo.getPointerInfo().getLocation().x)) / (102-100);
            System.out.println(dir);

            gameFrame.getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor"));
            moveMouse(new Point((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
        } else {
            gameFrame.getContentPane().setCursor(gameFrame.cursor);
        }
    }

    public void moveMouse(Point p) {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        // Search the devices for the one that draws the specified point.
        for (GraphicsDevice device: gs) {
            GraphicsConfiguration[] configurations =
                    device.getConfigurations();
            for (GraphicsConfiguration config: configurations) {
                Rectangle bounds = config.getBounds();
                if(bounds.contains(p)) {
                    // Set point to screen coordinates.
                    Point b = bounds.getLocation();
                    Point s = new Point(p.x - b.x, p.y - b.y);

                    try {
                        Robot r = new Robot(device);
                        r.mouseMove(s.x, s.y);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
        }
        // Couldn't move to the point, it may be off-screen.
        return;
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
                    moveMouse(new Point((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2));
                    switch (String.valueOf(menu)) {
                        case "true" -> {
                            menu = false;
                            FRAME = 0;
                        }
                        case "false" -> {
                            menu = true;
                            FRAME = 0;
                        }
                    }

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

