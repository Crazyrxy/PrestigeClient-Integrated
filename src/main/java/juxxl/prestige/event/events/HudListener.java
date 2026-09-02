package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.Event;
import juxxl.prestige.event.Listener;
import net.minecraft.class_332;

public interface HudListener extends Listener {
   void onRenderHud(HudListener.HudEvent var1);

   class HudEvent extends Event<HudListener> {
      public class_332 context;
      public float delta;

      public HudEvent(class_332 context, float delta) {
         this.context = context;
         this.delta = delta;
      }

      @Override
      public void fire(ArrayList<HudListener> listeners) {
         listeners.forEach(e -> e.onRenderHud(this));
      }

      @Override
      public Class<HudListener> getListenerType() {
         return HudListener.class;
      }
   }
}
