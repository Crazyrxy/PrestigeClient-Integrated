package juxxl.prestige.mixin;

import net.minecraft.class_310;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_310.class)
public interface MinecraftClientAccessor {
   @Accessor("field_1729")
   class_312 getMouse();

   @Invoker("method_1583")
   void invokeDoItemUse();

   @Invoker("method_1536")
   boolean invokeDoAttack();

   @Accessor("field_1752")
   void setItemUseCooldown(int var1);

   @Accessor("field_1771")
   void setAttackCooldown(int var1);
}
