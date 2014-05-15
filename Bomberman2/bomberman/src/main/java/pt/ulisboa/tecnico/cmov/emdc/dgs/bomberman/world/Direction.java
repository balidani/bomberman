package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;

/**
 * Created by savasci on 5/3/2014.
 */
public class Direction {
    public static final Direction DOWN  = new Direction(0);
    public static final Direction LEFT  = new Direction(1);
    public static final Direction UP    = new Direction(2);
    public static final Direction RIGHT = new Direction(3);

    public static final Direction[] AllDirections = {Direction.DOWN, Direction.LEFT, Direction.UP, Direction.RIGHT}; // order of this matters

    public int dir;
    public int i;
    public int j;

    private Direction(int dir) {

        this.dir = dir;

        switch (dir) {
            case 0:
                i = 1;
                j = 0;
                break;
            case 1:
                i = 0;
                j = -1;
                break;
            case 2:
                i = -1;
                j = 0;
                break;
            case 3:
                i = 0;
                j = 1;
                break;
        }
    }
}
