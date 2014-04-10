package pt.ulisboa.tecnico.bomberman.game;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.bomberman.MainActivity.PlayerColor;
import pt.ulisboa.tecnico.bomberman.game.Tile.TileType;
import pt.ulisboa.tecnico.bomberman.game.agents.Bomb;
import pt.ulisboa.tecnico.bomberman.game.agents.Flame;
import pt.ulisboa.tecnico.bomberman.game.agents.Player;
import pt.ulisboa.tecnico.bomberman.game.agents.Robot;

public class Map {

	public Tile[][] tiles;
	public int width;
	public int height;
	
	private List<Bomb> bombs;
	private List<Flame> flames;
	private List<Robot> robots;
	private List<Player> players;
	
	public Object agentLock;
	
	protected Map(String map) {
		
		agentLock = new Object();
		
		// Initialize agent lists
		bombs = new ArrayList<Bomb>();
		flames = new ArrayList<Flame>();
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
					players.add(new Player(new Coordinate(j, i), PlayerColor.WHITE));
					newTile.type = TileType.EMPTY;
					break;
				case '2':
					players.add(new Player(new Coordinate(j, i), PlayerColor.BLUE));
					newTile.type = TileType.EMPTY;
					break;
				case '3':
					players.add(new Player(new Coordinate(j, i), PlayerColor.RED));
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
	
	public void addBomb(Bomb b) {
		synchronized (agentLock) {
			bombs.add(b);
		}
	}
	
	public void addFlame(Flame f) {
		synchronized (agentLock) {
			flames.add(f);
		}
	}
	
	public void addRobot(Robot r) {
		synchronized (agentLock) {
			robots.add(r);
		}
	}
	
	public void addPlayer(Player p) {
		synchronized (agentLock) {
			players.add(p);
		}
	}
	
	public void removeBomb(Bomb b) {
		synchronized (agentLock) {
			bombs.remove(b);
		}
	}
	
	public void removeFlame(Flame f) {
		synchronized (agentLock) {
			flames.remove(f);
		}
	}
	
	public void removeRobot(Robot r) {
		synchronized (agentLock) {
			robots.remove(r);
		}
	}
	
	public void removePlayer(Player p) {
		synchronized (agentLock) {
			players.remove(p);
		}
	}
	
	public List<Bomb> getBombs() {
		return bombs;
	}
	
	public List<Flame> getFlames() {
		return flames;
	}
	
	public List<Robot> getRobots() {
		return robots;
	}
	
	public List<Player> getPlayers() {
		return players;
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
