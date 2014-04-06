package pt.ulisboa.tecnico.bomberman.game;

public class Tile {
	
	public enum TileType {
		EMPTY, WALL, OBSTACLE
	}
	
	public TileType type;
	
	public Tile() {
		type = TileType.EMPTY;
	}
	
}