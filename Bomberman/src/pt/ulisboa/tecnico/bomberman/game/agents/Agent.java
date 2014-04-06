package pt.ulisboa.tecnico.bomberman.game.agents;

import pt.ulisboa.tecnico.bomberman.game.Coordinate;


public abstract class Agent {
	
	public Coordinate position;
	
	public Agent(Coordinate position) {
		this.position = position;
	}
}
