package me.boom;

import rafgfxlib.Util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile
{

    private BufferedImage image = null;

    private int x;
    private int y;
    private int width;
    private int height;

    private Shape shape;

    private String type;


    public Tile(String fileName, int x, int y, int width, int height, String type)
    {
        this.image = Util.loadImage(fileName);


        this.shape = new Rectangle(x, y, width, height);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

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

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void render(Graphics2D g) {

    }

    public void update() {

    }
}