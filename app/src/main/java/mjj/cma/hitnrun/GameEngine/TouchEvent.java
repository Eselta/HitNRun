package mjj.cma.hitnrun.GameEngine;

/**
 * Created by Eselta on 19-09-2016.
 */
public class TouchEvent
{
    public enum TouchEventType
    {
        Down,
        Up,
        Dragged
    }

    public TouchEventType type;
    public int x;
    public int y;
    public int pointer;




}
