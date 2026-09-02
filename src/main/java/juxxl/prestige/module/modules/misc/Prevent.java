package juxxl.prestige.module.modules.misc;

import juxxl.prestige.event.events.AttackListener;
import juxxl.prestige.event.events.BlockBreakingListener;
import juxxl.prestige.event.events.ItemUseListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.utils.BlockUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_3965;

public final class Prevent extends Module implements ItemUseListener, AttackListener, BlockBreakingListener {
   private final BooleanSetting doubleGlowstone = new BooleanSetting("Double Glowstone", false)
      .setDescription("Makes it so you can't charge the anchor again if it's already charged");
   private final BooleanSetting glowstoneMisplace = new BooleanSetting("Glowstone Misplace", false)
      .setDescription("Makes it so you can only right-click with glowstone only when aiming at an anchor");
   private final BooleanSetting anchorOnAnchor = new BooleanSetting("Anchor on Anchor", false)
      .setDescription("Makes it so you can't place an anchor on/next to another anchor unless charged");
   private final BooleanSetting obiPunch = new BooleanSetting("Obi Punch", false)
      .setDescription("Makes it so you can crystal faster by not letting you left click/start breaking the obsidian");
   private final BooleanSetting echestClick = new BooleanSetting("E-chest click", false)
      .setDescription("Makes it so you can't click on e-chests with PvP items");

   public Prevent() {
      super("Prevent", "Prevents you from certain actions", -1, Category.MISC);
      this.addSettings(this.doubleGlowstone, this.glowstoneMisplace, this.anchorOnAnchor, this.obiPunch, this.echestClick);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(BlockBreakingListener.class, this);
      this.eventManager.add(AttackListener.class, this);
      this.eventManager.add(ItemUseListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(BlockBreakingListener.class, this);
      this.eventManager.remove(AttackListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      super.onDisable();
   }

   @Override
   public void onAttack(AttackListener.AttackEvent event) {
      if (this.mc.field_1765 instanceof class_3965 hit
         && BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540)
         && this.obiPunch.getValue()
         && this.mc.field_1724.method_24518(class_1802.field_8301)) {
         event.cancel();
      }
   }

   @Override
   public void onBlockBreaking(BlockBreakingListener.BlockBreakingEvent event) {
      if (this.mc.field_1765 instanceof class_3965 hit
         && BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540)
         && this.obiPunch.getValue()
         && this.mc.field_1724.method_24518(class_1802.field_8301)) {
         event.cancel();
      }
   }

   @Override
   public void onItemUse(ItemUseListener.ItemUseEvent event) {
      if (this.mc.field_1765 instanceof class_3965 hit) {
         if (BlockUtils.isAnchorCharged(hit.method_17777()) && this.doubleGlowstone.getValue() && this.mc.field_1724.method_24518(class_1802.field_8801)) {
            event.cancel();
         }

         if (!BlockUtils.isBlock(hit.method_17777(), class_2246.field_23152)
            && this.glowstoneMisplace.getValue()
            && this.mc.field_1724.method_24518(class_1802.field_8801)) {
            event.cancel();
         }

         if (BlockUtils.isAnchorNotCharged(hit.method_17777()) && this.anchorOnAnchor.getValue() && this.mc.field_1724.method_24518(class_1802.field_23141)) {
            event.cancel();
         }

         if (BlockUtils.isBlock(hit.method_17777(), class_2246.field_10443)
            && this.echestClick.getValue()
            && (
               WorldUtils.isSword(this.mc.field_1724.method_6047())
                  || this.mc.field_1724.method_6047().method_7909() == class_1802.field_8301
                  || this.mc.field_1724.method_6047().method_7909() == class_1802.field_8281
                  || this.mc.field_1724.method_6047().method_7909() == class_1802.field_23141
                  || this.mc.field_1724.method_6047().method_7909() == class_1802.field_8801
            )) {
            event.cancel();
         }
      }
   }
}
