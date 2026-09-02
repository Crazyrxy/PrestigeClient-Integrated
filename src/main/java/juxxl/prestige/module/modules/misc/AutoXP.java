package juxxl.prestige.module.modules.misc;

import juxxl.prestige.event.events.ItemUseListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.MathUtils;
import juxxl.prestige.utils.MouseSimulation;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1802;
import org.lwjgl.glfw.GLFW;

public final class AutoXP extends Module implements TickListener, ItemUseListener {
   private final NumberSetting delay = new NumberSetting("Delay", 0.0, 20.0, 0.0, 1.0);
   private final NumberSetting chance = new NumberSetting("Chance", 0.0, 100.0, 100.0, 1.0).setDescription("Randomization");
   private final BooleanSetting clickSimulation = new BooleanSetting("Click Simulation", false).setDescription("Makes the CPS hud think you're legit");
   int clock;

   public AutoXP() {
      super("Auto XP", "Automatically throws XP bottles for you", -1, Category.MISC);
      this.addSettings(this.delay, this.chance, this.clickSimulation);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(ItemUseListener.class, this);
      this.clock = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (this.mc.field_1755 == null) {
         boolean dontThrow = this.clock != 0;
         int randomInt = MathUtils.randomInt(1, 100);
         if (this.mc.field_1724.method_6047().method_7909() == class_1802.field_8287) {
            if (GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 1) == 1) {
               if (dontThrow) {
                  this.clock--;
               }

               if (!dontThrow && randomInt <= this.chance.getValueInt()) {
                  if (this.clickSimulation.getValue()) {
                     MouseSimulation.mouseClick(1);
                  }

                  class_1269 result = this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                  if (result.method_23665()) {
                     this.mc.field_1724.method_6104(class_1268.field_5808);
                  }

                  this.clock = this.delay.getValueInt();
               }
            }
         }
      }
   }

   @Override
   public void onItemUse(ItemUseListener.ItemUseEvent event) {
      if (this.mc.field_1724.method_6047().method_7909() == class_1802.field_8287) {
         event.cancel();
      }
   }
}
