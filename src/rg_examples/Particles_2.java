package rg_examples;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import rafgfxlib.GameFrame;
import rafgfxlib.Util;

public class Particles_2 extends GameFrame
{
	private static final long serialVersionUID = -7968395365237904502L;

	private static final int MAX_SPRITES = 6;
	private BufferedImage[] partImages = new BufferedImage[MAX_SPRITES];
	
	public static class Particle
	{
		public float posX;
		public float posY;
		public float dX;
		public float dY;
		public int life = 0;
		public int lifeMax = 0;
		public int imageID = 0;
		public float angle = 0.0f;
		public float rot = 0.0f;
	}
	
	private static final int PARTICLE_MAX = 1000;
	
	private Particle[] parts = new Particle[PARTICLE_MAX];

	protected Particles_2(String title, int sizeX, int sizeY)
	{
		super(title, sizeX, sizeY);
		setUpdateRate(60);
		
		for(int i = 0; i < PARTICLE_MAX; ++i)
			parts[i] = new Particle();
		
		for(int i = 0; i < MAX_SPRITES; ++i)
			partImages[i] = Util.loadImage("stars/star" + (i + 1) + ".png");
		
		startThread();
	}
	
	public static void main(String[] args)
	{
		GameFrame gf = new Particles_2("RAF Particles 2", 800, 600);
		gf.initGameWindow();
	}

	@Override
	public void render(Graphics2D g, int sw, int sh)
	{
		AffineTransform transform = new AffineTransform();
		
		for(Particle p : parts)
		{
			if(p.life <= 0) continue;
			
			transform.setToIdentity();
			transform.translate(p.posX, p.posY);
			transform.rotate(p.angle);
			transform.translate(-16.0, -16.0);
			
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    (float)p.life / (float)p.lifeMax));

			g.drawImage(partImages[p.imageID], transform, null);
		}
		
	}

	@Override
	public void update()
	{
		if(isMouseButtonDown(GFMouseButton.Right))
			genEx(getMouseX(), getMouseY(), 0.0f, 200, 2);
		
		for(Particle p : parts)
		{
			if(p.life <= 0) continue;
			
			p.life--;
			p.posX += p.dX;
			p.posY += p.dY;
			p.dX *= 0.99f;
			p.dY *= 0.99f;
			p.dY += 0.1f;
			p.angle += p.rot;
			p.rot *= 0.99f;
		}
	}
	
	private void genEx(float cX, float cY, float radius, int life, int count)
	{
		for(Particle p : parts)
		{
			if(p.life <= 0)
			{
				p.life = p.lifeMax = (int)(Math.random() * life * 0.5) + life / 2;
				p.posX = cX;
				p.posY = cY;
				double angle = Math.random() * Math.PI * 2.0;
				double speed = 1.0 * radius;
				p.dX = (float)(Math.cos(angle) * speed);
				p.dY = (float)(Math.sin(angle) * speed);
				p.angle = (float)(Math.random() * Math.PI * 2.0);
				p.rot = (float)(Math.random() - 0.5) * 0.3f;
				
				p.imageID = (int)(Math.random() * MAX_SPRITES);
				
				count--;
				if(count <= 0) return;
			}
		}
	}

	@Override
	public void handleMouseDown(int x, int y, GFMouseButton button)
	{
		genEx(x, y, 10.0f, 300, 50);
	}

	@Override
	public void handleWindowInit() { }

	@Override
	public void handleWindowDestroy() { }
	
	@Override
	public void handleMouseUp(int x, int y, GFMouseButton button) { }

	@Override
	public void handleMouseMove(int x, int y) { }

	@Override
	public void handleKeyDown(int keyCode) { }

	@Override
	public void handleKeyUp(int keyCode) { }

}