package juxxl.prestige.util.render.nanovg;

import java.awt.Color;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

public class NanoVGDrawing {
   private static final float INV_255 = 0.003921569F;
   private static final ThreadLocal<NVGColor> COLOR_1 = ThreadLocal.withInitial(NVGColor::create);
   private static final ThreadLocal<NVGColor> COLOR_2 = ThreadLocal.withInitial(NVGColor::create);
   private static final ThreadLocal<NVGPaint> PAINT = ThreadLocal.withInitial(NVGPaint::create);

   private static NVGColor getColor1(Color color) {
      NVGColor c = COLOR_1.get();
      c.r(color.getRed() * 0.003921569F);
      c.g(color.getGreen() * 0.003921569F);
      c.b(color.getBlue() * 0.003921569F);
      c.a(color.getAlpha() * 0.003921569F);
      return c;
   }

   private static NVGColor getColor2(Color color) {
      NVGColor c = COLOR_2.get();
      c.r(color.getRed() * 0.003921569F);
      c.g(color.getGreen() * 0.003921569F);
      c.b(color.getBlue() * 0.003921569F);
      c.a(color.getAlpha() * 0.003921569F);
      return c;
   }

   private static NVGColor getColor1Int(int argb) {
      NVGColor c = COLOR_1.get();
      c.r((argb >>> 16 & 0xFF) * 0.003921569F);
      c.g((argb >>> 8 & 0xFF) * 0.003921569F);
      c.b((argb & 0xFF) * 0.003921569F);
      c.a((argb >>> 24 & 0xFF) * 0.003921569F);
      return c;
   }

   public static void drawRoundedRect(float x, float y, float width, float height, float radius, Color color) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRect(vg, x, y, width, height, radius);
         NanoVG.nvgFillColor(vg, getColor1(color));
         NanoVG.nvgFill(vg);
      }
   }

   public static void drawRoundedRect(float x, float y, float width, float height, float radius, int argb) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRect(vg, x, y, width, height, radius);
         NanoVG.nvgFillColor(vg, getColor1Int(argb));
         NanoVG.nvgFill(vg);
      }
   }

   public static void drawRoundedRectVarying(
      float x, float y, float width, float height, float radTopLeft, float radTopRight, float radBottomRight, float radBottomLeft, Color color
   ) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRectVarying(vg, x, y, width, height, radTopLeft, radTopRight, radBottomRight, radBottomLeft);
         NanoVG.nvgFillColor(vg, getColor1(color));
         NanoVG.nvgFill(vg);
      }
   }

   public static void drawRoundedRectOutline(float x, float y, float width, float height, float radius, float strokeWidth, Color color) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRect(vg, x, y, width, height, radius);
         NanoVG.nvgStrokeColor(vg, getColor1(color));
         NanoVG.nvgStrokeWidth(vg, strokeWidth);
         NanoVG.nvgStroke(vg);
      }
   }

   public static void drawRoundedRectGradient(float x, float y, float width, float height, float radius, Color colorTop, Color colorBottom) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NVGColor c1 = getColor1(colorTop);
         NVGColor c2 = getColor2(colorBottom);
         NVGPaint p = NanoVG.nvgLinearGradient(vg, x, y, x, y + height, c1, c2, PAINT.get());
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRect(vg, x, y, width, height, radius);
         NanoVG.nvgFillPaint(vg, p);
         NanoVG.nvgFill(vg);
      }
   }

   public static void drawCircle(float x, float y, float radius, Color color) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgCircle(vg, x, y, radius);
         NanoVG.nvgFillColor(vg, getColor1(color));
         NanoVG.nvgFill(vg);
      }
   }

   public static void drawRect(float x, float y, float width, float height, Color color) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRect(vg, x, y, width, height);
         NanoVG.nvgFillColor(vg, getColor1(color));
         NanoVG.nvgFill(vg);
      }
   }

   public static void drawRectOutline(float x, float y, float width, float height, float strokeWidth, Color color) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRect(vg, x, y, width, height);
         NanoVG.nvgStrokeColor(vg, getColor1(color));
         NanoVG.nvgStrokeWidth(vg, strokeWidth);
         NanoVG.nvgStroke(vg);
      }
   }

   public static void drawLine(float x1, float y1, float x2, float y2, float strokeWidth, Color color) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgMoveTo(vg, x1, y1);
         NanoVG.nvgLineTo(vg, x2, y2);
         NanoVG.nvgStrokeColor(vg, getColor1(color));
         NanoVG.nvgStrokeWidth(vg, strokeWidth);
         NanoVG.nvgStroke(vg);
      }
   }

   public static void drawRoundedRectWithShadow(
      float x, float y, float width, float height, float radius, Color color, Color shadowColor, float shadowBlur, float shadowSpread
   ) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NVGColor shadowC = getColor1(shadowColor);
         NVGColor transparentC = getColor2(new Color(0, 0, 0, 0));
         NVGPaint shadowPaint = NanoVG.nvgBoxGradient(vg, x, y + shadowSpread, width, height, radius, shadowBlur, shadowC, transparentC, PAINT.get());
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRect(vg, x - shadowBlur, y - shadowBlur, width + shadowBlur * 2.0F, height + shadowBlur * 2.0F + shadowSpread);
         NanoVG.nvgRoundedRect(vg, x, y, width, height, radius);
         NanoVG.nvgPathWinding(vg, 2);
         NanoVG.nvgFillPaint(vg, shadowPaint);
         NanoVG.nvgFill(vg);
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRect(vg, x, y, width, height, radius);
         NanoVG.nvgFillColor(vg, getColor1(color));
         NanoVG.nvgFill(vg);
      }
   }

   public static void drawQuad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, Color color) {
      if (NanoVGFrameManager.isInFrame()) {
         long vg = NanoVGContext.getHandle();
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgMoveTo(vg, x1, y1);
         NanoVG.nvgLineTo(vg, x2, y2);
         NanoVG.nvgLineTo(vg, x3, y3);
         NanoVG.nvgLineTo(vg, x4, y4);
         NanoVG.nvgClosePath(vg);
         NanoVG.nvgFillColor(vg, getColor1(color));
         NanoVG.nvgFill(vg);
      }
   }
}
