package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl;

import android.content.Context;
import android.view.View;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Input;

import java.util.List;

/**
 * Created by savasci on 4/26/2014.
 */
public class AndroidInput implements Input {
    AccelerometerHandler accelerometerHandler;
    KeyboardHandler keyboardHandler;
    MultiTouchHandler multiTouchHandler;

    public AndroidInput(Context context, View view, float scaleX, float scaleY) {
        accelerometerHandler = new AccelerometerHandler(context);
        keyboardHandler = new KeyboardHandler(view);
        multiTouchHandler = new MultiTouchHandler(view,scaleX,scaleY);
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        return keyboardHandler.isKeyPressed(keyCode);
    }

    @Override
    public boolean isTouchDown(int pointer) {
        return multiTouchHandler.isTouchDown(pointer);
    }

    @Override
    public int getTouchX(int pointer) {
        return multiTouchHandler.getTouchX(pointer);
    }

    @Override
    public int getTouchY(int pointer) {
        return multiTouchHandler.getTouchY(pointer);
    }

    @Override
    public float getAccelX() {
        return accelerometerHandler.getAccelX();
    }

    @Override
    public float getAccelY() {
        return accelerometerHandler.getAccelY();
    }

    @Override
    public float getAccelZ() {
        return accelerometerHandler.getAccelZ();
    }

    @Override
    public List<KeyEvent> getKeyEvents() {
        return keyboardHandler.getKeyEvents();
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        return multiTouchHandler.getTouchEvents();
    }
}
