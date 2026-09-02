package juxxl.prestige.module.modules.mace;

import juxxl.prestige.event.events.AttackListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_3966;
import net.minecraft.class_7923;

/**
 * Axe shield-disable then mace follow-up.
 * Compatible with Mace Swap / Auto Mace: exposes isBusy() and tryBegin() so they yield.
 */
public final class StunSlam extends Module implements AttackListener, TickListener {
   private final BooleanSetting onlyWhenBlocking = new BooleanSetting("Only If Blocking", true)
      .setDescription("Only run if the target player is blocking with a shield");
   private final NumberSetting minFall = new NumberSetting("Min Fall Distance", 0.0, 20.0, 0.0, 0.1)
      .setDescription("0 = no minimum. Mace follow-up only if falling at least this far");
   private final NumberSetting maceDelay = new NumberSetting("Mace Delay Ticks", 1.0, 8.0, 1.0, 1.0)
      .setDescription("Ticks after the axe hit before the mace smash (min 1)");
   private final BooleanSetting swapBack = new BooleanSetting("Swap Back", true);
   private final NumberSetting swapBackDelay = new NumberSetting("Swap Back Ticks", 0.0, 10.0, 1.0, 1.0);
   private final BooleanSetting swing = new BooleanSetting("Swing", true);

   private int returnSlot = -1;
   private int maceSlot = -1;
   private class_1297 pendingTarget = null;
   private int maceClock = -1;
   private int restoreClock = -1;
   private boolean waitingMace = false;
   private boolean waitingRestore = false;
   private int waitFallTicks = 0;

   public StunSlam() {
      super("Stun Slam", "Axe shield disable then mace follow-up", -1, Category.MACE);
      this.addSettings(this.onlyWhenBlocking, this.minFall, this.maceDelay, this.swapBack, this.swapBackDelay, this.swing);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(AttackListener.class, this);
      this.eventManager.add(TickListener.class, this);
      this.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(AttackListener.class, this);
      this.eventManager.remove(TickListener.class, this);
      if ((this.waitingMace || this.waitingRestore) && this.returnSlot >= 0 && this.mc.field_1724 != null) {
         InventoryUtils.setInvSlot(this.returnSlot);
      }
      this.clear();
      super.onDisable();
   }

   /** True while axe→mace sequence owns the hotbar. */
   public boolean isBusy() {
      return this.isEnabled() && (this.waitingMace || this.waitingRestore);
   }

   private void clear() {
      this.returnSlot = -1;
      this.maceSlot = -1;
      this.pendingTarget = null;
      this.maceClock = -1;
      this.restoreClock = -1;
      this.waitingMace = false;
      this.waitingRestore = false;
      this.waitFallTicks = 0;
   }

   /**
    * Strict shield-block check:
    * player must be actively blocking (isBlocking) AND the active item must be a shield.
    * method_6039 alone is isBlocking() (not "any item use"), but we still require a shield item.
    */
   private static boolean isBlockingShield(class_1657 player) {
      if (player == null) {
         return false;
      }
      // isBlocking() — false while eating/bowing/etc.
      if (!player.method_6039()) {
         return false;
      }
      try {
         class_1799 active = player.method_6030();
         if (active == null || active.method_7960()) {
            return false;
         }
         if (active.method_7909() == class_1802.field_8255) {
            return true;
         }
         String id = class_7923.field_41178.method_10221(active.method_7909()).toString();
         return id.contains("shield");
      } catch (Throwable ignored) {
         // Mapping glitch: still trust isBlocking if we cannot read the item
         return true;
      }
   }

   /** Whether Stun Slam would start on this target right now. */
   public boolean wouldActivate(class_1297 target) {
      if (!this.isEnabled() || this.isBusy() || target == null || this.mc.field_1724 == null) {
         return false;
      }
      if (this.onlyWhenBlocking.getValue()) {
         if (!(target instanceof class_1657 player) || !isBlockingShield(player)) {
            return false;
         }
      }
      return InventoryUtils.getAxeSlot() != -1 && InventoryUtils.getMaceSlot() != -1;
   }

   /**
    * Start axe-then-mace sequence (used by AttackEvent and AutoMace).
    * @return true if sequence started (caller should still do the axe hit / click)
    */
   public boolean tryBegin(class_1297 target) {
      if (!this.wouldActivate(target)) {
         return false;
      }

      int axeSlot = InventoryUtils.getAxeSlot();
      int mace = InventoryUtils.getMaceSlot();
      if (axeSlot == -1 || mace == -1) {
         return false;
      }

      this.returnSlot = this.mc.field_1724.method_31548().method_67532();
      this.maceSlot = mace;
      this.pendingTarget = target;
      this.waitFallTicks = 0;

      if (!WorldUtils.isAxe(this.mc.field_1724.method_6047())) {
         InventoryUtils.setInvSlot(axeSlot);
      }

      this.waitingMace = true;
      this.maceClock = Math.max(1, this.maceDelay.getValueInt());
      return true;
   }

   @Override
   public void onAttack(AttackListener.AttackEvent event) {
      if (this.mc.field_1724 == null || this.mc.field_1755 != null || this.isBusy()) {
         return;
      }

      class_1297 target = null;
      if (this.mc.field_1765 instanceof class_3966 hit) {
         target = hit.method_17782();
      }
      this.tryBegin(target);
   }

   @Override
   public void onTick() {
      if (this.mc.field_1724 == null) {
         return;
      }

      if (this.waitingMace) {
         if (this.maceClock > 0) {
            this.maceClock--;
            return;
         }

         float fall = (float) this.mc.field_1724.field_6017;
         if (this.minFall.getValueFloat() > 0.0F && fall < this.minFall.getValueFloat()) {
            this.waitFallTicks++;
            if (this.waitFallTicks > 40) {
               if (this.swapBack.getValue() && this.returnSlot >= 0) {
                  InventoryUtils.setInvSlot(this.returnSlot);
               }
               this.clear();
            }
            return;
         }

         if (this.maceSlot >= 0) {
            InventoryUtils.setInvSlot(this.maceSlot);
         }
         if (this.pendingTarget != null && this.pendingTarget.method_5805()) {
            WorldUtils.hitEntity(this.pendingTarget, this.swing.getValue());
         }

         this.waitingMace = false;
         if (this.swapBack.getValue() && this.returnSlot >= 0) {
            this.waitingRestore = true;
            this.restoreClock = Math.max(1, this.swapBackDelay.getValueInt());
         } else {
            this.clear();
         }
         return;
      }

      if (this.waitingRestore) {
         if (this.restoreClock > 0) {
            this.restoreClock--;
            return;
         }
         if (this.returnSlot >= 0 && this.returnSlot <= 8) {
            InventoryUtils.setInvSlot(this.returnSlot);
         }
         this.clear();
      }
   }
}
