package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework;

/**
 * Created by savasci on 4/26/2014.
 */
public interface Music {
    public void play();

    public void stop();

    public void pause();

    public void setLooping(boolean looping);

    public void setVolume(float volume);

    public boolean isPlaying();

    public boolean isStopped();

    public boolean isLooping();

    public void dispose();
}
