package juxxl.prestige.gui.nanovg;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import juxxl.prestige.util.render.nanovg.NanoVGRenderer;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

public class ColorPicker {
   private float hue = 0.76F;
   private float saturation = 0.8F;
   private float value = 0.95F;
   private boolean draggingSv = false;
   private boolean draggingHue = false;
   public float width = 170.0F;
   public float height = 135.0F;

   public ColorPicker(Color initialColor) {
      if (initialColor != null) {
         this.setColor(initialColor);
      }
   }

   public Color getColor() {
      return Color.getHSBColor(this.hue, this.saturation, this.value);
   }

   public void setColor(Color color) {
      if (color != null) {
         float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
         this.hue = hsv[0];
         this.saturation = hsv[1];
         this.value = hsv[2];
      }
   }

   public void render(long vg, float x, float y, int mouseX, int mouseY) {
      float svW = this.width;
      float svH = 75.0F;
      Color pureHueColor = Color.getHSBColor(this.hue, 1.0F, 1.0F);
      NanoVGRenderer.drawRoundedRect(x, y, svW, svH, 5.0F, pureHueColor);
      NVGColor white1 = NVGColor.create();
      NanoVG.nvgRGBAf(1.0F, 1.0F, 1.0F, 1.0F, white1);
      NVGColor white0 = NVGColor.create();
      NanoVG.nvgRGBAf(1.0F, 1.0F, 1.0F, 0.0F, white0);
      NVGPaint horizPaint = NVGPaint.create();
      NanoVG.nvgLinearGradient(vg, x, y, x + svW, y, white1, white0, horizPaint);
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, svW, svH, 5.0F);
      NanoVG.nvgFillPaint(vg, horizPaint);
      NanoVG.nvgFill(vg);
      NVGColor black0 = NVGColor.create();
      NanoVG.nvgRGBAf(0.0F, 0.0F, 0.0F, 0.0F, black0);
      NVGColor black1 = NVGColor.create();
      NanoVG.nvgRGBAf(0.0F, 0.0F, 0.0F, 1.0F, black1);
      NVGPaint vertPaint = NVGPaint.create();
      NanoVG.nvgLinearGradient(vg, x, y, x, y + svH, black0, black1, vertPaint);
      NanoVG.nvgBeginPath(vg);
      NanoVG.nvgRoundedRect(vg, x, y, svW, svH, 5.0F);
      NanoVG.nvgFillPaint(vg, vertPaint);
      NanoVG.nvgFill(vg);
      NanoVGRenderer.drawRoundedRectOutline(x, y, svW, svH, 5.0F, 1.0F, new Color(255, 255, 255, 30));
      float handleX = x + this.saturation * svW;
      float handleY = y + (1.0F - this.value) * svH;
      NanoVGRenderer.drawCircle(handleX, handleY, 5.5F, Color.WHITE);
      NanoVGRenderer.drawCircle(handleX, handleY, 4.0F, this.getColor());
      float hueY = y + svH + 8.0F;
      float hueH = 10.0F;
      int segments = 12;
      float segW = svW / segments;

      for (int i = 0; i < segments; i++) {
         float h1 = (float)i / segments;
         float h2 = (float)(i + 1) / segments;
         Color c1 = Color.getHSBColor(h1, 1.0F, 1.0F);
         Color c2 = Color.getHSBColor(h2, 1.0F, 1.0F);
         NanoVGRenderer.drawRoundedRectGradient(x + i * segW, hueY, segW + 0.5F, hueH, 2.0F, c1, c2);
      }

      NanoVGRenderer.drawRoundedRectOutline(x, hueY, svW, hueH, 4.0F, 1.0F, new Color(255, 255, 255, 40));
      float hueHandleX = x + this.hue * svW;
      NanoVGRenderer.drawRoundedRect(hueHandleX - 2.5F, hueY - 1.0F, 5.0F, hueH + 2.0F, 2.5F, Color.WHITE);
      float btnY = hueY + hueH + 8.0F;
      float btnW = (svW - 8.0F) / 2.0F;
      float btnH = 20.0F;
      boolean copyHover = mouseX >= x && mouseX <= x + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
      boolean pasteHover = mouseX >= x + btnW + 8.0F && mouseX <= x + svW && mouseY >= btnY && mouseY <= btnY + btnH;
      NanoVGRenderer.drawRoundedRect(x, btnY, btnW, btnH, 4.0F, copyHover ? new Color(35, 35, 45) : new Color(22, 22, 28));
      NanoVGRenderer.drawRoundedRectOutline(x, btnY, btnW, btnH, 4.0F, 1.0F, new Color(255, 255, 255, 20));
      NanoVGRenderer.drawText(
         "Copy", x + btnW / 2.0F - NanoVGRenderer.getTextWidth("Copy", 10.5F) / 2.0F, btnY + 4.5F, 10.5F, copyHover ? Color.WHITE : new Color(180, 180, 195)
      );
      NanoVGRenderer.drawRoundedRect(x + btnW + 8.0F, btnY, btnW, btnH, 4.0F, pasteHover ? new Color(35, 35, 45) : new Color(22, 22, 28));
      NanoVGRenderer.drawRoundedRectOutline(x + btnW + 8.0F, btnY, btnW, btnH, 4.0F, 1.0F, new Color(255, 255, 255, 20));
      NanoVGRenderer.drawText(
         "Paste",
         x + btnW + 8.0F + btnW / 2.0F - NanoVGRenderer.getTextWidth("Paste", 10.5F) / 2.0F,
         btnY + 4.5F,
         10.5F,
         pasteHover ? Color.WHITE : new Color(180, 180, 195)
      );
      if (this.draggingSv) {
         this.saturation = Math.max(0.0F, Math.min(1.0F, (mouseX - x) / svW));
         this.value = Math.max(0.0F, Math.min(1.0F, 1.0F - (mouseY - y) / svH));
      } else if (this.draggingHue) {
         this.hue = Math.max(0.0F, Math.min(1.0F, (mouseX - x) / svW));
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y) {
      if (button != 0) {
         return false;
      } else {
         float svW = this.width;
         float svH = 75.0F;
         if (mouseX >= x && mouseX <= x + svW && mouseY >= y && mouseY <= y + svH) {
            this.draggingSv = true;
            this.saturation = Math.max(0.0F, Math.min(1.0F, (float)(mouseX - x) / svW));
            this.value = Math.max(0.0F, Math.min(1.0F, 1.0F - (float)(mouseY - y) / svH));
            return true;
         } else {
            float hueY = y + svH + 8.0F;
            float hueH = 10.0F;
            if (mouseX >= x && mouseX <= x + svW && mouseY >= hueY - 2.0F && mouseY <= hueY + hueH + 2.0F) {
               this.draggingHue = true;
               this.hue = Math.max(0.0F, Math.min(1.0F, (float)(mouseX - x) / svW));
               return true;
            } else {
               float btnY = hueY + hueH + 8.0F;
               float btnW = (svW - 8.0F) / 2.0F;
               float btnH = 20.0F;
               if (mouseX >= x && mouseX <= x + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                  this.copyToClipboard();
                  return true;
               } else if (mouseX >= x + btnW + 8.0F && mouseX <= x + svW && mouseY >= btnY && mouseY <= btnY + btnH) {
                  this.pasteFromClipboard();
                  return true;
               } else {
                  return false;
               }
            }
         }
      }
   }

   public void mouseReleased(int button) {
      if (button == 0) {
         this.draggingSv = false;
         this.draggingHue = false;
      }
   }

   private void copyToClipboard() {
      try {
         Color c = this.getColor();
         String hex = String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hex), null);
      } catch (Exception var3) {
      }
   }

   private void pasteFromClipboard() {
      try {
         String text = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
         if (text != null) {
            text = text.trim();
            if (text.startsWith("#")) {
               text = text.substring(1);
            }

            if (text.length() == 6) {
               int r = Integer.parseInt(text.substring(0, 2), 16);
               int g = Integer.parseInt(text.substring(2, 4), 16);
               int b = Integer.parseInt(text.substring(4, 6), 16);
               this.setColor(new Color(r, g, b));
            }
         }
      } catch (Exception var5) {
      }
   }
}
