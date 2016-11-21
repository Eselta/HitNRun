package mjj.cma.hitnrun.GameEngine;

public class KeyEventPool extends Pool<KeyEvent>
{

    @Override
    protected KeyEvent newItem()
    {
        return new KeyEvent();
    }


}
