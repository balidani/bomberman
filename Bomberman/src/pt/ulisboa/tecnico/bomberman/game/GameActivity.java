package pt.ulisboa.tecnico.bomberman.game;

import java.util.ArrayList;
import java.util.Random;

import pt.ulisboa.tecnico.bomberman.R;
import pt.ulisboa.tecnico.bomberman.game.Tile.TileType;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {

	private enum Direction {
		UP, RIGHT, DOWN, LEFT
	}

	GameView gameView;
	Player player;
	Config config;
	Map map;
	
	// Move this to the rendering module, once there is one
	int cellSize;

	// These have to be moved to some in-game event handler class
	ArrayList<Coordinate> bombFlames;
	Coordinate bombLocation;
	Handler bombTimeOutHandler;
	Handler flameTimeoutHandler;
	boolean hasBombSet = false;

	Handler robotHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		// Initialize config
		config = new Config(1);

		// Initialize map
		Map.init(config.mapString);
		map = Map.instance();

		// Initialize view
		RelativeLayout gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
		gameView = new GameView(GameActivity.this, map);
		gameLayout.addView(gameView);
		
		// Initialize components
		player = map.players.get(0);

		bombTimeOutHandler = new Handler();
		flameTimeoutHandler = new Handler();

		robotHandler = new Handler();
		robotHandler.post(robotUpdate);
		
		// Initial render
		gameView.render();
	}

	public void onMoveUp(View v) {
		moveAgent(player, Direction.UP);
		gameView.render();
	}

	public void onMoveDown(View v) {
		moveAgent(player, Direction.DOWN);
		gameView.render();
	}
	
	public void onMoveLeft(View v) {
		moveAgent(player, Direction.LEFT);
		gameView.render();
	}
	
	public void onMoveRight(View v) {

		moveAgent(player, Direction.RIGHT);
		gameView.render();
	}
	
	public void onBomb(View view) {
		setBomb();
	}

	/*
	 * TODO: implement this for the agent class that we will create later So we
	 * can write something like: player.moveTo(nextCoord)
	 */
	public synchronized void moveAgent(Agent agent, Direction direction) {
		
		Coordinate next = new Coordinate(agent.position);
				
		switch (direction) {
		case UP:
			next.y--;
			break;
		case DOWN:
			next.y++;
			break;
		case LEFT:
			next.x--;
			break;
		case RIGHT:
			next.x++;
			break;
		}

		// Handle wrap-around
		if (next.x > map.width - 1) {
			next.x = 0;
		} else if (next.x < 0) {
			next.x = map.width - 1;
		}

		if (next.y > map.height - 1) {
			next.y = 0;
		} else if (next.y < 0) {
			next.y = map.height - 1;
		}
		
		if (map.tileAt(next).type != TileType.EMPTY) {
			return;
		}

		agent.position = next;
	}

	/*
	 * This also has to be moved to somewhere else
	 */

	private Runnable robotUpdate = new Runnable() {

		public void run() {
			
			if (map.robots.isEmpty()) {
				robotHandler.removeCallbacks(this);
				return;
			}

			try {

				for (int i = 0; i < map.robots.size(); i++) {
						
					int directionValue = new Random().nextInt(Direction.values().length);
					Direction randomDirection = Direction.values()[directionValue];
					
					moveAgent(map.robots.get(i), randomDirection);
				}
			} catch (Exception ex) {
				// This will happen when main thread removes a robot. This is
				// expected
				// TODO: change it to the specific exception type that occurs
			} finally {
				robotHandler.postDelayed(this, config.robotSpeed);
			}
		}
	};

	/*
	 * Bomb handling functions These have to be moved to somewhere else
	 */

	private Runnable bombTimeOut = new Runnable() {
		public void run() {
			BombTimeOut();
		}

		public void BombTimeOut() {
			try {
				bombExploded();
			} catch (Exception ex) {
				// TODO: Why is there a catch for generic Exception here?
			} finally {
				bombTimeOutHandler.removeCallbacks(this);
			}
		}
	};

	private Runnable flameTimeOut = new Runnable() {
		public void run() {
			FlameTimeOut();
		}

		public void FlameTimeOut() {
			try {
				removeFlame();
			} catch (Exception ex) {
				// TODO: Why is there a catch for generic Exception here?
			} finally {
				flameTimeoutHandler.removeCallbacks(this);
			}
		}
	};

	public synchronized void setBomb() {
		if (!hasBombSet) {
			// TODO fix:
			// map.tileAt(player.position).type = TileType.PLAYER_WITH_BOMB;
			
			// map.findPlayer(playerCoordinate).image.setImageResource(R.drawable.bomb);

			bombLocation = new Coordinate(player.position);
			bombTimeOutHandler.postDelayed(bombTimeOut, config.explosionTimeOut);
			hasBombSet = true;
		}
	}

	public synchronized void setFlame() {
		int tempX = bombLocation.x;
		int tempY = bombLocation.y;

		// Here
		bombFlames.add(new Coordinate(tempX, tempY));

		/*
		 * TODO: remove code duplication using this structure for (Direction dir
		 * : Direction.values()) { int ord = dir.ordinal(); for (int i = 0; i <
		 * config.explosionRange; ++i) { ... } }
		 */

		// UP
		for (int i = 0; i < config.explosionRange; i++) {
			if (((bombLocation.x - 1) - i) < 0)
				tempX = (map.width - 1) - i;
			else
				tempX = (bombLocation.x - 1) - i;
			tempY = bombLocation.y;
			if (map.tiles[tempX][tempY].type != TileType.WALL)
				// map.tiles[tempX][tempY].image.setImageResource(R.drawable.explosion);
				bombFlames.add(new Coordinate(tempX, tempY));
		}
		// Left
		for (int i = 0; i < config.explosionRange; i++) {
			if (((bombLocation.y - 1) - i) < 0)
				tempY = (map.height - 1) - i;
			else
				tempY = (bombLocation.y - 1) - i;
			tempX = bombLocation.x;
			if (map.tiles[tempX][tempY].type != TileType.WALL)
				// map.tiles[tempX][tempY].image.setImageResource(R.drawable.explosion);
				bombFlames.add(new Coordinate(tempX, tempY));
		}
		// Down
		for (int i = 0; i < config.explosionRange; i++) {
			if (((bombLocation.x + 1) + i) > (map.width - 1))
				tempX = i;
			else
				tempX = (bombLocation.x + 1) + i;
			tempY = bombLocation.y;
			if (map.tiles[tempX][tempY].type != TileType.WALL)
				// map.tiles[tempX][tempY].image.setImageResource(R.drawable.explosion);
				bombFlames.add(new Coordinate(tempX, tempY));
		}
		// Right
		for (int i = 0; i < config.explosionRange; i++) {
			if (((bombLocation.y + 1) + i) > (map.height - 1))
				tempY = i;
			else
				tempY = (bombLocation.y + 1) + i;
			tempX = bombLocation.x;
			if (map.tiles[tempX][tempY].type != TileType.WALL)
				// map.tiles[tempX][tempY].image.setImageResource(R.drawable.explosion);
				bombFlames.add(new Coordinate(tempX, tempY));
		}

	}

	// TODO:The proper clearance and point assignment should be done in this
	// method.
	// Currently just clears the flames only
	public synchronized void removeFlame() {
		for (int i = 0; i < bombFlames.size(); i++) {
			// map.tiles[bombFlames.get(i).x][bombFlames.get(i).y].image.setImageResource(R.drawable.empty);
			map.tiles[bombFlames.get(i).x][bombFlames.get(i).y].type = TileType.EMPTY;
		}
		hasBombSet = false;
		bombLocation = null;
		bombFlames = null;
	}

	public synchronized void bombExploded() {
		if (hasBombSet) {
			bombFlames = new ArrayList<Coordinate>();
			setFlame();
			flameTimeoutHandler.postDelayed(flameTimeOut, config.explosionDuration);
		}
	}
}
