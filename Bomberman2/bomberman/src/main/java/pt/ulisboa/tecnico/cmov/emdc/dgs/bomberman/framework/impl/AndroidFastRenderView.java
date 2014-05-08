package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by savasci on 4/26/2014.
 */
public class AndroidFastRenderView extends SurfaceView implements Runnable {
    AndroidGame game;


    Bitmap frameBuffer;
    Thread renderThread = null;
    SurfaceHolder holder;
    Rect visibleRect;
    volatile boolean running = false;

    public AndroidFastRenderView(AndroidGame game, Bitmap framebuffer){
        super(game);
        this.game = game;
        this.frameBuffer = framebuffer;
        this.holder = getHolder();
        this.visibleRect = new Rect(0,0,AndroidGame.FRAME_BUFFER_WIDTH-1,AndroidGame.FRAME_BUFFER_HEIGHT-1);
    }


    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    @Override
    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime();
        float deltaTime;
        Canvas canvas;
        while(running) {
            if(!holder.getSurface().isValid()) {
                continue;
            }

            deltaTime = (System.nanoTime() - startTime) / 1000000000.0f; // in seconds
            startTime = System.nanoTime();

            game.getCurrentScreen().update(deltaTime);
            game.getCurrentScreen().present(deltaTime);

            canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(frameBuffer,visibleRect,dstRect,null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        running = false;
        while(true) {
            try {
                renderThread.join();
                return;
            } catch (InterruptedException e) {
                // retry
            }
        }
    }

    public void visibleAt(int left, int top) {
        synchronized (visibleRect) {
            visibleRect.left =left;
            visibleRect.top = top;
            visibleRect.right = left + AndroidGame.VISIBLE_WIDTH;
            visibleRect.bottom = top + (int)((float)(AndroidGame.VISIBLE_WIDTH)*(float)getHeight()/(float)getWidth());

        }
    }



}
