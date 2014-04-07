package pt.ulisboa.tecnico.bomberman.game;

import pt.ulisboa.tecnico.bomberman.R;
import pt.ulisboa.tecnico.bomberman.game.Tile.TileType;
import pt.ulisboa.tecnico.bomberman.game.agents.Agent;
import pt.ulisboa.tecnico.bomberman.game.agents.Player;
import pt.ulisboa.tecnico.bomberman.game.events.BombEvents;
import pt.ulisboa.tecnico.bomberman.game.events.RobotEvents;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {

	// Game components
	public GameView gameView;
	public Player player;
	public Map map;
	
	// Events
	public RobotEvents robotEvents;
	public BombEvents bombEvents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		// Inititialize resources
		GameResources.init(getResources());

		// Initialize map
		Map.init(Config.mapString);
		map = Map.instance();

		// Initialize player
		player = map.players.get(0);
		
		// Initialize view
		RelativeLayout gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
		gameView = new GameView(GameActivity.this, this);
		gameLayout.addView(gameView);

		// Initialize robot events
		robotEvents = new RobotEvents(this);
		bombEvents = new BombEvents(this);
		
		// Initial render
		render();
	}
	
	public void render() {
		gameView.render();
		RelativeLayout uiLayout = (RelativeLayout) findViewById(R.id.uiLayout);
		uiLayout.invalidate();
	}

	public void onMoveUp(View view) {
		moveAgent(player, Direction.UP);
		player.facing = Direction.UP;
		render();
	}

	public void onMoveDown(View view) {
		moveAgent(player, Direction.DOWN);
		player.facing = Direction.DOWN;
		render();
	}
	
	public void onMoveLeft(View view) {
		moveAgent(player, Direction.LEFT);
		player.facing = Direction.LEFT;
		render();
	}
	
	public void onMoveRight(View view) {
		moveAgent(player, Direction.RIGHT);
		player.facing = Direction.RIGHT;
		render();
	}
	
	public void onBomb(View view) {
		bombEvents.addBomb();
	}

	public synchronized void moveAgent(Agent agent, Direction direction) {
		
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
			return;
		}

		agent.position = next;
	}
}
