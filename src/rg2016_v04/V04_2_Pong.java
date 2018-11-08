package rg2016_v04;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import rafgfxlib.GameFrame;
import rafgfxlib.Util;

public class V04_2_Pong extends GameFrame
{
	private static final long serialVersionUID = -6231071571406880709L;
	
	// Dimenzije palice
	private static final int paddleW = 30;
	private static final int paddleH = 80;
	
	// Velicina prozora/prostora za igru
	private final static int gameW = 640;
	private final static int gameH = 480;
	
	// Boje palica i loptice
	private Color leftPaddleColor = Color.yellow;
	private Color rightPaddleColor = Color.green;
	private Color ballColor = Color.red;
	
	// Pocetna pozicija lijeve palice
	private int pad1Y = gameH / 2 - paddleH / 2;
	private int pad2Y = gameH / 2 - paddleH / 2;
	
	// Pocetna pozicija desne palice
	private int pad1X = paddleW * 2;
	private int pad2X = gameW - paddleW * 2;
	
	// Pocetna pozicija loptice, centar ekrana
	private float ballX = gameW / 2;
	private float ballY = gameH / 2;
	
	// Inercija loptice
	private float ballDX = 0.0f;
	private float ballDY = 0.0f;
	
	// Precnik i poluprecnik loptice
	private static final int ballSize = 30;
	private static final int halfBall = ballSize / 2;
	
	// Skor lijevog i desnog igraca
	private int score1 = 0;
	private int score2 = 0;
	
	// Ikonica
	private BufferedImage imgRAF = null;
	
	public V04_2_Pong()
	{
		super("RAF Pong", gameW, gameH);
		setHighQuality(true);
		
		imgRAF = Util.loadImage("/slike/raf-icon.png");
		
		startThread();
	}

	// Resetuje lopticu u sredinu i daje joj ubrzanje
	private void resetBall()
	{
		// Pozicija na centar ekrana
		ballX = gameW / 2;
		ballY = gameH / 2;
		
		// Daje horizontalno ubrzanje od 7 do 9 i vertikalno od 5 do 8
		ballDX = (float)Math.random() * 2.0f + 7.0f;
		ballDY = (float)Math.random() * 3.0f + 5.0f;
		
		// Po 50% sanse da se i jedno i drugu negiraju
		if(Math.random() > 0.5) ballDX *= -1;
		if(Math.random() > 0.5) ballDY *= -1;
	}
	
	@Override
	public void render(Graphics2D g, int sw, int sh)
	{
		// Crtanje lijeve palice u obliku zaobljenog pravougaonika
		g.setColor(leftPaddleColor);
		g.fillRoundRect(pad1X - paddleW, pad1Y, paddleW, paddleH, 10, 10);

		// Crtanje dense palice u obliku zaobljenog pravougaonika
		g.setColor(rightPaddleColor);
		g.fillRoundRect(pad2X, pad2Y, paddleW, paddleH, 10, 10);
		
		// Crtanje loptice
		g.setColor(ballColor);
		g.fillOval((int)ballX - halfBall, (int)ballY - halfBall, ballSize, ballSize);
		
		// Rezultat
		g.setColor(Color.white);
		g.drawString(score1 + " - " + score2, 10, 10);
	}

	@Override
	public void update()
	{
		// Lijeva palica se kontrolise misem
		pad1Y = getMouseY() - paddleH / 2;
		
		// "AI" protivnika, koji se polako pomijera gore i dole, zavisno da li
		// je loptica iznad ili ispod centra palice
		if(ballY > pad2Y + paddleH / 2)
			pad2Y += 4;
		if(ballY < pad2Y + paddleH / 2)
			pad2Y -= 4;
		
		// Palice ne smiju izaci iz ekrana
		if(pad1Y < 0) pad1Y = 0;
		if(pad2Y < 0) pad2Y = 0;
		if(pad1Y + paddleH > gameH) pad1Y = gameH - paddleH;
		if(pad2Y + paddleH > gameH) pad2Y = gameH - paddleH;
		
		// Da li loptica trenutno NE dodiruje lijevu i desnu palicu
		boolean leftClear = ballX >= pad1X;
		boolean rightClear = ballX <= pad2X;
		
		// Azuriranje pozicije loptice
		ballX += ballDX;
		ballY += ballDY;
		
		// Odbijanje loptice od gornju ivicu ekrana
		if(ballY < halfBall)
		{
			ballY = halfBall;
			ballDY = Math.abs(ballDY);
		}
		
		// Odbijanje loptice od donju ivicu ekrana
		if(ballY > gameH - halfBall)
		{
			ballY = gameH - halfBall;
			ballDY = Math.abs(ballDY) * -1.0f;
		}
		
		// Loptica je izasla na lijevu stranu, poen za desnog igraca i reset loptice
		if(ballX < -halfBall)
		{
			score2++;
			resetBall();
			return;
		}
		
		// Loptica je izasla na desnu stranu, poen za lijevog igraca i reset loptice
		if(ballX > gameW + halfBall)
		{
			score1++;
			resetBall();
			return;
		}
		
		// Ako loptica prethodno nije dodirivala lijevu palicu, a sada joj se centar lijeve
		// stranice nalazi unutar pravougaonika lijeve palice, radimo odbijanje
		if(leftClear && ballX - halfBall <= pad1X && ballX - halfBall >= pad1X - paddleW &&
				ballY >= pad1Y && ballY <= pad1Y + paddleH)
		{
			ballX = pad1X + halfBall + 0.1f;
			ballDX = Math.abs(ballDX);
		}
		
		// Ako loptica prethodno nije dodirivala desnu palicu, a sada joj se centar desne
		// stranice nalazi unutar pravougaonika desne palice, radimo odbijanje
		if(rightClear && ballX + halfBall >= pad2X && ballX + halfBall <= pad2X + paddleW &&
				ballY >= pad2Y && ballY <= pad2Y + paddleH)
		{
			ballX = pad2X - halfBall - 0.1f;
			ballDX = Math.abs(ballDX) * -1.0f;
		}
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

	@Override
	public void handleWindowInit()
	{
		setIcon(imgRAF);
		
		resetBall();
	}

	@Override
	public void handleWindowDestroy() { }
	
	public static void main(String[] args)
	{
		GameFrame gf = new V04_2_Pong();
		gf.initGameWindow();
	}
}
