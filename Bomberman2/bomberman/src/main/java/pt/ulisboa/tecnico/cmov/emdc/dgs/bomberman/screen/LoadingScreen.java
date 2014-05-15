package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.screen;

import java.util.List;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.BombingActivity;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Screen;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Player;

/**
 * Created by savasci on 4/29/2014.
 */
public class LoadingScreen extends Screen {
    public LoadingScreen(Game game, int level) {
        super(game);

        ((BombingActivity) game).currentLevel = new World(game, level);
        if(!((BombingActivity) game).multiplayer)
        {
            for(Player current : ((BombingActivity) game).currentLevel.players) {
                if(current.id == 0)
                {
                    current.playerName = ((BombingActivity)game).playerName;
                    ((BombingActivity) game).currentLevel.myPlayer = current;

                }
            }

        }

    }

    public LoadingScreen(Game game, int level,List<Player> players,Player myplayer) {
        super(game);

        ((BombingActivity) game).currentLevel = new World(game, level);
        if(players!=null) {
            for(int i=0;i<players.size();i++){
                ((BombingActivity) game).currentLevel.players.get(i).score = players.get(i).score;
                if( ((BombingActivity) game).currentLevel.players.get(i).id == myplayer.id) {
                    ((BombingActivity) game).currentLevel.myPlayer = ((BombingActivity) game).currentLevel.players.get(i) ;
                }

            }
        }


    }

    @Override
    public void update(float deltaTime) {
        game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        game.setScreen(new GameScreen(game));

    }

    @Override
    public void present(float deltaTime) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
