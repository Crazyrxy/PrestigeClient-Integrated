package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.CancellableEvent;
import juxxl.prestige.event.Listener;
import net.minecraft.class_2596;

public interface PacketSendListener extends Listener {
   void onPacketSend(PacketSendListener.PacketSendEvent var1);

   class PacketSendEvent extends CancellableEvent<PacketSendListener> {
      public class_2596 packet;

      public PacketSendEvent(class_2596 packet) {
         this.packet = packet;
      }

      @Override
      public void fire(ArrayList<PacketSendListener> listeners) {
         listeners.forEach(e -> e.onPacketSend(this));
      }

      @Override
      public Class<PacketSendListener> getListenerType() {
         return PacketSendListener.class;
      }
   }
}
