package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.Event;
import juxxl.prestige.event.Listener;

public interface ButtonListener extends Listener {
   void onButtonPress(ButtonListener.ButtonEvent var1);

   class ButtonEvent extends Event<ButtonListener> {
      public int button;
      public int action;
      public long window;

      public ButtonEvent(int button, long window, int action) {
         this.button = button;
         this.window = window;
         this.action = action;
      }

      @Override
      public void fire(ArrayList<ButtonListener> listeners) {
         listeners.forEach(e -> e.onButtonPress(this));
      }

      @Override
      public Class<ButtonListener> getListenerType() {
         return ButtonListener.class;
      }
   }
}
