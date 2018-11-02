package rg_statetest;

import java.awt.Color;
import java.awt.Graphics2D;

import rafgfxlib.GameHost;
import rafgfxlib.GameHost.GFMouseButton;
import rafgfxlib.GameState;
import rg_statetest.Transition.TransitionType;

public class FirstState extends GameState
{
	// Dimenzija jednog kvadrata i boje
	private static final int TILE_SIZE = 128;
	private static final Color COLOR_1 = new Color(255, 0, 0);
	private static final Color COLOR_2 = new Color(255, 128, 128);
	
	private int offset = 0;

	public FirstState(GameHost host)
	{
		super(host);
		
	}

	@Override
	public boolean handleWindowClose()
	{
		return true;
	}

	@Override
	public void resumeState() { }

	@Override
	public void suspendState() { }

	@Override
	public void render(Graphics2D g, int sw, int sh)
	{
		// Iscrtavanje checkerboard patterna
		for(int y = -1; y < sh / TILE_SIZE + 2; ++y)
		{
			for(int x = -1; x < sw / TILE_SIZE + 2; ++x)
			{
				if((x + y) % 2 == 0)
					g.setColor(COLOR_1);
				else
					g.setColor(COLOR_2);
				
				g.fillRect(x * TILE_SIZE + offset, y * TILE_SIZE + offset, TILE_SIZE, TILE_SIZE);
			}
		}
		
	}

	@Override
	public void update()
	{
		offset = (offset + 3) % TILE_SIZE;
	}

	@Override
	public void handleMouseDown(int x, int y, GFMouseButton button) { }

	@Override
	public void handleMouseUp(int x, int y, GFMouseButton button) 
	{ 
		if(button == GFMouseButton.Left)
		{
			// Na lijevi klik radimo trenutno prebacivanje na drugi GameState
			host.setState("second");
		}
		else
		{
			// Na desni (ili bilo koji drugi) klik, nasumicno biramo jednu od
			// implementiranih tranzicija iz TransitionType enuma
			TransitionType transType = TransitionType.values()[(int)(Math.random() * TransitionType.values().length)];
			
			// Pozivamo staticki metod transitionTo, koji ce prvo da nas prebaci
			// u stanje tranzicije, koje ce da traje 0.5 sekundi, a zatim
			// u "second" stanje igre
			Transition.transitionTo("second", transType, 0.5f);
		}
	}

	@Override
	public void handleMouseMove(int x, int y) { }

	@Override
	public void handleKeyDown(int keyCode) { }

	@Override
	public void handleKeyUp(int keyCode) { }

	@Override
	public String getName()
	{
		return "first";
	}

}
