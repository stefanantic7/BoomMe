package me.boom;

import rafgfxlib.GameFrame;

import java.awt.*;
import java.util.ArrayList;

public class Player extends Tile {

    private int defaultX;
    private int defaultY;

    private ArrayList<Tile> hearts;

    public Player(String fileName, int x, int y, int width, int height, int livesCounter) {
        super(fileName, x, y, width, height, "p");
        this.defaultX = x;
        this.defaultY = y;

        this.hearts = new ArrayList<>();
        for (int i = 0; i < livesCounter; i++) {
            Tile heart = new Tile("Tiles/heart.png",i*25, 0, 25, 25, "h");
            hearts.add(heart);
        }


    }

    public void update() {
        checkDeath();
    }

    public void render(Graphics2D g) {
        for (Tile heart:hearts) {
            g.drawImage(heart.getImage(), heart.getX(), heart.getY(), heart.getWidth(), heart.getHeight(), null);
        }
    }

    private void checkDeath() {
        if(this.getY() > Game.getInstance().getHeight()) {

            hearts.remove(hearts.size()-1);

            if(hearts.isEmpty()) {
                Game.getInstance().closeGame();
            }
            this.setX(defaultX);
            this.setY(defaultY);
        }
    }

}
