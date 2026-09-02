package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.mixin.HandledScreenMixin;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.ModeSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.InventoryUtils;
import net.minecraft.class_1291;
import net.minecraft.class_1294;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1735;
import net.minecraft.class_490;

public final class AutoPotRefill extends Module implements TickListener {
   private final ModeSetting<AutoPotRefill.Mode> mode = new ModeSetting<>("Mode", AutoPotRefill.Mode.Auto, AutoPotRefill.Mode.class);
   private final NumberSetting delay = new NumberSetting("Delay", 0.0, 10.0, 0.0, 1.0);
   private int clock;

   public AutoPotRefill() {
      super("Auto Pot Refill", "Refills your hotbar with potions", -1, Category.COMBAT);
      this.addSettings(this.mode, this.delay);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.clock = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (this.mc.field_1755 instanceof class_490 inventoryScreen) {
         if (this.mode.isMode(AutoPotRefill.Mode.Hover)) {
            class_1735 focusedSlot = ((HandledScreenMixin)inventoryScreen).getFocusedSlot();
            if (focusedSlot == null) {
               return;
            }

            class_1661 inventory = this.mc.field_1724.method_31548();
            int emptySlot = -1;

            for (int i = 0; i <= 8; i++) {
               if (inventory.method_5438(i).method_7960()) {
                  emptySlot = i;
                  break;
               }
            }

            if (emptySlot == -1) {
               return;
            }

            if (InventoryUtils.isThatSplash((class_1291)class_1294.field_5915.comp_349(), 1, 1, focusedSlot.method_7677())) {
               if (this.clock < this.delay.getValueInt()) {
                  this.clock++;
                  return;
               }

               this.mc
                  .field_1761
                  .method_2906(
                     ((class_1723)inventoryScreen.method_17577()).field_7763, focusedSlot.method_34266(), emptySlot, class_1713.field_7791, this.mc.field_1724
                  );
               this.clock = 0;
            }
         }

         if (this.mode.isMode(AutoPotRefill.Mode.Auto)) {
            int slot = InventoryUtils.findPot((class_1291)class_1294.field_5915.comp_349(), 1, 1);
            if (slot != -1) {
               class_1661 inventory = this.mc.field_1724.method_31548();
               int emptySlot = -1;

               for (int i = 0; i <= 8; i++) {
                  if (inventory.method_5438(i).method_7960()) {
                     emptySlot = i;
                     break;
                  }
               }

               if (emptySlot == -1) {
                  return;
               }

               if (this.clock < this.delay.getValueInt()) {
                  this.clock++;
                  return;
               }

               this.mc
                  .field_1761
                  .method_2906(((class_1723)inventoryScreen.method_17577()).field_7763, slot, emptySlot, class_1713.field_7791, this.mc.field_1724);
               this.clock = 0;
            }
         }
      }
   }

   public enum Mode {
      Auto,
      Hover;
   }
}
