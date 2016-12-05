package mjj.cma.hitnrun.HitNRun;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import mjj.cma.hitnrun.GameEngine.*;

public class World
{
    private enum State
    {
        Running,
        Paused,
    }

    public static final float MIN_X = 0;
    public static final float MAX_X = 479;
    public static final float MIN_Y = 0;
    public static final float MAX_Y = 319;
    public static final int   point = 0;
    public State state = State.Running;



    GameEngine game;
    Sound wallHit;
    Car car = new Car();
    List<Monster> monsterList = new ArrayList<>();
    ScrollingBackground scrollingBG = new ScrollingBackground();
    float gameSpeed = 100;
    Typeface font;

    int points = 0;
    int lives = 3;
    int monsterGenerateSpeed = 980;

    public World(GameEngine game)
    {
        this.game = game;
        this.wallHit = game.loadSound("explosion.ogg");
        this.font = game.loadFont("aller.ttf");
    }

    public void update(float deltaTime)
    {
        if (state == State.Paused)
        {
            if (game.isTouchDown(0) && game.getTouchX(0) < 240)
            {
                state = State.Running;
            }
        }

        if (state == State.Running)
        {
        /*
            Background moving
         */
            scrollingBG.scrollX = scrollingBG.scrollX + (gameSpeed * deltaTime);
            if (scrollingBG.scrollX > (scrollingBG.WIDTH - 480))
            {
                scrollingBG.scrollX = 0;
            }
            // End


        /*
            Car moving when touch
         */
            synchronized (this)
            {
                if (game.isTouchDown(0))
                {
                    car.y = game.getTouchY(0) - car.HEIGHT / 2;
                }
            }

        /*
            Checking if monster has been hit
         */
            collideCarMonster();


        /*
            Checking for wall hit
         */
            if (car.y < MIN_Y + 20)
            {
                car.y = MIN_Y + 20;
                wallHit.play(1);
            }

            if (car.y + car.HEIGHT > MAX_Y - 20)
            {
                car.y = MAX_Y - car.HEIGHT - 20;
                wallHit.play(1);
            }
            //Ending wall check


        /*
            Generate monsters
         */
            int random = (int) (1000 * Math.random());
            if (random > monsterGenerateSpeed)
            {
                Monster monster = new Monster();
                monster.y = 30 + (int) (250 * Math.random());

                if (Math.random() < 0.5)
                    monster.isGood = false;

                monsterList.add(monster);
            }

        /*
            Monster movement and removal when out of screen
         */
            Monster monster;
            for (int i = 0; i < monsterList.size(); i++)
            {
                monster = monsterList.get(i);
                monster.x = monster.x - (gameSpeed * deltaTime);
                if (monster.x < -32)
                {
                    monsterList.remove(i);
                }
            }
            // Ending monsters create and remove
        }
    }
    // update() end



    private void collideCarMonster()
    {
        Monster monster;
        for (int i = 0; i < monsterList.size(); i++)
        {
            monster = monsterList.get( i );
            if( collideRecs( car.x, car.y, Car.WIDTH, Car.HEIGHT, monster.x, monster.y, Monster.WIDTH, Monster.HEIGHT ) )
            {
                if(monster.isGood)
                {
                    points += 10;
                    monsterList.remove(i);

                    if(points > 0 && points%3 == 0)
                    {
                        gameSpeed += 30;
                        monsterGenerateSpeed -= 4;
                    }

                }
                else
                {
                    lives--;
                    monsterList.remove(i);
                    //scrollingBG.scrollX = 0;

                    if(lives == 0)
                    {
                        game.setScreen( new MainMenuScreen( game ) );
                        return;
                    }

                    state = State.Paused;

                }
            }
        }
    }

    private boolean collideRecs( float x1, float y1, float width1, float height1,
                                 float x2, float y2, float width2, float height2 )
    {
        if( x1 < (x2 + width2) &&       // left edge of Obj1 is to the left of the right edge Obj2
                (x1 + width1) > x2 &&   // right edge of the Obj1 is to the right of edge Obj2
                (y1 + height1) > y2 &&  // bottom edge Obj1 is below top edge Obj2
                y1 < (y2 + height2)     // top edge Obj1 is above edge Obj2
                )
        {
            return true;
        }
        return false;
    }
}
