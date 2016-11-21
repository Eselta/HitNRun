package mjj.cma.hitnrun.GameEngine;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Eselta on 03-10-2016.
 */
public class Music implements MediaPlayer.OnCompletionListener
{
    private MediaPlayer mediaPlayer; //For reading music files and streaming to the speakers
    private boolean isPrepared = false;// Is the media player ready?

    public Music(AssetFileDescriptor assDescriptor)
    {
        mediaPlayer = new MediaPlayer();
        try
        {
            mediaPlayer.setDataSource(assDescriptor.getFileDescriptor(), assDescriptor.getStartOffset(), assDescriptor.getLength());
            mediaPlayer.prepare();
            isPrepared = true;
            mediaPlayer.setOnCompletionListener(this);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Media player could not load music file");
        }
    }


    public void dispose()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    public boolean isLooping()
    {
        return mediaPlayer.isLooping();
    }
    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public boolean isStopped()
    {
        return !isPrepared;
    }

    public void pause()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
    }

    public void play()
    {
        if (mediaPlayer.isPlaying())
        {
            return;
        }

        try
        {
            synchronized (this)
            {
                if (!isPrepared)
                {
                    mediaPlayer.prepare();
                    isPrepared = true;
                }
                mediaPlayer.start();
            }
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
            Log.d("Class: Music", "Could not play() the bloody music");
        }
        catch (IOException e)
        {
            Log.d("Class: Music", "Oh shit, IO exception");
        }
    }

    public void stop()
    {
        synchronized (this)
        {
            if (!isPrepared)
            {
                return;
            }
            mediaPlayer.stop();
            isPrepared = false;
        }
    }

    public void setLooping(boolean loop)
    {
        mediaPlayer.setLooping(loop);
    }

    public void setVolume(float volume)
    {
        mediaPlayer.setVolume(volume, volume);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer)
    {
        isPrepared = false;

    }
}
