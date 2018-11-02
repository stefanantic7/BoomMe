package me.boom;

import rafgfxlib.Util;
import java.awt.image.BufferedImage;

class Tile
{

    private BufferedImage image = null;

    private int x;
    private int y;
    private int width;
    private int height;


    public Tile(String fileName, int x, int y, int width, int height)
    {
        this.image = Util.loadImage(fileName);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        if(this.image == null)
        {
            System.out.println("Fail at \"" + fileName + "\"");
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}