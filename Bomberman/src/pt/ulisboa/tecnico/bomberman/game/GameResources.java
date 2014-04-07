package pt.ulisboa.tecnico.bomberman.game;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.bomberman.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

public class GameResources {

	public static List<Bitmap> Tiles;
	public static SparseArray<List<Bitmap>> Players;
	public static Bitmap Robot;
	public static Bitmap Bomb;
	public static Bitmap Flame;

	public static void init(Resources res) {

		int tileDrawables[] = { 
			R.drawable.empty, // EMPTY
			R.drawable.wall, // WALL
			R.drawable.obstacle, // OBSTACLE
		};
		
		int playerDrawable = R.drawable.player_sprites;
		int robotDrawable = R.drawable.robot;
		int bombDrawable = R.drawable.bomb;
		int flameDrawable = R.drawable.explosion;
		
		// Pre-render all image resources to Bitmaps
		Tiles = new ArrayList<Bitmap>();
		for (int id : tileDrawables) {
			Tiles.add(BitmapFactory.decodeResource(res, id));
		}

		// Parse player sprites
		
		Players = new SparseArray<List<Bitmap>>();
		Bitmap spriteSheet = BitmapFactory.decodeResource(res, playerDrawable);
		for (int p = 0; p < 3; ++p) {
			List<Bitmap> spriteList = new ArrayList<Bitmap>();
			
			for (int dir = 0; dir < Direction.values().length; ++dir) {
				Bitmap sprite = Bitmap.createBitmap(spriteSheet, dir * 32, p * 32, 32, 32);
				spriteList.add(sprite);
			}
			
			Players.put(p, spriteList);
		}

		Robot = BitmapFactory.decodeResource(res, robotDrawable);
		Bomb = BitmapFactory.decodeResource(res, bombDrawable);
		Flame = BitmapFactory.decodeResource(res, flameDrawable);
	}
}
