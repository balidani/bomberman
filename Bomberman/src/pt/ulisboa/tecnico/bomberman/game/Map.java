package pt.ulisboa.tecnico.bomberman.game;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.bomberman.game.Tile.TileType;

public class Map {

	public Tile[][] tiles;
	public int width;
	public int height;
	
	List<Coordinate> robots;
	List<Coordinate> players;
	
	protected Map(String map) {
		
		// Initialize agent lists
		robots = new ArrayList<Coordinate>();
		players = new ArrayList<Coordinate>();
		
		// Trim trailing newlines
		map = map.trim();
		
		String[] rows = map.split("\n");
		
		height = rows.length;
		width = rows[0].length();
		
		tiles = new Tile[height][width];
		
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				
				Tile newTile = new Tile();
				
				// TODO: Create a player class to identify players with an id
				switch (rows[i].charAt(j)) {
				case 'W':
					newTile.type = TileType.WALL;
					break;
				case 'O':
					newTile.type = TileType.OBSTACLE;
					break;
				case 'R':
					newTile.type = TileType.ROBOT;
					robots.add(new Coordinate(j, i));
					break;
				case '1':
				case '2':
				case '3':
					newTile.type = TileType.PLAYER;
					players.add(new Coordinate(j, i));
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
