package juxxl.prestige.module.modules.misc;

import juxxl.prestige.event.events.CameraUpdateListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.mixin.KeyBindingAccessor;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.NumberSetting;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_3532;
import org.lwjgl.glfw.GLFW;

public final class Freecam extends Module implements TickListener, CameraUpdateListener {
   private final NumberSetting speed = new NumberSetting("Speed", 1.0, 10.0, 1.0, 1.0);
   public class_243 oldPos;
   public class_243 pos;

   public Freecam() {
      super("Freecam", "Lets you move freely around the world without actually moving", -1, Category.MISC);
      this.addSettings(this.speed);
      this.oldPos = class_243.field_1353;
      this.pos = class_243.field_1353;
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(CameraUpdateListener.class, this);
      if (this.mc.field_1687 != null) {
         this.oldPos = this.pos = this.mc.field_1724.method_33571();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(CameraUpdateListener.class, this);
      if (this.mc.field_1687 != null) {
         this.mc.field_1724.method_18799(class_243.field_1353);
         this.mc.field_1769.method_3279();
      }

      super.onDisable();
   }

   @Override
   public void onTick() {
      if (this.mc.field_1755 == null) {
         this.mc.field_1690.field_1904.method_23481(false);
         this.mc.field_1690.field_1886.method_23481(false);
         this.mc.field_1690.field_1894.method_23481(false);
         this.mc.field_1690.field_1881.method_23481(false);
         this.mc.field_1690.field_1913.method_23481(false);
         this.mc.field_1690.field_1849.method_23481(false);
         this.mc.field_1690.field_1903.method_23481(false);
         this.mc.field_1690.field_1832.method_23481(false);
         float f = (float) (Math.PI / 180.0);
         float f2 = (float) Math.PI;
         class_243 vec3d = new class_243(
            -class_3532.method_15374(-this.mc.field_1724.method_36454() * f - f2), 0.0, -class_3532.method_15362(-this.mc.field_1724.method_36454() * f - f2)
         );
         class_243 vec3d2 = new class_243(0.0, 1.0, 0.0);
         class_243 vec3d3 = vec3d2.method_1036(vec3d);
         class_243 vec3d4 = vec3d.method_1036(vec3d2);
         class_243 vec3d5 = class_243.field_1353;
         class_304 keyBinding = this.mc.field_1690.field_1894;
         if (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), ((KeyBindingAccessor)keyBinding).getBoundKey().method_1444()) == 1) {
            vec3d5 = vec3d5.method_1019(vec3d);
         }

         class_304 keyBinding2 = this.mc.field_1690.field_1881;
         if (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), ((KeyBindingAccessor)keyBinding2).getBoundKey().method_1444()) == 1) {
            vec3d5 = vec3d5.method_1020(vec3d);
         }

         class_304 keyBinding3 = this.mc.field_1690.field_1913;
         if (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), ((KeyBindingAccessor)keyBinding3).getBoundKey().method_1444()) == 1) {
            vec3d5 = vec3d5.method_1019(vec3d3);
         }

         class_304 keyBinding4 = this.mc.field_1690.field_1849;
         if (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), ((KeyBindingAccessor)keyBinding4).getBoundKey().method_1444()) == 1) {
            vec3d5 = vec3d5.method_1019(vec3d4);
         }

         class_304 keyBinding5 = this.mc.field_1690.field_1903;
         if (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), ((KeyBindingAccessor)keyBinding5).getBoundKey().method_1444()) == 1) {
            vec3d5 = vec3d5.method_1031(0.0, this.speed.getValue(), 0.0);
         }

         class_304 keyBinding6 = this.mc.field_1690.field_1832;
         if (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), ((KeyBindingAccessor)keyBinding6).getBoundKey().method_1444()) == 1) {
            vec3d5 = vec3d5.method_1031(0.0, -this.speed.getValue(), 0.0);
         }

         class_304 keyBinding7 = this.mc.field_1690.field_1867;
         vec3d5 = vec3d5.method_1029()
            .method_1021(
               this.speed.getValue()
                  * (GLFW.glfwGetKey(this.mc.method_22683().method_4490(), ((KeyBindingAccessor)keyBinding7).getBoundKey().method_1444()) == 1 ? 2 : 1)
            );
         this.oldPos = this.pos;
         this.pos = this.pos.method_1019(vec3d5);
      }
   }

   @Override
   public void onCameraUpdate(CameraUpdateListener.CameraUpdateEvent event) {
      float tickDelta = this.mc.method_61966().method_60637(true);
      if (this.mc.field_1755 == null) {
         event.setX(class_3532.method_16436(tickDelta, this.oldPos.field_1352, this.pos.field_1352));
         event.setY(class_3532.method_16436(tickDelta, this.oldPos.field_1351, this.pos.field_1351));
         event.setZ(class_3532.method_16436(tickDelta, this.oldPos.field_1350, this.pos.field_1350));
      }
   }
}
