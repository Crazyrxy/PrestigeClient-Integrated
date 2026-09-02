package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.CancellableEvent;
import juxxl.prestige.event.Listener;

public interface ItemUseListener extends Listener {
   void onItemUse(ItemUseListener.ItemUseEvent var1);

   class ItemUseEvent extends CancellableEvent<ItemUseListener> {
      @Override
      public void fire(ArrayList<ItemUseListener> listeners) {
         listeners.forEach(e -> e.onItemUse(this));
      }

      @Override
      public Class<ItemUseListener> getListenerType() {
         return ItemUseListener.class;
      }
   }
}
