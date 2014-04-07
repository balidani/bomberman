package pt.ulisboa.tecnico.bomberman.game;

public class Config {
	
	/*
	 * TODO: this data should be filled from a config file.
	 */
	public static final int gameDuration = 120;
	public static final int explosionTimeOut = 2000;
	public static final int explosionDuration = 1000;
	public static final int explosionRange = 2;
	public static final int robotSpeed = 1000;
	public static final int pointsPerRobot = 3;
	public static final int pointsPerOpponent = 10;

	public static final String mapString = 
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
}