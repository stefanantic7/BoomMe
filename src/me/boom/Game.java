package me.boom;

import me.maps.MapLoader;
import rafgfxlib.GameFrame;
import rafgfxlib.Util;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Scanner;

public class Game extends GameFrame {

    private static Game instance;


    private BufferedImage background;

    private ArrayList<Tile> tiles;
    private Tile player;

    private int bombCnt;
    private Tile bomb;

    private double angle = 0;
    private double speedX = 0;
    private double speedY = 0;
    private double radius = 10.0;
    private double gravity = -0.1;


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
        g.drawImage(player.getImage(), player.getX(), player.getY(), player.getWidth(), player.getHeight(), null);
        if (bomb != null) {
            g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), bomb.getWidth(), bomb.getHeight(), null);
        }

        player.render(g);
    }

    @Override
    public void update() {

        // na klik se pojavljuje bomba

        if (isMouseButtonDown(GFMouseButton.Left)) {
            if (bomb == null) {
                bombCnt = 1;
                bomb = new Tile("Tiles/Bomb.png", getMouseX(), getMouseY(), 30, 30, "b");
            }
            bombCnt++;
            if (bombCnt % 15 == 0) {
                bomb.setHeight(bomb.getHeight() + 2);
                bomb.setWidth(bomb.getWidth() + 2);
            }
        } else if (bomb != null) {
            handleBoom();
            bomb = null;
        }

        handleMovement();

        player.update();

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

        angle = Math.toDegrees(Math.atan2(player.getY() + (player.getHeight() / 2) - bomb.getY(), player.getX() + (player.getWidth() / 2) - bomb.getX()));

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

        speedX = 7;
        speedY = 7;


    }

    public void handleMovement() {

        speedY -= gravity;

        double dX = (float) (Math.cos(angle) * speedX);
        double dY = (float) (Math.sin(angle) * speedY);

        if (!handleColision(dX, dY)) {


            player.setX((int) (player.getX() + dX));
            player.setY((int) (player.getY() + dY));
        }
        //
        //
// }

    }

    private boolean handleColision(double dX, double dY) {

        for (Tile t : tiles) {
            if (t.getType().equals("g")) {
//                (x1 + w1) - x2 >= 0 and (x2 + w2) - x1 >= 0


                Area temp = new Area(t.getShape());
                if (temp.intersects((int) (player.getX() + dX), (int) (player.getY() + dY), player.getWidth(), player.getHeight())) {
                    Scanner sc = new Scanner(System.in);
//                    sc.next();
                    System.out.println((player.getY() + " " + player.getX() + " " + t.getX() + " " + t.getY() + " " + t.getHeight() + " " + t.getWidth()));
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
                            System.out.println("evo me preko dy > 0");
                            player.setX(t.getX() + t.getWidth() + 2);
                            angle = Math.PI - angle;
                        } else {
                            System.out.println("evo me preko dy < 0");
                            player.setY(t.getY() + t.getHeight() + 2);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;
                        }
                    }

                    if (dX > 0 && dY < 0) {
                        if (player.getY() <= t.getY() + t.getHeight()) {
                            System.out.println("evo me preko dy > 0");
                            player.setX(t.getX() - t.getWidth());
                            angle = Math.PI - angle;

                        } else {
                            System.out.println("evo me preko dy < 0");
                            player.setY(t.getY() + t.getHeight() + 2);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;
                        }
                    }

                    if (dX < 0 && dY > 0) {

                        if (player.getY() + player.getHeight() <= t.getY()) {
                            speedX = 0;
                            speedY = 0;
                            System.out.println("evo me preko dy > 0");
                            player.setY(t.getY() - player.getHeight() - 2);
                            angle = Math.PI - angle;
                        } else {
                            System.out.println("evo me preko dy < 0");
//                            player.setX(t.getX() + t.getWidth() + 2);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;

                        }
                    }

                    if (dX > 0 && dY > 0) {
                        if (player.getY() + player.getHeight() <= t.getY()) {
                            speedX = 0;
                            speedY = 0;
                            System.out.println("evo me preko dy > 0");
                            player.setY(t.getY() - player.getHeight() - 2);
                            angle = Math.PI - angle;
                        } else {

                            System.out.println("evo me preko dy < 0");
                            player.setX(t.getX() - player.getWidth() - 4);
                            angle = 2 * Math.PI - angle;
                            gravity *= -1;

                        }
                    }

                    if(speedX > 0)
                        speedX -= 0.3;

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
