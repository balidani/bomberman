package pt.ulisboa.tecnico.bomberman.game;

class Coordinate {
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
}