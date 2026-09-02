package juxxl.prestige.module.modules.cart;

import juxxl.prestige.event.events.HudListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.KeybindSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.KeyUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3965;
import net.minecraft.class_7923;

/**
 * Sequence: rail -> TNT cart -> look at the block BEFORE the cart (player-side,
 * along look vector) -> flint & steel on that block's TOP so you can shoot.
 */
public final class XBowCart extends Module implements TickListener, HudListener {
   private final KeybindSetting activateKey = new KeybindSetting("Activate Key", 86, false)
      .setDescription("Default: V");
   private final NumberSetting stepDelay = new NumberSetting("Step Delay", 0.0, 10.0, 2.0, 1.0);
   private final BooleanSetting swing = new BooleanSetting("Swing", true);
   private final BooleanSetting aimForFlint = new BooleanSetting("Aim For Flint", true);
   private final NumberSetting aimSmooth = new NumberSetting("Aim Smooth", 4.0, 20.0, 9.0, 0.5)
      .setDescription("Higher = faster flint aim; lower = smoother");

   private int step = 0;
   private int stepClock = 0;
   private boolean wasDown = false;
   private int prevSlot = -1;
   private class_3965 savedHit = null;
   private class_2338 railBlock = null;
   private class_2338 flintBlock = null;
   private double lookX = 0.0;
   private double lookZ = 1.0;

   public XBowCart() {
      super("XBow Cart", "Rail + cart + flint on the block before the cart", -1, Category.CART);
      this.addSettings(this.activateKey, this.stepDelay, this.swing, this.aimForFlint, this.aimSmooth);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(HudListener.class, this);
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(HudListener.class, this);
      this.reset();
      super.onDisable();
   }

   private void reset() {
      this.step = 0;
      this.stepClock = 0;
      this.wasDown = false;
      this.prevSlot = -1;
      this.savedHit = null;
      this.railBlock = null;
      this.flintBlock = null;
   }

   private static String itemId(class_1799 stack) {
      if (stack == null || stack.method_7960()) return "";
      return class_7923.field_41178.method_10221(stack.method_7909()).toString();
   }

   private int findSlotContaining(String... parts) {
      for (int i = 0; i < 9; i++) {
         String id = itemId(this.mc.field_1724.method_31548().method_5438(i));
         for (String p : parts) {
            if (id.contains(p)) return i;
         }
      }
      return -1;
   }

   private int findRail() {
      int s = this.findSlotContaining(":rail");
      if (s != -1) return s;
      for (int i = 0; i < 9; i++) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_31574(class_1802.field_8798)) return i;
      }
      return this.findSlotContaining("rail");
   }

   private int findTntCart() {
      int s = this.findSlotContaining("tnt_minecart");
      if (s != -1) return s;
      for (int i = 0; i < 9; i++) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_31574(class_1802.field_8366)) return i;
      }
      return -1;
   }

   private int findFlint() {
      int s = this.findSlotContaining("flint_and_steel");
      if (s != -1) return s;
      for (int i = 0; i < 9; i++) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_31574(class_1802.field_8834)) return i;
      }
      return -1;
   }

   /** Horizontal direction from cart toward the player (opposite of look). */
   private class_2350 towardPlayer() {
      if (Math.abs(this.lookX) > Math.abs(this.lookZ)) {
         return this.lookX > 0.0 ? class_2350.field_11039 : class_2350.field_11034; // west : east
      }
      return this.lookZ > 0.0 ? class_2350.field_11043 : class_2350.field_11035; // north : south
   }

   private float[] flintTargetRot() {
      class_1657 p = this.mc.field_1724;
      if (p == null || this.flintBlock == null) return null;
      class_243 eyes = p.method_5836(1.0F);
      class_243 top = class_243.method_24953(this.flintBlock).method_1031(0.0, 0.55, 0.0);
      double dx = top.field_1352 - eyes.field_1352;
      double dy = top.field_1351 - eyes.field_1351;
      double dz = top.field_1350 - eyes.field_1350;
      double dist = Math.sqrt(dx * dx + dz * dz);
      float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      float pitch = class_3532.method_15363(
         (float) -Math.toDegrees(Math.atan2(dy, Math.max(dist, 1.0E-4))),
         -89.0F, 89.0F);
      return new float[]{yaw, pitch};
   }

   /** Smooth exponential step toward flint aim (every HUD frame). */
   private void lerpTowardFlint() {
      class_1657 p = this.mc.field_1724;
      float[] dest = this.flintTargetRot();
      if (p == null || dest == null) return;

      float yawDiff = class_3532.method_15393(dest[0] - p.method_36454());
      float pitchDiff = dest[1] - p.method_36455();
      float dt = 1.0F / 60.0F;
      try {
         float fd = juxxl.prestige.utils.RenderUtils.frameDelta();
         if (fd > 0.0F && fd < 0.1F) dt = fd;
      } catch (Throwable ignored) {}
      float k = this.aimSmooth.getValueFloat();
      float a = 1.0F - (float) Math.exp(-k * dt);
      a = class_3532.method_15363(a, 0.04F, 0.4F);
      p.method_36456(p.method_36454() + yawDiff * a);
      p.method_36457(class_3532.method_15363(p.method_36455() + pitchDiff * a, -89.0F, 89.0F));
   }

   @Override
   public void onRenderHud(HudListener.HudEvent event) {
      if (this.step == 3 || this.step == 4) {
         if (this.aimForFlint.getValue()) {
            this.lerpTowardFlint();
         }
      }
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
      if (this.findRail() == -1 || this.findTntCart() == -1 || this.findFlint() == -1) {
         return;
      }
      if (!(this.mc.field_1765 instanceof class_3965 hit) || hit.method_17783() != class_240.field_1332) {
         return;
      }

      this.savedHit = hit;
      class_2338 looked = hit.method_17777();
      class_2350 face = hit.method_17780();

      // Where the rail/cart actually sit: on top if looking at the top face, else offset
      if (face == class_2350.field_11036) {
         this.railBlock = looked.method_10084(); // up
         if (!this.mc.field_1687.method_22347(this.railBlock)) {
            this.railBlock = looked;
         }
      } else {
         this.railBlock = looked.method_10093(face);
      }

      class_243 look = this.mc.field_1724.method_5720();
      this.lookX = look.field_1352;
      this.lookZ = look.field_1350;

      // Block BEFORE the cart, along LOS, toward the player
      this.flintBlock = this.railBlock.method_10093(this.towardPlayer());
      if (this.mc.field_1687.method_22347(this.flintBlock)) {
         this.flintBlock = this.flintBlock.method_10074(); // down onto solid
      }

      this.prevSlot = this.mc.field_1724.method_31548().method_67532();
      this.step = 1;
      this.stepClock = 0;
   }

   private void runStep() {
      class_3965 hit = this.mc.field_1765 instanceof class_3965 h && h.method_17783() == class_240.field_1332
         ? h
         : this.savedHit;

      if (this.step == 1) {
         int slot = this.findRail();
         if (slot != -1 && hit != null) {
            InventoryUtils.setInvSlot(slot);
            WorldUtils.placeBlock(hit, this.swing.getValue());
         }
         this.step = 2;
         this.stepClock = this.stepDelay.getValueInt();
         return;
      }
      if (this.step == 2) {
         int slot = this.findTntCart();
         if (slot != -1 && hit != null) {
            InventoryUtils.setInvSlot(slot);
            WorldUtils.placeBlock(hit, this.swing.getValue());
         }
         this.step = 3;
         this.stepClock = this.stepDelay.getValueInt();
         return;
      }
      if (this.step == 3) {
         // HUD lerpTowardFlint runs every frame while we wait
         this.step = 4;
         this.stepClock = Math.max(3, this.stepDelay.getValueInt() + 2);
         return;
      }
      if (this.step == 4) {
         int slot = this.findFlint();
         if (slot != -1 && this.flintBlock != null) {
            InventoryUtils.setInvSlot(slot);
            class_243 top = class_243.method_24953(this.flintBlock).method_1031(0.0, 0.5, 0.0);
            class_3965 flintHit = new class_3965(top, class_2350.field_11036, this.flintBlock, false);
            WorldUtils.placeBlock(flintHit, this.swing.getValue());
         }
         if (this.prevSlot >= 0) {
            InventoryUtils.setInvSlot(this.prevSlot);
         }
         this.reset();
      }
   }
}
