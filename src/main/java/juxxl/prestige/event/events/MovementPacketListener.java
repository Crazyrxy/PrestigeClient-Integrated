package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.Event;
import juxxl.prestige.event.Listener;

public interface MovementPacketListener extends Listener {
   void onSendMovementPackets();

   class MovementPacketEvent extends Event<MovementPacketListener> {
      @Override
      public void fire(ArrayList<MovementPacketListener> listeners) {
         listeners.forEach(MovementPacketListener::onSendMovementPackets);
      }

      @Override
      public Class<MovementPacketListener> getListenerType() {
         return MovementPacketListener.class;
      }
   }
}
