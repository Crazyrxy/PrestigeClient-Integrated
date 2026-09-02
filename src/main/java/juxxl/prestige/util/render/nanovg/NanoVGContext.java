package juxxl.prestige.util.render.nanovg;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.nanovg.NanoVGGL3;

public class NanoVGContext {
   private static long handle = 0L;
   private static boolean initialized = false;

   public static long getHandle() {
      return handle;
   }

   public static void init() {
      if (!initialized || !isValid()) {
         RenderSystem.assertOnRenderThread();
         if (handle != 0L) {
            cleanup();
         }

         handle = NanoVGGL3.nvgCreate(3);
         if (!isValid()) {
            throw new RuntimeException("Failed to initialize NanoVG");
         }

         initialized = true;
      }
   }

   public static void reinit() {
      cleanup();
      init();
   }

   public static boolean isValid() {
      return handle != 0L && handle != -1L;
   }

   public static void assertValid() {
      if (!isValid()) {
         throw new IllegalStateException("Invalid NanoVG context");
      }
   }

   public static boolean isInitialized() {
      return initialized;
   }

   public static void cleanup() {
      if (handle != 0L) {
         try {
            NanoVGGL3.nvgDelete(handle);
         } catch (Exception var1) {
         }

         handle = 0L;
      }

      initialized = false;
   }
}
