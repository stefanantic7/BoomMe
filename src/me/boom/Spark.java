package me.boom;

import java.awt.*;

public class Spark {

    public float posX;
    public float posY;
    public float dX;
    public float dY;
    public int life = 0;

    public void render(Graphics2D g) {
        g.setColor(Color.YELLOW);
        if(this.life > 0) {
            g.drawLine((int)(this.posX - this.dX), (int)(this.posY - this.dY), (int)this.posX, (int)this.posY);
        }
    }

    public void update() {
        if(this.life > 0) {
            this.life--;
            this.posX += this.dX;
            this.posY += this.dY;
            this.dX *= 0.99f;
            this.dY *= 0.99f;
            this.dY += 0.1f;

        }
    }

    public void reGenerate(float x, float y, float radius, int life) {
        this.life = (int) (Math.random() * life * 0.5) + life / 2;
        this.posX = x;
        this.posY = y;
        double angle = Math.random() * Math.PI * 2.0;
        double speed = Math.random() * radius;
        this.dX = (float) (Math.cos(angle) * speed);
        this.dY = (float) (Math.sin(angle) * speed);
    }
}
