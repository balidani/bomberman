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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class GameView extends View {

	List<Bitmap> tileBitmaps;
	List<Bitmap> playerBitmaps;
	Bitmap robotBitmap;
	Bitmap bombBitmap;
	Bitmap flameBitmap;

	List<List<? extends Agent>> agentList;

	Map map;
	Paint paint;
	Player player;

	public GameView(Context context, GameActivity game) {
		super(context);

		paint = new Paint();
		this.map = game.map;
		this.player = game.player;

		// Pre-render all image resources to BitMaps
		tileBitmaps = new ArrayList<Bitmap>();
		for (int id : Config.TileImages) {
			tileBitmaps.add(BitmapFactory.decodeResource(getResources(), id));
		}

		playerBitmaps = new ArrayList<Bitmap>();
		for (int id : Config.PlayerImages) {
			playerBitmaps.add(BitmapFactory.decodeResource(getResources(), id));
		}

		robotBitmap = BitmapFactory.decodeResource(getResources(), Config.RobotImage);
		bombBitmap = BitmapFactory.decodeResource(getResources(), Config.BombImage);
		flameBitmap = BitmapFactory.decodeResource(getResources(), Config.FlameImage);
		
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
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int tileID;
		int screenWidth = getWidth();
		int screenHeight = getHeight();
		int cellSize = Math.min(screenWidth / map.width, screenHeight / map.height);
		cellSize *= 1.5;
		
		// Adjust map so that the player is close to the center
		int adjustX = screenWidth/2 - player.position.x * cellSize;
		int adjustY = screenHeight/2 - player.position.y * cellSize;

		// Render tiles
		for (int i = 0; i < map.height; ++i) {
			for (int j = 0; j < map.width; ++j) {

				tileID = map.tiles[i][j].type.ordinal();
				Bitmap bmp = tileBitmaps.get(tileID);

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
					bmp = playerBitmaps.get(0);
				} else if (agent instanceof Robot) {
					bmp = robotBitmap;
				} else if (agent instanceof Bomb) {
					bmp = bombBitmap;
				} else if (agent instanceof Flame) {
					bmp = flameBitmap;
				}
				
				canvas.drawBitmap(bmp, null, new Rect(j * cellSize + adjustX, i * cellSize + adjustY, 
						j * cellSize + cellSize + adjustX, i * cellSize + cellSize + adjustY), paint);
			}
		}
	}

}
