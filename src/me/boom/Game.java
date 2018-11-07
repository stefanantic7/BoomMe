package me.boom;

import me.maps.MapLoader;
import rafgfxlib.GameFrame;
import rafgfxlib.Util;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Game extends GameFrame {

    private static final int UPDATE_RATE = 90;


    private static final int SPARKS_MAX = 350;

    private static int DEFAULT_BOMB_WIDTH = 30;
    private static int DEFAULT_BOMB_HEIGHT = 30;
    /** Determinate whether bomb should increase or decrease it's size */
    private static boolean BOMB_GROWING = true;
    /** How much we are increasing/decreasing bomb size */
    private static int BOMB_CHANGING_FACTOR = 10;
    private static int BOMB_CHANGING_LIMIT = 50;
    private static int BOMB_CHANGING_COUNT;

    /** Counting starts from this number */
    private static int EXPLODE_COUNTDOWN_START;
    /** Bomb explodes when it's 0 */
    private static int EXPLODE_COUNTDOWN;


    /** How much we are moving - this makes shaking effect */
    private int[] shakeX = {0, 2, -1, -3, 0, -2, 1, 3, 0};
    private int[] shakeY = {0, 1, -2, -1, 2,-1, 2, 0, -1};
    /** Counter for shakeX/Y array */
    private int shakeCount = 0;

    private Spark[] sparks = new Spark[SPARKS_MAX];

    private BufferedImage background;

    private ArrayList<Tile> tiles;
    private Tile player;

    private Tile bomb;
    private Tile countdownTile;
    private int bombX;
    private int bombY;

    private static Game instance;

    private double angle = 0;
    private double speedX = 0;
    private double speedY = 0;
    private double radius = 10.0;
    private double gravity = -0.1;

    private static int scalingFactor = 3;

    private int windowWidth;
    private int windowHeight;

    private String[][] bitMap;

    protected Game(int sizeX, int sizeY, String[][] bitMap) {
        super("Boom Me", sizeX, sizeY);

        this.windowWidth = sizeX;
        this.windowHeight = sizeY;
        this.bitMap = bitMap;

        this.tiles = new ArrayList<>();
        this.background = Util.loadImage("bg/BG.png");

        for(int i = 0; i < SPARKS_MAX; ++i)
            sparks[i] = new Spark();

        EXPLODE_COUNTDOWN = EXPLODE_COUNTDOWN_START = UPDATE_RATE * 5; // because we are counting down from 5
    }

    public static Game getInstance() {
        if (instance == null) {
            synchronized (Game.class) {
                if(instance == null) {
                    String[][] bitMap = MapLoader.loadFromFile("maps/1.txt");

                    instance = new Game(800, 640, bitMap);
                }
            }
        }
        return instance;
    }

    private void loadTiles() {
        int rows = bitMap.length;
        int columns = bitMap[0].length;

        for (int i = 0; i < bitMap.length; i++) {
            for (int j = 0; j < bitMap[i].length; j++) {
                int width = this.windowWidth / columns;
                int height = this.windowHeight / rows;


                if (bitMap[i][j].equals("#")) {
                    Tile tile = new Player("Tiles/" + "player" + ".png",
                            j * width, i * height, width, height, 3);
                    player = tile;
                } else {

                    if (!bitMap[i][j].equals("0")){
                        Tile tile = new Tile("Tiles/" + bitMap[i][j] + ".png",
                                j * width, i * height, width, height, "g");
                        tiles.add(tile);
                    }
                }
            }
        }
    }

    @Override
    public void handleWindowInit() {
        loadTiles();
        setUpdateRate(UPDATE_RATE);
        startThread();
    }

    @Override
    public void render(Graphics2D g, int i, int i1) {
        g.drawImage(background, 0, 0, null);

        for (Tile tile : this.tiles) {
            g.drawImage(tile.getImage(), tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), null);
        }

        player.render(g);

        if (bombExists()) {
            renderBomb(g);
            if(EXPLODE_COUNTDOWN < EXPLODE_COUNTDOWN_START) {
                renderNumber(g);
            }
        }

        for(Spark spark : sparks) {
            spark.render(g);
        }
    }

    private boolean bombExists() {
        return bomb != null;
    }

    private void renderBomb(Graphics2D g) {
        g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), bomb.getWidth(), bomb.getHeight(), null);
    }

    private void renderNumber(Graphics2D g) {
        g.drawImage(countdownTile.getImage(), countdownTile.getX(), countdownTile.getY(), countdownTile.getWidth(), countdownTile.getHeight(), null);
    }

    @Override
    public void update() {

        // na klik se pojavljuje bomba
        if (isMouseButtonDown(GFMouseButton.Left)) {
            if (bomb == null) {
                BOMB_GROWING = true;
                BOMB_CHANGING_COUNT = 1;
                bomb = new Tile("Tiles/bomb.png",getMouseX() - DEFAULT_BOMB_WIDTH / 2,getMouseY() - DEFAULT_BOMB_HEIGHT / 2, DEFAULT_BOMB_WIDTH, DEFAULT_BOMB_HEIGHT, "b");
                bombX = getMouseX();
                bombY = getMouseY();
            }
            BOMB_CHANGING_COUNT++;

            if(BOMB_CHANGING_COUNT > BOMB_CHANGING_LIMIT) {
                BOMB_CHANGING_COUNT = 1;
                if(BOMB_GROWING) {
                    BOMB_GROWING = false;
                } else {
                    BOMB_GROWING = true;
                }
            }

            if (timeToChange(BOMB_CHANGING_COUNT)) {
                if(BOMB_GROWING) {
                    bomb.setHeight(bomb.getHeight() + BOMB_CHANGING_FACTOR);
                    bomb.setWidth(bomb.getWidth() + BOMB_CHANGING_FACTOR);
                    bomb.setX(bomb.getX() - BOMB_CHANGING_FACTOR / 2);
                    bomb.setY(bomb.getY() - BOMB_CHANGING_FACTOR / 2);
                } else {
                    bomb.setHeight(bomb.getHeight() - BOMB_CHANGING_FACTOR);
                    bomb.setWidth(bomb.getWidth() - BOMB_CHANGING_FACTOR);
                    bomb.setX(bomb.getX() + BOMB_CHANGING_FACTOR / 2);
                    bomb.setY(bomb.getY() + BOMB_CHANGING_FACTOR / 2);
                }
            }


        } else if (bomb != null) {
            EXPLODE_COUNTDOWN--;
            generateSparks(bomb.getX() + bomb.getHeight() / 1.65f, bomb.getY(), 4.0f, bomb.getHeight() / 4, 8 - EXPLODE_COUNTDOWN / UPDATE_RATE );
            if(timeToExplode()) {
                handleBoom();
            } else {
                handleCountdown();
            }
        }

        for(Spark spark : sparks) {
             spark.update();
        }

        handleMovement();

        player.update();
    }


    private boolean timeToExplode() {
        return EXPLODE_COUNTDOWN == 0;
    }

    private void handleCountdown() {
        int countdownTileW = (int)(bomb.getWidth() * 0.3);
        int countdownTileH = (int)(bomb.getHeight() * 0.3);
        int countdownTileX = bomb.getX() + bomb.getWidth() / 2 - countdownTileW ;
        int countdownTileY = bomb.getY()+ bomb.getHeight() / 2 - countdownTileH;

        int currentNumber = EXPLODE_COUNTDOWN / UPDATE_RATE + 1;
        switch (currentNumber) {
            case 1:
                if(countdownTile == null || !countdownTile.getType().equals("1")) {
                    countdownTile = new Tile("Tiles/number1.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "1");
                }
                shakeBomb(currentNumber);
                break;
            case 2:
                if(countdownTile == null || !countdownTile.getType().equals("2")) {
                    countdownTile = new Tile("Tiles/number2.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "2");
                }
                shakeBomb(currentNumber);
                break;
            case 3:
                if(countdownTile == null || !countdownTile.getType().equals("3")) {
                    countdownTile = new Tile("Tiles/number3.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "3");
                }
                shakeBomb(currentNumber);
                break;
            case 4:
                if(countdownTile == null || !countdownTile.getType().equals("4")) {
                    countdownTile = new Tile("Tiles/number4.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "4");
                }
                break;
            default:
                if(countdownTile == null || !countdownTile.getType().equals("5")) {
                    countdownTile = new Tile("Tiles/number5.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "5");
                }
        }

        if(EXPLODE_COUNTDOWN % 3 == 0) {
            countdownTile.setWidth(countdownTile.getWidth() + 1);
            countdownTile.setHeight(countdownTile.getHeight() + 1);
            countdownTile.setX(bomb.getX() + bomb.getWidth() / 2 - countdownTile.getWidth() / 2);
            countdownTile.setY(bomb.getY() + bomb.getHeight() / 2 - countdownTile.getHeight() / 2);
        }
    }

    /**
     * Bomb is shaking only if current number is 3 or less.
     * As currentNumber is lower, the bomb shaking is more frequent,
     * so if currentNumber is 3 it will shake every 4th frame, and if currentNumber is 2 it will shake every third frame...
     * On numbers 2 and 1 we are adding additional moving so it looks like bomb is shaking  more
     *
     * @param currentNumber - number on the bomb
     */
    private void shakeBomb(int currentNumber) {
        boolean moved = false;
        switch (currentNumber) {
            case 1:
                if (EXPLODE_COUNTDOWN % 2 == 0) {
                    bomb.setX(bomb.getX() + shakeX[shakeCount] * 2);
                    bomb.setY(bomb.getY() + shakeY[shakeCount] * 2);
                    moved = true;
                }
                break;
            case 2:
                if (EXPLODE_COUNTDOWN % 3 == 0) {
                    bomb.setX(bomb.getX() + shakeX[shakeCount] * 2);
                    bomb.setY(bomb.getY() + shakeY[shakeCount] * 2);
                    moved = true;
                }
                break;
            case 3:
                if (EXPLODE_COUNTDOWN % 4 == 0) {
                    bomb.setX(bomb.getX() + shakeX[shakeCount] );
                    bomb.setY(bomb.getY() + shakeY[shakeCount] );
                    moved = true;
                }
                break;
        }
        if(moved) {
            shakeCount++;
            if(shakeCount == shakeX.length) shakeCount = 0;
        }
    }

    /**
     * We are changing bomb size every 10th frame
     * @param bombCnt - frame counter
     * @return true if bomb needs to grow/decrease
     */
    private boolean timeToChange(int bombCnt) {
        return bombCnt % 4 == 0;
    }


    private void generateSparks(float cX, float cY, float radius, int life, int count)
    {
        for(Spark spark : sparks)
        {
            if(spark.life <= 0)
            {
                spark.life = (int)(Math.random() * life * 0.5) + life / 2;
                spark.posX = cX;
                spark.posY = cY;
                double angle = Math.random() * Math.PI * 2.0;
                double speed = Math.random() * radius;
                spark.dX = (float)(Math.cos(angle) * speed);
                spark.dY = (float)(Math.sin(angle) * speed);

                count--;
                if(count <= 0) return;
            }
        }
    }

    @Override
    public void handleWindowDestroy() {

    }

    @Override
    public void handleMouseDown(int i, int i1, GFMouseButton gfMouseButton) {

    }

    @Override
    public void handleMouseUp(int i, int i1, GFMouseButton gfMouseButton) {

    }

    @Override
    public void handleMouseMove(int i, int i1) {

    }

    @Override
    public void handleKeyDown(int i) {

    }

    @Override
    public void handleKeyUp(int i) {

    }

    public void handleBoom() {

        setAngle();
        setSpeed();
        ((Player)player).enableRotation((getMouseX()>player.getX())?Player.LEFT_ROTATION_DIRECTION:Player.RIGHT_ROTATION_DIRECTION);
        EXPLODE_COUNTDOWN = EXPLODE_COUNTDOWN_START;
        bomb = null;
    }

    public void setAngle() {

        angle = Math.toDegrees(Math.atan2(player.getY() + (player.getHeight() / 2) - (bombY), player.getX() + (player.getWidth() / 2) - (bombX)));

        if (angle < 0)
            angle += 360;


        angle /= 180;
        angle *= Math.PI;

        if (angle <= Math.PI) {
            gravity = -0.1;
        } else {
            gravity = 0.1;
        }

    }

    public void setSpeed() {
        speedX = 10;
        speedY = 10;
    }

    public void handleMovement() {

        speedY -= gravity;

        double dX = (float) (Math.cos(angle) * speedX);
        double dY = (float) (Math.sin(angle) * speedY);

        if (!handleColision(dX, dY)) {


            player.setX((int) (player.getX() + dX));
            player.setY((int) (player.getY() + dY));
        }
    }

    private boolean handleColision(double dX, double dY) {

        for (Tile t : tiles) {
            if (t.getType().equals("g")) {
//                (x1 + w1) - x2 >= 0 and (x2 + w2) - x1 >= 0


                Area temp = new Area(t.getShape());
                if (temp.intersects((int) (player.getX() + dX), (int) (player.getY() + dY), player.getWidth(), player.getHeight())) {
//                    Scanner sc = new Scanner(System.in);
//                    sc.next();
//                    System.out.println((player.getY() + " " + player.getX() + " " + t.getX() + " " + t.getY() + " " + t.getHeight() + " " + t.getWidth()));
//
//
//
//                    if (!(player.getX() > t.getX())) {
//                        angle = Math.PI - angle;
//                        if(dX > 0) {
//                            player.setX(t.getX() - player.getWidth() - 2);
//                            System.out.println("evo me preko dx > 0");
//
//                        } else {
//                            System.out.println("evo me preko dx < 0");
//
//                            player.setX(t.getX() + t.getWidth() + 2);
//                        }
//
//                    } else {
//                        angle = 2 * Math.PI - angle;
//                        if(dY > 0) {
//                            System.out.println("evo me preko dy > 0");
//
//                            player.setY(t.getY() - player.getHeight() - 2);
//                            speedY = 0;
//                            speedX = 0;
//                        } else {
//                            System.out.println("evo me preko dy < 0");
//
//                            player.setY(t.getY() + t.getHeight() + 2);
//                        }
//                        gravity *= -1;
//                    }

                    if (dX < 0 && dY < 0) {
                        if (player.getY() <= t.getY() + t.getHeight()) {
//                            System.out.println("evo me preko dy > 0");
                            player.setX(t.getX() + t.getWidth() + 2);
                            angle = Math.PI - angle;
                        } else {
//                            System.out.println("evo me preko dy < 0");
                            player.setY(t.getY() + t.getHeight() + 2);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;
                        }
                    }

                    if (dX > 0 && dY < 0) {
                        if (player.getY() <= t.getY() + t.getHeight()) {
//                            System.out.println("evo me preko dy > 0");
                            player.setX(t.getX() - t.getWidth());
                            angle = Math.PI - angle;

                        } else {
//                            System.out.println("evo me preko dy < 0");
                            player.setY(t.getY() + t.getHeight() + 2);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;
                        }
                    }

                    if (dX < 0 && dY > 0) {

                        if (player.getY() + player.getHeight() <= t.getY()) {
                            speedX = 0;
                            speedY = 0;
//                            System.out.println("evo me preko dy > 0");
                            player.setY(t.getY() - player.getHeight() - 2);
                            angle = Math.PI - angle;
                        } else {
//                            System.out.println("evo me preko dy < 0");
//                            player.setX(t.getX() + t.getWidth() + 2);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;

                        }
                    }

                    if (dX > 0 && dY > 0) {
                        ((Player)player).disableRotation();
                        if (player.getY() + player.getHeight() <= t.getY()) {
                            speedX = 0;
                            speedY = 0;
//                            System.out.println("evo me preko dy > 0");
                            player.setY(t.getY() - player.getHeight() - 2);
                            angle = Math.PI - angle;
                        } else {

//                            System.out.println("evo me preko dy < 0");
                            player.setX(t.getX() - player.getWidth() - 4);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;

                        }
                    }

                    if(speedX > 0)
                        speedX *= 0.2;

                    speedY += gravity;

                    return true;
                }
            }
        }
        return false;
    }

    public void closeGame() {
        this.getWindow().dispatchEvent(new WindowEvent(this.getWindow(), WindowEvent.WINDOW_CLOSING));

    }

}
