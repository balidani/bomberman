package pt.ulisboa.tecnico.bomberman.game;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.bomberman.game.agents.Agent;
import pt.ulisboa.tecnico.bomberman.game.agents.Bomb;
import pt.ulisboa.tecnico.bomberman.game.agents.Flame;
import pt.ulisboa.tecnico.bomberman.game.agents.Player;
import pt.ulisboa.tecnico.bomberman.game.agents.Robot;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class GameView extends View {

	List<List<? extends Agent>> agentList;

	Map map;
	Paint paint;
	Player player;

	public GameView(Context context, GameActivity game) {
		super(context);

		paint = new Paint();
		this.map = game.map;
		this.player = game.player;
		
		// Collect all agent list references to render
		agentList = new ArrayList<List<? extends Agent>>();
		agentList.add(map.bombs);
		agentList.add(map.robots);
		agentList.add(map.players);
		agentList.add(map.flames);
	}

	public void render() {
		this.invalidate();
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int tileID;
		int screenWidth = getWidth();
		int screenHeight = getHeight();
		int cellSize = Math.min(screenWidth / map.width, screenHeight / map.height);
		cellSize *= 1.75;
		
		// Adjust map so that the player is close to the center
		int adjustX = screenWidth/2 - player.position.x * cellSize;
		int adjustY = screenHeight/2 - player.position.y * cellSize;

		// Render tiles
		for (int i = 0; i < map.height; ++i) {
			for (int j = 0; j < map.width; ++j) {

				tileID = map.tiles[i][j].type.ordinal();
				Bitmap bmp = GameResources.Tiles.get(tileID);

				canvas.drawBitmap(bmp, null, new Rect(j * cellSize + adjustX, i * cellSize + adjustY, 
						j * cellSize + cellSize + adjustX, i * cellSize + cellSize + adjustY), paint);
			}
		}

		// Render agents
		for (List<? extends Agent> list : agentList) {
			for (Agent agent : list) {

				int i = agent.position.y;
				int j = agent.position.x;
				Bitmap bmp = null;
				
				if (agent instanceof Player) {
					bmp = GameResources.Players.get(0).get(player.facing.value());
				} else if (agent instanceof Robot) {
					bmp = GameResources.Robot;
				} else if (agent instanceof Bomb) {
					bmp = GameResources.Bomb;
				} else if (agent instanceof Flame) {
					paint.setAlpha(((Flame) agent).alpha);
					bmp = GameResources.Flame;
				}
				
				canvas.drawBitmap(bmp, null, new Rect(j * cellSize + adjustX, i * cellSize + adjustY, 
						j * cellSize + cellSize + adjustX, i * cellSize + cellSize + adjustY), paint);
				paint.setAlpha(255);
				
			}
		}
	}

}
