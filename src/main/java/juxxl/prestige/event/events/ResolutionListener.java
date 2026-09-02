package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.Event;
import juxxl.prestige.event.Listener;
import net.minecraft.class_1041;

public interface ResolutionListener extends Listener {
   void onResolution(ResolutionListener.ResolutionEvent var1);

   class ResolutionEvent extends Event<ResolutionListener> {
      public class_1041 window;

      public ResolutionEvent(class_1041 window) {
         this.window = window;
      }

      @Override
      public void fire(ArrayList<ResolutionListener> listeners) {
         listeners.forEach(l -> l.onResolution(this));
      }

      @Override
      public Class<ResolutionListener> getListenerType() {
         return ResolutionListener.class;
      }
   }
}
