package mjj.cma.hitnrun.HitNRun;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import mjj.cma.hitnrun.GameEngine.*;

public class MainMenuScreen extends Screen
{

    Bitmap car;
    Bitmap startGame;
    Bitmap backGround;
    Typeface font;
    float passedTime    = 0.0f;

    public MainMenuScreen(GameEngine game)
    {
        super(game);
        backGround  = game.loadBitmap("xcarbackground.png");
        car         = game.loadBitmap("xbluecar2.png");
        startGame   = game.loadBitmap("xstartgame.png");
        font        = game.loadFont("aller.ttf");
    }

    @Override
    public void update(float deltaTime)
    {
        game.drawBitmap(backGround, 0, 0, 0, 0, 480, 320);
        game.drawBitmap(car, 50, 160);
        game.drawText(font, "Highest score: " + (int)game.getHighScore(), 50, 250, Color.WHITE, 16);

        passedTime = passedTime + deltaTime;
        if(passedTime - (int)passedTime < 0.5f)
        {
            game.drawBitmap(startGame, (240 - startGame.getWidth()), (160 - startGame.getHeight()));
        }


        if(game.isTouchDown(0))
        {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {

    }

}
