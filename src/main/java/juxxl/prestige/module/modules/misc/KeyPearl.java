package juxxl.prestige.module.modules.misc;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.KeybindSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.KeyUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1802;

public final class KeyPearl extends Module implements TickListener {
   private final KeybindSetting activateKey = new KeybindSetting("Activate Key", -1, false);
   private final NumberSetting delay = new NumberSetting("Delay", 0.0, 20.0, 0.0, 1.0);
   private final BooleanSetting switchBack = new BooleanSetting("Switch Back", true);
   private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0.0, 20.0, 0.0, 1.0)
      .setDescription("Delay after throwing pearl before switching back");
   private boolean active;
   private boolean hasActivated;
   private int clock;
   private int previousSlot;
   private int switchClock;

   public KeyPearl() {
      super("Key Pearl", "Switches to an ender pearl and throws it when you press a bind", -1, Category.MISC);
      this.addSettings(this.activateKey, this.delay, this.switchBack, this.switchDelay);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.reset();
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
         if (KeyUtils.isKeyPressed(this.activateKey.getKey())) {
            this.active = true;
         }

         if (this.active) {
            if (this.previousSlot == -1) {
               this.previousSlot = this.mc.field_1724.method_31548().method_67532();
            }

            InventoryUtils.selectItemFromHotbar(class_1802.field_8634);
            if (this.clock < this.delay.getValueInt()) {
               this.clock++;
               return;
            }

            if (!this.hasActivated) {
               class_1269 result = this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
               if (result.method_23665()) {
                  this.mc.field_1724.method_6104(class_1268.field_5808);
               }

               this.hasActivated = true;
            }

            if (this.switchBack.getValue()) {
               this.switchBack();
            } else {
               this.reset();
            }
         }
      }
   }

   private void switchBack() {
      if (this.switchClock < this.switchDelay.getValueInt()) {
         this.switchClock++;
      } else {
         InventoryUtils.setInvSlot(this.previousSlot);
         this.reset();
      }
   }

   private void reset() {
      this.previousSlot = -1;
      this.clock = 0;
      this.switchClock = 0;
      this.active = false;
      this.hasActivated = false;
   }
}
