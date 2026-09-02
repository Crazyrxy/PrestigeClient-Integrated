package juxxl.prestige.mixin;

import juxxl.prestige.event.EventManager;
import juxxl.prestige.event.events.ButtonListener;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_309.class)
public class KeyboardMixin {
   @Shadow
   @Final
   private class_310 field_1678;

   @Inject(method = "method_1466", at = @At("HEAD"))
   private void onPress(long window, int action, class_11908 input, CallbackInfo ci) {
      System.out.println("[Prestige/KeyboardMixin] key=" + input.comp_4795() + " action=" + action);
      EventManager.fire(new ButtonListener.ButtonEvent(input.comp_4795(), window, action));
   }
}
