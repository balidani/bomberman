package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Audio;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Music;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Sound;

import java.io.IOException;

/**
 * Created by savasci on 4/26/2014.
 */
public class AndroidAudio implements Audio {
    AssetManager assets;
    SoundPool soundPool;

    public AndroidAudio(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.assets = activity.getAssets();
        this.soundPool = new SoundPool(20,AudioManager.STREAM_MUSIC,0);
    }

    @Override
    public Music newMusic(String filename) {
        try {
            AssetFileDescriptor assetFileDescriptor = assets.openFd(filename);
            return new AndroidMusic(assetFileDescriptor);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load music '"+filename+"'");
        }
    }

    @Override
    public Sound newSound(String filename) {
        try {
            AssetFileDescriptor assetFileDescriptor = assets.openFd(filename);
            int soundId = soundPool.load(assetFileDescriptor,0);
            return new AndroidSound(soundPool,soundId);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load sound'"+filename+"'");
        }
    }
}
