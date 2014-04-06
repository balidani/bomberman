package pt.ulisboa.tecnico.bomberman.game;

import pt.ulisboa.tecnico.bomberman.R;

class Config {
	
	public int gameDuration;
	public int explosionTimeOut;
	public int explosionDuration;
	public int explosionRange;
	public int robotSpeed;
	public int pointsPerRobot;
	public int pointsPerOpponent;

	public String mapString;
	
	public static final int TileImages[] = {
		R.drawable.empty, 		// EMPTY
		R.drawable.wall,		// WALL
		R.drawable.obstacle, 	// OBSTACLE
		R.drawable.bomb			// BOMB
	};
	
	public static final int PlayerImages[] = {
		R.drawable.player_1,
		R.drawable.player_2,
		R.drawable.player_3
	};
	
	public static final int RobotImage = R.drawable.robot;

	public Config(int level) {
		loadConfiguration(level);
	}

	private void loadConfiguration(int level) {
		
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
		explosionTimeOut = 3000;
		explosionDuration = 2000;
		
		explosionRange = 1;
	}
}