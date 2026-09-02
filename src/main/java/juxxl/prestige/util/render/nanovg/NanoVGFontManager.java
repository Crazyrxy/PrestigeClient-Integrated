package juxxl.prestige.util.render.nanovg;

import java.io.InputStream;
import java.nio.ByteBuffer;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public class NanoVGFontManager {
   private static int regularFont = -1;
   private static int boldFont = -1;
   private static int iconFont = -1;
   private static int jetbrainsFont = -1;
   private static int poppinsFont = -1;
   private static int monacoFont = -1;
   private static int pandawareFont = -1;

   public static int getRegularFont() {
      return regularFont;
   }

   public static int getBoldFont() {
      return boldFont;
   }

   public static int getIconFont() {
      return iconFont;
   }

   public static int getJetbrainsFont() {
      return jetbrainsFont;
   }

   public static int getPoppinsFont() {
      return poppinsFont;
   }

   public static int getMonacoFont() {
      return monacoFont;
   }

   public static int getPandawareFont() {
      return pandawareFont;
   }

   public static void loadFonts() {
      NanoVGContext.assertValid();

      try {
         // Inter for the entire client — clean modern grotesque with strong hinting.
         // Falls back to the shipped sans.ttf if Inter isn't found.
         try {
            regularFont = loadFont("default", "/assets/prestige/font/inter.ttf");
         } catch (Exception fallback) {
            regularFont = loadFont("default", "/assets/prestige/font/sans.ttf");
         }
         try {
            boldFont = loadFont("sans", "/assets/prestige/font/inter-semibold.ttf");
         } catch (Exception fallback) {
            boldFont = loadFont("sans", "/assets/prestige/font/sans.ttf");
         }

         try {
            pandawareFont = loadFont("pandaware", "/assets/prestige/font/pandaware.ttf");
         } catch (Exception e) {
            pandawareFont = regularFont;
         }

         jetbrainsFont = regularFont;
         poppinsFont = regularFont;
         monacoFont = regularFont;

         try {
            iconFont = loadFont("fa-solid", "/assets/prestige/font/fa-solid-900.ttf");
         } catch (Exception e) {
            iconFont = regularFont;
         }

         if (regularFont == -1) {
            regularFont = 0;
         }

         if (boldFont == -1) {
            boldFont = regularFont;
         }
      } catch (Exception e) {
         e.printStackTrace();
         regularFont = 0;
         boldFont = 0;
      }
   }

   private static int loadFont(String name, String path) throws Exception {
      String cleanPath = path.startsWith("/") ? path : "/" + path;

      try (InputStream is = NanoVGFontManager.class.getResourceAsStream(cleanPath)) {
         InputStream stream = is != null ? is : Thread.currentThread().getContextClassLoader().getResourceAsStream(cleanPath.substring(1));
         if (stream == null) {
            throw new RuntimeException("Font not found on classpath: " + cleanPath);
         } else {
            return createFontFromStream(name, stream);
         }
      }
   }

   private static int createFontFromStream(String name, InputStream is) throws Exception {
      byte[] bytes = is.readAllBytes();
      ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
      buffer.put(bytes).flip();
      int font = NanoVG.nvgCreateFontMem(NanoVGContext.getHandle(), name, buffer, true);
      if (font == -1) {
         MemoryUtil.nmemFree(MemoryUtil.memAddress(buffer));
         throw new RuntimeException("Failed to create NanoVG font: " + name);
      } else {
         return font;
      }
   }

   public static int getFontId(boolean bold) {
      int fontId = bold ? boldFont : regularFont;
      if (fontId <= 0) {
         fontId = regularFont > 0 ? regularFont : boldFont;
      }

      if (fontId <= 0) {
         fontId = 1;
      }

      return fontId;
   }
}
