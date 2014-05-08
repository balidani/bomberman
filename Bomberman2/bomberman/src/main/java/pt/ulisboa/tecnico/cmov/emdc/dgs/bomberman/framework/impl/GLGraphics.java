package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by savasci on 4/28/2014.
 */
public class GLGraphics {
    GLSurfaceView glSurfaceView;
    GL10 gl10;

    public GLGraphics(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
    }

    public GL10 getGl10() {
        return gl10;
    }

    public void setGl10(GL10 gl10) {
        this.gl10 = gl10;
    }

    public int getWidth() {
        return glSurfaceView.getWidth();
    }

    public int getHeight() {
        return glSurfaceView.getHeight();
    }
}
