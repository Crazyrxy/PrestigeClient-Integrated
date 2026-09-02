package juxxl.prestige.module.modules.misc;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import org.lwjgl.glfw.GLFW;

public final class NoJumpDelay extends Module implements TickListener {
   public NoJumpDelay() {
      super("No Jump Delay", "Lets you jump faster, removing the delay", -1, Category.MISC);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
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
         if (this.mc.field_1724.method_24828()) {
            if (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), 32) == 1) {
               this.mc.field_1690.field_1903.method_23481(false);
               this.mc.field_1724.method_6043();
            }
         }
      }
   }
}
