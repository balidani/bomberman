package pt.ulisboa.tecnico.bomberman.game.agents;

import java.util.Timer;

import pt.ulisboa.tecnico.bomberman.game.Coordinate;


public class Bomb extends Agent {

	public Timer bombTimer;
	
	public Bomb(Coordinate position) {
		super(position);
	}

}
