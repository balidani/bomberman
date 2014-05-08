package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world;

/**
 * Created by savasci on 5/3/2014.
 */
public class GridLocation {
    public int i;
    public int j;

    public GridLocation(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public GridLocation(GridLocation copy) {
        this.i = copy.i;
        this.j = copy.j;
    }

    @Override
    public boolean equals(Object o) {
        try {
            GridLocation c = (GridLocation) o;
            return (c.i == this.i) && (c.j == this.j);
        } catch (ClassCastException c) {
            return super.equals(o);
        }
    }
}
