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

    public static class Particle
    {
        public float posX;
        public float posY;
        public float dX;
        public float dY;
        public int life = 0;
    }

    private static final int PARTICLE_MAX = 350;
    /** Determinate whether bomb should increase or decrease it's size */
    private static boolean BOMB_GROWING = true;
    /** How much we are increasing/decreasing bomb size*/
    private static int BOMB_CHANGING_FACTOR = 10;
    private static int BOMB_CHANGING_LIMIT = 50;

    private Particle[] parts = new Particle[PARTICLE_MAX];

    private int [] aniX = {0, 2 ,-1 -3, 0, 1,-1,-3, 2};
    private int [] aniY = {0, 1, -2, 0, 2,-1, 2, 1, 1};
    private int [] aniR = {0, 0, -1, 1, 0, 1,-1, 0,-1};


    private BufferedImage background;

    private ArrayList<Tile> tiles;
    private Tile player;

    private int bombCnt;
    private Tile bomb;
    private Tile countdownTile;


    private static Game instance;

    private double angle = 0;
    private double speedX = 0;
    private double speedY = 0;
    private double radius = 10.0;
    private double gravity = -0.1;

    private static int bombW = 30;
    private static int bombH = 30;


    private static int scalingFactor = 3;

    private static int explodeCountdown = 300;
    private static int explodeLimit = 300;

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


        for(int i = 0; i < PARTICLE_MAX; ++i)
            parts[i] = new Particle();


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
        setUpdateRate(90);
        startThread();
    }

    @Override
    public void render(Graphics2D g, int i, int i1) {
        g.drawImage(background, 0, 0, null);

        for (Tile tile : this.tiles) {
            g.drawImage(tile.getImage(), tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), null);
        }
        if (bomb != null) {
            g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), bomb.getWidth(), bomb.getHeight(), null);
            if(explodeCountdown < explodeLimit){
                g.drawImage(countdownTile.getImage(), countdownTile.getX(), countdownTile.getY(), countdownTile.getWidth(), countdownTile.getHeight(), null);
            }
        }

        //varnice
        g.setColor(Color.YELLOW);

        for(Particle p : parts)
        {
            if(p.life <= 0) continue;

            g.drawLine((int)(p.posX - p.dX), (int)(p.posY - p.dY), (int)p.posX, (int)p.posY);
        }
        player.render(g);

    }

    @Override
    public void update() {

        // na klik se pojavljuje bomba
        if (isMouseButtonDown(GFMouseButton.Left)) {
            if (bomb == null) {
                BOMB_GROWING = true;
                bombCnt = 1;
                bomb = new Tile("Tiles/bomb.png", getMouseX() - bombW / 2, getMouseY() - bombH / 2, bombW, bombH, "b");
            }
            bombCnt++;

            if(bombCnt > BOMB_CHANGING_LIMIT) {
                bombCnt = 1;
                if(BOMB_GROWING) {
                    BOMB_GROWING = false;
                } else {
                    BOMB_GROWING = true;
                }
            }

            if (timeToChange(bombCnt)) {
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
            genEx(bomb.getX() + bomb.getHeight() / 1.65f, bomb.getY(), 4.0f, bomb.getHeight() / 3, 3);
            explodeCountdown--;
            if(explodeCountdown == 0) {
                handleBoom();
                bomb = null;
                explodeCountdown = explodeLimit;
            } else {
                int numberToShow = explodeCountdown / 60;
                int countdownTileX = getX() + getWidth() / 2 - bomb.getWidth()/(2*scalingFactor) ;
                int countdownTileY = getY()+ getHeight()/ 2 - bomb.getHeight()/(2*scalingFactor);
                int countdownTileW = bomb.getWidth()/scalingFactor;
                int countdownTileH = bomb.getHeight()/scalingFactor;
                switch (numberToShow) {
                    case 0:
                        if(countdownTile == null || !countdownTile.getType().equals("1")) {
                            countdownTile = new Tile("Tiles/number1.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "1");
                        }
                        break;
                    case 1:
                        if(countdownTile == null || !countdownTile.getType().equals("2")) {
                            countdownTile = new Tile("Tiles/number2.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "2");
                        }
                        break;
                    case 2:
                        if(countdownTile == null || !countdownTile.getType().equals("3")) {
                            countdownTile = new Tile("Tiles/number3.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "3");
                        }
                        break;
                    case 3:
                        if(countdownTile == null || !countdownTile.getType().equals("4")) {
                            countdownTile = new Tile("Tiles/number4.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "4");
                        }
                        break;
                    default:
                        if(countdownTile == null || !countdownTile.getType().equals("5")) {
                            countdownTile = new Tile("Tiles/number5.png", countdownTileX, countdownTileY , countdownTileW, countdownTileH, "5");
                        }
                }
                if(explodeCountdown % 5 == 0) {
                    countdownTile.setX(countdownTile.getX() - BOMB_CHANGING_FACTOR / 2);
                    countdownTile.setY(countdownTile.getY() - BOMB_CHANGING_FACTOR / 2);
                    countdownTile.setWidth(countdownTile.getWidth() + BOMB_CHANGING_FACTOR);
                    countdownTile.setHeight(countdownTile.getHeight() + BOMB_CHANGING_FACTOR);
                }

            }
        }

        for(Particle p : parts)
        {
            if(p.life <= 0) continue;

            p.life--;
            p.posX += p.dX;
            p.posY += p.dY;
            p.dX *= 0.99f;
            p.dY *= 0.99f;
            p.dY += 0.1f;

        }

        handleMovement();
        player.update();
    }



    /**
     * We are changing bomb size every 10th frame
     * @param bombCnt - frame counter
     * @return true if bomb needs to grow/decrease
     */
    private boolean timeToChange(int bombCnt) {
        return bombCnt % 4 == 0;
    }


    private void genEx(float cX, float cY, float radius, int life, int count)
    {
        for(Particle p : parts)
        {
            if(p.life <= 0)
            {
                p.life = (int)(Math.random() * life * 0.5) + life / 2;
                p.posX = cX;
                p.posY = cY;
                double angle = Math.random() * Math.PI * 2.0;
                double speed = Math.random() * radius;
                p.dX = (float)(Math.cos(angle) * speed);
                p.dY = (float)(Math.sin(angle) * speed);

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

    }

    public void setAngle() {

        angle = Math.toDegrees(Math.atan2(player.getY() + (player.getHeight() / 2) - (bomb.getY() + bombH / 2), player.getX() + (player.getWidth() / 2) - (bomb.getX() + bombW / 2)));

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
