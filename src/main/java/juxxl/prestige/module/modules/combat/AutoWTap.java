package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.HudListener;
import juxxl.prestige.event.events.PacketSendListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.MinMaxSetting;
import juxxl.prestige.utils.TimerUtils;
import net.minecraft.class_1268;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_2824.class_5908;
import org.lwjgl.glfw.GLFW;

public final class AutoWTap extends Module implements PacketSendListener, HudListener {
   private final MinMaxSetting delay = new MinMaxSetting("Delay", 0.0, 1000.0, 1.0, 230.0, 270.0);
   private final BooleanSetting inAir = new BooleanSetting("In Air", false).setDescription("Whether it should W tap in air");
   private final TimerUtils sprintTimer = new TimerUtils();
   private final TimerUtils tapTimer = new TimerUtils();
   private boolean holdingForward;
   private boolean sprinting;
   private int currentDelay;
   private boolean jumpedWhileHitting;

   public AutoWTap() {
      super("Auto WTap", "Automatically W Taps for you so the opponent takes more knockback", -1, Category.COMBAT);
      this.addSettings(this.delay, this.inAir);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(PacketSendListener.class, this);
      this.eventManager.add(HudListener.class, this);
      this.currentDelay = this.delay.getRandomValueInt();
      this.jumpedWhileHitting = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(PacketSendListener.class, this);
      this.eventManager.remove(HudListener.class, this);
      super.onDisable();
   }

   @Override
   public void onRenderHud(HudListener.HudEvent event) {
      if (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), 87) != 1) {
         this.sprinting = false;
         this.holdingForward = false;
      } else if (this.inAir.getValue() || this.mc.field_1724.method_24828()) {
         if (this.mc.field_1724.method_24828()) {
            this.jumpedWhileHitting = false;
         }

         if (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), 32) != 1 || this.inAir.getValue() || !this.holdingForward && !this.sprinting) {
            if (this.holdingForward && this.tapTimer.delay(1.0F)) {
               this.mc.field_1690.field_1894.method_23481(false);
               this.sprintTimer.reset();
               this.sprinting = true;
               this.holdingForward = false;
            }

            if (this.sprinting && this.sprintTimer.delay(this.currentDelay)) {
               this.mc.field_1690.field_1894.method_23481(true);
               this.sprinting = false;
               this.currentDelay = this.delay.getRandomValueInt();
            }
         } else {
            this.mc.field_1690.field_1894.method_23481(true);
            this.holdingForward = false;
            this.sprinting = false;
         }
      }
   }

   @Override
   public void onPacketSend(PacketSendListener.PacketSendEvent event) {
      if (event.packet instanceof class_2824 packet) {
         packet.method_34209(new class_5908() {
            public void method_34219(class_1268 hand) {
            }

            public void method_34220(class_1268 hand, class_243 pos) {
            }

            public void method_34218() {
               if (GLFW.glfwGetKey(AutoWTap.this.mc.method_22683().method_4490(), 32) == 1 && !AutoWTap.this.inAir.getValue()) {
                  AutoWTap.this.jumpedWhileHitting = true;
               }

               if (AutoWTap.this.inAir.getValue() || AutoWTap.this.mc.field_1724.method_24828()) {
                  if (!AutoWTap.this.jumpedWhileHitting && AutoWTap.this.mc.field_1690.field_1894.method_1434() && AutoWTap.this.mc.field_1724.method_5624()) {
                     AutoWTap.this.sprintTimer.reset();
                     AutoWTap.this.holdingForward = true;
                  }
               }
            }
         });
      }
   }
}
