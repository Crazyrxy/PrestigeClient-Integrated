package juxxl.prestige.module.modules.mace;

import juxxl.prestige.event.events.GameRenderListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.ModeSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.RenderUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_3532;

/**
 * Mace aim while falling. Smooth exponential lerp every render frame
 * (not 20-tick snaps).
 */
public final class MaceAim extends Module implements GameRenderListener {
   private final ModeSetting<Mode> mode = new ModeSetting<>("Mode", Mode.Legit, Mode.class);
   private final NumberSetting range = new NumberSetting("Range", 1.0, 8.0, 5.0, 0.1);
   private final NumberSetting fov = new NumberSetting("FOV", 30.0, 360.0, 120.0, 1.0);
   private final NumberSetting legitSpeed = new NumberSetting("Legit Speed", 1.0, 20.0, 6.0, 0.5)
      .setDescription("Higher = faster lock (Legit)");
   private final NumberSetting blatantSpeed = new NumberSetting("Blatant Speed", 4.0, 30.0, 11.0, 0.5)
      .setDescription("Higher = faster; keep moderate for smoothness");
   private final NumberSetting minFall = new NumberSetting("Min Fall Distance", 0.0, 10.0, 0.5, 0.1);
   private final BooleanSetting requireMace = new BooleanSetting("Require Mace", true);
   private final BooleanSetting seeOnly = new BooleanSetting("See Only", true);

   public MaceAim() {
      super("Mace Aim", "Smooth aim assist while falling with a mace", -1, Category.MACE);
      this.addSettings(this.mode, this.range, this.fov, this.legitSpeed, this.blatantSpeed, this.minFall, this.requireMace, this.seeOnly);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(GameRenderListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(GameRenderListener.class, this);
      super.onDisable();
   }

   private static Rotation eyesTo(class_1657 self, class_243 target) {
      class_243 eyes = self.method_5836(RenderUtils.tickProgress());
      double dx = target.field_1352 - eyes.field_1352;
      double dy = target.field_1351 - eyes.field_1351;
      double dz = target.field_1350 - eyes.field_1350;
      double dist = Math.sqrt(dx * dx + dz * dz);
      float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.max(dist, 1.0E-4)));
      return new Rotation(yaw, class_3532.method_15363(pitch, -90.0F, 90.0F));
   }

   @Override
   public void onGameRender(GameRenderListener.GameRenderEvent event) {
      if (this.mc.field_1724 == null || this.mc.field_1687 == null || this.mc.field_1755 != null) {
         return;
      }
      if (this.mc.field_1724.method_24828()) {
         return;
      }
      if ((float) this.mc.field_1724.field_6017 < this.minFall.getValueFloat()) {
         return;
      }
      if (this.requireMace.getValue() && !WorldUtils.isMace(this.mc.field_1724.method_6047())) {
         return;
      }

      class_1657 self = this.mc.field_1724;
      class_1657 target = WorldUtils.findNearestPlayer(self, this.range.getValueFloat(), this.seeOnly.getValue(), true);
      if (target == null) {
         return;
      }

      class_243 body = target.method_30950(RenderUtils.tickProgress()).method_1031(0.0, 0.9, 0.0);
      Rotation dest = eyesTo(self, body);

      float curYaw = self.method_36454();
      float curPitch = self.method_36455();
      float yawDiff = class_3532.method_15393(dest.yaw - curYaw);
      float pitchDiff = dest.pitch - curPitch;

      float ang = (float) Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
      if (ang > this.fov.getValueFloat() / 2.0F) {
         return;
      }

      float dt = RenderUtils.frameDelta();
      if (dt <= 0.0F || dt > 0.1F) {
         dt = 1.0F / 60.0F;
      }

      // Exponential smoothing — frame-rate independent, no 20-tick steps
      float k = this.mode.isMode(Mode.Blatant)
         ? this.blatantSpeed.getValueFloat()
         : this.legitSpeed.getValueFloat();
      float alpha = 1.0F - (float) Math.exp(-k * dt);
      alpha = class_3532.method_15363(alpha, 0.0F, 0.55F);

      self.method_36456(curYaw + yawDiff * alpha);
      self.method_36457(class_3532.method_15363(curPitch + pitchDiff * alpha, -90.0F, 90.0F));
   }

   private static final class Rotation {
      final float yaw;
      final float pitch;
      Rotation(float yaw, float pitch) {
         this.yaw = yaw;
         this.pitch = pitch;
      }
   }

   public enum Mode {
      Legit,
      Blatant
   }
}
