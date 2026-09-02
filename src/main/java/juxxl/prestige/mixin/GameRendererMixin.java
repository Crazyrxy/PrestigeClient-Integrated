package juxxl.prestige.mixin;

import juxxl.prestige.Prestige;
import juxxl.prestige.event.EventManager;
import juxxl.prestige.event.events.GameRenderListener;
import juxxl.prestige.module.modules.misc.Freecam;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_757.class)
public abstract class GameRendererMixin {
   @Inject(
      method = "method_3188",
      at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/class_3695;method_15405(Ljava/lang/String;)V", args = "ldc=hand")
   )
   private void onWorldRender(class_9779 tickCounter, CallbackInfo ci) {
      class_4587 matrixStack = new class_4587();
      EventManager.fire(new GameRenderListener.GameRenderEvent(matrixStack, tickCounter.method_60637(true)));
   }

   @Inject(method = "method_3202", at = @At("HEAD"), cancellable = true)
   private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
      if (Prestige.INSTANCE.getModuleManager().getModule(Freecam.class).isEnabled()) {
         cir.setReturnValue(false);
      }
   }
}
