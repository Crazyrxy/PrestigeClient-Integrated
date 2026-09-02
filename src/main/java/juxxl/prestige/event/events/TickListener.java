package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.Event;
import juxxl.prestige.event.Listener;

public interface TickListener extends Listener {
   void onTick();

   class TickEvent extends Event<TickListener> {
      @Override
      public void fire(ArrayList<TickListener> listeners) {
         listeners.forEach(TickListener::onTick);
      }

      @Override
      public Class<TickListener> getListenerType() {
         return TickListener.class;
      }
   }
}
