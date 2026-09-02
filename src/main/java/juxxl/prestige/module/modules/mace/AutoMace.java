package juxxl.prestige.module.modules.mace;

import juxxl.prestige.Prestige;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_3966;

/**
 * Auto-attacks crosshair target.
 * Priority: Stun Slam (if it would activate) → Mace Swap → plain hit.
 * Pauses while Stun Slam owns the hotbar.
 */
public final class AutoMace extends Module implements TickListener {
   private final NumberSetting cooldown = new NumberSetting("Cooldown Ticks", 0.0, 20.0, 8.0, 1.0);
   private final BooleanSetting autoEquip = new BooleanSetting("Auto Equip Mace", false);
   private final BooleanSetting onlyWhenMace = new BooleanSetting("Only When Holding Mace", false);
   private final BooleanSetting onlyFalling = new BooleanSetting("Only While Falling", false);
   private final NumberSetting minFall = new NumberSetting("Min Fall Distance", 0.0, 20.0, 0.0, 0.1)
      .setDescription("0 = no minimum. Only attacks if falling at least this far");
   private final BooleanSetting swing = new BooleanSetting("Swing", true);
   private final BooleanSetting useMaceSwap = new BooleanSetting("Use Mace Swap", true)
      .setDescription("Call Mace Swap when Stun Slam does not take the hit");
   private final BooleanSetting useStunSlam = new BooleanSetting("Use Stun Slam", true)
      .setDescription("Prefer Stun Slam when the target qualifies");

   private int clock = 0;

   public AutoMace() {
      super("Auto Mace", "Auto attack; works with Stun Slam + Mace Swap", -1, Category.MACE);
      this.addSettings(this.cooldown, this.autoEquip, this.onlyWhenMace, this.onlyFalling, this.minFall, this.swing, this.useMaceSwap, this.useStunSlam);
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
      if (this.mc.field_1724 == null || this.mc.field_1687 == null || this.mc.field_1755 != null) {
         return;
      }

      if (this.clock > 0) {
         this.clock--;
         return;
      }

      // Let Stun Slam finish axe→mace without extra auto hits
      if (Prestige.INSTANCE != null) {
         StunSlam slam = Prestige.INSTANCE.getModuleManager().getModule(StunSlam.class);
         if (slam != null && slam.isBusy()) {
            return;
         }
      }

      if (this.onlyFalling.getValue() && this.mc.field_1724.method_24828()) {
         return;
      }
      float fall = (float) this.mc.field_1724.field_6017;
      if (this.minFall.getValueFloat() > 0.0F && fall < this.minFall.getValueFloat()) {
         return;
      }

      boolean holdingMace = WorldUtils.isMace(this.mc.field_1724.method_6047());
      if (!holdingMace) {
         if (this.onlyWhenMace.getValue()) {
            return;
         }
         if (this.autoEquip.getValue() && InventoryUtils.hasMace()) {
            InventoryUtils.selectMace();
         }
      }

      if (!(this.mc.field_1765 instanceof class_3966 hit)) {
         return;
      }

      class_1297 entity = hit.method_17782();
      if (!(entity instanceof class_1309)) {
         return;
      }

      boolean stunStarted = false;
      if (this.useStunSlam.getValue() && Prestige.INSTANCE != null) {
         StunSlam slam = Prestige.INSTANCE.getModuleManager().getModule(StunSlam.class);
         if (slam != null && slam.isEnabled()) {
            stunStarted = slam.tryBegin(entity);
         }
      }

      // If Stun Slam started, this hit is the axe hit — don't also Mace Swap
      if (!stunStarted && this.useMaceSwap.getValue() && Prestige.INSTANCE != null) {
         MaceSwap swap = Prestige.INSTANCE.getModuleManager().getModule(MaceSwap.class);
         if (swap != null && swap.isEnabled()) {
            swap.performSwap();
         }
      }

      WorldUtils.hitEntity(entity, this.swing.getValue());
      this.clock = this.cooldown.getValueInt();
   }
}
