package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl;

import android.util.Log;

/**
 * Created by savasci on 4/28/2014.
 */
public class FPSCounter {
    long startTime = System.nanoTime();
    int frames = 0;

    public void logFrame() {
        frames++;
        if(System.nanoTime() -startTime > 1000000000.0f) {
            Log.d("FPSCounter", "fps: "+frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }
}
