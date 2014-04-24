package pt.ulisboa.tecnico.bomberman.game;

import java.util.Timer;
import java.util.TimerTask;

import pt.ulisboa.tecnico.bomberman.MainActivity.PlayerColor;
import pt.ulisboa.tecnico.bomberman.R;
import pt.ulisboa.tecnico.bomberman.game.Tile.TileType;
import pt.ulisboa.tecnico.bomberman.game.agents.Agent;
import pt.ulisboa.tecnico.bomberman.game.agents.Bomb;
import pt.ulisboa.tecnico.bomberman.game.agents.Player;
import pt.ulisboa.tecnico.bomberman.game.agents.Robot;
import pt.ulisboa.tecnico.bomberman.game.events.BombEvents;
import pt.ulisboa.tecnico.bomberman.game.events.MultiplayerEvents;
import pt.ulisboa.tecnico.bomberman.game.events.RobotEvents;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {

	// Game components
	public PlayerColor playerColor;
	public GameView gameView;
	public Player player;
	public Map map;
	
	// Events
	public MultiplayerEvents multiEvents;
	public RobotEvents robotEvents;
	public BombEvents bombEvents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		Intent i = getIntent();
		int colorIndex = i.getIntExtra("player_color", 0);
		playerColor = PlayerColor.values()[colorIndex];
		
		// Inititialize resources
		GameResources.init(getResources());
		// Initialize map
		//TODO: Set the level integer 1,2 or 3 below, from user selection
		int level=3;
		Config.LoadGameSettings(this, level);
		Map.init(Config.mapString);
		map = Map.instance();

		// Find our own player
		for (Player p : map.getPlayers()) {
			if (p.color.ordinal() == playerColor.ordinal()) {
				player = p;
				break;
			}
		}
		
		// Initialize view
		RelativeLayout gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
		gameView = new GameView(GameActivity.this, this);
		gameLayout.addView(gameView);

		// Initialize events
		multiEvents = new MultiplayerEvents(this);
		robotEvents = new RobotEvents(this);
		bombEvents = new BombEvents(this);
		
		// Wait for others to connect
		final ProgressDialog progress = new ProgressDialog(GameActivity.this);
		progress.setTitle("Waiting for other players");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setProgress(0);
		progress.setMax(100);
		progress.setCancelable(false);
		progress.show();
		
		new Thread() {
			public void run() {
				try {
					while (!multiEvents.started) {
						
						progress.incrementProgressBy(1);
						if (progress.getProgress() >= 100) {
							progress.setProgress(0);
						}
						
						Thread.sleep(100);
					}
					
					if (multiEvents.master) {
						robotEvents.start();
					}

					progress.dismiss();
					this.join();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		
		// Start rendering thread
		// Wow, clean up this mess
		Timer renderTimer = new Timer();
		final GameActivity game = this;
		renderTimer.schedule(new TimerTask() {
			public void run() {
				game.runOnUiThread(new Thread() {
					@Override
					public void run() {
						render();
					}
				});
			}
		}, 100L, 50L);
	}
	
	public void render() {
		gameView.render();
		RelativeLayout uiLayout = (RelativeLayout) findViewById(R.id.uiLayout);
		uiLayout.invalidate();
	}

	public void vibrate(int t) {

		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		// Vibrate for 400 milliseconds
		v.vibrate(t);
	}
	
	public void onMoveUp(View view) {

		vibrate(10);
		
		if (!player.alive) {
			return;
		}
		
		moveAgent(player, Direction.UP, true);
		player.facing = Direction.UP;
	}

	public void onMoveDown(View view) {

		vibrate(10);
		
		if (!player.alive) {
			return;
		}
		
		moveAgent(player, Direction.DOWN, true);
		player.facing = Direction.DOWN;
	}
	
	public void onMoveLeft(View view) {

		vibrate(10);
		
		if (!player.alive) {
			return;
		}
		
		moveAgent(player, Direction.LEFT, true);
		player.facing = Direction.LEFT;
	}
	
	public void onMoveRight(View view) {

		vibrate(10);
		
		if (!player.alive) {
			return;
		}
		
		moveAgent(player, Direction.RIGHT, true);
		player.facing = Direction.RIGHT;
	}
	
	public void onBomb(View view) {
		
		vibrate(10);
		
		if (!player.alive) {
			return;
		}
		
		bombEvents.addBomb();
		multiEvents.addBomb(player);
	}

	/**
	 * Moves an agent in the specified direction, if possible
	 * 
	 * @param agent -- The agent to move
	 * @param direction -- The direction to move to
	 * @param original -- Original movement, created by this client, must be sent to others
	 * @return -- true, if the movement succeeded
	 */
	public synchronized boolean moveAgent(Agent agent, Direction direction, boolean original) {
		
		Coordinate next = new Coordinate(agent.position);

		next.x += direction.getX();
		next.y += direction.getY();

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
			return false;
		}
		
		// Check for bombs
		for (Bomb b : map.getBombs()) {
			if (b.position.equals(next)) {
				return false;
			}
		}
		
		// Check for player robot collision
		if (agent instanceof Robot) {
			for (Player p : map.getPlayers()) {
				if (agent.position.equals(p.position)) {
					p.alive = false;
				}
			}
		} else if (agent instanceof Player) {
			for (Robot r : map.getRobots()) {
				if (agent.position.equals(r.position)) {
					((Player) agent).alive = false;
				}
			}
		}

		agent.position = next;
		
		// Send multiplayer event
		if (original) {
			if (agent instanceof Player) {
				multiEvents.playerMove((Player) agent, direction.value());
			} else if (agent instanceof Robot) {
				multiEvents.robotMove((Robot) agent, direction.value());
			}
		}
		
		return true;
	}

	public void movePlayer(int color, int dir) {
		// Find player
		Player moved = map.findPlayer(color);
		Direction direction = Direction.values()[dir];
		
		// TODO: handle this
		if (moved == null) {
			return;
		}
		
		moveAgent(moved, direction, false);
	}

	public void moveRobot(int id, int dir) {
		// Find player
		Robot moved = map.findRobot(id);
		Direction direction = Direction.values()[dir];

		// TODO: handle this
		if (moved == null) {
			return;
		}
		
		moveAgent(moved, direction, false);
	}
}
