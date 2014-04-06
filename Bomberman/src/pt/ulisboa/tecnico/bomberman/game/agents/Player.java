package pt.ulisboa.tecnico.bomberman.game.agents;

import pt.ulisboa.tecnico.bomberman.game.Coordinate;


public class Player extends Agent {

	public int bombCount;
	
	public Player(Coordinate position) {
		super(position);
		
		bombCount = 1;
	}
	
}
