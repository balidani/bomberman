package pt.ulisboa.tecnico.bomberman.game;

import pt.ulisboa.tecnico.bomberman.R;

public class Config {
	
	public static int gameDuration;
	public static int explosionTimeOut;
	public static int explosionDuration;
	public static int explosionRange;
	public static int robotSpeed;
	public static int pointsPerRobot;
	public static int pointsPerOpponent;

	public static String mapString;
	
	public static final int TileImages[] = {
		R.drawable.empty, 		// EMPTY
		R.drawable.wall,		// WALL
		R.drawable.obstacle, 	// OBSTACLE
	};
	
	public static final int PlayerImages[] = {
		R.drawable.player_1,
		R.drawable.player_2,
		R.drawable.player_3
	};
	
	public static final int RobotImage = R.drawable.robot;
	public static final int BombImage = R.drawable.bomb;
	public static final int FlameImage = R.drawable.explosion;

	public static void init() {
		Config.loadConfiguration();
	}

	private static void loadConfiguration() {
		
		/*
		 * TODO: these data should be filled from a config file.
		 * 
		 * This is the map we will read from the config, 
		 * according to the project description.
		 */
		mapString = 
			"WWWWWWWWWWWWWWWWWWW\n" +
			"W-1-----R----O----W\n" +
			"WWOW-W-W-W-W-W-W-WW\n" +
			"W-O--------R------W\n" +
			"WWOWOW-W-W-W-WOW-WW\n" +
			"W--O--------O-R---W\n" +
			"WWOW-W-W-W-W-W-W-WW\n" +
			"W-OOOO-----------OW\n" +
			"WWOWOWOWOW-W-W-W-WW\n" +
			"W--OOOO--R--OOO---W\n" +
			"WW-W-WOWOW-WOWOWOWW\n" +
			"W------OOO--OOO-ORW\n" +
			"WWWWWWWWWWWWWWWWWWW\n";
		
		// Values in milliseconds
		robotSpeed = 1000;
		explosionTimeOut = 2000;
		explosionDuration = 1000;
		
		explosionRange = 2;
	}
}