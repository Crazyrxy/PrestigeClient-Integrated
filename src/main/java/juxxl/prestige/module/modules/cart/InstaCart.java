package juxxl.prestige.module.modules.cart;

import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.KeybindSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.KeyUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3965;
import net.minecraft.class_7923;

/**
 * Keybind places rail then TNT minecart using the same interact path as Anchor Macro.
 * Item matching uses registry id contains (survives mapping quirks).
 */
public final class InstaCart extends Module implements TickListener {
   private final KeybindSetting activateKey = new KeybindSetting("Activate Key", 67, false)
      .setDescription("Default: C");
   private final NumberSetting stepDelay = new NumberSetting("Step Delay", 0.0, 10.0, 1.0, 1.0);
   private final BooleanSetting swing = new BooleanSetting("Swing", true);

   private int step = 0; // 0 idle, 1 place rail done wait, 2 place cart
   private int stepClock = 0;
   private boolean wasDown = false;
   private int prevSlot = -1;
   private class_3965 savedHit = null;

   public InstaCart() {
      super("Insta Cart", "Keybind: place rail + TNT minecart at crosshair", -1, Category.CART);
      this.addSettings(this.activateKey, this.stepDelay, this.swing);
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
      this.reset();
      super.onDisable();
   }

   private void reset() {
      this.step = 0;
      this.stepClock = 0;
      this.wasDown = false;
      this.prevSlot = -1;
      this.savedHit = null;
   }

   private static String itemId(class_1799 stack) {
      if (stack == null || stack.method_7960()) {
         return "";
      }
      return class_7923.field_41178.method_10221(stack.method_7909()).toString();
   }

   private int findHotbar(java.util.function.Predicate<String> idPred) {
      for (int i = 0; i < 9; i++) {
         String id = itemId(this.mc.field_1724.method_31548().method_5438(i));
         if (idPred.test(id)) {
            return i;
         }
      }
      // Fallback to known fields
      for (int i = 0; i < 9; i++) {
         class_1799 s = this.mc.field_1724.method_31548().method_5438(i);
         if (!s.method_7960() && (s.method_31574(class_1802.field_8798) || s.method_31574(class_1802.field_8366))) {
            // handled below per-type
         }
      }
      return -1;
   }

   private int findRailSlot() {
      for (int i = 0; i < 9; i++) {
         class_1799 s = this.mc.field_1724.method_31548().method_5438(i);
         String id = itemId(s);
         if (id.endsWith(":rail") || id.contains("rail") && !id.contains("minecart")) {
            return i;
         }
         if (s.method_31574(class_1802.field_8798)) {
            return i;
         }
      }
      return -1;
   }

   private int findTntCartSlot() {
      for (int i = 0; i < 9; i++) {
         class_1799 s = this.mc.field_1724.method_31548().method_5438(i);
         String id = itemId(s);
         if (id.contains("tnt_minecart") || id.contains("tnt_minecart")) {
            return i;
         }
         if (s.method_31574(class_1802.field_8366)) {
            return i;
         }
      }
      return -1;
   }

   @Override
   public void onTick() {
      if (this.mc.field_1724 == null || this.mc.field_1687 == null || this.mc.field_1755 != null) {
         return;
      }

      if (this.step > 0) {
         if (this.stepClock > 0) {
            this.stepClock--;
            return;
         }
         this.runStep();
         return;
      }

      boolean down = KeyUtils.isKeyPressed(this.activateKey.getKey());
      if (down && !this.wasDown) {
         this.start();
      }
      this.wasDown = down;
   }

   private void start() {
      if (this.findRailSlot() == -1 || this.findTntCartSlot() == -1) {
         return;
      }
      if (!(this.mc.field_1765 instanceof class_3965 hit) || hit.method_17783() != class_240.field_1332) {
         return;
      }
      this.savedHit = hit;
      this.prevSlot = this.mc.field_1724.method_31548().method_67532();
      this.step = 1;
      this.stepClock = 0;
   }

   private void runStep() {
      if (this.step == 1) {
         int rail = this.findRailSlot();
         if (rail != -1 && this.savedHit != null) {
            InventoryUtils.setInvSlot(rail);
            WorldUtils.placeBlock(this.savedHit, this.swing.getValue());
         }
         this.step = 2;
         this.stepClock = this.stepDelay.getValueInt();
         return;
      }

      if (this.step == 2) {
         int cart = this.findTntCartSlot();
         if (cart != -1) {
            InventoryUtils.setInvSlot(cart);
            // Prefer current look target after rail place; else saved hit
            class_3965 hit = this.mc.field_1765 instanceof class_3965 h && h.method_17783() == class_240.field_1332
               ? h
               : this.savedHit;
            if (hit != null) {
               WorldUtils.placeBlock(hit, this.swing.getValue());
            }
         }
         if (this.prevSlot >= 0) {
            InventoryUtils.setInvSlot(this.prevSlot);
         }
         this.reset();
      }
   }
}
