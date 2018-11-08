package rg_examples;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import rafgfxlib.GameFrame;
import rafgfxlib.Util;

public class AnimatedSpriteDemo extends GameFrame
{
	private static final long serialVersionUID = 1L;
	
	public static class SpriteSheet
	{
		private BufferedImage sheet;
		private int frameW, frameH;
		private int sheetW, sheetH;
		private int offsetX = 0, offsetY = 0;
		
		public SpriteSheet(String imageName, int columns, int rows)
		{
			sheet = Util.loadImage(imageName);
			if(imageName == null)
			{
				sheet = null;
				System.out.println("Error loading sprite sheet!");
				return;
			}
			
			sheetW = columns;
			sheetH = rows;
			frameW = sheet.getWidth() / sheetW;
			frameH = sheet.getHeight() / sheetH;
		}
		
		public int getColumnCount() { return sheetW; }
		public int getRowCount() { return sheetH; }
		public int getFrameWidth() { return frameW; }
		public int getFrameHeight() { return frameH; }
		
		public void drawTo(Graphics g, int posX, int posY, int frameX, int frameY)
		{
			if(sheet == null) return;
			if(frameX < 0 || frameY < 0 || frameX >= sheetW || frameY >= sheetH) return;
			
			g.drawImage(sheet,
					posX - offsetX, posY - offsetY, 
					posX - offsetX + frameW, posY - offsetY + frameH, 
					frameX * frameW, frameY * frameH, 
					frameX * frameW + frameW, frameY * frameH + frameH, 
					null);
		}
		
		public void setOffsets(int x, int y)
		{
			offsetX = x;
			offsetY = y;
		}
		
		public void setOffsetX(int x) { offsetX = x; }
		public void setOffsetY(int y) { offsetY = y; }
		public int getOffsetX() { return offsetX; }
		public int getOffsetY() { return offsetY; }
	}
	
	public static class AnimatedEntity
	{
		private int posX, posY;
		private SpriteSheet mySheet;
		private int animationID = 0;
		private int animFrame = 0;
		private boolean animPlaying = false;
		private int frameInterval = 2;
		private int frameCountdown = 0;
		
		public AnimatedEntity(SpriteSheet sheet, int X, int Y)
		{
			posX = X;
			posY = Y;
			mySheet = sheet;
		}
		
		public void update()
		{
			if(animPlaying)
			{
				frameCountdown--;
				if(frameCountdown < 0)
				{
					animFrame = (animFrame + 1) % mySheet.getColumnCount();
					frameCountdown = frameInterval;
				}
			}
		}
		
		public void draw(Graphics g)
		{
			mySheet.drawTo(g, posX, posY, animFrame, animationID);
		}
		
		public int getAnimation() { return animationID; }
		
		public void setAnimation(int anim)
		{
			if(anim >= 0 && anim < mySheet.getRowCount())
				animationID = anim;
		}
		
		public int getFrame() { return animFrame; }
		
		public void setFrame(int frame)
		{
			if(frame >= 0 && frame < mySheet.getColumnCount())
				animFrame = frame;
		}
		
		public void play() { animPlaying = true; }
		public void pause() { animPlaying = false; }
		public void stop() { animPlaying = false; animFrame = 0; frameCountdown = frameInterval; }
		
		public boolean isPlaying() { return animPlaying; }
		
		public int getAnimationInterval() { return frameInterval; }
		public void setAnimationInterval(int i)
		{
			if(i >= 0)
				frameInterval = i;
		}
		
		public void setPosition(int x, int y)
		{
			posX = x;
			posY = y;
		}
		
		public int getPositionX() { return posX; }
		public int getPositionY() { return posY; }
		
		public void move(int movX, int movY)
		{
			posX += movX;
			posY += movY;
		}
	}
	
	private SpriteSheet heroSheet;
	private AnimatedEntity player;
	private Color backgroundColor = new Color(32, 64, 0);
	
	private static final int ANIM_DOWN = 0;
	private static final int ANIM_LEFT = 1;
	private static final int ANIM_UP = 2;
	private static final int ANIM_RIGHT = 3;
	
	public static final int PLAYER_SPEED = 3;

	public AnimatedSpriteDemo()
	{
		super("RAF Tilemap", 640, 480);
		
		setUpdateRate(60);
		
		heroSheet = new SpriteSheet("character.png", 10, 4);
		heroSheet.setOffsets(32, 64);
		
		player = new AnimatedEntity(heroSheet, 320, 320);
		
		startThread();
	}

	@Override
	public void handleWindowInit() { }

	@Override
	public void handleWindowDestroy() { }

	@Override
	public void render(Graphics2D g, int sw, int sh) 
	{
		g.setBackground(backgroundColor);
		g.clearRect(0, 0, sw, sh);
		
		player.draw(g);
	}

	@Override
	public void update() 
	{	
		if(isKeyDown(KeyEvent.VK_DOWN))
			player.move(0, PLAYER_SPEED);
		else if(isKeyDown(KeyEvent.VK_UP))
			player.move(0, -PLAYER_SPEED);
		else if(isKeyDown(KeyEvent.VK_LEFT))
			player.move(-PLAYER_SPEED, 0);
		else if(isKeyDown(KeyEvent.VK_RIGHT))
			player.move(PLAYER_SPEED, 0);
		
		player.update();
	}

	@Override
	public void handleMouseDown(int x, int y, GFMouseButton button) { }

	@Override
	public void handleMouseUp(int x, int y, GFMouseButton button) { }

	@Override
	public void handleMouseMove(int x, int y) { }

	@Override
	public void handleKeyDown(int keyCode) 
	{ 
		if(keyCode == KeyEvent.VK_DOWN)
		{
			player.setAnimation(ANIM_DOWN);
			player.play();
		}
		else if(keyCode == KeyEvent.VK_UP)
		{
			player.setAnimation(ANIM_UP);
			player.play();
		}
		else if(keyCode == KeyEvent.VK_LEFT)
		{
			player.setAnimation(ANIM_LEFT);
			player.play();
		}
		else if(keyCode == KeyEvent.VK_RIGHT)
		{
			player.setAnimation(ANIM_RIGHT);
			player.play();
		}
	}

	@Override
	public void handleKeyUp(int keyCode) 
	{ 
		if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP ||
				keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT)
		{
			player.stop();
			player.setFrame(5);
		}
	}

	public static void main(String[] args) 
	{
		GameFrame gf = new AnimatedSpriteDemo();
		gf.initGameWindow();
	}

}
