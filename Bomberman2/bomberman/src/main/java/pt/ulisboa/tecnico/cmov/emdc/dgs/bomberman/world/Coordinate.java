package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world;

/**
 * Created by savasci on 5/3/2014.
 */
public class Coordinate {
    public float x;
    public float y;

    public Coordinate(float x, float y) {
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
