package mjj.cma.hitnrun.GameEngine;

public interface TouchHandler
{
    boolean isTouchDown(int pointer);
    int getTouchX(int pointer);
    int getTouchY(int pointer);

}
