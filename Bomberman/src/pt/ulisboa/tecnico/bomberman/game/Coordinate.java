package pt.ulisboa.tecnico.bomberman.game;

public class Coordinate {
	public int x;
	public int y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coordinate(Coordinate copy) {
		this.x = copy.x;
		this.y = copy.y;
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			Coordinate c = (Coordinate) o;
			return (c.x == this.x) && (c.y == this.y);
		} catch (ClassCastException c) {
			return super.equals(o);
		}
	}
}