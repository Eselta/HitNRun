package mjj.cma.hitnrun.GameEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eselta on 19-09-2016.
 */
public abstract class Pool<T>
{
    private List<T> items = new ArrayList<>();

    protected abstract T newItem();

    public T obtain()
    {
        int last = items.size() - 1;

        if(last == -1)
        {
            return newItem();
        }

        return items.remove(last);
    }

    public void free(T item)
    {
        items.add(item);
    }

}
