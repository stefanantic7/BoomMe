package rg2016_v04;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import rafgfxlib.GameFrame;
import rafgfxlib.Util;

public class V04_1_GameFrame extends GameFrame
{
	private static final long serialVersionUID = 6968041805103007681L;

	// Boja kruga, koja ce se moci mijenjati na klik
	private Color circleColor = Color.green;
	
	// Trenutna pozicija slike
	private int boxX = 300;
	private int boxY = 300;
	
	// Slika koju cemo ucitati iz fajla
	private BufferedImage imgRAF = null;
	
	public V04_1_GameFrame()
	{
		super("RAF Game", 640, 480);
		setHighQuality(true);
		
		// Kada putanja pocinje sa / tada se ucitava iz paketa (paket slike u projektu)
		imgRAF = Util.loadImage("/slike/raf-icon.png");
		
		startThread();
	}

	@Override
	public void render(Graphics2D g, int sw, int sh)
	{
		// Za crtanje primitiva poput pravougaonika, krugova i linija, koristi se
		// unaprijed postavljena boja, u ovom slucaju trenutno postavljena boja kruga
		g.setColor(circleColor);
		
		// Ispunjen krug se crta jednako kao i elipsa, zadajuci pravougaonik u koji
		// ce biti upisana elipsa - (x, y, sirina, visina)
		g.fillOval(getMouseX() - 20, getMouseY() - 20, 40, 40);
		
		// Najjednostavniji oblik crtanja pripremljene slike na ekran, ovdje na (boxX, boxY) poziciju
		g.drawImage(imgRAF, boxX, boxY, null);
	}

	@Override
	public void update()
	{
		// Osnovna kontrola preko tastature, pozicija slike se mijenja strelicama
		if(isKeyDown(KeyEvent.VK_LEFT)) boxX -= 5;
		if(isKeyDown(KeyEvent.VK_RIGHT)) boxX += 5;
		if(isKeyDown(KeyEvent.VK_UP)) boxY -= 5;
		if(isKeyDown(KeyEvent.VK_DOWN)) boxY += 5;
	}

	@Override
	public void handleMouseDown(int x, int y, GFMouseButton button)
	{
		// Na lijevi klik misa mijenjamo boju kruga izmedju zelene i zute
		if(button == GFMouseButton.Left)
		{
			if(circleColor.equals(Color.green))
				circleColor = Color.yellow;
			else
				circleColor = Color.green;
		}
	}

	@Override
	public void handleMouseUp(int x, int y, GFMouseButton button) { }

	@Override
	public void handleMouseMove(int x, int y) { }

	@Override
	public void handleKeyDown(int keyCode) { }

	@Override
	public void handleKeyUp(int keyCode) { }

	@Override
	public void handleWindowInit()
	{
		// Ucitanu sliku postavljamo i kao ikonicu prozora
		setIcon(imgRAF);
	}

	@Override
	public void handleWindowDestroy() { }
	
	public static void main(String[] args)
	{
		GameFrame gf = new V04_1_GameFrame();
		gf.initGameWindow();
	}

}
