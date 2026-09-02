package juxxl.prestige.mixin;

import juxxl.prestige.Prestige;
import juxxl.prestige.module.modules.render.NoBounce;
import juxxl.prestige.utils.CrystalUtils;
import juxxl.prestige.utils.RenderUtils;
import net.minecraft.class_1269;
import net.minecraft.class_1657;
import net.minecraft.class_1774;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1838;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1774.class)
public class EndCrystalItemMixin {
   @Unique
   private class_243 getPlayerLookVec(class_1657 p) {
      return RenderUtils.getPlayerLookVec(p);
   }

   @Unique
   private class_243 getClientLookVec() {
      assert Prestige.mc.field_1724 != null;
      return this.getPlayerLookVec(Prestige.mc.field_1724);
   }

   @Unique
   private boolean isBlock(class_2248 b, class_2338 p) {
      return this.getBlockState(p).method_26204() == b;
   }

   @Unique
   private class_2680 getBlockState(class_2338 p) {
      return Prestige.mc.field_1687.method_8320(p);
   }

   @Unique
   private boolean canPlaceCrystalServer(class_2338 blockPos) {
      class_2680 blockState = Prestige.mc.field_1687.method_8320(blockPos);
      return !blockState.method_27852(class_2246.field_10540) && !blockState.method_27852(class_2246.field_9987)
         ? false
         : CrystalUtils.canPlaceCrystalClientAssumeObsidian(blockPos);
   }

   @Inject(method = "method_7884", at = @At("HEAD"))
   private void onUse(class_1838 context, CallbackInfoReturnable<class_1269> cir) {
      NoBounce noBounce = Prestige.INSTANCE.getModuleManager().getModule(NoBounce.class);
      if (noBounce.isEnabled() && Prestige.INSTANCE != null && Prestige.mc.field_1724 != null) {
         class_1799 mainHandStack = Prestige.mc.field_1724.method_6047();
         if (mainHandStack.method_31574(class_1802.field_8301)) {
            class_243 e = Prestige.mc.field_1724.method_33571();
            class_3965 blockHit = Prestige.mc
               .field_1687
               .method_17742(
                  new class_3959(
                     e, e.method_1019(this.getClientLookVec().method_1021(4.5)), class_3960.field_17559, class_242.field_1348, Prestige.mc.field_1724
                  )
               );
            if ((this.isBlock(class_2246.field_10540, blockHit.method_17777()) || this.isBlock(class_2246.field_9987, blockHit.method_17777()))
               && Prestige.mc.field_1765 instanceof class_3965 blockHit2) {
               class_2338 pos = blockHit2.method_17777();
               if (this.canPlaceCrystalServer(pos)) {
                  context.method_8041().method_7934(-1);
               }
            }
         }
      }
   }
}
