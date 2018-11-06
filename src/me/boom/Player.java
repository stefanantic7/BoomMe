package me.boom;

import rafgfxlib.GameFrame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Player extends Tile {

    public static final float DEFAULT_ROTATION_SPEED = 0.215f;
    public static final int LEFT_ROTATION_DIRECTION = 1;
    public static final int RIGHT_ROTATION_DIRECTION = 2;

    private int defaultX;
    private int defaultY;

    private float rotationSpeed;
    private float rotationAngle;
    private boolean enabledRotation;
    private int rotationDirection;

    private ArrayList<Tile> hearts;

    private AffineTransform playerTransformation;

    public Player(String fileName, int x, int y, int width, int height, int livesCounter) {
        super(fileName, x, y, width, height, "p");
        this.defaultX = x;
        this.defaultY = y;
        this.rotationSpeed = DEFAULT_ROTATION_SPEED;
        this.rotationAngle = 0;
        this.enabledRotation = false;
        this.playerTransformation = new AffineTransform();

        this.hearts = new ArrayList<>();
        for (int i = 0; i < livesCounter; i++) {
            Tile heart = new Tile("Tiles/heart.png",i*25, 0, 25, 25, "h");
            hearts.add(heart);
        }


    }

    public void update() {
        updateTransformation();
        checkDeath();
    }

    public void render(Graphics2D g) {


        for (Tile heart:hearts) {
            g.drawImage(heart.getImage(), heart.getX(), heart.getY(), heart.getWidth(), heart.getHeight(), null);
        }



        g.drawImage(this.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_DEFAULT), playerTransformation, null);
//        g.drawImage(this.getImage(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);


    }

    private void checkDeath() {
        if(this.getY() > Game.getInstance().getHeight()) {

            this.disableRotation();

            hearts.remove(hearts.size()-1);

            if(hearts.isEmpty()) {
                Game.getInstance().closeGame();
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
            if(rotationDirection == LEFT_ROTATION_DIRECTION) {
                rotationDirection = -rotationDirection;
            }

            playerTransformation.rotate(rotationAngle);
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
        this.rotationSpeed = DEFAULT_ROTATION_SPEED;
        this.rotationDirection = leftOrRightDirection;
    }

    public void disableRotation() {
        this.enabledRotation = false;
    }
}
