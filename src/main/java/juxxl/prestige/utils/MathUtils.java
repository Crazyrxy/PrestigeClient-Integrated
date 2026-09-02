package juxxl.prestige.utils;

import java.util.Random;

public final class MathUtils {
   public static Random random = new Random(System.currentTimeMillis());

   public static double roundToDecimal(double n, double point) {
      return point * Math.round(n / point);
   }

   public static int randomInt(int start, int bound) {
      return random.nextInt(start, bound);
   }

   public static double smoothStepLerp(double delta, double start, double end) {
      delta = Math.max(0.0, Math.min(1.0, delta));
      double t = delta * delta * (3.0 - 2.0 * delta);
      return start + (end - start) * t;
   }

   public static double goodLerp(float delta, double start, double end) {
      int step = (int)Math.ceil(Math.abs(end - start) * delta);
      return start < end ? Math.min(start + step, end) : Math.max(start - step, end);
   }
}
