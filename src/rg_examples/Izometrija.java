package rg_examples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import rafgfxlib.GameFrame;
import rafgfxlib.Util;

public class Izometrija  extends GameFrame 
{
	private static final long serialVersionUID = 7636268720113395780L;
	
	private static final int TILE_W = 128;
	private static final int TILE_H = 64;
	
	private Point mousePoint = new Point();
	private Point mouseWorld = new Point();
	private Point zeroPoint = new Point(0, 0);
	
	private int mapW = 100;
	private int mapH = 100;
	
	private int camX = 0;
	private int camY = 0;
	
	private int selX = 0;
	private int selY = 0;
	
	private class Tile
	{
		public BufferedImage image = null;
		@SuppressWarnings("unused")
		public int offsetX = 0;
		@SuppressWarnings("unused")
		public int offsetY = 0;
		@SuppressWarnings("unused")
		public int tileID = 0;
		
		public Tile(BufferedImage image, int ID)
		{
			this.image = image;
			tileID = ID;
			if(image != null)
			{
				offsetX = 0;
				offsetY = -(image.getHeight() - TILE_H);
			}
		}
	}
	
	private Tile[] tileset = null;
	private int[][] tileMap = new int[mapW][mapH];
	
	public Izometrija()
	{
		super("RAF Isometric", 800, 600);
		
		setUpdateRate(60);
		
		BufferedImage tileSheet = Util.loadImage("iso-tiles.png");
		if(tileSheet == null) { System.out.println("Nema tajlova!"); System.exit(1); }
		
		BufferedImage[] tileImages = Util.cutTiles1D(8, 5, tileSheet);
		tileset = new Tile[tileImages.length];
		for(int i = 0; i < tileset.length; ++i)
			tileset[i] = new Tile(tileImages[i], i);
		
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
	
	private void unprojectFromScreen(Point scrPt, Point worldPt)
	{
		int mX = scrPt.x + camX;
		int mY = scrPt.y + camY;
		worldPt.y = (mY * 2 - mX) / 2;
		worldPt.x = mX + worldPt.y;
	}
	
	private void projectToScreen(Point worldPt, Point scrPt)
	{
		scrPt.x = (worldPt.x - worldPt.y) / 1 - camX;
		scrPt.y = (worldPt.x + worldPt.y) / 2 - camY;
	}

	@Override
	public void handleWindowInit() { }

	@Override
	public void handleWindowDestroy() { }

	private void highlightTile(int tx, int ty, Color color, Graphics2D g)
	{
		int tilePosX = tx * TILE_H;
		int tilePosY = ty * TILE_H;
		int isoX = (tilePosX - tilePosY) / 1 - camX;
		int isoY = (tilePosX + tilePosY) / 2 - camY;
		
		g.setColor(color);
		g.drawLine(isoX, isoY, isoX + TILE_W / 2, isoY + TILE_H / 2);
		g.drawLine(isoX, isoY, isoX - TILE_W / 2, isoY + TILE_H / 2);
		g.drawLine(isoX, isoY + TILE_H, isoX + TILE_W / 2, isoY + TILE_H / 2);
		g.drawLine(isoX, isoY + TILE_H, isoX - TILE_W / 2, isoY + TILE_H / 2);
	}
	
	@Override
	public void render(Graphics2D g, int sw, int sh) 
	{
		Point startWorld = new Point();
		
		Point tileView = new Point();
		Point tileWorld = new Point();
		
		unprojectFromScreen(zeroPoint, startWorld);
		
		int x0 = startWorld.x / TILE_H - 2;
		int y0 = startWorld.y / TILE_H - 1;
		int x1 = sw / TILE_W + 4;
		int y1 = sh / TILE_H * 2 + 5;
		
		for(int y = 0; y <= y1; ++y)
		{
			for(int x = 0; x <= x1; ++x)
			{
				int X = x0 + x + y/2;
				int Y = y0 + y/2 - x + (y % 2);
				
				if(X < 0 || Y < 0 || X >= mapW || Y >= mapH) continue;
				
				tileWorld.x = X * TILE_H;
				tileWorld.y = Y * TILE_H;
				
				projectToScreen(tileWorld, tileView);
				
				g.drawImage(tileset[tileMap[X][Y]].image, tileView.x - TILE_W / 2, tileView.y, null);
			}
		}

		highlightTile(selX, selY, Color.blue, g);
		highlightTile(x0, y0, Color.red, g);
	}

	@Override
	public void update() 
	{	
		if(isKeyDown(KeyEvent.VK_LEFT)) camX -= 10;
		if(isKeyDown(KeyEvent.VK_RIGHT)) camX += 10;
		if(isKeyDown(KeyEvent.VK_UP)) camY -= 10;
		if(isKeyDown(KeyEvent.VK_DOWN)) camY += 10;

		mousePoint.x = getMouseX();
		mousePoint.y = getMouseY();
		
		unprojectFromScreen(mousePoint, mouseWorld);
		
		selX = mouseWorld.x / TILE_H;
		selY = mouseWorld.y / TILE_H;
		
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
		GameFrame gf = new Izometrija();
		gf.initGameWindow();
	}

}
