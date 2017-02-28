package mjj.cma.hitnrun.HitNRun;

import android.graphics.Color;
import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.List;

import mjj.cma.hitnrun.GameEngine.GameEngine;
import mjj.cma.hitnrun.GameEngine.Sound;

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
    Sound smash;
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
        this.font = game.loadFont("aller.ttf");
        this.smash = game.loadSound("smashing.mp3");
    }

    public void update(float deltaTime)
    {

        //If hitting pause button in top left corner
        if (game.getTouchX(0) > 0 && game.getTouchX(0) < 30 && game.getTouchY(0) > 0 && game.getTouchY(0) < 30)
        {
            state = State.Paused;
        }



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
            // End background scroll


        /*
            Car movement related to touching the screen
         */
            synchronized (this)
            {
                if (game.isTouchDown(0) && game.getTouchY(0) > 50 && game.getTouchY(0) < 270)
                {
                    car.y = game.getTouchY(0) - car.HEIGHT / 2;
                }
            }

        /*
           Generation and behaviour of monsters, and Car collision checks
         */
            collideCarMonster();
            generateMonsters();
            moveMonsters(deltaTime);

        }
    }
    // update() end


    //Checking if mosnters are hit
    private void collideCarMonster()
    {
        Monster monster;
        for (int i = 0; i < monsterList.size(); i++)
        {
            monster = monsterList.get( i );
            if( collideRecs( car.x, car.y, Car.WIDTH, Car.HEIGHT, monster.x, monster.y, Monster.WIDTH, Monster.HEIGHT ) )
            {


                if(monster.isMagic)
                {
                    smash.play(1);
                    gameSpeed -= 30;
                    monsterList.remove(i);
                    continue;
                }
                //Awards points for every good monster hit
                if(monster.isGood)
                {
                    smash.play(1);
                    points += 10;
                    monsterList.remove(i);

                    if(points > 0 && points%3 == 0)
                    {
                        gameSpeed += 30;
                        monsterGenerateSpeed -= 4;
                    }

                }
                //Crashes car if bad monster is hit
                else
                {
                    lives--;
                    monsterList.remove(i);
                    if(lives == 0)
                    {
                        game.setScreen( new GameOverScreen( game, points ) );
                        return;
                    }
                    state = State.Paused;
                }
            }
        }
    }//Ending monster hit check

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


    // Generate monsters
    public void generateMonsters()
    {
        int random = (int) (1000 * Math.random());
        if (random > monsterGenerateSpeed)
        {
            Monster monster = new Monster();
            monster.y = 30 + (int) (250 * Math.random());

            double isGoodRand = Math.random();
            if (isGoodRand < 0.5)
                monster.isGood = false;
            if(isGoodRand < 0.051)
            {
                monster.isMagic = true;
                monster.isGood = true;
            }
            monsterList.add(monster);
        }
    }//End generate monsters



    //Move and remove monsters
    public void moveMonsters(float deltaTime)
    {
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
    }// ending move and remove monsters
}
