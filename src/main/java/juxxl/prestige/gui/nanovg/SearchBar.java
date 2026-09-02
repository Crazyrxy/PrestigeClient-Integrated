package juxxl.prestige.gui.nanovg;

import java.awt.Color;
import juxxl.prestige.util.render.nanovg.NanoVGImage;
import juxxl.prestige.util.render.nanovg.NanoVGRenderer;
import org.lwjgl.nanovg.NanoVG;

public class SearchBar {
   private String query = "";
   private boolean focused = false;
   private static int searchIconId = -1;
   private static int userIconId = -1;
   public float width = 360.0F;
   public float height = 36.0F;

   public String getQuery() {
      return this.query;
   }

   public void setQuery(String query) {
      this.query = query != null ? query : "";
   }

   public boolean isFocused() {
      return this.focused;
   }

   public void setFocused(boolean focused) {
      this.focused = focused;
   }

   private static void loadIcons() {
      if (searchIconId == -1) {
         searchIconId = NanoVGImage.loadImage("assets/prestige/icons/search.png");
      }

      if (userIconId == -1) {
         userIconId = NanoVGImage.loadImage("assets/prestige/icons/user-round.png");
      }
   }

   public void render(long vg, float screenWidth, float screenHeight, int mouseX, int mouseY, Color accentColor) {
      loadIcons();
      float x = (screenWidth - this.width) / 2.0F;
      float y = screenHeight - 68.0F;
      float radius = this.height / 2.0F;
      boolean hover = mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height;
      Color bg = new Color(9, 10, 14, 240);
      NanoVGRenderer.drawRoundedRectWithShadow(x, y, this.width, this.height, radius, bg, new Color(0, 0, 0, 170), 20.0F, 4.0F);
      // Inner highlight — a thin lighter rim inside the outline sells the glass look.
      NanoVGRenderer.drawRoundedRect(x + 1.0F, y + 1.0F, this.width - 2.0F, this.height - 2.0F, radius - 1.0F, new Color(255, 255, 255, 8));
      NanoVGRenderer.drawRoundedRect(x + 1.0F, y + 1.0F, this.width - 2.0F, this.height - 2.0F, radius - 1.0F, bg);
      Color outline = this.focused ? accentColor : new Color(255, 255, 255, hover ? 42 : 22);
      NanoVGRenderer.drawRoundedRectOutline(x, y, this.width, this.height, radius, 1.0F, outline);
      float textX = x + 16.0F;
      float textY = y + 8.5F;
      NanoVG.nvgSave(vg);
      NanoVG.nvgIntersectScissor(vg, textX, y, this.width - 85.0F, this.height);
      if (this.query.isEmpty()) {
         NanoVGRenderer.drawText("Search...", textX, textY, 11.5F, new Color(130, 130, 145), false);
      } else {
         NanoVGRenderer.drawText(this.query, textX, textY, 11.5F, Color.WHITE, false);
      }

      if (this.focused && System.currentTimeMillis() / 500L % 2L == 0L) {
         float tw = this.query.isEmpty() ? 0.0F : NanoVGRenderer.getTextWidth(this.query, 11.5F);
         NanoVGRenderer.drawRect(textX + tw + 1.5F, textY + 1.0F, 1.5F, 11.0F, accentColor);
      }

      NanoVG.nvgRestore(vg);
      float searchIconX = x + this.width - 56.0F;
      float iconY = y + 4.0F;
      float btnSize = 24.0F;
      float iconSize = 14.0F;
      boolean searchHover = mouseX >= searchIconX && mouseX <= searchIconX + btnSize && mouseY >= iconY && mouseY <= iconY + btnSize;
      NanoVGRenderer.drawRoundedRect(
         searchIconX,
         iconY,
         btnSize,
         btnSize,
         6.0F,
         searchHover ? new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80) : new Color(40, 40, 52, 180)
      );
      if (searchIconId > 0) {
         NanoVGImage.drawImage(searchIconId, searchIconX + (btnSize - iconSize) / 2.0F, iconY + (btnSize - iconSize) / 2.0F, iconSize, iconSize, null);
      }

      float friendsIconX = x + this.width - 28.0F;
      boolean friendsHover = mouseX >= friendsIconX && mouseX <= friendsIconX + btnSize && mouseY >= iconY && mouseY <= iconY + btnSize;
      NanoVGRenderer.drawRoundedRect(
         friendsIconX,
         iconY,
         btnSize,
         btnSize,
         6.0F,
         friendsHover ? new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80) : new Color(40, 40, 52, 180)
      );
      if (userIconId > 0) {
         NanoVGImage.drawImage(userIconId, friendsIconX + (btnSize - iconSize) / 2.0F, iconY + (btnSize - iconSize) / 2.0F, iconSize, iconSize, null);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button, float screenWidth, float screenHeight) {
      float x = (screenWidth - this.width) / 2.0F;
      float y = screenHeight - 68.0F;
      if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height) {
         this.focused = true;
         return true;
      } else {
         this.focused = false;
         return false;
      }
   }

   public boolean keyPressed(int keyCode) {
      if (!this.focused) {
         return false;
      }

      if (keyCode == 259) {
         if (!this.query.isEmpty()) {
            this.query = this.query.substring(0, this.query.length() - 1);
         }

         return true;
      } else if (keyCode == 256) {
         this.focused = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean charTyped(char chr) {
      if (!this.focused) {
         return false;
      } else if (chr >= ' ' && chr != 127) {
         this.query = this.query + chr;
         return true;
      } else {
         return false;
      }
   }
}
