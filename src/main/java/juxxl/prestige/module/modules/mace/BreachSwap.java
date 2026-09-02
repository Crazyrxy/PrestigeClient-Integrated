package juxxl.prestige.module.modules.mace;

import juxxl.prestige.event.events.AttackListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1799;

/**
 * Same proven attack-HEAD swap as Mace Swap (BetterMaceSwap-style attribute swap).
 * Separate toggle for users who want it on its own bind.
 */
public final class BreachSwap extends Module implements AttackListener, TickListener {
   private final BooleanSetting onlyWeapon = new BooleanSetting("Only Sword/Axe", true);
   private final BooleanSetting swapBack = new BooleanSetting("Swap Back", true);
   private final NumberSetting delay = new NumberSetting("Swap Back Ticks", 0.0, 20.0, 2.0, 1.0);

   private int previousSlot = -1;
   private int clock = -1;
   private boolean active = false;

   public BreachSwap() {
      super("Breach Swap", "Swap to mace on attack start (attribute swap)", -1, Category.MACE);
      this.addSettings(this.onlyWeapon, this.swapBack, this.delay);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(AttackListener.class, this);
      this.eventManager.add(TickListener.class, this);
      this.previousSlot = -1;
      this.clock = -1;
      this.active = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(AttackListener.class, this);
      this.eventManager.remove(TickListener.class, this);
      if (this.active && this.previousSlot >= 0 && this.mc.field_1724 != null) {
         InventoryUtils.setInvSlot(this.previousSlot);
      }
      super.onDisable();
   }

   @Override
   public void onAttack(AttackListener.AttackEvent event) {
      if (this.mc.field_1724 == null || this.mc.field_1755 != null) {
         return;
      }
      class_1799 held = this.mc.field_1724.method_6047();
      if (this.onlyWeapon.getValue() && !WorldUtils.isSword(held) && !WorldUtils.isAxe(held)) {
         return;
      }
      if (WorldUtils.isMace(held)) {
         return;
      }
      int mace = InventoryUtils.getMaceSlot();
      if (mace == -1) {
         return;
      }
      this.previousSlot = this.mc.field_1724.method_31548().method_67532();
      InventoryUtils.setInvSlot(mace);
      this.active = true;
      this.clock = this.swapBack.getValue() ? this.delay.getValueInt() : -1;
      if (!this.swapBack.getValue()) {
         this.active = false;
         this.previousSlot = -1;
      }
   }

   @Override
   public void onTick() {
      if (!this.active || this.clock < 0 || this.mc.field_1724 == null) {
         return;
      }
      if (this.clock == 0) {
         if (this.previousSlot >= 0) {
            InventoryUtils.setInvSlot(this.previousSlot);
         }
         this.active = false;
         this.previousSlot = -1;
         this.clock = -1;
      } else {
         this.clock--;
      }
   }
}
