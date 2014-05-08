package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework;

/**
 * Created by savasci on 4/26/2014.
 */
public abstract class Screen {
    protected final Game game;

    public Screen(Game game) {
        this.game = game;
    }

    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
}
