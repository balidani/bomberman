package pt.ulisboa.tecnico.bomberman.game.agents;

import pt.ulisboa.tecnico.bomberman.MainActivity.PlayerColor;
import pt.ulisboa.tecnico.bomberman.game.Coordinate;
import pt.ulisboa.tecnico.bomberman.game.Direction;


public class Player extends Agent {

	public Direction facing;
	public boolean alive;
	public int bombCount;
	
	public PlayerColor color;
	
	public Player(Coordinate position, PlayerColor color) {
		super(position);

		alive = true;
		bombCount = 3;
		facing = Direction.DOWN;
		
		this.color = color;
	}
	
}
