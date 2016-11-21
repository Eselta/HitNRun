package mjj.cma.hitnrun.HitNRun;

import mjj.cma.hitnrun.GameEngine.*;

public class GameScreen extends Screen
{

    enum State
    {
        Running,
        Paused,
        GameOver
    }

    State state = State.Running;
    World world;
    WorldRenderer renderer;

    public GameScreen(GameEngine game)
    {
        super(game);
        world = new World(game);
        renderer = new WorldRenderer(game, world);
    }

    @Override
    public void update(float deltaTime)
    {
        if (state == State.Running)
        {
            world.update(deltaTime);
        }
        renderer.render();
    }

    @Override
    public void pause()
    {
        if(state == State.Paused)
        {
            state = State.Paused;
        }
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
