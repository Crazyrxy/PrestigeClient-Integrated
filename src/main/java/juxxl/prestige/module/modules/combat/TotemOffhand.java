package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.InventoryUtils;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_2846.class_2847;

public final class TotemOffhand extends Module implements TickListener {
   private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0.0, 5.0, 0.0, 1.0);
   private final NumberSetting equipDelay = new NumberSetting("Equip Delay", 1.0, 5.0, 1.0, 1.0);
   private final BooleanSetting switchBack = new BooleanSetting("Switch Back", false);
   private int switchClock;
   private int equipClock;
   private int switchBackClock;
   private int previousSlot = -1;
   boolean sent;
   boolean active = false;

   public TotemOffhand() {
      super("Totem Offhand", "Switches to your totem slot and offhands a totem if you dont have one already", -1, Category.COMBAT);
      this.addSettings(this.switchDelay, this.equipDelay, this.switchBack);
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
         if (this.mc.field_1724.method_6079().method_7909() != class_1802.field_8288) {
            this.active = true;
         }

         if (this.active) {
            if (this.switchClock < this.switchDelay.getValueInt()) {
               this.switchClock++;
               return;
            }

            if (this.previousSlot == -1) {
               this.previousSlot = this.mc.field_1724.method_31548().method_67532();
            }

            if (InventoryUtils.selectItemFromHotbar(class_1802.field_8288)) {
               if (this.equipClock < this.equipDelay.getValueInt()) {
                  this.equipClock++;
                  return;
               }

               if (!this.sent) {
                  this.mc.method_1562().method_48296().method_10743(new class_2846(class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
                  this.sent = true;
                  return;
               }
            }

            if (this.switchBackClock < this.switchDelay.getValue()) {
               this.switchBackClock++;
            } else {
               if (this.switchBack.getValue()) {
                  InventoryUtils.setInvSlot(this.previousSlot);
               }

               this.reset();
            }
         }
      }
   }

   public void reset() {
      this.switchClock = 0;
      this.equipClock = 0;
      this.switchBackClock = 0;
      this.previousSlot = -1;
      this.sent = false;
      this.active = false;
   }
}
