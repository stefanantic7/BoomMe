package rg_statetest;

import rafgfxlib.GameHost;

public class Main
{
	public static void main(String[] args)
	{
		// Prvo se konstruise GameHost objekat sa naslovom i dimenzijama prozora
		GameHost host = new GameHost("RAF Tranzicije (desni klik)", 800, 600, false);
		
		// Ciljamo na 60 fps
		host.setUpdateRate(60);
		
		// Konstruisemo sva stanja igre, koja ce biti dostupna kroz host objekat,
		// pa nije potrebno cuvati reference ovdje
		new Transition(host);
		new FirstState(host);
		new SecondState(host);
		
		// Za pocetak rada, treba samo postaviti trenutno aktivno stanje
		host.setState("first");
	}
}
