package pt.ulisboa.tecnico.bomberman.game.agents;

import pt.ulisboa.tecnico.bomberman.game.Coordinate;

public class Robot extends Agent {

	public int id;
	
	public Robot(Coordinate position, int id) {
		super(position);
		
		this.id = id;
	}

}
