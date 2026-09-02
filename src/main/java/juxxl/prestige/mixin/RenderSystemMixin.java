package juxxl.prestige.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import juxxl.prestige.gui.ClickGui;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
   @Inject(method = "flipFrame", at = @At("HEAD"), require = 0)
   private static void onFlipFrame(CallbackInfo ci) {
      class_310 mc = class_310.method_1551();
      if (mc != null && mc.field_1755 instanceof ClickGui gui) {
         gui.renderNanoVG();
      }
   }
}
