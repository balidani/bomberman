package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Coordinate;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Direction;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.GridLocation;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.map.Map;

/**
 * Created by savasci on 5/3/2014.
 */
public class Agent {
    public Game game;
    public Coordinate position;
    public Coordinate destination;
    protected Direction direction;
    float destinationArrivalTime;
    float currentTime;
    int speed;
    public boolean isAlive;
    public boolean isDying;

    public Agent(Game game, int i, int j) {
        this.game = game;
        float x = j* GameAssets.ASSET_DIMENSION ;
        float y = - i* GameAssets.ASSET_DIMENSION ;
        position = new Coordinate(x,y);
        destination = new Coordinate(x,y);
        destinationArrivalTime = 0.0f;
        currentTime = 0.0f;
        direction = null;
        isAlive = true;
    }

    public void die() {
        if (isDying) return;

        isDying = true;
        currentTime = 0.0f;
        destinationArrivalTime = 0.0f;
    }

}
