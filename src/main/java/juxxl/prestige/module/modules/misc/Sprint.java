package juxxl.prestige.module.modules.misc;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;

public final class Sprint extends Module implements TickListener {
   public Sprint() {
      super("Sprint", "Keeps you sprinting at all times", -1, Category.MISC);
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
      this.mc.field_1724.method_5728(this.mc.field_1724.field_3913.method_20622());
   }
}
