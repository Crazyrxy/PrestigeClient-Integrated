package juxxl.prestige.mixin;

import juxxl.prestige.event.EventManager;
import juxxl.prestige.event.events.PacketReceiveListener;
import juxxl.prestige.event.events.PacketSendListener;
import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_2535.class)
public class ClientConnectionMixin {
   @Inject(method = "method_10759", at = @At("HEAD"), cancellable = true)
   private static <T extends class_2547> void onPacketReceive(class_2596<T> packet, class_2547 listener, CallbackInfo ci) {
      PacketReceiveListener.PacketReceiveEvent event = new PacketReceiveListener.PacketReceiveEvent(packet);
      EventManager.fire(event);
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_10743", at = @At("HEAD"), cancellable = true)
   private void onPacketSend(class_2596<?> packet, CallbackInfo ci) {
      PacketSendListener.PacketSendEvent event = new PacketSendListener.PacketSendEvent(packet);
      EventManager.fire(event);
      if (event.isCancelled()) {
         ci.cancel();
      }
   }
}
