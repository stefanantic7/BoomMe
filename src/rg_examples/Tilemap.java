package rg_examples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import rafgfxlib.GameFrame;
import rafgfxlib.Util;

public class Tilemap extends GameFrame 
{
	private static final long serialVersionUID = 7636268720113395780L;
	
	private static final int TILE_W = 64;
	private static final int TILE_H = 64;
	
	private int mapW = 100;
	private int mapH = 100;
	
	private int camX = 0;
	private int camY = 0;
	
	private int selX = 0;
	private int selY = 0;
	
	private class Tile
	{
		public BufferedImage image = null;
		public int offsetX = 0;
		public int offsetY = 0;
		@SuppressWarnings("unused")
		public int tileID = 0;
		
		public Tile(String fileName, int ID)
		{
			image = Util.loadImage(fileName);
			tileID = ID;
			if(image != null)
			{
				offsetX = 0;
				offsetY = -(image.getHeight() - TILE_H);
			}
			else
			{
				System.out.println("Fail at \"" + fileName + "\"");
			}
		}
	}
	
	private Tile[] tileset = new Tile[32];
	private int[][] tileMap = new int[mapW][mapH];
	
	public Tilemap()
	{
		super("RAF Tilemap", 640, 480);
		
		setUpdateRate(60);
		
		for(int i = 0; i <= 16; ++i)
		{
			tileset[i] = new Tile("tileset/svgset" + i + ".png", i);
		}
		
		Random rnd = new Random();
		for(int y = 0; y < mapH; ++y)
		{
			for(int x = 0; x < mapW; ++x)
			{
				tileMap[x][y] = Math.abs(rnd.nextInt()) % 3;
			}
		}
		
		for(int i = 0; i < 1000; ++i)
		{
			int x = Math.abs(rnd.nextInt()) % mapW;
			int y = Math.abs(rnd.nextInt()) % mapH;
			int tree = Math.abs(rnd.nextInt()) % 3;
			tileMap[x][y] = 2 + tree;
		}
		
		startThread();
	}

	@Override
	public void handleWindowInit() { }

	@Override
	public void handleWindowDestroy() { }

	@Override
	public void render(Graphics2D g, int sw, int sh) 
	{
		int x0 = camX / TILE_W;
		int x1 = x0 + (getWidth() / TILE_W) + 1;
		int y0 = camY / TILE_H;
		int y1 = y0 + (getHeight() / TILE_H) + 1;
		
		if(x0 < 0) x0 = 0;
		if(y0 < 0) y0 = 0;
		if(x1 < 0) x1 = 0;
		if(y1 < 0) y1 = 0;
		
		if(x0 >= mapW) x0 = mapW - 1;
		if(y0 >= mapH) y0 = mapH - 1;
		if(x1 >= mapW) x1 = mapW - 1;
		if(y1 >= mapH) y1 = mapH - 1;
		
		for(int y = y0; y <= y1; ++y)
		{
			for(int x = x0; x <= x1; ++x)
			{
				g.drawImage(tileset[tileMap[x][y]].image, 
						x * TILE_W + tileset[tileMap[x][y]].offsetX - camX, 
						y * TILE_H + tileset[tileMap[x][y]].offsetY - camY, 
						null);
			}
		}

		g.setColor(Color.yellow);
		g.drawRect(selX * TILE_W - camX, selY * TILE_H - camY, TILE_W, TILE_H);
	}

	@Override
	public void update() 
	{	
		if(isKeyDown(KeyEvent.VK_LEFT)) camX -= 10;
		if(isKeyDown(KeyEvent.VK_RIGHT)) camX += 10;
		if(isKeyDown(KeyEvent.VK_UP)) camY -= 10;
		if(isKeyDown(KeyEvent.VK_DOWN)) camY += 10;

		selX = (getMouseX() + camX) / TILE_W;
		selY = (getMouseY() + camY) / TILE_H;
		
		if(selX < 0) selX = 0;
		if(selY < 0) selY = 0;
		if(selX >= mapW) selX = mapW - 1;
		if(selY >= mapH) selY = mapH - 1;
	}

	@Override
	public void handleMouseDown(int x, int y, GFMouseButton button) { }

	@Override
	public void handleMouseUp(int x, int y, GFMouseButton button) { }

	@Override
	public void handleMouseMove(int x, int y) { }

	@Override
	public void handleKeyDown(int keyCode) { }

	@Override
	public void handleKeyUp(int keyCode) { }

	public static void main(String[] args) 
	{
		GameFrame gf = new Tilemap();
		gf.initGameWindow();
	}

}
