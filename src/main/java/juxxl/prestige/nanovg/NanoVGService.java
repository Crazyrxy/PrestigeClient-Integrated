package juxxl.prestige.nanovg;

import juxxl.prestige.util.render.nanovg.NanoVGContext;
import juxxl.prestige.util.render.nanovg.NanoVGFontManager;
import org.lwjgl.nanovg.NanoVG;

public class NanoVGService {
   public static long get() {
      if (!NanoVGContext.isInitialized() || !NanoVGContext.isValid()) {
         NanoVGContext.init();
         NanoVGFontManager.loadFonts();
      }

      return NanoVGContext.getHandle();
   }

   public static boolean hasIconFont() {
      long vg = get();
      return vg <= 0L ? false : NanoVGFontManager.getIconFont() > 0 || NanoVG.nvgFindFont(vg, "icons") != -1 || NanoVG.nvgFindFont(vg, "fa-solid") != -1;
   }
}
