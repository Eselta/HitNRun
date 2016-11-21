package mjj.cma.hitnrun.GameEngine;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Eselta on 05-09-2016.
 */
public abstract class GameEngine extends Activity implements Runnable, View.OnKeyListener, SensorEventListener
{

    private Thread mainLoopThread;
    private State state = State.Paused;
    private List<State> stateChanges = new LinkedList<State>();

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private Screen screen;
    private Canvas canvas;
    private Bitmap offscreenSurface;

    private Rect src = new Rect();
    private Rect dst = new Rect();

    private boolean pressedKeys[] = new boolean[256];

    private KeyEventPool keyEventPool = new KeyEventPool();
    private List<mjj.cma.hitnrun.GameEngine.KeyEvent> keyEvents = new ArrayList<>();
    private List<mjj.cma.hitnrun.GameEngine.KeyEvent> keyEventBuffer = new ArrayList<>();


    private TouchHandler touchHandler;
    private TouchEventPool touchEventPool = new TouchEventPool();
    private List<TouchEvent> touchEvents = new ArrayList<>();
    private List<TouchEvent> touchEventsBuffer = new ArrayList<>();

    private float[] acceleromenter = new float[3];

    private SoundPool soundPool;

    private int framesPerSecond = 0;

    private Paint paint = new Paint();

    public Music music;

    //*************Variables above, and Methods below

    //Henter StartScreen fra det specifikke spil, siden det her kun er basic instructioner
    public abstract Screen createStartScreen();


    public void onCreate(Bundle instanceBundle)
    {
        super.onCreate(instanceBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceHolder = surfaceView.getHolder();
        int testScreenOrientation = this.getResources().getConfiguration().orientation;

        if(testScreenOrientation == 2)
        {
            setOffscreenSurface(480, 320);
        }
        else
        {
            setOffscreenSurface(320, 480);
        }

        surfaceView.setFocusableInTouchMode(true);
        surfaceView.requestFocus();
        surfaceView.setOnKeyListener(this);
        touchHandler = new MultiTouchHandler(surfaceView, touchEventsBuffer, touchEventPool);
        SensorManager manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0)
        {
            Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);


        screen = createStartScreen();

    }//End of onCreate method

    public void setOffscreenSurface(int width, int height)
    {
        if (offscreenSurface != null)
        {
            offscreenSurface.recycle();
        }

        offscreenSurface = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(offscreenSurface);

    }

    //Så vi kan ændre screen på et hilket som helst givet tidspunk
    public void setScreen(Screen screen)
    {
        if (this.screen != null) this.screen.dispose();

        this.screen = screen;
    }

    //Loading Reasources
    public Bitmap loadBitmap (String fileName)
    {
        InputStream inFromFile = null;
        Bitmap bitmap = null;

        try
        {
            inFromFile = getAssets().open(fileName);
            bitmap = BitmapFactory.decodeStream(inFromFile);
            if (bitmap == null)
            {
                throw new RuntimeException("Load bitmap from asset " + fileName + " resulting object returning null");
            }
        }

        catch (IOException e)
        {
            throw new RuntimeException("Could not load bitmap from asset "+fileName+"!");
        }
        finally
        {
            if (inFromFile != null)
            {
                try
                {
                    inFromFile.close();
                }
                catch (IOException e){}
            }
        }

        return bitmap;
    }

    public Sound loadSound(String fileName)
    {
        try
        {
            AssetFileDescriptor assDescriptor = getAssets().openFd(fileName);
            int soundId = soundPool.load(assDescriptor, 0);
            return new Sound(soundPool, soundId);
        }

        catch(IOException e)
        {
            throw new RuntimeException("Could not load sound: " + fileName + " !!! stupid !!!");
        }
    }

    public Music loadMusic(String fileName)
    {
        try
        {
            AssetFileDescriptor assDescriptor = getAssets().openFd(fileName);
            return new Music(assDescriptor);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Game Engine: CRAP!!! Could not create Music object");
        }
    }

    public Typeface loadFont(String filename)
    {
        Typeface font = Typeface.createFromAsset(getAssets(), filename);
        if (font == null)
        {
            throw new RuntimeException("CRAP... Coudn't load font file");
        }

        return font;
    }

    //Load resources end

    //
    public void clearFramebuffer(int color)
    {
        if(canvas != null)canvas.drawColor(color);
    }

    public int getFramebufferWidth()
    {
        return offscreenSurface.getWidth();
    }

    public int getFramebufferHeight()
    {
        return offscreenSurface.getHeight();
    }

    public void drawBitmap(Bitmap bitmap, int x, int y)
    {
        if (canvas != null)
        {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    public void drawBitmap(Bitmap bitmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight)
    {
        if (canvas == null)
        {
            return;
        }
        Rect src = new Rect();
        Rect dst = new Rect();
        src.left = srcX;
        src.top = srcY;
        src.right = srcX + srcWidth;
        src. bottom = srcY + srcHeight;
        dst.left = x;
        dst.top = y;
        dst.right = x + srcWidth;
        dst.bottom = y + srcHeight;

        canvas.drawBitmap(bitmap, src, dst, null);

    }

    public void drawText(Typeface font, String text, int x, int y, int color, int size)
    {
        paint.setTypeface(font);
        paint.setTextSize(size);
        paint.setColor(color);

        canvas.drawText(text, x, y, paint);

    }

    public boolean onKey(View view, int keyCode, KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            pressedKeys[keyCode] = true;
            synchronized (keyEventBuffer)
            {
                mjj.cma.hitnrun.GameEngine.KeyEvent keyEvent = keyEventPool.obtain();
                keyEvent.type = mjj.cma.hitnrun.GameEngine.KeyEvent.KeyEventType.Down;
                keyEvent.keyCode = keyCode;
                keyEvent.character = (char) event.getUnicodeChar();

                keyEventBuffer.add(keyEvent);
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP)
        {
            pressedKeys[keyCode] = false;
            synchronized (keyEventBuffer)
            {
                mjj.cma.hitnrun.GameEngine.KeyEvent keyEvent = keyEventPool.obtain();
                keyEvent.type = mjj.cma.hitnrun.GameEngine.KeyEvent.KeyEventType.Up;
                keyEvent.keyCode = keyCode;
                keyEvent.character = (char) event.getUnicodeChar();

                keyEventBuffer.add(keyEvent);
            }
        }

        return false;
    }

    public boolean isKeyPressed(int keyCode)
    {
        return pressedKeys[keyCode];
    }

    public boolean isTouchDown(int pointer)
    {

        return touchHandler.isTouchDown(pointer);
    }

    public int getTouchX(int pointer)
    {
        return (int) ((float) touchHandler.getTouchX(pointer) / (float) surfaceView.getWidth() * offscreenSurface.getWidth());
    }

    public int getTouchY(int pointer)
    {

        return (int) ((float) touchHandler.getTouchY(pointer) / (float) surfaceView.getHeight() * offscreenSurface.getHeight());
    }

    public List<mjj.cma.hitnrun.GameEngine.KeyEvent> getKeyEvents()
    {
        return keyEvents;
    }

    public List<TouchEvent> getTouchEvents()
    {
        return touchEvents;
    }


    private void fillEvents()
    {
        synchronized (keyEventBuffer)
        {
            int stop = keyEventBuffer.size();

            for (int i = 0; i < stop; i++)
            {
                keyEvents.add(keyEventBuffer.get(i));
            }
            keyEventBuffer.clear();
        }

        synchronized (touchEventsBuffer)
        {
            int stop = touchEventsBuffer.size();
            for (int  i = 0; i < stop; i++)
            {
                touchEvents.add(touchEventsBuffer.get(i));
            }
            touchEventsBuffer.clear();
        }
    }

    private void freeEvents()
    {
        synchronized (keyEvents)
        {
            int stop = keyEvents.size();
            for (int i = 0; i < stop; i++)
            {
                keyEventPool.free(keyEvents.get(i));
            }
            keyEvents.clear();
        }

        synchronized (touchEvents)
        {
            int stop = touchEvents.size();
            for (int i = 0; i < stop; i++)
            {
                touchEventPool.free(touchEvents.get(i));
            }

            touchEvents.clear();
        }
    }

    public float[] getAccelerometer()
    {

        return acceleromenter;
    }

    public void clearAccelerometer()
    {
        acceleromenter[0] = 0;
        acceleromenter[1] = 0;
        acceleromenter[2] = 0;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void onSensorChanged(SensorEvent event)
    {
        System.arraycopy(event.values, 0, acceleromenter, 0, 3);
    }

    public int getFramesPerSecond()
    {
        return framesPerSecond;
    }

    public void run()
    {
        int frames = 0;
        long startTime = System.nanoTime();
        long lastTime = System.nanoTime();
        long currentTime = 0;
        while (true)
        {
            synchronized (stateChanges)
            {
                for (int i = 0; i < stateChanges.size(); i++)
                {
                    state = stateChanges.get(i);
                    if (state == State.Disposed)
                    {
                        Log.d("GameEngine", "State is cahgend to Disposed");
                        return;
                    } else if (state == State.Paused)
                    {
                        Log.d("GameEngine", "State is cahgend to Paused");
                        return;
                    } else if (state == State.Resumed)
                    {
                        state = State.Running;
                        Log.d("GameEngine", "State is cahgend to Resume");
                    }
                    else
                    {
                        Log.d("Error", "Something REALLY bad Happened");
                    }
                }//end of for-lopp

                stateChanges.clear();

            }//End of Synchronized

            if(state == State.Running)
            {
                if(!surfaceHolder.getSurface().isValid()) continue;
                Canvas canvas = surfaceHolder.lockCanvas();
                // all drwaing happens here!!!!!!!!!!!!!!!
                fillEvents();

                currentTime = System.nanoTime();
                if (screen != null) screen.update((currentTime - lastTime) / 1000000000.0f);
                freeEvents();
                lastTime = currentTime;
                src.left = 0;
                src.top = 0;
                src.right = offscreenSurface.getWidth() - 1;
                src.bottom = offscreenSurface.getHeight() - 1;

                dst.left = 0;
                dst.top = 0;
                dst.right = surfaceView.getWidth();
                dst.bottom = surfaceView.getHeight();

                canvas.drawBitmap(offscreenSurface, src, dst, null);

                surfaceHolder.unlockCanvasAndPost(canvas);
                canvas = null;

            }

            frames += 1;
            if (System.nanoTime() - startTime > 1000000000)
            {
                framesPerSecond = frames;
                frames = 0;
                startTime = System.nanoTime();
            }

        }//End of while
    }//End of run method

    public void onPause()
    {
        super.onPause();
        synchronized (stateChanges)
        {
            if(isFinishing())
            {
                stateChanges.add(stateChanges.size(), State.Disposed);
            }
            else
            {
                stateChanges.add(stateChanges.size(), State.Paused);
            }
        }

        try
        {
            mainLoopThread.join();
        }
        catch (InterruptedException e)
        {
            //TODO
        }

        if (isFinishing())
        {
            ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
            soundPool.release();
        }
    }

    public void onResume()
    {
        super.onResume();
        mainLoopThread = new Thread( this );
        mainLoopThread.start();
        synchronized (stateChanges)
        {
            stateChanges.add(stateChanges.size(), State.Resumed);
        }
    }

}
