package jp.caliconography.welco.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import jp.caliconography.welco.R;

/**
 * Created by abe on 2015/02/11.
 */
public class ChimePlayer {

    private Context mContext;
    private SoundPool mSoundPool;
    private int mChimeId;

    public ChimePlayer(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        this.mContext = context;

        init();
    }

    private void init() {
        buildSoundPool();
        makeChimeId();
    }

    /**
     * SoundPoolを作る。
     */
    private void buildSoundPool() {
        // 予め音声データを読み込む
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build()).build();
        } else {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        }
    }

    /**
     * チャイムをロードし、そのIDを保管。
     */
    private void makeChimeId() {
        mChimeId = mSoundPool.load(this.mContext, R.raw.se_maoudamashii_chime10, 0);
    }

    /**
     * チャイムを再生する。
     */
    public void play() {
        // 再生
        mSoundPool.play(mChimeId, 1.0F, 1.0F, 0, 0, 1.0F);
    }

    public void releaseSoundPool() {
        mSoundPool.release();
    }
}
