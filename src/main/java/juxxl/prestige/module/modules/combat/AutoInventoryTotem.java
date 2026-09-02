package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.ModeSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.FakeInvScreen;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.TimerUtils;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_490;

public final class AutoInventoryTotem extends Module implements TickListener {
   private final ModeSetting<AutoInventoryTotem.Mode> mode = new ModeSetting<>("Mode", AutoInventoryTotem.Mode.Blatant, AutoInventoryTotem.Mode.class)
      .setDescription("Whether to randomize the toteming pattern or no");
   private final NumberSetting delay = new NumberSetting("Delay", 0.0, 20.0, 0.0, 1.0);
   private final BooleanSetting hotbar = new BooleanSetting("Hotbar", true)
      .setDescription("Puts a totem in your hotbar as well, if enabled (Setting below will work if this is enabled)");
   private final NumberSetting totemSlot = new NumberSetting("Totem Slot", 1.0, 9.0, 1.0, 1.0).setDescription("Your preferred totem slot");
   private final BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", false).setDescription("Switches to totem slot when going inside the inventory");
   private final BooleanSetting forceTotem = new BooleanSetting("Force Totem", false)
      .setDescription("Puts the totem in the slot, regardless if its space is taken up by something else");
   private final BooleanSetting autoOpen = new BooleanSetting("Auto Open", false).setDescription("Automatically opens and closes the inventory for you");
   private final NumberSetting stayOpenFor = new NumberSetting("Stay Open For", 0.0, 20.0, 0.0, 1.0);
   int clock = -1;
   int closeClock = -1;
   TimerUtils openTimer = new TimerUtils();
   TimerUtils closeTimer = new TimerUtils();

   public AutoInventoryTotem() {
      super("Auto Inventory Totem", "Automatically equips a totem in your offhand and main hand if empty", -1, Category.COMBAT);
      this.addSettings(this.mode, this.delay, this.hotbar, this.totemSlot, this.autoSwitch, this.forceTotem, this.autoOpen, this.stayOpenFor);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.clock = -1;
      this.closeClock = -1;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (this.shouldOpenScreen() && this.autoOpen.getValue()) {
         this.mc.method_1507(new FakeInvScreen(this.mc.field_1724));
      }

      if (!(this.mc.field_1755 instanceof class_490) && !(this.mc.field_1755 instanceof FakeInvScreen)) {
         this.clock = -1;
         this.closeClock = -1;
      } else {
         if (this.clock == -1) {
            this.clock = this.delay.getValueInt();
         }

         if (this.closeClock == -1) {
            this.closeClock = this.stayOpenFor.getValueInt();
         }

         if (this.clock > 0) {
            this.clock--;
         }

         class_1661 inventory = this.mc.field_1724.method_31548();
         if (this.autoSwitch.getValue()) {
            inventory.method_61496(this.totemSlot.getValueInt() - 1);
         }

         if (this.clock <= 0) {
            if (this.mc.field_1724.method_6079().method_7909() != class_1802.field_8288) {
               int slot = this.mode.isMode(AutoInventoryTotem.Mode.Blatant) ? InventoryUtils.findTotemSlot() : InventoryUtils.findRandomTotemSlot();
               if (slot != -1) {
                  this.mc
                     .field_1761
                     .method_2906(((class_1723)((class_490)this.mc.field_1755).method_17577()).field_7763, slot, 40, class_1713.field_7791, this.mc.field_1724);
                  return;
               }
            }

            if (this.hotbar.getValue()) {
               class_1799 mainHand = this.mc.field_1724.method_6047();
               if (mainHand.method_7960() || this.forceTotem.getValue() && mainHand.method_7909() != class_1802.field_8288) {
                  int slot = this.mode.isMode(AutoInventoryTotem.Mode.Blatant) ? InventoryUtils.findTotemSlot() : InventoryUtils.findRandomTotemSlot();
                  if (slot != -1) {
                     this.mc
                        .field_1761
                        .method_2906(
                           ((class_1723)((class_490)this.mc.field_1755).method_17577()).field_7763,
                           slot,
                           inventory.method_67532(),
                           class_1713.field_7791,
                           this.mc.field_1724
                        );
                     return;
                  }
               }
            }

            if (this.shouldCloseScreen() && this.autoOpen.getValue()) {
               if (this.closeClock != 0) {
                  this.closeClock--;
                  return;
               }

               this.mc.field_1755.method_25419();
               this.closeClock = this.stayOpenFor.getValueInt();
            }
         }
      }
   }

   public boolean shouldCloseScreen() {
      return this.hotbar.getValue()
         ? this.mc.field_1724.method_31548().method_5438(this.totemSlot.getValueInt() - 1).method_7909() == class_1802.field_8288
            && this.mc.field_1724.method_6079().method_7909() == class_1802.field_8288
            && this.mc.field_1755 instanceof FakeInvScreen
         : this.mc.field_1724.method_6079().method_7909() == class_1802.field_8288 && this.mc.field_1755 instanceof FakeInvScreen;
   }

   public boolean shouldOpenScreen() {
      return this.hotbar.getValue()
         ? (
               this.mc.field_1724.method_6079().method_7909() != class_1802.field_8288
                  || this.mc.field_1724.method_31548().method_5438(this.totemSlot.getValueInt() - 1).method_7909() != class_1802.field_8288
            )
            && !(this.mc.field_1755 instanceof FakeInvScreen)
            && InventoryUtils.countItemExceptHotbar(item -> item == class_1802.field_8288) != 0
         : this.mc.field_1724.method_6079().method_7909() != class_1802.field_8288
            && !(this.mc.field_1755 instanceof FakeInvScreen)
            && InventoryUtils.countItemExceptHotbar(item -> item == class_1802.field_8288) != 0;
   }

   public enum Mode {
      Blatant,
      Random;
   }
}
