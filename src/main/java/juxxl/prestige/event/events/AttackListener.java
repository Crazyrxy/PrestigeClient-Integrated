package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.CancellableEvent;
import juxxl.prestige.event.Listener;

public interface AttackListener extends Listener {
   void onAttack(AttackListener.AttackEvent var1);

   class AttackEvent extends CancellableEvent<AttackListener> {
      @Override
      public void fire(ArrayList<AttackListener> listeners) {
         listeners.forEach(e -> e.onAttack(this));
      }

      @Override
      public Class<AttackListener> getListenerType() {
         return AttackListener.class;
      }
   }
}
