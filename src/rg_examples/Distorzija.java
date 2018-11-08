package rg_examples;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import rafgfxlib.GameFrame;
import rafgfxlib.Util;

public class Distorzija extends GameFrame
{
	private static final long serialVersionUID = -8647499295718102730L;
	
	private BufferedImage imgOriginal = null;
	private WritableRaster imgRaster = null;
	private BufferedImage imgDistort = null;
	
	private int[][] lookupTableX = new int[256][256];
	private int[][] lookupTableY = new int[256][256];
	
	private int distX = 0;
	private int distY = 0;
	
	public Distorzija()
	{
		super("Distorzija", 512, 512);

		imgOriginal = Util.loadImage("doge.png");
		imgRaster = imgOriginal.getRaster();
		
		imgDistort = new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
		
		BufferedImage imgLookup = Util.loadImage("lookup-whirl.png");
		WritableRaster lookupRaster = imgLookup.getRaster();
		int[] rgb = new int[3];
		for(int y = 0; y < 256; y++)
		{
			for(int x = 0; x < 256; ++x)
			{
				lookupRaster.getPixel(x, y, rgb);
				lookupTableX[y][x] = rgb[0];
				lookupTableY[y][x] = rgb[1];
			}
		}
		
		setUpdateRate(60);
		startThread();
	}

	@Override
	public void handleWindowInit() { }

	@Override
	public void handleWindowDestroy() { }

	@Override
	public void render(Graphics2D g, int sw, int sh)
	{
		g.drawImage(imgOriginal, 0, 0, null);
		
		int origMaxX = imgOriginal.getWidth() - 1;
		int origMaxY = imgOriginal.getHeight() - 1;
		
		int rgb[] = new int[3];
		for(int y = 0; y < 256; y++)
		{
			if(distY + y < 0) continue;
			if(distY + y > origMaxY) continue;
			
			for(int x = 0; x < 256; ++x)
			{
				if(distX + x < 0) continue;
				if(distX + x > origMaxX) continue;
				
				int sx = distX + lookupTableX[y][x];
				int sy = distY + lookupTableY[y][x];
				if(sx < 0) sx = 0;
				if(sy < 0) sy = 0;
				if(sx > origMaxX) sx = origMaxX;
				if(sy > origMaxY) sy = origMaxY;
				
				imgRaster.getPixel(sx, sy, rgb);
				imgDistort.setRGB(x, y, (rgb[0] << 16) | (rgb[1] << 8) | (rgb[2]));
			}
		}
		
		g.drawImage(imgDistort, distX, distY, null);

	}

	@Override
	public void update()
	{
		distX = getMouseX() - 128;
		distY = getMouseY() - 128;
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
		GameFrame gf = new Distorzija();
		gf.initGameWindow();

	}

}
