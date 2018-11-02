package rg_statetest;

import java.awt.Color;
import java.awt.Graphics2D;

import rafgfxlib.GameHost;
import rafgfxlib.GameHost.GFMouseButton;
import rafgfxlib.GameState;
import rg_statetest.Transition.TransitionType;

public class SecondState extends GameState
{
	// Pogledati FirstState.java
	private static final int TILE_SIZE = 128;
	private static final Color COLOR_1 = new Color(0, 255, 0);
	private static final Color COLOR_2 = new Color(128, 255, 128);
	
	private int offset = 0;

	public SecondState(GameHost host)
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
		for(int y = -1; y < sh / TILE_SIZE + 2; ++y)
		{
			for(int x = -1; x < sw / TILE_SIZE + 2; ++x)
			{
				if((x + y) % 2 == 0)
					g.setColor(COLOR_1);
				else
					g.setColor(COLOR_2);
				
				g.fillRect(x * TILE_SIZE - offset, y * TILE_SIZE + offset, TILE_SIZE, TILE_SIZE);
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
			host.setState("first");
		}
		else
		{
			TransitionType transType = TransitionType.values()[(int)(Math.random() * TransitionType.values().length)];
			Transition.transitionTo("first", transType, 0.5f);
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
		return "second";
	}
}
