package pt.ulisboa.tecnico.bomberman.game;

class Tile {
	
	public enum TileType {
		EMPTY, WALL, OBSTACLE, BOMB
	}
	
	public TileType type;
	
	public Tile() {
		type = TileType.EMPTY;
	}
	
}