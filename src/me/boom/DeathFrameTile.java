package me.boom;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DeathFrameTile {
    public static int WIDTH = 50;
    public static int HEIGHT = 50;

    private int x;
    private int y;
    private float dY;

    private BufferedImage image;

    public DeathFrameTile(BufferedImage bufferedImage, int x, int y) {
        this.image = bufferedImage;
        this.x = x;
        this.y = y;

        this.dY = ((float) Math.random() * 10);
    }

    public void update() {
        this.y += this.dY;
        this.dY += 0.01f;
    }

    public void render(Graphics2D g) {
        g.drawImage(this.image,this.x, this.y, WIDTH, HEIGHT, null);
    }

    public int getY() {
        return y;
    }
}
