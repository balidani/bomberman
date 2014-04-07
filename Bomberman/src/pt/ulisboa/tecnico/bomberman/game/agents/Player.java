package pt.ulisboa.tecnico.bomberman.game.agents;

import pt.ulisboa.tecnico.bomberman.game.Coordinate;
import pt.ulisboa.tecnico.bomberman.game.Direction;


public class Player extends Agent {

	public int bombCount;
	public boolean alive;
	public Direction facing;
	
	public Player(Coordinate position) {
		super(position);

		alive = true;
		bombCount = 3;
		facing = Direction.DOWN;
	}
	
}
