package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;



import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Audio;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.FileIO;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Graphics;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Input;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Screen;

/**
 * Created by savasci on 4/28/2014.
 */
public abstract class GLGame extends Activity implements Game, GLSurfaceView.Renderer {
    enum GLGameState{
        Initialized,
        Running,
        Paused,
        Finished,
        Idle

    }

    public GLSurfaceView glSurfaceView;
    GLGraphics glGraphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    GLGameState state = GLGameState.Initialized;
    Object stateChanged = new Object();
    long startTime = System.nanoTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setRenderer(this);
        //setContentView(glSurfaceView);

        glGraphics = new GLGraphics(glSurfaceView);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this,glSurfaceView,1,1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glGraphics.setGl10(gl10);

        synchronized (stateChanged) {
            if(state == GLGameState.Initialized) {
                screen = getStartScreen();
            }
            state = GLGameState.Running;
            screen.resume();
            startTime = System.nanoTime();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLGameState state = null;
        synchronized (stateChanged) {
            state = this.state;
        }

        if(state == GLGameState.Running) {
            float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            screen.update(deltaTime);
            screen.present(deltaTime);
        }
        if(state == GLGameState.Paused) {
            screen.pause();
            synchronized (stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }
        if(state == GLGameState.Finished) {
            screen.pause();
            screen.dispose();
            synchronized (stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }
    }

    @Override
    protected void onPause() {
        synchronized (stateChanged) {
            if(isFinishing())
                state = GLGameState.Finished;
            else
                state = GLGameState.Paused;
            while (true) {
                try {
                    stateChanged.wait();
                    break;
                } catch (InterruptedException e) {

                }
            }
        }
        glSurfaceView.onPause();
        super.onPause();
    }

    public GLGraphics getGlGraphics() {
        return glGraphics;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public FileIO getFileIO() {
        return fileIO;
    }

    @Override
    public Graphics getGraphics() {
        throw new IllegalStateException("We are using OpenGL!");
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public void setScreen(Screen newScreen) {
        if(screen ==null)
            throw  new IllegalArgumentException("Screen must not be null!");

        this.screen.pause();
        this.screen.dispose();
        newScreen.resume();
        newScreen.update(0.0f);
        this.screen = newScreen;
    }

    @Override
    public Screen getCurrentScreen() {
        return screen;
    }


}
