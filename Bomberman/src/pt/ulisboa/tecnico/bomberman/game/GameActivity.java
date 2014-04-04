package pt.ulisboa.tecnico.bomberman.game;

import java.util.ArrayList;
import java.util.Random;

import pt.ulisboa.tecnico.bomberman.R;
import pt.ulisboa.tecnico.bomberman.game.Tile.TileType;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;

public class GameActivity extends Activity {

	private enum Direction {
		UP, RIGHT, DOWN, LEFT
	}

	Config config;
	Map map;

	Coordinate playerCoordinate;

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

		// Initialize components
		playerCoordinate = map.players.get(0);

		bombTimeOutHandler = new Handler();
		flameTimeoutHandler = new Handler();

		robotHandler = new Handler();
		robotHandler.post(robotUpdate);
		
		// Initializes and renders the grid
		initGrid();
	}

	public void onMoveUp(View view) {
		moveAgent(playerCoordinate, Direction.UP);
		renderGrid();
	}

	public void onMoveDown(View view) {
		moveAgent(playerCoordinate, Direction.DOWN);
		renderGrid();
	}
	
	public void onMoveLeft(View view) {
		moveAgent(playerCoordinate, Direction.LEFT);
		renderGrid();
	}
	
	public void onMoveRight(View view) {

		moveAgent(playerCoordinate, Direction.RIGHT);
		renderGrid();
	}

	/*
	 * TODO: implement this for the agent class that we will create later So we
	 * can write something like: player.moveTo(nextCoord)
	 */
	public synchronized void moveAgent(Coordinate coord, Direction direction) {
		
		// TODO: implement cloneable
		Coordinate next = new Coordinate(coord.x, coord.y);

		// Log.d("Bomberman", String.format("Moving ROBOT at %d,%d to %s", direction.toString(), coord.x, coord.y));
		
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

		Tile fromTile = map.tileAt(coord);
		Tile nextTile = map.tileAt(next);

		if (fromTile.type == TileType.PLAYER) {

			if (nextTile.type == TileType.EMPTY) {
				map.tileAt(next).type = TileType.PLAYER;

				if (fromTile.type == TileType.PLAYER_WITH_BOMB) {
					fromTile.type = TileType.BOMB;
				} else {
					fromTile.type = TileType.EMPTY;
				}
			} else {
				return;
			}

		} else if (fromTile.type == TileType.ROBOT) {

			if (nextTile.type == TileType.EMPTY) {
				nextTile.type = TileType.ROBOT;
				fromTile.type = TileType.EMPTY;
			} else {
				return;
			}
		}

		// TODO: add movements to other elements

		coord.x = next.x;
		coord.y = next.y;
	}

	public void initGrid() {

		GridLayout grid = (GridLayout) findViewById(R.id.gameGrid);
		grid = (GridLayout) findViewById(R.id.gameGrid);
		grid.setColumnCount(map.width);
		grid.setRowCount(map.height);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		/*
		 * TODO: come up with a better scaling method, because having square
		 * sized cells would be the ideal solution
		 */
		int screenWidth = size.x;
		int screenHeight = grid.getLayoutParams().height;

		int cellSize = Math.min(screenWidth / (map.width + 1), screenHeight / map.height);

		for (int i = 0; i < map.height; ++i) {
			for (int j = 0; j < map.width; ++j) {
				ImageView newCell = new ImageView(GameActivity.this);
				grid.addView(newCell, cellSize, cellSize);
			}
		}

		renderGrid();
	}

	public void renderGrid() {

		GridLayout grid = (GridLayout) findViewById(R.id.gameGrid);

		for (int i = 0; i < map.height; ++i) {
			for (int j = 0; j < map.width; ++j) {
				ImageView cell = (ImageView) grid.getChildAt(i * map.width + j);

				// TODO: make a more efficient render method
				
				// TODO: support more players (by making the player an agent
				// rather than a tile)
				cell.setImageResource(Config.ImageResources[map.tiles[i][j].type.ordinal()]);
			}
		}
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

			renderGrid();
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
			map.tileAt(playerCoordinate).type = TileType.PLAYER_WITH_BOMB;
			// map.findPlayer(playerCoordinate).image.setImageResource(R.drawable.bomb);

			bombLocation = new Coordinate(playerCoordinate.x, playerCoordinate.y);
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
