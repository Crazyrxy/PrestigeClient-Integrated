package juxxl.prestige.mixin;

import juxxl.prestige.event.EventManager;
import juxxl.prestige.event.events.ButtonListener;
import juxxl.prestige.event.events.MouseMoveListener;
import juxxl.prestige.event.events.MouseUpdateListener;
import net.minecraft.class_310;
import net.minecraft.class_312;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_312.class)
public abstract class MouseMixin {
   @Shadow
   @Final
   private class_310 field_1779;
   @Unique
   private double prestige$lastMouseX;
   @Unique
   private double prestige$lastMouseY;
   @Unique
   private boolean prestige$initialized;
   @Unique
   private int[] prestige$buttonStates = new int[8];

   @Shadow
   public abstract double method_1603();

   @Shadow
   public abstract double method_1604();

   @Inject(method = "method_55793", at = @At("TAIL"))
   private void onMouseUpdate(CallbackInfo ci) {
      if (this.prestige$buttonStates == null) {
         this.prestige$buttonStates = new int[8];
      }

      EventManager.fire(new MouseUpdateListener.MouseUpdateEvent());
      long window = this.field_1779.method_22683().method_4490();
      double x = this.method_1603();
      double y = this.method_1604();
      if (!this.prestige$initialized) {
         this.prestige$initialized = true;
         this.prestige$lastMouseX = x;
         this.prestige$lastMouseY = y;

         for (int button = 0; button < this.prestige$buttonStates.length; button++) {
            this.prestige$buttonStates[button] = GLFW.glfwGetMouseButton(window, button);
         }
      } else {
         if (x != this.prestige$lastMouseX || y != this.prestige$lastMouseY) {
            this.prestige$lastMouseX = x;
            this.prestige$lastMouseY = y;
            EventManager.fire(new MouseMoveListener.MouseMoveEvent(window, x, y));
         }

         for (int button = 0; button < this.prestige$buttonStates.length; button++) {
            int state = GLFW.glfwGetMouseButton(window, button);
            if (state != this.prestige$buttonStates[button]) {
               this.prestige$buttonStates[button] = state;
               EventManager.fire(new ButtonListener.ButtonEvent(button, window, state));
            }
         }
      }
   }
}
