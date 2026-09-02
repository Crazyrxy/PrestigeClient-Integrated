package juxxl.prestige.mixin;

import juxxl.prestige.gui.ClickGui;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_437.class)
public class ScreenMixin {
   @Shadow
   @Nullable
   protected class_310 field_22787;

   @Inject(method = "method_25420", at = @At("HEAD"), cancellable = true)
   private void dontRenderBackground(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.field_22787.field_1755 instanceof ClickGui) {
         ci.cancel();
      }
   }
}
