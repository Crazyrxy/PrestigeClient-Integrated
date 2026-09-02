package juxxl.prestige.mixin;

import juxxl.prestige.Prestige;
import juxxl.prestige.event.EventManager;
import juxxl.prestige.event.events.CameraUpdateListener;
import juxxl.prestige.utils.rotation.RotationHandler;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(class_4184.class)
public class CameraMixin {
   @ModifyArgs(method = "method_19321", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4184;method_19327(DDD)V"))
   private void update(Args args) {
      CameraUpdateListener.CameraUpdateEvent event = new CameraUpdateListener.CameraUpdateEvent((Double)args.get(0), (Double)args.get(1), (Double)args.get(2));
      EventManager.fire(event);
      args.set(0, event.getX());
      args.set(1, event.getY());
      args.set(2, event.getZ());
   }

   @ModifyVariable(method = "method_19325", at = @At("HEAD"), ordinal = 0, argsOnly = true)
   private float modifyYRot(float original) {
      return RotationHandler.isSilentActive()
         ? class_3532.method_16439(Prestige.mc.method_61966().method_60637(true), RotationHandler.getPrevRenderYaw(), RotationHandler.getRenderYaw())
         : original;
   }

   @ModifyVariable(method = "method_19325", at = @At("HEAD"), ordinal = 1, argsOnly = true)
   private float modifyXRot(float original) {
      return RotationHandler.isSilentActive()
         ? class_3532.method_16439(Prestige.mc.method_61966().method_60637(true), RotationHandler.getPrevRenderPitch(), RotationHandler.getRenderPitch())
         : original;
   }
}
