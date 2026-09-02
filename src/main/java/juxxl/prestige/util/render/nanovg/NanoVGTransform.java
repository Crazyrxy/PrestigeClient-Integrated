package juxxl.prestige.util.render.nanovg;

import org.lwjgl.nanovg.NanoVG;

public class NanoVGTransform {
   public static void save() {
      if (NanoVGContext.isValid()) {
         NanoVG.nvgSave(NanoVGContext.getHandle());
      }
   }

   public static void restore() {
      if (NanoVGContext.isValid()) {
         NanoVG.nvgRestore(NanoVGContext.getHandle());
      }
   }

   public static void translate(float x, float y) {
      if (NanoVGContext.isValid()) {
         NanoVG.nvgTranslate(NanoVGContext.getHandle(), x, y);
      }
   }

   public static void scale(float x, float y) {
      if (NanoVGContext.isValid()) {
         NanoVG.nvgScale(NanoVGContext.getHandle(), x, y);
      }
   }

   public static void scissor(float x, float y, float width, float height) {
      if (NanoVGContext.isValid()) {
         NanoVG.nvgScissor(NanoVGContext.getHandle(), x, y, width, height);
      }
   }

   public static void resetScissor() {
      if (NanoVGContext.isValid()) {
         NanoVG.nvgResetScissor(NanoVGContext.getHandle());
      }
   }
}
