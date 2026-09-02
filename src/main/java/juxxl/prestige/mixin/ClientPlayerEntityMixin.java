package juxxl.prestige.mixin;

import com.mojang.authlib.GameProfile;
import juxxl.prestige.event.EventManager;
import juxxl.prestige.event.events.MovementPacketListener;
import juxxl.prestige.event.events.PlayerTickListener;
import juxxl.prestige.utils.rotation.RotationHandler;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_742;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_746.class)
public class ClientPlayerEntityMixin extends class_742 {
   @Shadow
   @Final
   protected class_310 field_3937;

   public ClientPlayerEntityMixin(class_638 world, GameProfile profile) {
      super(world, profile);
   }

   @Inject(method = "method_3136", at = @At("HEAD"))
   private void onSendMovementPackets(CallbackInfo ci) {
      EventManager.fire(new MovementPacketListener.MovementPacketEvent());
   }

   @Inject(method = "method_3136", at = @At("RETURN"))
   private void onSendMovementPacketsPost(CallbackInfo ci) {
      class_746 player = (class_746)(Object)this;
      if (RotationHandler.isSilentActive()) {
         player.field_6241 = RotationHandler.getRenderYaw();
         player.field_6283 = RotationHandler.getRenderYaw();
      }

      RotationHandler.endTick();
   }

   @Inject(method = "method_5773", at = @At("HEAD"))
   private void onPlayerTick(CallbackInfo ci) {
      class_746 player = (class_746)(Object)this;
      RotationHandler.startTick();
      RotationHandler.setOriginal(player.method_36454(), player.method_36455());
      EventManager.fire(new PlayerTickListener.PlayerTickEvent());
   }
}
