package juxxl.prestige.module.modules.misc;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.mixin.MinecraftClientAccessor;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.ModeSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.MathUtils;
import juxxl.prestige.utils.MouseSimulation;
import juxxl.prestige.utils.TimerUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1792;
import net.minecraft.class_1811;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import org.lwjgl.glfw.GLFW;

public final class AutoClicker extends Module implements TickListener {
   private final BooleanSetting onlyWeapon = new BooleanSetting("Only Weapon", true).setDescription("Only left clicks with weapon in hand");
   private final BooleanSetting onlyBlocks = new BooleanSetting("Only Blocks", true).setDescription("Only right clicks blocks");
   private final BooleanSetting onClick = new BooleanSetting("On Click", true);
   private final NumberSetting delay = new NumberSetting("Delay", 0.0, 1000.0, 0.0, 1.0);
   private final NumberSetting chance = new NumberSetting("Chance", 0.0, 100.0, 100.0, 1.0);
   private final ModeSetting<AutoClicker.Mode> mode = new ModeSetting<>("Actions", AutoClicker.Mode.All, AutoClicker.Mode.class);
   private final TimerUtils timer = new TimerUtils();

   public AutoClicker() {
      super("Auto Clicker", "Automatically clicks for you", -1, Category.MISC);
      this.addSettings(this.onlyWeapon, this.onClick, this.delay, this.chance, this.mode);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.timer.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (this.mc.field_1724 != null) {
         if (this.mc.field_1755 == null) {
            if (this.mc.field_1765 != null) {
               if (this.timer.delay(this.delay.getValueFloat()) && this.chance.getValueInt() >= MathUtils.randomInt(1, 100)) {
                  if (this.mode.isMode(AutoClicker.Mode.Left)) {
                     this.performLeftClick();
                  }

                  if (this.mode.isMode(AutoClicker.Mode.Right)) {
                     this.performRightClick();
                  }

                  if (this.mode.isMode(AutoClicker.Mode.All)) {
                     this.performLeftClick();
                     this.performRightClick();
                  }
               }
            }
         }
      }
   }

   private void performRightClick() {
      class_1792 mainhand = this.mc.field_1724.method_6047().method_7909();
      class_1792 offhand = this.mc.field_1724.method_6079().method_7909();
      if (!mainhand.method_57347().method_57832(class_9334.field_50075)) {
         if (!offhand.method_57347().method_57832(class_9334.field_50075)) {
            if (!(mainhand instanceof class_1811) && !(offhand instanceof class_1811)) {
               if (!this.onClick.getValue() || GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 1) == 1) {
                  MouseSimulation.mouseClick(1);
                  ((MinecraftClientAccessor)this.mc).invokeDoItemUse();
                  this.timer.reset();
               }
            }
         }
      }
   }

   private void performLeftClick() {
      class_1792 mainhand = this.mc.field_1724.method_6047().method_7909();
      class_1792 offhand = this.mc.field_1724.method_6079().method_7909();
      if (this.mc.field_1765.method_17783() != class_240.field_1332) {
         if (!this.mc.field_1724.method_6115()) {
            if (!this.onlyWeapon.getValue() || WorldUtils.isWeapon(this.mc.field_1724.method_6047())) {
               if (!this.onClick.getValue() || GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 0) == 1) {
                  MouseSimulation.mouseClick(0);
                  ((MinecraftClientAccessor)this.mc).invokeDoAttack();
                  this.timer.reset();
               }
            }
         }
      }
   }

   public enum Mode {
      All,
      Left,
      Right;
   }
}
