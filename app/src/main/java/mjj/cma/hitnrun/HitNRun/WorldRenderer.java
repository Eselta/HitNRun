package mjj.cma.hitnrun.HitNRun;

import android.graphics.Bitmap;
import android.graphics.Color;

import mjj.cma.hitnrun.GameEngine.*;

public class WorldRenderer
{
    GameEngine game;
    World world;
    Bitmap backgroundImage;
    Bitmap carImage;
    Bitmap monsterImage;
    Bitmap monsterImageEvil;

    int backgroundWidth = 0;
    int backgroundHeight = 0;

    public WorldRenderer(GameEngine game, World world)
    {
        this.game = game;
        this.world = world;
        this.backgroundImage = game.loadBitmap("xcarbackground.png");
        this.carImage = game.loadBitmap("xbluecar2.png");
        this.monsterImage = game.loadBitmap("xyellowmonster.png");
        this.monsterImageEvil = game.loadBitmap("xyellowmonsterEvil.png");
        this.backgroundWidth = backgroundImage.getWidth();
        this.backgroundHeight = backgroundImage.getHeight();
    }

    public void render()
    {
        game.drawBitmap(backgroundImage, 0, 0, (int)world.scrollingBG.scrollX, 0, 480, 320);
        game.drawBitmap(carImage, (int)world.car.x, (int)world.car.y);
        game.drawText(world.font, "Points: " + Integer.toString( world.points ), 10, 310, Color.WHITE, 16 );
        game.drawText(world.font, "Lives: " + Integer.toString(world.lives), 410, 310, Color.WHITE, 16);


        for (Monster monster : world.monsterList)
        {
            if(monster.isGood == true)
                game.drawBitmap(monsterImage, (int)monster.x, (int)monster.y);
            else
                game.drawBitmap(monsterImageEvil, (int)monster.x, (int)monster.y);
        }
    }
}
