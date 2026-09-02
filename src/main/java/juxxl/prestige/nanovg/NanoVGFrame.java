package juxxl.prestige.nanovg;

import net.minecraft.class_1041;
import net.minecraft.class_310;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class NanoVGFrame {
   private static final float FIXED_SCALE = 1.5F;

   public static void render(NanoVGFrame.FrameContent content) {
      class_310 mc = class_310.method_1551();
      int[] prevProg = new int[1];
      GL11.glGetIntegerv(35725, prevProg);
      int[] prevVao = new int[1];
      GL30.glGetIntegerv(34229, prevVao);
      boolean depthWas = GL11.glIsEnabled(2929);
      boolean blendWas = GL11.glIsEnabled(3042);
      boolean cullWas = GL11.glIsEnabled(2884);
      boolean stencilWas = GL11.glIsEnabled(2960);
      GL20.glUseProgram(0);
      GL30.glBindVertexArray(0);
      GL11.glDisable(2929);
      GL11.glDisable(2884);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL15.glBindBuffer(34962, 0);
      GL33.glBindSampler(0, 0);
      long vg = NanoVGService.get();
      class_1041 window = mc.method_22683();
      float screenW = window.method_4480() / 1.5F;
      float screenH = window.method_4507() / 1.5F;
      NanoVG.nvgBeginFrame(vg, screenW, screenH, 1.5F);

      try {
         content.render(vg, screenW, screenH);
      } finally {
         NanoVG.nvgEndFrame(vg);
      }

      GL20.glUseProgram(prevProg[0]);
      GL30.glBindVertexArray(prevVao[0]);
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
   }

   @FunctionalInterface
   public interface FrameContent {
      void render(long var1, float var3, float var4);
   }
}
