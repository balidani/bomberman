package pt.ulisboa.tecnico.bomberman.game;

import java.util.ArrayList;
import java.util.List;

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

	Paint paint;
	Map map;

	public GameView(Context context, Map map) {
		super(context);

		paint = new Paint();
		this.map = map;

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
	}

	public void render() {
		this.invalidate();
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int tileID;
		int cellSize = Math.min(getWidth() / map.width, getHeight() / map.height);

		// Render tiles
		for (int i = 0; i < map.height; ++i) {
			for (int j = 0; j < map.width; ++j) {

				tileID = map.tiles[i][j].type.ordinal();
				Bitmap bmp = tileBitmaps.get(tileID);

				canvas.drawBitmap(bmp, null, new Rect(j * cellSize, i * cellSize, 
						j * cellSize + cellSize, i * cellSize + cellSize), paint);
			}
		}

		// Render robots
		for (Robot robot : map.robots) {
			int i = robot.position.y;
			int j = robot.position.x;

			canvas.drawBitmap(robotBitmap, null, new Rect(j * cellSize, i * cellSize, 
					j * cellSize + cellSize, i * cellSize + cellSize), paint);
		}

		// Render players
		for (Player player : map.players) {
			int i = player.position.y;
			int j = player.position.x;
			
			canvas.drawBitmap(playerBitmaps.get(0), null, new Rect(j * cellSize, i * cellSize, 
					j * cellSize + cellSize, i * cellSize + cellSize), paint);
		}
	}

}
