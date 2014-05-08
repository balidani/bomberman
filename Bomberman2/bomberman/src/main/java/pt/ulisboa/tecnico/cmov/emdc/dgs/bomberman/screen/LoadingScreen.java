package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.screen;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.BombingActivity;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Pixmap;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Graphics;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Input;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Screen;

/**
 * Created by savasci on 4/29/2014.
 */
public class LoadingScreen extends Screen {
    public LoadingScreen(Game game, int level) {
        super(game);

        ((BombingActivity) game).currentLevel = new World(game, level);


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
