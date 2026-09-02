package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.utils.BlockUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import org.lwjgl.glfw.GLFW;

public final class DoubleAnchor extends Module implements TickListener {
   private class_2338 pos;
   private int count;

   public DoubleAnchor() {
      super("Double Anchor", "Helps you do the air place/double anchor", -1, Category.COMBAT);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.pos = null;
      this.count = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (this.mc.field_1755 == null) {
         assert this.mc.field_1724 != null;
         if (this.mc.field_1724.method_6047().method_31574(class_1802.field_23141)) {
            assert this.mc.field_1687 != null;
            if (this.mc.field_1765 instanceof class_3965 h
               && BlockUtils.isAnchorCharged(h.method_17777())
               && GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 1) == 1) {
               if (h.method_17777().equals(this.pos)) {
                  if (this.count >= 1) {
                     return;
                  }
               } else {
                  this.pos = h.method_17777();
                  this.count = 0;
               }

               this.mc.method_1562().method_52787(new class_2885(class_1268.field_5808, h, 0));
               this.count++;
            }
         }
      }
   }
}
