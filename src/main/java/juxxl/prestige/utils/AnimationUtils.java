package juxxl.prestige.utils;

import juxxl.prestige.module.modules.client.ClickGUI;

public final class AnimationUtils {
   private double value;
   private final double originalValue;
   private double endValue;

   public AnimationUtils(double value) {
      this.value = value;
      this.originalValue = value;
   }

   public double animate(double delta, double end) {
      this.endValue = end;
      if (ClickGUI.animationMode.isMode(ClickGUI.AnimationMode.Normal)) {
         this.value = MathUtils.goodLerp((float)delta, this.value, end);
      } else if (ClickGUI.animationMode.isMode(ClickGUI.AnimationMode.Positive)) {
         this.value = MathUtils.smoothStepLerp(delta, this.value, end);
      } else if (ClickGUI.animationMode.isMode(ClickGUI.AnimationMode.Off)) {
         this.value = end;
      }

      return this.value;
   }

   public double getValue() {
      return this.value;
   }

   public double getOriginalValue() {
      return this.originalValue;
   }

   public double getEndValue() {
      return this.endValue;
   }

   public double getAnimationProgress() {
      return this.value / this.endValue;
   }

   public void reset(double delta) {
      this.value = MathUtils.smoothStepLerp(delta, this.value, this.originalValue);
   }
}
