package juxxl.prestige.nanovg;

import java.awt.Color;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

public class NanoVGWidgets {
   private static float[] DEFAULT_ACCENT = new float[]{0.35F, 0.55F, 1.0F};
   private static final ThreadLocal<NVGColor> COLOR_CACHE_1 = ThreadLocal.withInitial(NVGColor::create);
   private static final ThreadLocal<NVGColor> COLOR_CACHE_2 = ThreadLocal.withInitial(NVGColor::create);
   private static final ThreadLocal<NVGPaint> PAINT_CACHE = ThreadLocal.withInitial(NVGPaint::create);
   private static final ThreadLocal<float[]> BOUNDS = ThreadLocal.withInitial(() -> new float[4]);

   public static NVGColor getColor1() {
      return COLOR_CACHE_1.get();
   }

   public static NVGColor getColor2() {
      return COLOR_CACHE_2.get();
   }

   public static NVGPaint getPaint() {
      return PAINT_CACHE.get();
   }

   public static void setAccentColor(float r, float g, float b) {
      DEFAULT_ACCENT = new float[]{r, g, b};
   }

   public static void setAccentColor(Color color) {
      DEFAULT_ACCENT = new float[]{color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F};
   }

   public static float[] theme() {
      return DEFAULT_ACCENT;
   }

   public static void c(NVGColor col, float r, float g, float b, float a) {
      NanoVG.nvgRGBAf(r, g, b, a, col);
   }

   public static void cInt(NVGColor col, int argb, float alphaMul) {
      float a = (argb >> 24 & 0xFF) / 255.0F * alphaMul;
      float r = (argb >> 16 & 0xFF) / 255.0F;
      float g = (argb >> 8 & 0xFF) / 255.0F;
      float b = (argb & 0xFF) / 255.0F;
      c(col, r, g, b, a);
   }

   public static void pill(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean hover, float alpha) {
      pill(vg, col, x, y, w, h, label, hover, alpha, "default");
   }

   public static void pill(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean hover, float alpha, String fontName) {
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, h * 0.5F);
      c(col, 1.0F, 1.0F, 1.0F, (hover ? 0.08F : 0.03F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      if (hover) {
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRect(vg, x, y, w, h, h * 0.5F);
         c(col, 1.0F, 1.0F, 1.0F, 0.06F * alpha);
         NanoVG.nvgStrokeColor(vg, col);
         NanoVG.nvgStrokeWidth(vg, 1.0F);
         NanoVG.nvgStroke(vg);
      }

      NanoVG.nvgFontFace(vg, fontName);
      NanoVG.nvgFontSize(vg, 10.0F);
      NanoVG.nvgTextAlign(vg, 18);
      c(col, 1.0F, 1.0F, 1.0F, (hover ? 0.85F : 0.4F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgText(vg, x + w * 0.5F, y + h * 0.5F, label);
   }

   public static void accentPill(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean hover, float alpha) {
      accentPill(vg, col, x, y, w, h, label, hover, alpha, DEFAULT_ACCENT);
   }

   public static void accentPill(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean hover, float alpha, float[] accentRgb) {
      float[] t = accentRgb != null ? accentRgb : DEFAULT_ACCENT;
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, h * 0.5F);
      c(col, t[0], t[1], t[2], (hover ? 0.4F : 0.2F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, h * 0.5F);
      c(col, t[0], t[1], t[2], (hover ? 0.3F : 0.1F) * alpha);
      NanoVG.nvgStrokeColor(vg, col);
      NanoVG.nvgStrokeWidth(vg, 1.0F);
      NanoVG.nvgStroke(vg);
      NanoVG.nvgFontFace(vg, "default");
      NanoVG.nvgFontSize(vg, 10.0F);
      NanoVG.nvgTextAlign(vg, 18);
      c(col, 1.0F, 1.0F, 1.0F, (hover ? 0.95F : 0.7F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgText(vg, x + w * 0.5F, y + h * 0.5F, label);
   }

   public static void dangerPill(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean hover, float alpha) {
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, h * 0.5F);
      c(col, 1.0F, 0.3F, 0.3F, (hover ? 0.3F : 0.1F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      NanoVG.nvgFontFace(vg, "default");
      NanoVG.nvgFontSize(vg, 10.0F);
      NanoVG.nvgTextAlign(vg, 18);
      c(col, 1.0F, 0.4F, 0.4F, (hover ? 0.95F : 0.5F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgText(vg, x + w * 0.5F, y + h * 0.5F, label);
   }

   public static void rect(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean hover, float alpha) {
      rect(vg, col, x, y, w, h, label, hover, alpha, 5.0F);
   }

   public static void rect(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean hover, float alpha, float radius) {
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, radius);
      c(col, 1.0F, 1.0F, 1.0F, (hover ? 0.08F : 0.03F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      if (hover) {
         float[] t = DEFAULT_ACCENT;
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRectVarying(vg, x, y, 3.0F, h, radius, 0.0F, 0.0F, radius);
         c(col, t[0], t[1], t[2], 0.7F * alpha);
         NanoVG.nvgFillColor(vg, col);
         NanoVG.nvgFill(vg);
      }

      NanoVG.nvgFontFace(vg, "default");
      NanoVG.nvgFontSize(vg, 10.0F);
      NanoVG.nvgTextAlign(vg, 18);
      c(col, 1.0F, 1.0F, 1.0F, (hover ? 0.85F : 0.4F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgText(vg, x + w * 0.5F, y + h * 0.5F, label);
   }

   public static void accentRect(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean hover, float alpha) {
      accentRect(vg, col, x, y, w, h, label, hover, alpha, DEFAULT_ACCENT);
   }

   public static void accentRect(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean hover, float alpha, float[] accentRgb) {
      float[] t = accentRgb != null ? accentRgb : DEFAULT_ACCENT;
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, 5.0F);
      c(col, t[0], t[1], t[2], (hover ? 0.35F : 0.18F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      NanoVG.nvgFontFace(vg, "default");
      NanoVG.nvgFontSize(vg, 10.0F);
      NanoVG.nvgTextAlign(vg, 18);
      c(col, 1.0F, 1.0F, 1.0F, (hover ? 0.95F : 0.65F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgText(vg, x + w * 0.5F, y + h * 0.5F, label);
   }

   public static void icon(long vg, NVGColor col, float cx, float cy, float radius, String iconChar, boolean hover, float alpha) {
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgCircle(vg, cx, cy, radius);
      c(col, 1.0F, 1.0F, 1.0F, (hover ? 0.1F : 0.04F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      if (hover) {
         float[] t = DEFAULT_ACCENT;
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgCircle(vg, cx, cy, radius);
         c(col, t[0], t[1], t[2], 0.2F * alpha);
         NanoVG.nvgStrokeColor(vg, col);
         NanoVG.nvgStrokeWidth(vg, 1.5F);
         NanoVG.nvgStroke(vg);
      }

      if (NanoVGService.hasIconFont()) {
         NanoVG.nvgFontFace(vg, "icons");
      } else {
         NanoVG.nvgFontFace(vg, "default");
      }

      NanoVG.nvgFontSize(vg, radius * 0.9F);
      NanoVG.nvgTextAlign(vg, 18);
      c(col, 1.0F, 1.0F, 1.0F, (hover ? 0.85F : 0.4F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgText(vg, cx, cy, iconChar);
      NanoVG.nvgFontFace(vg, "default");
   }

   public static void toggle(long vg, NVGColor col, float x, float y, boolean enabled, boolean hover, float alpha) {
      toggle(vg, col, x, y, enabled, hover, alpha, DEFAULT_ACCENT);
   }

   public static void toggle(long vg, NVGColor col, float x, float y, boolean enabled, boolean hover, float alpha, float[] accentRgb) {
      float w = 26.0F;
      float h = 13.0F;
      float[] t = accentRgb != null ? accentRgb : DEFAULT_ACCENT;
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, h * 0.5F);
      if (enabled) {
         c(col, t[0], t[1], t[2], (hover ? 1.0F : 0.9F) * alpha);
      } else {
         c(col, 0.15F, 0.14F, 0.2F, 0.95F * alpha);
      }

      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      float knobR = (h - 3.0F) * 0.5F;
      float knobX = enabled ? x + w - knobR - 1.8F : x + knobR + 1.8F;
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgCircle(vg, knobX, y + h * 0.5F, knobR);
      if (enabled) {
         c(col, 1.0F, 1.0F, 1.0F, 1.0F * alpha);
      } else {
         c(col, 0.5F, 0.48F, 0.58F, 1.0F * alpha);
      }

      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
   }

   public static void slider(long vg, NVGColor col, float x, float y, float w, float h, float progress, boolean hover, float alpha) {
      slider(vg, col, x, y, w, h, progress, hover, alpha, DEFAULT_ACCENT);
   }

   public static void slider(long vg, NVGColor col, float x, float y, float w, float h, float progress, boolean hover, float alpha, float[] accentRgb) {
      float[] t = accentRgb != null ? accentRgb : DEFAULT_ACCENT;
      float clampedProgress = Math.max(0.0F, Math.min(1.0F, progress));
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, h * 0.5F);
      c(col, 0.12F, 0.1F, 0.2F, 0.95F * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      if (clampedProgress > 0.0F) {
         float fillW = Math.max(h, w * clampedProgress);
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRect(vg, x, y, fillW, h, h * 0.5F);
         c(col, t[0], t[1], t[2], (hover ? 1.0F : 0.9F) * alpha);
         NanoVG.nvgFillColor(vg, col);
         NanoVG.nvgFill(vg);
      }

      float knobR = (h + 3.0F) * 0.5F;
      float knobX = x + (w - knobR * 2.0F) * clampedProgress + knobR;
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgCircle(vg, knobX, y + h * 0.5F, knobR);
      c(col, 1.0F, 1.0F, 1.0F, 1.0F * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
   }

   public static void inputField(long vg, NVGColor col, float x, float y, float w, float h, String text, String placeholder, boolean focused, float alpha) {
      float[] t = DEFAULT_ACCENT;
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, 5.0F);
      c(col, 1.0F, 1.0F, 1.0F, (focused ? 0.06F : 0.025F) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, 5.0F);
      c(col, focused ? t[0] : 1.0F, focused ? t[1] : 1.0F, focused ? t[2] : 1.0F, (focused ? 0.3F : 0.05F) * alpha);
      NanoVG.nvgStrokeColor(vg, col);
      NanoVG.nvgStrokeWidth(vg, 1.0F);
      NanoVG.nvgStroke(vg);
      NanoVG.nvgFontFace(vg, "default");
      NanoVG.nvgFontSize(vg, 10.0F);
      NanoVG.nvgTextAlign(vg, 17);
      NanoVG.nvgSave(vg);
      NanoVG.nvgIntersectScissor(vg, x + 6.0F, y, w - 12.0F, h);
      if (text.isEmpty()) {
         c(col, 1.0F, 1.0F, 1.0F, 0.18F * alpha);
         NanoVG.nvgFillColor(vg, col);
         NanoVG.nvgText(vg, x + 8.0F, y + h * 0.5F, placeholder);
      } else {
         c(col, 1.0F, 1.0F, 1.0F, 0.8F * alpha);
         NanoVG.nvgFillColor(vg, col);
         NanoVG.nvgText(vg, x + 8.0F, y + h * 0.5F, text);
      }

      NanoVG.nvgRestore(vg);
      if (focused && System.currentTimeMillis() / 530L % 2L == 0L) {
         float[] b = BOUNDS.get();
         float tw = NanoVG.nvgTextBounds(vg, 0.0F, 0.0F, text.isEmpty() ? "" : text, b);
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRect(vg, x + 8.0F + tw + 1.0F, y + 5.0F, 1.0F, h - 10.0F);
         c(col, 1.0F, 1.0F, 1.0F, 0.5F * alpha);
         NanoVG.nvgFillColor(vg, col);
         NanoVG.nvgFill(vg);
      }
   }

   public static void tab(long vg, NVGColor col, float x, float y, float w, float h, String label, boolean active, boolean hover, float alpha) {
      tab(vg, col, x, y, w, h, label, active, hover, alpha, DEFAULT_ACCENT);
   }

   public static void tab(
      long vg, NVGColor col, float x, float y, float w, float h, String label, boolean active, boolean hover, float alpha, float[] accentRgb
   ) {
      float[] t = accentRgb != null ? accentRgb : DEFAULT_ACCENT;
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, 5.0F);
      if (active) {
         c(col, t[0], t[1], t[2], 0.2F * alpha);
      } else {
         c(col, 1.0F, 1.0F, 1.0F, (hover ? 0.06F : 0.02F) * alpha);
      }

      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      if (active) {
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRect(vg, x, y, w, h, 5.0F);
         c(col, t[0], t[1], t[2], 0.25F * alpha);
         NanoVG.nvgStrokeColor(vg, col);
         NanoVG.nvgStrokeWidth(vg, 1.0F);
         NanoVG.nvgStroke(vg);
         NanoVG.nvgBeginPath(vg);
         NanoVG.nvgRoundedRectVarying(vg, x, y + h - 2.0F, w, 2.0F, 0.0F, 0.0F, 5.0F, 5.0F);
         c(col, t[0], t[1], t[2], 0.6F * alpha);
         NanoVG.nvgFillColor(vg, col);
         NanoVG.nvgFill(vg);
      }

      NanoVG.nvgFontFace(vg, "default");
      NanoVG.nvgFontSize(vg, 10.0F);
      NanoVG.nvgTextAlign(vg, 18);
      c(col, 1.0F, 1.0F, 1.0F, (active ? 0.9F : (hover ? 0.6F : 0.3F)) * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgText(vg, x + w * 0.5F, y + h * 0.5F, label);
   }

   public static void badge(long vg, NVGColor col, float x, float y, String text, float alpha, float[] accentRgb) {
      float[] t = accentRgb != null ? accentRgb : DEFAULT_ACCENT;
      NanoVG.nvgFontFace(vg, "default");
      NanoVG.nvgFontSize(vg, 9.0F);
      float tw = textWidth(vg, text, 9.0F);
      float padX = 5.0F;
      float h = 12.0F;
      float w = tw + padX * 2.0F;
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, w, h, 3.0F);
      c(col, t[0], t[1], t[2], 0.2F * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
      NanoVG.nvgTextAlign(vg, 18);
      c(col, 1.0F, 1.0F, 1.0F, 0.85F * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgText(vg, x + w * 0.5F, y + h * 0.5F, text);
   }

   public static void separator(long vg, NVGColor col, float x, float y, float w, float alpha) {
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRect(vg, x, y, w, 1.0F);
      c(col, 1.0F, 1.0F, 1.0F, 0.04F * alpha);
      NanoVG.nvgFillColor(vg, col);
      NanoVG.nvgFill(vg);
   }

   public static float textWidth(long vg, String text, float fontSize) {
      return textWidth(vg, text, fontSize, "default");
   }

   public static float textWidth(long vg, String text, float fontSize, String fontName) {
      NanoVG.nvgFontFace(vg, fontName);
      NanoVG.nvgFontSize(vg, fontSize);
      float[] bounds = BOUNDS.get();
      NanoVG.nvgTextBounds(vg, 0.0F, 0.0F, text, bounds);
      return bounds[2] - bounds[0];
   }
}
