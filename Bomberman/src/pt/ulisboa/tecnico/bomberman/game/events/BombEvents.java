package pt.ulisboa.tecnico.bomberman.game.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pt.ulisboa.tecnico.bomberman.game.Config;
import pt.ulisboa.tecnico.bomberman.game.Coordinate;
import pt.ulisboa.tecnico.bomberman.game.Direction;
import pt.ulisboa.tecnico.bomberman.game.GameActivity;
import pt.ulisboa.tecnico.bomberman.game.Tile;
import pt.ulisboa.tecnico.bomberman.game.Tile.TileType;
import pt.ulisboa.tecnico.bomberman.game.agents.Bomb;
import pt.ulisboa.tecnico.bomberman.game.agents.Flame;

public class BombEvents {
	
	private GameActivity game;
	
	// These also have to be moved to some in-game event handler class
	private List<Bomb> trackedBombs;
	private List<Flame> trackedFlames;
	
	public BombEvents(GameActivity game) {
		this.game = game;

		trackedBombs = new ArrayList<Bomb>();
		trackedFlames = new ArrayList<Flame>();
	}

	public void addBomb() {
		
		// Check bomb count
		if (game.player.bombCount <= 0) {
			return;
		}
		
		final Bomb bomb = new Bomb(game.player.position);
		
		game.map.bombs.add(bomb);
		trackedBombs.add(bomb);
		game.player.bombCount--;
		
		Timer bombTimer = new Timer();
		bombTimer.schedule(new TimerTask() {
			
			@Override
			public synchronized void run() {
				Bomb exploded = trackedBombs.remove(0);
				game.map.bombs.remove(exploded);
				
				game.player.bombCount++;
				addFlames(exploded);
			}
		}, Config.explosionTimeOut);
	}
	
	private synchronized void addFlames(Bomb bomb) {
		
		for (Direction d : Direction.values()) {
			for (int i = 1; i <= Config.explosionRange; ++i) {
				Coordinate bombCoord = bomb.position;
				Coordinate flameCoord = new Coordinate(bombCoord.x + i * d.getX(), bombCoord.y + i * d.getY());
				
				// Check for walls and obstacles
				if (game.map.tileAt(flameCoord).type == Tile.TileType.WALL) {

					// Stop the explosion at walls
					break;
				}
				
				// Check for robots and players and bombs
				// TODO
				
				addSingleFlame(flameCoord);

				// Stop the explosion at obstacles, but remove the obstacle first
				if (game.map.tileAt(flameCoord).type == TileType.OBSTACLE) {
					
					game.map.tiles[flameCoord.y][flameCoord.x].type = TileType.EMPTY;
					break;
				}
			}
		}
		
		// Add an extra flame for the bomb's location
		addSingleFlame(bomb.position);
		
	}
	
	private synchronized void addSingleFlame(Coordinate position) {
		final Flame newFlame = new Flame(position);
		
		game.map.flames.add(newFlame);
		trackedFlames.add(newFlame);
		
		Timer flameTimer = new Timer();
		flameTimer.schedule(new TimerTask() {
			
			@Override
			public synchronized void run() {
				Flame expired = trackedFlames.remove(0);
				game.map.flames.remove(expired);
			}
		}, Config.explosionDuration);
	}
	
}
