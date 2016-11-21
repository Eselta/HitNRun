package mjj.cma.hitnrun.HitNRun;

import java.util.ArrayList;
import java.util.List;
import mjj.cma.hitnrun.GameEngine.*;

/**
 * Created by Eselta on 31-10-2016.
 */
public class World
{
    public static final float MIN_X = 0;
    public static final float MAX_X = 479;
    public static final float MIN_Y = 0;
    public static final float MAX_Y = 319;

    GameEngine game;
    Sound wallHit;
    Car car = new Car();
    List<Monster> monsterList = new ArrayList<>();
    ScrollingBackground scrollingBG = new ScrollingBackground();
    float gameSpeed = 100;

    public World(GameEngine game)
    {
        this.game = game;
        this.wallHit = game.loadSound("bounce.wav");
    }

    public void update(float deltaTime)
    {
        scrollingBG.scrollX = scrollingBG.scrollX + (gameSpeed * deltaTime);
        if(scrollingBG.scrollX > (scrollingBG.WIDTH - 480))
        {
            scrollingBG.scrollX = 0;
        }

        if(!game.isTouchDown(0))
        {
            float[] accel = game.getAccelerometer();
            car.y = car.y + (accel[0] * 20 * deltaTime);
            game.clearAccelerometer();
        }

        synchronized (car)
        {
            if (game.isTouchDown(0))
            {

                List<TouchEvent> touchList = game.getTouchEvents();
                int touchListSize = touchList.size();
                if (touchListSize > 0)
                {
                    car.y = game.getTouchY(touchListSize - 1) - (car.HEIGHT / 2);
                }
            }
        }

        if (car.y < MIN_Y + 20)
        {
            car.y = MIN_Y + 20;
            wallHit.play(1);
        }

        if(car.y + car.HEIGHT > MAX_Y - 20)
        {
            car.y = MAX_Y - car.HEIGHT - 20;
            wallHit.play(1);
        }

        int random = (int)(1000 * Math.random());
        if(random > 980)
        {
            Monster monster = new Monster();
            monster.y = 30 + (int) (250 * Math.random());
            monsterList.add(monster);
        }

        Monster monster;
        for(int i = 0; i < monsterList.size(); i++)
        {
            monster = monsterList.get(i);
            monster.x = monster.x - (gameSpeed * deltaTime);
            if(monster.x < -32)
            {
                monsterList.remove(i);
            }
        }

    }
}
