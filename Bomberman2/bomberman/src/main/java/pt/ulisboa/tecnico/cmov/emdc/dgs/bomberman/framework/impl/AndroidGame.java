package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.R;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Audio;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.FileIO;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Graphics;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Input;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Screen;

/**
 * Created by savasci on 4/26/2014.
 */
public class AndroidGame extends Activity implements Game{

    // Frame Buffer width and height defines the absolute world dimensions
    // visible width defines the width of the portion that the user see at once
    // visible height is calculated according to current renderView
    public static final int FRAME_BUFFER_WIDTH = 640;
    public static final int FRAME_BUFFER_HEIGHT = 640;
    public static final int VISIBLE_WIDTH = 320;
    protected AndroidFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int frameBufferWidth = FRAME_BUFFER_WIDTH;
        int frameBufferHeight = FRAME_BUFFER_HEIGHT;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,frameBufferHeight, Bitmap.Config.RGB_565);

        // TODO fix here
        // AndroidInput is not working properly for touch
        // yet currently renderview doesn't receive any touch events
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float scaleX = (float) frameBufferWidth / size.x;
        float scaleY = (float) frameBufferHeight / size.y;

        renderView = new AndroidFastRenderView(this,frameBuffer);
        graphics = new AndroidGraphics(getAssets(),frameBuffer);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this,renderView,scaleX,scaleY);
        screen = getStartScreen();
        // Full Screen game
        //setContentView(renderView);

        // GAMESCREEN is set elsewhere!

    }

    @Override
    protected void onResume() {
        super.onResume();
        screen.resume();
        renderView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        renderView.pause();
        screen.pause();

        if(isFinishing())
            screen.dispose();
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
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public void setScreen(Screen screen) {
        if(screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    @Override
    public Screen getCurrentScreen() {
        return screen;
    }

    @Override
    public Screen getStartScreen() {
        return null;
    }

    @Override
    public void lookAt(int left, int top) {
        renderView.visibleAt(left,top);
    }

}
