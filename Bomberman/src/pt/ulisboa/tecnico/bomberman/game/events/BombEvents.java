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
import pt.ulisboa.tecnico.bomberman.game.agents.Player;
import pt.ulisboa.tecnico.bomberman.game.agents.Robot;

public class BombEvents {
	
	private GameActivity game;
	
	// These also have to be moved to some in-game event handler class
	private List<Bomb> trackedBombs;
	private List<List<Flame>> trackedFlameSets;
	
	public BombEvents(GameActivity game) {
		this.game = game;

		trackedBombs = new ArrayList<Bomb>();
		trackedFlameSets = new ArrayList<List<Flame>>();
	}

	public void addBomb(Coordinate location, int color) {
		
		// Check bomb count
		Player player = game.map.findPlayer(color);
		
		if (player.bombCount <= 0) {
			return;
		}

		final Bomb bomb = new Bomb(player.position);
		
		game.map.addBomb(bomb);
		trackedBombs.add(bomb);
		player.bombCount--;
		
		bomb.bombTimer = new Timer();
		bomb.bombTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Bomb exploded = trackedBombs.remove(0);
				game.map.removeBomb(exploded);
				game.player.bombCount++;
				addFlames(exploded);
				
			}
		}, Config.explosionTimeOut);
	}
	
	public void addBomb() {
		addBomb(game.player.position, game.playerColor.ordinal());
	}
	
	private void addFlames(Bomb bomb) {
		
		List<Flame> flameSet = new ArrayList<Flame>();

		game.vibrate(200);
		
		for (Direction d : Direction.values()) {
			for (int i = 1; i <= Config.explosionRange; ++i) {
				Coordinate bombCoord = bomb.position;
				Coordinate flameCoord = new Coordinate(bombCoord.x + i * d.getX(), bombCoord.y + i * d.getY());
				
				// Check for walls and obstacles
				if (game.map.tileAt(flameCoord).type == Tile.TileType.WALL) {

					// Stop the explosion at walls
					break;
				}
				
				// Check for robots
				for (Robot robot : game.map.getRobots()) {
					if (robot.position.equals(flameCoord)) {
						game.map.removeRobot(robot);
						break;
					}
				}
				
				// Check for players
				for (Player player : game.map.getPlayers()) {
					if (player.position.equals(flameCoord)) {
						player.alive = false;
						break;
					}
				}
				
				// Check for other bombs
				for (final Bomb otherBomb: game.map.getBombs()) {
					if (otherBomb.position.equals(flameCoord)) {
						trackedBombs.remove(otherBomb);
						game.map.removeBomb(otherBomb);
						
						/*
						 * TODO: java.lang.NullPointerException
						 */
						
						otherBomb.bombTimer.cancel();
						game.player.bombCount++;
						
						new Timer().schedule(new TimerTask() {
							
							@Override
							public void run() {
								addFlames(otherBomb);								
							}
						}, 300);
						
						break;
					}
				}

				Flame newFlame = new Flame(flameCoord, this);
				game.map.addFlame(newFlame);
				flameSet.add(newFlame);

				// Stop the explosion at obstacles, but remove the obstacle first
				if (game.map.tileAt(flameCoord).type == TileType.OBSTACLE) {
					
					game.map.tiles[flameCoord.y][flameCoord.x].type = TileType.EMPTY;
					break;
				}
			}
		}
		
		// Add an extra flame for the bomb's location

		Flame newFlame = new Flame(bomb.position, this);
		game.map.addFlame(newFlame);
		flameSet.add(newFlame);
		
		trackedFlameSets.add(flameSet);
		
		Timer flameTimer = new Timer();
		flameTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				List<Flame> expiredList = trackedFlameSets.remove(0);
				for (Flame expired : expiredList) {
					game.map.removeFlame(expired);	
				}
			}
		}, Config.explosionDuration);
	}
	
//	public void requestRender() {
//		game.runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				game.render();
//			}
//		});
//	}
}
