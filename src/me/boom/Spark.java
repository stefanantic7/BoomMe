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
}
