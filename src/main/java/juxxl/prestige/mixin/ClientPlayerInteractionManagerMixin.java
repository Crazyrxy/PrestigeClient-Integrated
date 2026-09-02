package juxxl.prestige.mixin;

import juxxl.prestige.Prestige;
import juxxl.prestige.module.modules.misc.NoBreakDelay;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_636.class)
public class ClientPlayerInteractionManagerMixin {
   @Shadow
   private int field_3716;

   @Redirect(method = "method_2902", at = @At(value = "FIELD", target = "Lnet/minecraft/class_636;field_3716:I", opcode = 180, ordinal = 0))
   public int updateBlockBreakingProgress(class_636 clientPlayerInteractionManager) {
      int cooldown = this.field_3716;
      return Prestige.INSTANCE.getModuleManager().getModule(NoBreakDelay.class).isEnabled() ? 0 : cooldown;
   }
}
