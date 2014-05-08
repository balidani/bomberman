package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework;

/**
 * Created by savasci on 4/26/2014.
 */
public interface Game {

    public Input getInput();

    public FileIO getFileIO();

    public Graphics getGraphics();

    public Audio getAudio();

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getStartScreen();

    public void lookAt(int left, int top);

}
