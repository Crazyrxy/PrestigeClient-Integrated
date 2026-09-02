package juxxl.prestige.event;

import java.util.ArrayList;

public abstract class Event<T extends Listener> {
   public abstract void fire(ArrayList<T> var1);

   public abstract Class<T> getListenerType();
}
