package pt.ulisboa.tecnico.bomberman.game;

public class Direction {

	public static final Direction UP    = new Direction(0);
	public static final Direction DOWN  = new Direction(1);
	public static final Direction LEFT  = new Direction(2);
	public static final Direction RIGHT = new Direction(3);
	
	public static Direction[] values() {
		Direction result[] = {
			Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT
		};
		
		return result;
	}
	
	private int x, y;
	
	private Direction(int dir) {
		
		switch (dir) {
		case 0:
			x = 0; 
			y = -1;
			break;
		case 1:
			x = 0; 
			y = 1; 
			break;
		case 2:
			x = -1;
			y = 0; 
			break;
		case 3:
			x = 1; 
			y = 0; 
			break;
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
