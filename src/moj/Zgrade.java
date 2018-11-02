package moj;

import rafgfxlib.GameFrame;
import rg_examples.Particles_1;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Zgrade extends GameFrame
{

    public Zgrade(String title, int sizeX, int sizeY) {
        super(title, sizeX, sizeY);
        setUpdateRate(60);


        startThread();
    }

    AffineTransform tx1 = new AffineTransform();
    AffineTransform tx2 = new AffineTransform();
    AffineTransform tx3 = new AffineTransform();





    @Override
    public void render(Graphics2D g, int sw, int sh) {
//        g.setPaint(Color.green);
//        g.drawRect(sw / 2, sh / 2, 10 , 10);

        tx1.setToIdentity();
        tx1.translate(sw, sh);

        g.setTransform(tx1);
        g.setPaint(Color.green);
        g.drawRect(0, 0, 80, 50);

        tx2.setToIdentity();
        tx2.translate(sw, sh);
        tx2.shear(0, 1);

        g.setTransform(tx2);
        g.setPaint(Color.blue);
        g.drawRect(-80, -50, 80, 50);

        tx3.setToIdentity();
        tx3.translate(sw , sh);
        tx3.shear(0, 3);

        g.setTransform(tx3);
        g.setPaint(Color.red);
        g.drawRect(0, 0, 80, 50);

        g.dispose();

    }

    @Override
    public void update() {

    }

    @Override
    public void handleMouseDown(int i, int i1, GFMouseButton gfMouseButton) {

    }

    @Override
    public void handleMouseUp(int i, int i1, GFMouseButton gfMouseButton) {

    }

    @Override
    public void handleMouseMove(int i, int i1) {

    }

    @Override
    public void handleKeyDown(int i) {

    }

    @Override
    public void handleKeyUp(int i) {

    }


    @Override
    public void handleWindowInit() {

    }

    @Override
    public void handleWindowDestroy() {

    }


    public static void main(String[] args)
    {
        GameFrame gf = new Zgrade("RAF  1", 800, 600);
        gf.initGameWindow();
    }
}
