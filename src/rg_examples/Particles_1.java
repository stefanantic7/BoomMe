package rg_examples;

import java.awt.Color;
import java.awt.Graphics2D;

import rafgfxlib.GameFrame;

public class Particles_1 extends GameFrame
{
	private static final long serialVersionUID = -7968395365237904502L;

	public static class Particle
	{
		public float posX;
		public float posY;
		public float dX;
		public float dY;
		public int life = 0;
	}
	
	private static final int PARTICLE_MAX = 350;
	
	private Particle[] parts = new Particle[PARTICLE_MAX];

	protected Particles_1(String title, int sizeX, int sizeY)
	{
		super(title, sizeX, sizeY);
		setUpdateRate(60);
		
		for(int i = 0; i < PARTICLE_MAX; ++i)
			parts[i] = new Particle();
		
		startThread();
	}
	
	public static void main(String[] args)
	{
		GameFrame gf = new Particles_1("RAF Particles 1", 800, 600);
		gf.initGameWindow();
	}

	@Override
	public void render(Graphics2D g, int sw, int sh)
	{
		g.setColor(Color.yellow);

		for(Particle p : parts)
		{
			if(p.life <= 0) continue;

			g.drawLine((int)(p.posX - p.dX), (int)(p.posY - p.dY), (int)p.posX, (int)p.posY);
		}

	}

	@Override
	public void update()
	{
		if(isMouseButtonDown(GFMouseButton.Right))
			genEx(getMouseX(), getMouseY(), 4.0f, 200, 3);
		
		for(Particle p : parts)
		{
			if(p.life <= 0) continue;
			
			p.life--;
			p.posX += p.dX;
			p.posY += p.dY;
			p.dX *= 0.99f;
			p.dY *= 0.99f;
			p.dY += 0.1f;
			
			if(p.posX < 0)
			{
				p.posX = 0.01f;
				p.dX = Math.abs(p.dX) * (float)Math.random() * 0.6f;
			}
			
			if(p.posY < 0)
			{
				p.posY = 0.01f;
				p.dY = Math.abs(p.dY) * (float)Math.random() * 0.6f;
			}
			
			if(p.posX > getWidth())
			{
				p.posX = getWidth() - 0.01f;
				p.dX = Math.abs(p.dX) * (float)Math.random() * -0.6f;
			}
			
			if(p.posY > getHeight())
			{
				p.posY = getHeight() - 0.01f;
				p.dY = Math.abs(p.dY) * (float)Math.random() * -0.6f;
			}
		}
	}
	
	private void genEx(float cX, float cY, float radius, int life, int count)
	{
		for(Particle p : parts)
		{
			if(p.life <= 0)
			{
				p.life = (int)(Math.random() * life * 0.5) + life / 2;
				p.posX = cX;
				p.posY = cY;
				double angle = Math.random() * Math.PI * 2.0;
				double speed = Math.random() * radius;
				p.dX = (float)(Math.cos(angle) * speed);
				p.dY = (float)(Math.sin(angle) * speed);
				
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
