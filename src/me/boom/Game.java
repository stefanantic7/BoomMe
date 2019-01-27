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
    private static final int LIVES_COUNTER = 3;

    private static int DEFAULT_BOMB_WIDTH = 30;
    private static int DEFAULT_BOMB_HEIGHT = 30;

    private BufferedImage background;

    private ArrayList<Tile> tiles;
    private Tile player;

    private Bomb bomb;
    private int bombX;
    private int bombY;

    private static Game instance;

    private double angle = 0;
    private double speedX = 0;
    private double speedY = 0;
    private double radius = 10.0;
    private double gravity = -0.1;

    private static int scalingFactor = 3;

    private BufferedImage deathFrame = null;
    private ArrayList<DeathFrameTile> deathFrameTiles = new ArrayList<>();

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

    }

    public static Game getInstance() {
        if (instance == null) {
            synchronized (Game.class) {
                if (instance == null) {
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
                            j * width, i * height, width, height, LIVES_COUNTER);
                    player = tile;
                } else {

                    if (!bitMap[i][j].equals("0")) {
                        Tile tile = new Tile("Tiles/" + bitMap[i][j] + ".png",
                                j * width, i * height, width, height, TileType.BLOCK);
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

        if (((Player) player).isDeath()) {

            //Initialize death frame and draw last image from window on it.
            if (deathFrame == null) {
                deathFrame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                g = deathFrame.createGraphics();
                renderGraphics(g);
            }

            for (DeathFrameTile deathFrameTile : deathFrameTiles) {
                deathFrameTile.render(g);
            }

        } else {
            renderGraphics(g);
        }

    }

    private void renderGraphics(Graphics2D g) {
        g.drawImage(background, 0, 0, null);

        for (Tile tile : this.tiles) {
            g.drawImage(tile.getImage(), tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), null);
        }

        player.render(g);

        if (bombExists()) {
            bomb.render(g);
        }

    }

    public boolean bombExists() {
        return bomb != null;
    }

    @Override
    public void update() {

        if (((Player) player).isDeath()) {
            runDeathAnimation();
        } else {
            // na klik se pojavljuje bomba
            if (isMouseButtonDown(GFMouseButton.Left)) {
                if (bomb == null) {
                    bomb = new Bomb("Tiles/bomb.png", getMouseX() - DEFAULT_BOMB_WIDTH / 2, getMouseY() - DEFAULT_BOMB_HEIGHT / 2, DEFAULT_BOMB_WIDTH, DEFAULT_BOMB_HEIGHT, 1, UPDATE_RATE);
                    bombX = getMouseX();
                    bombY = getMouseY();
                }
                bomb.increaseBombChangingCount();

                bomb.changeSize();

            } else if (bomb != null) {
                bomb.update();

                if (bomb.isTimeToExplode()) {
                    ((Player) player).hit();
                    handleBoom();
                }

            }

            handleMovement();

            player.update();
        }
    }

    private void runDeathAnimation() {
        if (deathFrameTiles.isEmpty()) {

            for (int y = 0; y < (getHeight() / DeathFrameTile.HEIGHT); y++) {
                for (int x = 0; x < (getWidth() / DeathFrameTile.WIDTH); x++) {

                    int posX = Math.abs((x * DeathFrameTile.WIDTH));
                    int posY = Math.abs((y * DeathFrameTile.HEIGHT));

                    BufferedImage bi = deathFrame.getSubimage(posX, posY, DeathFrameTile.WIDTH, DeathFrameTile.HEIGHT);
                    deathFrameTiles.add(new DeathFrameTile(bi, posX, posY));
                }
            }
        } else {
            boolean aboveLine = false;
            for (DeathFrameTile deathFrameTile : deathFrameTiles) {
                if (deathFrameTile.getY() < getHeight()) {
                    aboveLine = true;
                }
            }
            if (!aboveLine) {
                closeGame();
            }
            for (DeathFrameTile deathFrameTile : deathFrameTiles) {
                deathFrameTile.update();
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
        ((Player) player).enableRotation((getMouseX() > player.getX()) ? Player.LEFT_ROTATION_DIRECTION : Player.RIGHT_ROTATION_DIRECTION);
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


        if (!handleCollision(dX, dY)) {
            player.setX((int) (player.getX() + dX));
            player.setY((int) (player.getY() + dY));
        }
    }

    private boolean handleCollision(double dX, double dY) {

        for (Tile t : tiles) {
            if (t.getType().equals(TileType.BLOCK)) {


                Area temp = new Area(t.getShape());
                if (temp.intersects((int) (player.getX() + dX), (int) (player.getY() + dY), player.getWidth(), player.getHeight())) {

                    if (dX < 0 && dY < 0) {
                        if (player.getY() <= t.getY() + t.getHeight()) {
                            player.setX(t.getX() + t.getWidth() + 2);
                            angle = Math.PI - angle;
                        } else {
                            player.setY(t.getY() + t.getHeight() + 2);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;
                        }
                    }

                    if (dX > 0 && dY < 0) {
                        if (player.getY() <= t.getY() + t.getHeight()) {
                            player.setX(t.getX() - t.getWidth());
                            angle = Math.PI - angle;

                        } else {
                            player.setY(t.getY() + t.getHeight() + 2);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;
                        }
                    }

                    if (dX < 0 && dY > 0) {

                        if (player.getY() + player.getHeight() <= t.getY()) {
                            speedX = 0;
                            speedY = 0;
                            player.setY(t.getY() - player.getHeight() - 2);
                            angle = Math.PI - angle;
                        } else {
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;

                        }
                    }

                    if (dX > 0 && dY > 0) {
                        ((Player) player).disableRotation();
                        if (player.getY() + player.getHeight() <= t.getY()) {
                            speedX = 0;
                            speedY = 0;
                            player.setY(t.getY() - player.getHeight() - 2);
                            angle = Math.PI - angle;
                        } else {

                            player.setX(t.getX() - player.getWidth() - 4);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;

                        }
                    }

                    if (speedX > 0)
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
