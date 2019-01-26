package me.boom;

import java.awt.*;

public class Bomb extends Tile {

    private static final int SPARKS_MAX = 350;
    private static final int BOMB_CHANGING_FACTOR = 10;
    private static final int BOMB_CHANGING_LIMIT = 50;


    private Tile countdownTile;

    private int bombChangingCount;
    /**
     * How much we are moving - this makes shaking effect
     */
    private int[] shakeX = {0, 2, -1, -3, 0, -2, 1, 3, 0};
    private int[] shakeY = {0, 1, -2, -1, 2, -1, 2, 0, -1};
    /**
     * Counter for shakeX/Y array
     */
    private int shakeCount = 0;

    /**
     * Bomb explodes when it's 0
     */
    private int explodeCountdown;

    private int explodeCountdownStart;

    private int updateRate;

    private Spark[] sparks;

    private boolean growing;

    public Bomb(String fileName, int x, int y, int width, int height, int bombChangingCount, int updateRate) {
        super(fileName, x, y, width, height);
        this.bombChangingCount = bombChangingCount;

        this.updateRate = updateRate;
        this.explodeCountdown = this.explodeCountdownStart = updateRate * 5; // because we are counting down from 5

        this.sparks = new Spark[SPARKS_MAX];
        for (int i = 0; i < SPARKS_MAX; ++i) {
            sparks[i] = new Spark();
        }

        this.growing = true;
    }

    /**
     * We are changing bomb size every 10th frame
     *
     * @return true if bomb needs to grow/decrease
     */
    public boolean isTimeToChange() {
        return this.bombChangingCount % 4 == 0;
    }

    @Override
    public void update() {
        this.explodeCountdown--;
        generateSparks(this.getX() + this.getHeight() / 1.65f, this.getY(), 4.0f, this.getHeight() / 4, 8 - this.explodeCountdown / this.updateRate);
        if (!this.isTimeToExplode()) {
            handleCountdown();
        }

        for (Spark spark : sparks) {
            spark.update();
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(this.getImage(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
        if (this.explodeCountdown < this.explodeCountdownStart) {
            g.drawImage(countdownTile.getImage(), countdownTile.getX(), countdownTile.getY(), countdownTile.getWidth(), countdownTile.getHeight(), null);
        }

        for (Spark spark : sparks) {
            spark.render(g);
        }
    }

    /**
     * Generate new sparks.
     *
     * @param cX
     * @param cY
     * @param radius
     * @param life
     * @param count
     */
    private void generateSparks(float cX, float cY, float radius, int life, int count) {
        for (Spark spark : sparks) {
            if (spark.life <= 0) {
                spark.reGenerate(cX, cY, radius, life);

                count--;
                if (count <= 0) return;
            }
        }
    }

    public boolean isTimeToExplode() {
        return this.explodeCountdown == 0;
    }


    private void handleCountdown() {
        int countdownTileW = (int) (this.getWidth() * 0.3);
        int countdownTileH = (int) (this.getHeight() * 0.3);
        int countdownTileX = this.getX() + this.getWidth() / 2 - countdownTileW;
        int countdownTileY = this.getY() + this.getHeight() / 2 - countdownTileH;

        int currentNumber = this.explodeCountdown / this.updateRate + 1;
        switch (currentNumber) {
            case 1:
                if (countdownTile == null || !countdownTile.getType().equals(TileType.NO1)) {
                    countdownTile = new Tile("Tiles/number1.png", countdownTileX, countdownTileY, countdownTileW, countdownTileH, TileType.NO1);
                }
                shakeBomb(currentNumber);
                break;
            case 2:
                if (countdownTile == null || !countdownTile.getType().equals(TileType.NO2)) {
                    countdownTile = new Tile("Tiles/number2.png", countdownTileX, countdownTileY, countdownTileW, countdownTileH, TileType.NO2);
                }
                shakeBomb(currentNumber);
                break;
            case 3:
                if (countdownTile == null || !countdownTile.getType().equals(TileType.NO3)) {
                    countdownTile = new Tile("Tiles/number3.png", countdownTileX, countdownTileY, countdownTileW, countdownTileH, TileType.NO3);
                }
                shakeBomb(currentNumber);
                break;
            case 4:
                if (countdownTile == null || !countdownTile.getType().equals(TileType.NO4)) {
                    countdownTile = new Tile("Tiles/number4.png", countdownTileX, countdownTileY, countdownTileW, countdownTileH, TileType.NO4);
                }
                break;
            default:
                if (countdownTile == null || !countdownTile.getType().equals(TileType.NO5)) {
                    countdownTile = new Tile("Tiles/number5.png", countdownTileX, countdownTileY, countdownTileW, countdownTileH, TileType.NO5);
                }
        }

        if (this.explodeCountdown % 3 == 0) {
            countdownTile.setWidth(countdownTile.getWidth() + 1);
            countdownTile.setHeight(countdownTile.getHeight() + 1);
            countdownTile.setX(this.getX() + this.getWidth() / 2 - countdownTile.getWidth() / 2);
            countdownTile.setY(this.getY() + this.getHeight() / 2 - countdownTile.getHeight() / 2);
        }
    }

    /**
     * Bomb is shaking only if current number is 3 or less.
     * As currentNumber is lower, the bomb shaking is more frequent,
     * so if currentNumber is 3 it will shake every 4th frame, and if currentNumber is 2 it will shake every third frame...
     * On numbers 2 and 1 we are adding additional moving so it looks like bomb is shaking  more
     *
     * @param currentNumber - number on the bomb
     */
    private void shakeBomb(int currentNumber) {
        boolean moved = false;
        switch (currentNumber) {
            case 1:
                if (this.explodeCountdown % 2 == 0) {
                    this.setX(this.getX() + shakeX[shakeCount] * 2);
                    this.setY(this.getY() + shakeY[shakeCount] * 2);
                    moved = true;
                }
                break;
            case 2:
                if (this.explodeCountdown % 3 == 0) {
                    this.setX(this.getX() + shakeX[shakeCount] * 2);
                    this.setY(this.getY() + shakeY[shakeCount] * 2);
                    moved = true;
                }
                break;
            case 3:
                if (this.explodeCountdown % 4 == 0) {
                    this.setX(this.getX() + shakeX[shakeCount]);
                    this.setY(this.getY() + shakeY[shakeCount]);
                    moved = true;
                }
                break;
        }
        if (moved) {
            shakeCount++;
            if (shakeCount == shakeX.length) shakeCount = 0;
        }
    }

    public void increaseBombChangingCount() {
        this.bombChangingCount++;
    }

    public void sizeUp() {
        this.setHeight(this.getHeight() + BOMB_CHANGING_FACTOR);
        this.setWidth(this.getWidth() + BOMB_CHANGING_FACTOR);
        this.setX(this.getX() - BOMB_CHANGING_FACTOR / 2);
        this.setY(this.getY() - BOMB_CHANGING_FACTOR / 2);
    }

    public void sizeDown() {
        this.setHeight(this.getHeight() - BOMB_CHANGING_FACTOR);
        this.setWidth(this.getWidth() - BOMB_CHANGING_FACTOR);
        this.setX(this.getX() + BOMB_CHANGING_FACTOR / 2);
        this.setY(this.getY() + BOMB_CHANGING_FACTOR / 2);

    }

    public boolean isGrowing() {
        return growing;
    }

    public void toggleGrowing() {
        this.growing = !this.growing;
    }

    public void changeSize() {
        if (this.bombChangingCount > BOMB_CHANGING_LIMIT) {
            this.bombChangingCount = 1;
            this.toggleGrowing();
        }
        if(this.isTimeToChange()) {
            if(this.isGrowing()) {
                this.sizeUp();
            }
            else {
                this.sizeDown();
            }
        }
    }
}
