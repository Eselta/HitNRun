package mjj.cma.hitnrun.GameEngine;

/**
 * Created by Eselta on 05-09-2016.
 */
public abstract class Screen
{
    protected final GameEngine game;

    public Screen(GameEngine game)
    {
        this.game = game;
    }

    public abstract void update(float deltaTime);
    public abstract void pause();
    public abstract void resume();
    public abstract void dispose();



}
