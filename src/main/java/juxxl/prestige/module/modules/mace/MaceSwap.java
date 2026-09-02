package juxxl.prestige.module.modules.mace;

import juxxl.prestige.Prestige;
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
 * Swaps to mace on attack, then optionally swaps back.
 * Yields to Stun Slam when that module is handling the fight.
 */
public final class MaceSwap extends Module implements AttackListener, TickListener {
   private final BooleanSetting onlyWeapon = new BooleanSetting("Only Sword/Axe", true);
   private final BooleanSetting swapBack = new BooleanSetting("Swap Back", true);
   private final NumberSetting swapBackDelay = new NumberSetting("Swap Back Ticks", 0.0, 20.0, 2.0, 1.0);
   private final BooleanSetting yieldToStunSlam = new BooleanSetting("Yield To Stun Slam", true)
      .setDescription("Do not swap if Stun Slam is active / handling this hit");

   private int previousSlot = -1;
   private int swapBackClock = -1;
   private boolean swapped = false;

   public MaceSwap() {
      super("Mace Swap", "Swap to mace on attack, then back", -1, Category.MACE);
      this.addSettings(this.onlyWeapon, this.swapBack, this.swapBackDelay, this.yieldToStunSlam);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(AttackListener.class, this);
      this.eventManager.add(TickListener.class, this);
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(AttackListener.class, this);
      this.eventManager.remove(TickListener.class, this);
      this.forceRestore();
      super.onDisable();
   }

   private void resetState() {
      this.previousSlot = -1;
      this.swapBackClock = -1;
      this.swapped = false;
   }

   private void forceRestore() {
      if (this.swapped && this.previousSlot >= 0 && this.mc.field_1724 != null) {
         InventoryUtils.setInvSlot(this.previousSlot);
      }
      this.resetState();
   }

   private boolean stunSlamOwnsHotbar() {
      if (!this.yieldToStunSlam.getValue() || Prestige.INSTANCE == null) {
         return false;
      }
      StunSlam slam = Prestige.INSTANCE.getModuleManager().getModule(StunSlam.class);
      return slam != null && slam.isBusy();
   }

   private boolean stunSlamWouldHandle() {
      if (!this.yieldToStunSlam.getValue() || Prestige.INSTANCE == null || this.mc.field_1765 == null) {
         return false;
      }
      StunSlam slam = Prestige.INSTANCE.getModuleManager().getModule(StunSlam.class);
      if (slam == null || !slam.isEnabled()) {
         return false;
      }
      if (!(this.mc.field_1765 instanceof net.minecraft.class_3966 hit)) {
         return false;
      }
      return slam.wouldActivate(hit.method_17782());
   }

   /** Shared entry used by AttackEvent and AutoMace. */
   public boolean performSwap() {
      if (this.mc.field_1724 == null || this.mc.field_1755 != null) {
         return false;
      }
      if (!this.isEnabled()) {
         return false;
      }
      if (this.stunSlamOwnsHotbar() || this.stunSlamWouldHandle()) {
         return false;
      }

      class_1799 held = this.mc.field_1724.method_6047();
      if (this.onlyWeapon.getValue() && !WorldUtils.isSword(held) && !WorldUtils.isAxe(held)) {
         return false;
      }
      if (WorldUtils.isMace(held)) {
         return false;
      }

      int maceSlot = InventoryUtils.getMaceSlot();
      if (maceSlot == -1) {
         return false;
      }

      int current = this.mc.field_1724.method_31548().method_67532();
      if (maceSlot == current) {
         return false;
      }

      this.previousSlot = current;
      InventoryUtils.setInvSlot(maceSlot);
      this.swapped = true;
      if (this.swapBack.getValue()) {
         this.swapBackClock = this.swapBackDelay.getValueInt();
      } else {
         this.swapped = false;
         this.previousSlot = -1;
         this.swapBackClock = -1;
      }
      return true;
   }

   @Override
   public void onAttack(AttackListener.AttackEvent event) {
      this.performSwap();
   }

   @Override
   public void onTick() {
      if (this.stunSlamOwnsHotbar()) {
         // Don't restore while Stun Slam controls the hotbar
         return;
      }
      if (!this.swapped || this.mc.field_1724 == null) {
         return;
      }
      if (this.swapBackClock < 0) {
         return;
      }
      if (this.swapBackClock == 0) {
         if (this.previousSlot >= 0 && this.previousSlot <= 8) {
            InventoryUtils.setInvSlot(this.previousSlot);
         }
         this.resetState();
      } else {
         this.swapBackClock--;
      }
   }
}
