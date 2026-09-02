package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.AttackListener;
import juxxl.prestige.event.events.BlockBreakingListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_239.class_240;

public final class NoMissDelay extends Module implements AttackListener, BlockBreakingListener {
   private final BooleanSetting onlyWeapon = new BooleanSetting("Only weapon", true);
   private final BooleanSetting air = new BooleanSetting("Air", true).setDescription("Whether to stop hits directed to the air");
   private final BooleanSetting blocks = new BooleanSetting("Blocks", false).setDescription("Whether to stop hits directed to blocks");

   public NoMissDelay() {
      super("No Miss Delay", "Doesn't let you miss your sword/axe hits", -1, Category.COMBAT);
      this.addSettings(this.onlyWeapon, this.air, this.blocks);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(AttackListener.class, this);
      this.eventManager.add(BlockBreakingListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(AttackListener.class, this);
      this.eventManager.remove(BlockBreakingListener.class, this);
      super.onDisable();
   }

   @Override
   public void onAttack(AttackListener.AttackEvent event) {
      if (!this.onlyWeapon.getValue() || WorldUtils.isWeapon(this.mc.field_1724.method_6047())) {
         switch (this.mc.field_1765.method_17783()) {
            case field_1333:
               if (this.air.getValue()) {
                  event.cancel();
               }
               break;
            case field_1332:
               if (this.blocks.getValue()) {
                  event.cancel();
               }
         }
      }
   }

   @Override
   public void onBlockBreaking(BlockBreakingListener.BlockBreakingEvent event) {
      if (!this.onlyWeapon.getValue() || WorldUtils.isWeapon(this.mc.field_1724.method_6047())) {
         if (this.mc.field_1765.method_17783() == class_240.field_1332 && this.blocks.getValue()) {
            event.cancel();
         }
      }
   }
}
