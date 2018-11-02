package me.boom;

import rafgfxlib.GameFrame;
import rafgfxlib.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Game extends GameFrame {

    private BufferedImage background;

    private ArrayList<Tile> tiles;
    private Tile player;

    private int windowWidth;
    private int windowHeight;

    private String[][] bitMap;

    public Game(int sizeX, int sizeY, String[][] bitMap) {
        super("Boom Me", sizeX, sizeY);

        this.windowWidth = sizeX;
        this.windowHeight = sizeY;
        this.bitMap = bitMap;

        this.tiles = new ArrayList<>();
        this.background = Util.loadImage("bg/BG.png");

    }

    private void loadTiles() {
        int rows = bitMap.length;
        int columns = bitMap[0].length;

        for(int i = 0; i < bitMap.length; i++) {
            for(int j = 0; j < bitMap[i].length; j++) {
                int width = this.windowWidth/columns;
                int height = this.windowHeight/rows;


                if(bitMap[i][j].equals("#")) {
                    Tile tile = new Tile("Tiles/"+"player"+".png",
                            j*width, i*height, width, height);
                    player = tile;
                }
                else {
                    Tile tile = new Tile("Tiles/"+bitMap[i][j]+".png",
                            j*width, i*height, width, height);
                    tiles.add(tile);
                }
            }
        }
    }

    @Override
    public void handleWindowInit() {
        loadTiles();

        setUpdateRate(60);
        startThread();
    }

    @Override
    public void render(Graphics2D g, int i, int i1) {
        g.drawImage(background,0, 0, null );

        for (Tile tile : this.tiles) {
            g.drawImage(tile.getImage(), tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), null);
        }
        g.drawImage(player.getImage(), player.getX(), player.getY(), player.getWidth(), player.getHeight(), null);

//        g.setColor(Color.yellow);
//        g.drawRect(selX * TILE_W / 4, selY * TILE_H / 4, TILE_W / 4, TILE_H / 4);
    }

    @Override
    public void update() {

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
}
