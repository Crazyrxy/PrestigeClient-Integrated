package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.InventoryUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1291;
import net.minecraft.class_1294;

public final class AutoPot extends Module implements TickListener {
   private final NumberSetting minHealth = new NumberSetting("Min Health", 1.0, 20.0, 10.0, 1.0);
   private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0.0, 10.0, 0.0, 1.0);
   private final NumberSetting throwDelay = new NumberSetting("Throw Delay", 0.0, 10.0, 0.0, 1.0);
   private final BooleanSetting goToPrevSlot = new BooleanSetting("Switch Back", true);
   private final BooleanSetting lookDown = new BooleanSetting("Look Down", true);
   private int switchClock;
   private int throwClock;
   private int prevSlot;
   private float prevPitch;
   private boolean bool;

   public AutoPot() {
      super("Auto Pot", "Automatically throws health potions when low on health", -1, Category.COMBAT);
      this.addSettings(this.minHealth, this.switchDelay, this.throwDelay, this.goToPrevSlot, this.lookDown);
   }

   private void reset() {
      this.switchClock = 0;
      this.throwClock = 0;
      this.prevSlot = -1;
      this.prevPitch = -1.0F;
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
         if (!(this.mc.field_1724.method_6032() <= this.minHealth.getValueFloat()) && !this.bool) {
            if (this.prevSlot != -1 || this.prevPitch != -1.0F) {
               InventoryUtils.setInvSlot(this.prevSlot);
               this.prevSlot = -1;
               this.mc.field_1724.method_36457(this.prevPitch);
               this.prevPitch = -1.0F;
            }
         } else {
            if (this.bool && this.mc.field_1724.method_6032() >= this.mc.field_1724.method_6063()) {
               this.bool = false;
               return;
            }

            if (!InventoryUtils.isThatSplash((class_1291)class_1294.field_5915.comp_349(), 1, 1, this.mc.field_1724.method_6047())) {
               if (this.switchClock < this.switchDelay.getValue()) {
                  this.switchClock++;
                  return;
               }

               if (this.goToPrevSlot.getValue() && this.prevSlot == -1) {
                  this.prevSlot = this.mc.field_1724.method_31548().method_67532();
               }

               if (this.lookDown.getValue() && this.prevPitch == -1.0F) {
                  this.prevPitch = this.mc.field_1724.method_36455();
               }

               int potSlot = InventoryUtils.findSplash((class_1291)class_1294.field_5915.comp_349(), 1, 1);
               if (potSlot != -1) {
                  InventoryUtils.setInvSlot(potSlot);
                  this.switchClock = 0;
               }
            }

            if (InventoryUtils.isThatSplash((class_1291)class_1294.field_5915.comp_349(), 1, 1, this.mc.field_1724.method_6047())) {
               if (this.throwClock < this.throwDelay.getValue()) {
                  this.throwClock++;
                  return;
               }

               if (this.lookDown.getValue()) {
                  this.mc.field_1724.method_36457(90.0F);
               }

               class_1269 actionResult = this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
               if (actionResult.method_23665()) {
                  this.mc.field_1724.method_6104(class_1268.field_5808);
               }

               this.throwClock = 0;
            }
         }
      }
   }
}
