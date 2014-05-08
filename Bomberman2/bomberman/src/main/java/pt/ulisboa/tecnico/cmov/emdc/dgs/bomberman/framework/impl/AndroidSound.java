package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl;

import android.media.SoundPool;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Sound;

/**
 * Created by savasci on 4/26/2014.
 */
public class AndroidSound implements Sound {
    int soundId;
    SoundPool soundPool;

    public AndroidSound(SoundPool soundPool,int soundId) {
        this.soundId = soundId;
        this.soundPool = soundPool;
    }

    @Override
    public void play(float volume) {
        soundPool.play(soundId,volume,volume,0,0,1);
    }

    @Override
    public void dispose() {
        soundPool.unload(soundId);
    }
}
