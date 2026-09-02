package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.CancellableEvent;
import juxxl.prestige.event.Listener;

public interface BlockBreakingListener extends Listener {
   void onBlockBreaking(BlockBreakingListener.BlockBreakingEvent var1);

   class BlockBreakingEvent extends CancellableEvent<BlockBreakingListener> {
      @Override
      public void fire(ArrayList<BlockBreakingListener> listeners) {
         listeners.forEach(e -> e.onBlockBreaking(this));
      }

      @Override
      public Class<BlockBreakingListener> getListenerType() {
         return BlockBreakingListener.class;
      }
   }
}
