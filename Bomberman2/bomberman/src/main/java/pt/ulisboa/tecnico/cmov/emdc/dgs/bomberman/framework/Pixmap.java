package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Graphics.PixmapFormat;

/**
 * Created by savasci on 4/26/2014.
 */
public interface Pixmap {
    public int getWidth();

    public int getHeight();

    public PixmapFormat getFormat();

    public void dispose();
}
