package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.HudListener;
import juxxl.prestige.event.events.MouseMoveListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.MinMaxSetting;
import juxxl.prestige.module.setting.ModeSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.MathUtils;
import juxxl.prestige.utils.RenderUtils;
import juxxl.prestige.utils.RotationUtils;
import juxxl.prestige.utils.TimerUtils;
import juxxl.prestige.utils.WorldUtils;
import juxxl.prestige.utils.rotation.Rotation;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import org.lwjgl.glfw.GLFW;

public final class AimAssist extends Module implements HudListener, MouseMoveListener {
   private final BooleanSetting stickyAim = new BooleanSetting("Sticky Aim", false).setDescription("Aims at the last attacked player");
   private final BooleanSetting onlyWeapon = new BooleanSetting("Only Weapon", true);
   private final BooleanSetting onLeftClick = new BooleanSetting("On Left Click", false).setDescription("Only gets triggered if holding down left click");
   private final ModeSetting<AimAssist.AimMode> aimAt = new ModeSetting<>("Aim At", AimAssist.AimMode.Head, AimAssist.AimMode.class);
   private final BooleanSetting stopAtTargetVertical = new BooleanSetting("Stop at Target Vert", true)
      .setDescription("Stops vertically assisting if already aiming at the entity, helps bypass anti-cheat");
   private final BooleanSetting stopAtTargetHorizontal = new BooleanSetting("Stop at Target Horiz", false)
      .setDescription("Stops horizontally assisting if already aiming at the entity, helps bypass anti-cheat");
   private final NumberSetting radius = new NumberSetting("Radius", 0.1, 6.0, 5.0, 0.1);
   private final BooleanSetting seeOnly = new BooleanSetting("See Only", true);
   private final BooleanSetting lookAtNearest = new BooleanSetting("Look at Nearest", false);
   private final NumberSetting fov = new NumberSetting("FOV", 5.0, 360.0, 180.0, 1.0);
   private final MinMaxSetting pitchSpeed = new MinMaxSetting("Vertical Speed", 0.0, 10.0, 0.1, 2.0, 4.0);
   private final MinMaxSetting yawSpeed = new MinMaxSetting("Horizontal Speed", 0.0, 10.0, 0.1, 2.0, 4.0);
   private final NumberSetting speedChange = new NumberSetting("Speed Delay", 0.0, 1000.0, 250.0, 1.0)
      .setDescription("Time in milliseconds to wait after resetting random speed");
   private final NumberSetting randomization = new NumberSetting("Chance", 0.0, 100.0, 50.0, 1.0);
   private final BooleanSetting yawAssist = new BooleanSetting("Horizontal", true);
   private final BooleanSetting pitchAssist = new BooleanSetting("Vertical", true);
   private final NumberSetting waitFor = new NumberSetting("Wait on Move", 0.0, 1000.0, 0.0, 1.0)
      .setDescription("After you move your mouse aim assist will stop working for the selected amount of time");
   private final ModeSetting<AimAssist.LerpMode> lerp = new ModeSetting<>("Lerp", AimAssist.LerpMode.Normal, AimAssist.LerpMode.class)
      .setDescription("Linear interpolation to use to rotate");
   private final ModeSetting<AimAssist.PosMode> posMode = new ModeSetting<>("Pos mode", AimAssist.PosMode.Normal, AimAssist.PosMode.class)
      .setDescription("Precision of the target position");
   private final TimerUtils timer = new TimerUtils();
   private final TimerUtils resetSpeed = new TimerUtils();
   private boolean move;
   private float pitch;
   private float yaw;

   public AimAssist() {
      super("Aim Assist", "Automatically aims at players for you", -1, Category.COMBAT);
      this.addSettings(
         this.stickyAim,
         this.onlyWeapon,
         this.onLeftClick,
         this.aimAt,
         this.stopAtTargetVertical,
         this.stopAtTargetHorizontal,
         this.radius,
         this.seeOnly,
         this.lookAtNearest,
         this.fov,
         this.pitchSpeed,
         this.yawSpeed,
         this.speedChange,
         this.randomization,
         this.yawAssist,
         this.pitchAssist,
         this.waitFor,
         this.lerp,
         this.posMode
      );
   }

   @Override
   public void onEnable() {
      this.move = true;
      this.pitch = this.pitchSpeed.getRandomValueFloat();
      this.yaw = this.yawSpeed.getRandomValueFloat();
      this.eventManager.add(HudListener.class, this);
      this.eventManager.add(MouseMoveListener.class, this);
      this.timer.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(HudListener.class, this);
      this.eventManager.remove(MouseMoveListener.class, this);
      super.onDisable();
   }

   @Override
   public void onRenderHud(HudListener.HudEvent event) {
      if (this.timer.delay(this.waitFor.getValueFloat()) && !this.move) {
         this.move = true;
         this.timer.reset();
      }

      if (this.mc.field_1724 != null && this.mc.field_1755 == null) {
         if (!this.onlyWeapon.getValue() || WorldUtils.isWeapon(this.mc.field_1724.method_6047())) {
            if (!this.onLeftClick.getValue() || GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 0) == 1) {
               class_1657 target = WorldUtils.findNearestPlayer(this.mc.field_1724, this.radius.getValueFloat(), this.seeOnly.getValue(), true);
               if (this.stickyAim.getValue()
                  && this.mc.field_1724.method_6052() instanceof class_1657 player
                  && player.method_5739(this.mc.field_1724) < this.radius.getValue()) {
                  target = player;
               }

               if (target != null) {
                  if (this.resetSpeed.delay(this.speedChange.getValueFloat())) {
                     this.pitch = this.pitchSpeed.getRandomValueFloat();
                     this.yaw = this.yawSpeed.getRandomValueFloat();
                     this.resetSpeed.reset();
                  }

                  class_243 targetPos = this.posMode.isMode(AimAssist.PosMode.Normal) ? target.method_73189() : target.method_30950(RenderUtils.tickProgress());
                  if (this.aimAt.isMode(AimAssist.AimMode.Chest)) {
                     targetPos = targetPos.method_1031(0.0, -0.5, 0.0);
                  } else if (this.aimAt.isMode(AimAssist.AimMode.Legs)) {
                     targetPos = targetPos.method_1031(0.0, -1.2, 0.0);
                  }

                  if (this.lookAtNearest.getValue()) {
                     double offsetX = this.mc.field_1724.method_23317() - target.method_23317() > 0.0 ? 0.29 : -0.29;
                     double offsetZ = this.mc.field_1724.method_23321() - target.method_23321() > 0.0 ? 0.29 : -0.29;
                     targetPos = targetPos.method_1031(offsetX, 0.0, offsetZ);
                  }

                  Rotation rotation = RotationUtils.getDirection(this.mc.field_1724, targetPos);
                  double angleToRotation = RotationUtils.getAngleToRotation(rotation);
                  if (!(angleToRotation > this.fov.getValueInt() / 2.0)) {
                     float yawStrength = this.yaw / 50.0F;
                     float pitchStrength = this.pitch / 50.0F;
                     float yaw = this.mc.field_1724.method_36454();
                     float pitch = this.mc.field_1724.method_36455();
                     if (this.lerp.isMode(AimAssist.LerpMode.Smoothstep)) {
                        yaw = (float)this.smoothStepLerp(yawStrength, this.mc.field_1724.method_36454(), (float)rotation.yaw());
                        pitch = (float)this.smoothStepLerp(pitchStrength, this.mc.field_1724.method_36455(), (float)rotation.pitch());
                     }

                     if (this.lerp.isMode(AimAssist.LerpMode.Normal)) {
                        yaw = this.lerp(yawStrength, this.mc.field_1724.method_36454(), (float)rotation.yaw());
                        pitch = this.lerp(pitchStrength, this.mc.field_1724.method_36455(), (float)rotation.pitch());
                     }

                     if (this.lerp.isMode(AimAssist.LerpMode.EaseOut)) {
                        yaw = (float)easeOutBackDegrees(this.mc.field_1724.method_36454(), rotation.yaw(), yawStrength * RenderUtils.frameDelta());
                        pitch = (float)easeOutBackDegrees(this.mc.field_1724.method_36455(), rotation.pitch(), pitchStrength * RenderUtils.frameDelta());
                     }

                     if (MathUtils.randomInt(1, 100) <= this.randomization.getValueInt() && this.move) {
                        if (this.yawAssist.getValue()) {
                           if (this.stopAtTargetHorizontal.getValue()
                              && WorldUtils.getHitResult(this.radius.getValue()) instanceof class_3966 hitResult
                              && hitResult.method_17782() == target) {
                              return;
                           }

                           this.mc.field_1724.method_36456(yaw);
                        }

                        if (this.pitchAssist.getValue()) {
                           if (this.stopAtTargetVertical.getValue()
                              && WorldUtils.getHitResult(this.radius.getValue()) instanceof class_3966 hitResult
                              && hitResult.method_17782() == target) {
                              return;
                           }

                           this.mc.field_1724.method_36457(pitch);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public float lerp(float delta, float start, float end) {
      return start + class_3532.method_15393(end - start) * delta;
   }

   public static double easeOutBackDegrees(double start, double end, float speed) {
      double c1 = 1.70158;
      double c3 = 2.70158;
      double x = 1.0 - Math.pow(1.0 - speed, 3.0);
      return start + class_3532.method_15338(end - start) * (1.0 + c3 * Math.pow(x - 1.0, 3.0) + c1 * Math.pow(x - 1.0, 2.0));
   }

   public double smoothStepLerp(double delta, double start, double end) {
      delta = Math.max(0.0, Math.min(1.0, delta));
      double t = delta * delta * (3.0 - 2.0 * delta);
      return start + class_3532.method_15338(end - start) * t;
   }

   @Override
   public void onMouseMove(MouseMoveListener.MouseMoveEvent event) {
      this.move = false;
      this.timer.reset();
   }

   public enum AimMode {
      Head,
      Chest,
      Legs;
   }

   public enum LerpMode {
      Normal,
      Smoothstep,
      EaseOut;
   }

   public enum PosMode {
      Normal,
      Lerped;
   }
}
