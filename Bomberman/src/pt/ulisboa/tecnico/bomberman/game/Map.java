package pt.ulisboa.tecnico.bomberman.game;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.bomberman.game.Tile.TileType;

public class Map {

	public Tile[][] tiles;
	public int width;
	public int height;
	
	List<Robot> robots;
	List<Player> players;
	
	protected Map(String map) {
		
		// Initialize agent lists
		robots = new ArrayList<Robot>();
		players = new ArrayList<Player>();
		
		// Trim trailing newlines
		map = map.trim();
		
		String[] rows = map.split("\n");
		
		height = rows.length;
		width = rows[0].length();
		
		tiles = new Tile[height][width];
		
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				
				Tile newTile = new Tile();
				
				switch (rows[i].charAt(j)) {
				case 'W':
					newTile.type = TileType.WALL;
					break;
				case 'O':
					newTile.type = TileType.OBSTACLE;
					break;
				case 'R':
					robots.add(new Robot(new Coordinate(j, i)));
					newTile.type = TileType.EMPTY;
					break;
				case '1':
				case '2':
				case '3':
					players.add(new Player(new Coordinate(j, i)));
					newTile.type = TileType.EMPTY;
					break;
				default:
				case '-':
					newTile.type = TileType.EMPTY;
					break;
				}
				
				tiles[i][j] = newTile;
			}
		}
	}
	
	public Tile tileAt(Coordinate coord) {
		return tiles[coord.y][coord.x];
	}
	
	/*
	 * Singleton instance code
	 */
	
	private static Map inst = null;
	
	public static void init(String map) {
		inst = new Map(map);
	}
	
	public static Map instance() {
		return inst;
	}
}
