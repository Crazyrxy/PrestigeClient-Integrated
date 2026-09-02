package juxxl.prestige.utils;

import java.awt.Color;

public final class ColorUtils {
   public static Color getBreathingRGBColor(int increment, int alpha) {
      Color color = Color.getHSBColor((float)((System.currentTimeMillis() * 3L + increment * 175) % 7200L) / 7200.0F, 0.6F, 1.0F);
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
   }

   public static Color getMainColor(Color color, int n, int n2) {
      float[] fArray = new float[3];
      Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), fArray);
      float f = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F + (float)n / n2 * 2.0F) % 2.0F - 1.0F);
      fArray[2] = 0.25F + 0.75F * f % 2.0F;
      int rgb = Color.HSBtoRGB(fArray[0], fArray[1], fArray[2]);
      return new Color(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, color.getAlpha());
   }

   public static Color smoothColorTransition(float speed, Color toColor, Color fromColor) {
      return new Color(
         (int)MathUtils.goodLerp(speed, fromColor.getRed(), toColor.getRed()),
         (int)MathUtils.goodLerp(speed, fromColor.getGreen(), toColor.getGreen()),
         (int)MathUtils.goodLerp(speed, fromColor.getBlue(), toColor.getBlue())
      );
   }

   public static Color smoothAlphaTransition(float speed, int toAlpha, Color fromColor) {
      return new Color(fromColor.getRed(), fromColor.getGreen(), fromColor.getBlue(), (int)MathUtils.goodLerp(speed, fromColor.getAlpha(), toAlpha));
   }
}
