package me.boom;

import rafgfxlib.GameFrame;
import rafgfxlib.Util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class Player extends Tile {

    public static final float DEFAULT_ROTATION_SPEED = 0.215f;
    public static final int LEFT_ROTATION_DIRECTION = 1;
    public static final int RIGHT_ROTATION_DIRECTION = 2;

    public static final int WARNING_COUNTER_MAX = 60;
    private int currentWarningCounter = 0;
    private BufferedImage warningImage;

    private int defaultX;
    private int defaultY;

    private float rotationSpeed;
    private float rotationAngle;
    private boolean enabledRotation;
    private int rotationDirection;

    private boolean isDeath;

    private ArrayList<Tile> hearts;

    private AffineTransform playerTransformation;

    public Player(String fileName, int x, int y, int width, int height, int livesCounter) {
        super(fileName, x, y, width, height, TileType.PLAYER);
        this.defaultX = x;
        this.defaultY = y;
        this.rotationSpeed = DEFAULT_ROTATION_SPEED;
        this.rotationAngle = 0;
        this.enabledRotation = false;
        this.isDeath = false;
        this.playerTransformation = new AffineTransform();

        this.hearts = new ArrayList<>();
        for (int i = 0; i < livesCounter; i++) {
            Tile heart = new Tile("Tiles/heart.png",i*25, 0, 25, 25, TileType.NONE);
            hearts.add(heart);
        }

        loadWarningImage();
    }

    private void loadWarningImage() {
        int rgb[] = new int[4];

        WritableRaster source = super.getImage().getRaster();
        WritableRaster target = Util.createRaster(super.getImage().getWidth(),super.getImage().getHeight(),true);

        for(int y=0;y<super.getImage().getHeight();y++) {
            for(int x=0;x<super.getImage().getWidth();x++) {
                source.getPixel(x,y,rgb);
                if(rgb[3] != 0) {

                    rgb[1] = 0;
                    rgb[2] = 0;
                    rgb[3] = 255;
                }

                target.setPixel(x,y,rgb);
            }
        }

        this.warningImage = Util.rasterToImage(target);
    }

    public void update() {
        updateTransformation();
        checkDeath();
    }

    public void render(Graphics2D g) {

        if(currentWarningCounter > 0) {
            --currentWarningCounter;
        }


        for (Tile heart:hearts) {
            g.drawImage(heart.getImage(), heart.getX(), heart.getY(), heart.getWidth(), heart.getHeight(), null);
        }


        g.drawImage(this.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_DEFAULT), playerTransformation, null);


    }

    private void checkDeath() {
        if(this.getY() > Game.getInstance().getHeight()) {

            this.disableRotation();

            hearts.remove(hearts.size()-1);

            if(hearts.isEmpty()) {
                isDeath = true;
            }
            this.setX(defaultX);
            this.setY(defaultY);
        }
    }

    private void updateTransformation() {
        playerTransformation.setToIdentity();

        playerTransformation.translate(this.getX(), this.getY());

        if(enabledRotation) {
            playerTransformation.translate(this.getWidth()/2, this.getHeight()/2);

            rotationAngle += rotationSpeed;
            float rotationAngleDirection = rotationAngle;
            if(rotationDirection == LEFT_ROTATION_DIRECTION) {
                rotationAngleDirection = (-1)*rotationAngle;
            }

            playerTransformation.rotate(rotationAngleDirection);
            if(rotationSpeed>0) {
                rotationSpeed -= 0.001;
            }
            else {
                rotationSpeed=0;
            }
            playerTransformation.translate(-this.getWidth()/2, -this.getHeight()/2);

        }


    }

    public void enableRotation(int leftOrRightDirection) {
        this.enabledRotation = true;
        this.rotationSpeed = Player.DEFAULT_ROTATION_SPEED;
        this.rotationDirection = leftOrRightDirection;
    }

    public void disableRotation() {
        this.enabledRotation = false;
    }

    public boolean isDeath() {
        return isDeath;
    }

    public void hit() {
        currentWarningCounter = WARNING_COUNTER_MAX;
    }

    @Override
    public BufferedImage getImage() {
        BufferedImage image = super.getImage();

        if(currentWarningCounter>0) {

            return warningImage;
        }


        return image;
    }

}
