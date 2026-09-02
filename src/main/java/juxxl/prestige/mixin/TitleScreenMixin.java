package juxxl.prestige.mixin;

import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prestige Client credits on the title screen (Meteor-style).
 * Credits: CrazyRxz / Zerph
 */
@Mixin(class_442.class)
public class TitleScreenMixin {

   @Inject(method = "method_25394", at = @At("TAIL"))
   private void prestige$renderCredits(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      try {
         class_310 mc = class_310.method_1551();
         if (mc == null || mc.field_1772 == null || mc.method_22683() == null) {
            return;
         }

         class_327 tr = mc.field_1772;
         int screenW = mc.method_22683().method_4486();

         String title = "Prestige Client";
         String madeBy = "Made by CrazyRxz / Zerph";

         int titleW = tr.method_1727(title);
         int madeW = tr.method_1727(madeBy);
         int xTitle = (screenW - titleW) / 2;
         int xMade = (screenW - madeW) / 2;
         int y = 10;

         context.method_51433(tr, title, xTitle + 1, y + 1, 0x88000000, false);
         context.method_51433(tr, title, xTitle, y, 0xFFC48AFF, false);
         context.method_51433(tr, madeBy, xMade + 1, y + 12, 0x88000000, false);
         context.method_51433(tr, madeBy, xMade, y + 11, 0xFFB0B0B0, false);
      } catch (Throwable ignored) {
         // Never crash the title screen over credits
      }
   }
}
