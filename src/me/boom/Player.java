package me.boom;

import java.awt.*;

public class Player extends Tile {

    private int defaultX;
    private int defaultY;

    public Player(String fileName, int x, int y, int width, int height) {
        super(fileName, x, y, width, height, "p");
        this.defaultX = x;
        this.defaultY = y;
    }

    public void update() {
        checkDeath();
    }

    public void render(Graphics2D g) {

    }

    private void checkDeath() {
        if(this.getY() > Game.getInstance().getHeight()) {
            this.setX(defaultX);
            this.setY(defaultY);
        }
    }

}
