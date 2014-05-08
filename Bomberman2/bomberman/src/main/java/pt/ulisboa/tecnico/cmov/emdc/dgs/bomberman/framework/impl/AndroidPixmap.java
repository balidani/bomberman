package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl;

import android.graphics.Bitmap;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Graphics.PixmapFormat;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Pixmap;

/**
 * Created by savasci on 4/26/2014.
 */
public class AndroidPixmap implements Pixmap {
    Bitmap bitmap;
    PixmapFormat format;

    public AndroidPixmap(Bitmap bitmap, PixmapFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public PixmapFormat getFormat() {
        return format;
    }

    @Override
    public void dispose() {
        bitmap.recycle();
    }
}
