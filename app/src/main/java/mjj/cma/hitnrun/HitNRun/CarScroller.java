package mjj.cma.hitnrun.HitNRun;

import mjj.cma.hitnrun.GameEngine.*;

public class CarScroller extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        return new MainMenuScreen(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}
