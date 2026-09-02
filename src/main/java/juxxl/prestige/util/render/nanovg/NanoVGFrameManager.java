package juxxl.prestige.util.render.nanovg;

import com.mojang.blaze3d.systems.RenderSystem;
import juxxl.prestige.Prestige;
import net.minecraft.class_310;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class NanoVGFrameManager {
   private static boolean inFrame = false;
   private static boolean initFailed = false;
   private static int savedFbo = 0;
   private static int savedProgram = 0;
   private static int savedVao = 0;
   private static int savedArrayBuffer = 0;
   private static int savedElementBuffer = 0;
   private static boolean depthWas = false;
   private static boolean blendWas = false;
   private static boolean cullWas = false;
   private static boolean stencilWas = false;
   private static int prevBlendSrcRGB = 0;
   private static int prevBlendDstRGB = 0;
   private static int prevBlendSrcAlpha = 0;
   private static int prevBlendDstAlpha = 0;
   private static int prevBlendEqRGB = 0;
   private static int prevBlendEqAlpha = 0;
   private static int prevActiveTex = 0;
   private static int prevTex0 = 0;

   public static void beginFrame() {
      if (!inFrame && !initFailed) {
         class_310 mc = class_310.method_1551();
         if (mc != null && mc.method_22683() != null) {
            int framebufferWidth = mc.method_22683().method_4480();
            int framebufferHeight = mc.method_22683().method_4507();
            if (framebufferWidth > 0 && framebufferHeight > 0) {
               try {
                  if (!NanoVGContext.isInitialized() || !NanoVGContext.isValid()) {
                     NanoVGContext.init();
                     NanoVGFontManager.loadFonts();
                  }
               } catch (Throwable t) {
                  initFailed = true;
                  System.err.println("[Prestige] Failed to initialize NanoVG: " + t.getMessage());
                  t.printStackTrace();
                  return;
               }

               RenderSystem.assertOnRenderThread();
               savedFbo = GL11.glGetInteger(36006);
               savedProgram = GL11.glGetInteger(35725);
               savedVao = GL11.glGetInteger(34229);
               savedArrayBuffer = GL11.glGetInteger(34964);
               savedElementBuffer = GL11.glGetInteger(34965);
               depthWas = GL11.glIsEnabled(2929);
               blendWas = GL11.glIsEnabled(3042);
               cullWas = GL11.glIsEnabled(2884);
               stencilWas = GL11.glIsEnabled(2960);
               prevBlendSrcRGB = GL11.glGetInteger(32969);
               prevBlendDstRGB = GL11.glGetInteger(32968);
               prevBlendSrcAlpha = GL11.glGetInteger(32971);
               prevBlendDstAlpha = GL11.glGetInteger(32970);
               prevBlendEqRGB = GL11.glGetInteger(32777);
               prevBlendEqAlpha = GL11.glGetInteger(34877);
               prevActiveTex = GL11.glGetInteger(34016);
               GL13.glActiveTexture(33984);
               prevTex0 = GL11.glGetInteger(32873);
               GL30.glBindFramebuffer(36160, 0);
               GL11.glViewport(0, 0, framebufferWidth, framebufferHeight);
               GL20.glUseProgram(0);
               GL30.glBindVertexArray(0);
               GL15.glBindBuffer(34962, 0);
               GL15.glBindBuffer(34963, 0);
               GL33.glBindSampler(0, 0);
               GL11.glDisable(2929);
               GL11.glDisable(2884);
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               GL11.glEnable(2960);
               GL11.glClear(1024);
               long vg = NanoVGContext.getHandle();
               NanoVG.nvgBeginFrame(vg, framebufferWidth, framebufferHeight, 1.0F);
               int scaledWidth = mc.method_22683().method_4486();
               int scaledHeight = mc.method_22683().method_4502();
               float scaleX = (float)framebufferWidth / scaledWidth;
               float scaleY = (float)framebufferHeight / scaledHeight;
               float userScale = 0.75F;
               if (Prestige.INSTANCE != null && Prestige.INSTANCE.clickGui != null) {
                  userScale = (float) Prestige.INSTANCE.clickGui.getAutoScale();
               }

               NanoVG.nvgScale(vg, scaleX * userScale, scaleY * userScale);
               inFrame = true;
            }
         }
      }
   }

   public static void endFrame() {
      if (inFrame) {
         RenderSystem.assertOnRenderThread();

         try {
            NanoVG.nvgEndFrame(NanoVGContext.getHandle());
         } catch (Throwable t) {
            System.err.println("[Prestige] NanoVG nvgEndFrame error: " + t.getMessage());
         } finally {
            inFrame = false;
         }

         GL30.glBindFramebuffer(36160, savedFbo);
         GL20.glUseProgram(savedProgram);
         GL30.glBindVertexArray(savedVao);
         GL15.glBindBuffer(34962, savedArrayBuffer);
         GL15.glBindBuffer(34963, savedElementBuffer);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, prevTex0);
         GL13.glActiveTexture(prevActiveTex);
         GL14.glBlendFuncSeparate(prevBlendSrcRGB, prevBlendDstRGB, prevBlendSrcAlpha, prevBlendDstAlpha);
         GL20.glBlendEquationSeparate(prevBlendEqRGB, prevBlendEqAlpha);
         if (depthWas) {
            GL11.glEnable(2929);
         } else {
            GL11.glDisable(2929);
         }

         if (blendWas) {
            GL11.glEnable(3042);
         } else {
            GL11.glDisable(3042);
         }

         if (cullWas) {
            GL11.glEnable(2884);
         } else {
            GL11.glDisable(2884);
         }

         if (stencilWas) {
            GL11.glEnable(2960);
         } else {
            GL11.glDisable(2960);
         }

         GL11.glPixelStorei(3317, 4);
         GL11.glPixelStorei(3314, 0);
         GL11.glPixelStorei(3316, 0);
         GL11.glPixelStorei(3315, 0);
      }
   }

   public static boolean isInFrame() {
      return inFrame;
   }

   public static void resetInFrame() {
      inFrame = false;
   }
}
