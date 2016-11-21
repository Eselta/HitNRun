package mjj.cma.hitnrun.GameEngine;

import android.media.SoundPool;

public class Sound
{
    int soundID;
    SoundPool soundPool;

    public Sound(SoundPool soundPool, int soundID)
    {
        this.soundID = soundID;
        this.soundPool = soundPool;
    }

    public void play(float volume)
    {
        soundPool.play(soundID,volume, volume, 0, 0, 1);
    }

    public void dispose()
    {
        soundPool.unload(soundID);
    }

}
