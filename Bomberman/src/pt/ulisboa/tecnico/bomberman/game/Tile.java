package pt.ulisboa.tecnico.bomberman.game;

class Tile {
	
	public enum TileType {
		EMPTY, WALL, OBSTACLE, ROBOT, PLAYER, PLAYER_WITH_BOMB, BOMB
	}
	
	public TileType type;
	
	public Tile() {
		type = TileType.EMPTY;
	}
	
}