package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.Event;
import juxxl.prestige.event.Listener;

public interface PlayerTickListener extends Listener {
   void onPlayerTick();

   class PlayerTickEvent extends Event<PlayerTickListener> {
      @Override
      public void fire(ArrayList<PlayerTickListener> listeners) {
         listeners.forEach(PlayerTickListener::onPlayerTick);
      }

      @Override
      public Class<PlayerTickListener> getListenerType() {
         return PlayerTickListener.class;
      }
   }
}
