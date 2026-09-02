package juxxl.prestige.util.render.nanovg;

import java.awt.Color;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class NanoVGText {
   private static final float INV_255 = 0.003921569F;
   private static final ThreadLocal<NVGColor> COLOR = ThreadLocal.withInitial(NVGColor::create);
   private static final ThreadLocal<float[]> BOUNDS_BUFFER = ThreadLocal.withInitial(() -> new float[4]);

   private static NVGColor getColor(Color color) {
      NVGColor c = COLOR.get();
      c.r(color.getRed() * 0.003921569F);
      c.g(color.getGreen() * 0.003921569F);
      c.b(color.getBlue() * 0.003921569F);
      c.a(color.getAlpha() * 0.003921569F);
      return c;
   }

   public static void drawText(String text, float x, float y, float size, Color color, boolean bold) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgFontFaceId(vg, NanoVGFontManager.getFontId(bold));
         NanoVG.nvgFontSize(vg, size);
         NanoVG.nvgTextAlign(vg, 9);
         NanoVG.nvgFillColor(vg, getColor(color));
         NanoVG.nvgText(vg, x, y, text);
      }
   }

   public static void drawText(String text, float x, float y, float size, Color color) {
      drawText(text, x, y, size, color, false);
   }

   public static void drawTextWithFont(String text, float x, float y, float size, Color color, int fontId) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgFontFaceId(vg, fontId);
         NanoVG.nvgFontSize(vg, size);
         NanoVG.nvgTextAlign(vg, 9);
         NanoVG.nvgFillColor(vg, getColor(color));
         NanoVG.nvgText(vg, x, y, text);
      }
   }

   public static void drawIcon(String icon, float x, float y, float size, Color color) {
      if (NanoVGFrameManager.isInFrame()) {
         int fontId = NanoVGFontManager.getIconFont() > 0 ? NanoVGFontManager.getIconFont() : NanoVGFontManager.getRegularFont();
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgFontFaceId(vg, fontId);
         NanoVG.nvgFontSize(vg, size);
         NanoVG.nvgTextAlign(vg, 18);
         NanoVG.nvgFillColor(vg, getColor(color));
         NanoVG.nvgText(vg, x, y, icon);
      }
   }

   public static float getTextWidth(String text, float size, boolean bold) {
      long vg = NanoVGContext.getHandle();
      if (vg == 0L) {
         return 0.0F;
      }

      float[] bounds = BOUNDS_BUFFER.get();
      NanoVG.nvgFontFaceId(vg, NanoVGFontManager.getFontId(bold));
      NanoVG.nvgFontSize(vg, size);
      NanoVG.nvgTextBounds(vg, 0.0F, 0.0F, text, bounds);
      return bounds[2] - bounds[0];
   }

   public static float getTextWidth(String text, float size) {
      return getTextWidth(text, size, false);
   }

   public static float getTextWidthWithFont(String text, float size, int fontId) {
      long vg = NanoVGContext.getHandle();
      if (vg == 0L) {
         return 0.0F;
      }

      float[] bounds = BOUNDS_BUFFER.get();
      NanoVG.nvgFontFaceId(vg, fontId);
      NanoVG.nvgFontSize(vg, size);
      NanoVG.nvgTextBounds(vg, 0.0F, 0.0F, text, bounds);
      return bounds[2] - bounds[0];
   }

   public static float getTextHeight(float size) {
      return size;
   }
}
