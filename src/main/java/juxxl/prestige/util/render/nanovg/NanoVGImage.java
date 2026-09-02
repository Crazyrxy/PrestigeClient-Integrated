package juxxl.prestige.util.render.nanovg;

import java.awt.Color;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import juxxl.prestige.nanovg.NanoVGService;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public class NanoVGImage {
   private static final Map<String, Integer> IMAGE_CACHE = new ConcurrentHashMap<>();
   private static final ThreadLocal<NVGPaint> PAINT = ThreadLocal.withInitial(NVGPaint::create);

   public static int loadImage(String path) {
      if (path != null && !path.isEmpty()) {
         Integer cached = IMAGE_CACHE.get(path);
         if (cached != null) {
            return cached;
         }

         long vg = NanoVGService.get();
         if (vg == 0L) {
            return -1;
         }

         ByteBuffer buffer = null;

         try {
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;
            InputStream is = NanoVGImage.class.getClassLoader().getResourceAsStream(cleanPath);
            if (is == null) {
               is = Thread.currentThread().getContextClassLoader().getResourceAsStream(cleanPath);
            }

            if (is == null) {
               is = NanoVGImage.class.getResourceAsStream("/" + cleanPath);
            }

            if (is == null) {
               return -1;
            }

            byte[] bytes = is.readAllBytes();
            is.close();
            buffer = MemoryUtil.memAlloc(bytes.length);
            buffer.put(bytes).flip();
            int image = NanoVG.nvgCreateImageMem(vg, 1, buffer);
            if (image > 0) {
               IMAGE_CACHE.put(path, image);
            }

            return image;
         } catch (Exception e) {
            return -1;
         } finally {
            if (buffer != null) {
               MemoryUtil.nmemFree(MemoryUtil.memAddress(buffer));
            }
         }
      } else {
         return -1;
      }
   }

   public static void drawImage(int imageId, float x, float y, float width, float height, Color tint) {
      if (imageId > 0) {
         long vg = NanoVGService.get();
         if (vg != 0L) {
            float alpha = tint == null ? 1.0F : tint.getAlpha() * 0.003921569F;
            NVGPaint paint = NanoVG.nvgImagePattern(vg, x, y, width, height, 0.0F, imageId, alpha, PAINT.get());
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgRect(vg, x, y, width, height);
            NanoVG.nvgFillPaint(vg, paint);
            NanoVG.nvgFill(vg);
         }
      }
   }

   public static void drawImageCentered(int imageId, float cx, float cy, float size, Color tint) {
      drawImage(imageId, cx - size / 2.0F, cy - size / 2.0F, size, size, tint);
   }

   public static void drawImageSized(int imageId, float x, float y, float w, float h) {
      drawImage(imageId, x, y, w, h, null);
   }
}
