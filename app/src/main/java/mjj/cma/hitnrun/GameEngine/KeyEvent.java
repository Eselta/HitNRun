package mjj.cma.hitnrun.GameEngine;

public class KeyEvent
{
    public enum KeyEventType
    {
        Down,
        Up
    }

    public KeyEventType type;
    public int keyCode;
    public char character;
}
