package mjj.cma.hitnrun.HitNRun;

import mjj.cma.hitnrun.GameEngine.*;

public class CarScroller extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        music = this.loadMusic("music.ogg");

        return new MainMenuScreen(this);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        music.pause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        music.play();
    }
}
