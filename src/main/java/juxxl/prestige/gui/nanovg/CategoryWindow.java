package juxxl.prestige.gui.nanovg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import juxxl.prestige.Prestige;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.util.render.nanovg.NanoVGImage;
import juxxl.prestige.util.render.nanovg.NanoVGRenderer;
import org.lwjgl.nanovg.NanoVG;

/**
 * CategoryWindow — Nocturne panel layout (stacked cards, teal rail, soft slate glass).
 */
public class CategoryWindow {
   public final CategoryWindow.WindowType type;
   public final Category category;
   public final String title;
   public final String iconPath;
   public float x;
   public float y;
   public float width = 175.0F;
   public float maxBodyHeight = 420.0F;

   // Prestige Purple UI (reference-inspired)
   private static final float CORNER_RADIUS = 10.0F;
   private static final Color GLASS_BG = new Color(18, 18, 24, 235);
   private static final Color GLASS_HEADER = new Color(22, 22, 30, 250);
   private static final Color GLASS_HIGHLIGHT = new Color(168, 85, 247, 22);
   private static final Color GLASS_SHADOW = new Color(0, 0, 0, 190);
   private static final Color OUTLINE_IDLE = new Color(255, 255, 255, 18);
   private static final Color OUTLINE_HOVER = new Color(168, 85, 247, 100);
   private static final Color TEXT_MUTED = new Color(140, 140, 155);
   private static final Color TEXT_TITLE = new Color(245, 245, 250);
   private static final Color COUNT_BG = new Color(40, 40, 52, 200);

   public boolean dragging = false;
   private float dragX = 0.0F;
   private float dragY = 0.0F;
   public float scrollOffset = 0.0F;
   public final List<ModuleRow> moduleRows = new ArrayList<>();
   public final ColorPicker colorPicker = new ColorPicker(new Color(168, 85, 247));
   public boolean listeningMenuKey = false;
   public String configInputText = "";
   public boolean configInputFocused = false;
   private static String descriptionMode = "Mouse";
   private static final Map<String, Integer> iconCache = new HashMap<>();

   public CategoryWindow(float x, float y, Category category) {
      this.type = CategoryWindow.WindowType.CATEGORY;
      this.category = category;
      this.title = category.name.toString();
      this.iconPath = getCategoryIcon(category);
      this.x = x;
      this.y = y;

      for (Module module : Prestige.INSTANCE.getModuleManager().getModules()) {
         if (module.getCategory() == category) {
            this.moduleRows.add(new ModuleRow(module));
         }
      }
   }

   public CategoryWindow(float x, float y, String title, String iconPath, CategoryWindow.WindowType type) {
      this.type = type;
      this.category = null;
      this.title = title;
      this.iconPath = iconPath;
      this.x = x;
      this.y = y;
   }

   private static String getCategoryIcon(Category cat) {
      switch (cat) {
         case COMBAT: return "assets/prestige/icons/sword.png";
         case MISC: return "assets/prestige/icons/star.png";
         case RENDER: return "assets/prestige/icons/eye.png";
         case MACE: return "assets/prestige/icons/hammer.png";
         case CART: return "assets/prestige/icons/key-round.png";
         case CLIENT: return "assets/prestige/icons/user-round.png";
         default: return "assets/prestige/icons/sword.png";
      }
   }

   private static int getOrLoadIcon(String path) {
      if (!iconCache.containsKey(path)) {
         int id = NanoVGImage.loadImage(path);
         iconCache.put(path, id);
      }
      return iconCache.get(path);
   }

   public float getHeaderHeight() {
      return 46.0F;
   }

   public float getContentHeight() {
      if (this.type == CategoryWindow.WindowType.MENU) {
         return 220.0F;
      }
      if (this.type == CategoryWindow.WindowType.CONFIGS) {
         int count = Prestige.INSTANCE.getProfileManager().getAvailableConfigs().size();
         return 80.0F + count * 30.0F + 10.0F;
      }

      float total = 10.0F;
      for (ModuleRow row : this.moduleRows) {
         total += row.getHeight();
      }
      return total;
   }

   public void render(long vg, int mouseX, int mouseY, Color accentColor, String searchQuery) {
      float headerH = this.getHeaderHeight();
      float contentH = this.getContentHeight();
      float bodyH = Math.min(contentH, this.maxBodyHeight);
      float totalH = headerH + bodyH;
      Color accent = accentColor != null ? accentColor : new Color(139, 92, 246);

      // Glass panel with shadow
      NanoVGRenderer.drawRoundedRectWithShadow(
         this.x, this.y, this.width, totalH, CORNER_RADIUS,
         GLASS_BG, GLASS_SHADOW, 24.0F, 6.0F
      );

      // Header background
      NanoVGRenderer.drawRoundedRectVarying(
         this.x, this.y, this.width, headerH,
         CORNER_RADIUS, CORNER_RADIUS, 0.0F, 0.0F,
         GLASS_HEADER
      );

      // Header inner highlight
      NanoVGRenderer.drawRoundedRectVarying(
         this.x + 1.0F, this.y + 1.0F, this.width - 2.0F, headerH - 1.0F,
         CORNER_RADIUS - 1.0F, CORNER_RADIUS - 1.0F, 0.0F, 0.0F,
         GLASS_HIGHLIGHT
      );

      // Category icon badge (like reference clients)
      float iconSize = 16.0F;
      float iconBox = 26.0F;
      float iconBoxX = this.x + 10.0F;
      float iconBoxY = this.y + (headerH - iconBox) / 2.0F;
      NanoVGRenderer.drawRoundedRect(iconBoxX, iconBoxY, iconBox, iconBox, 7.0F, new Color(168, 85, 247, 40));
      NanoVGRenderer.drawRoundedRectOutline(iconBoxX, iconBoxY, iconBox, iconBox, 7.0F, 1.0F, new Color(168, 85, 247, 90));
      int iconId = getOrLoadIcon(this.iconPath);
      if (iconId > 0) {
         float ix = iconBoxX + (iconBox - iconSize) / 2.0F;
         float iy = iconBoxY + (iconBox - iconSize) / 2.0F;
         NanoVGRenderer.drawImage(iconId, ix, iy, iconSize, iconSize, new Color(210, 160, 255, 255));
      } else {
         // Fallback accent bar if icon missing
         NanoVGRenderer.drawRoundedRect(this.x + 12.0F, this.y + 12.0F, 3.0F, headerH - 24.0F, 1.5F, accent);
      }

      // Title next to icon
      float startX = iconBoxX + iconBox + 8.0F;
      NanoVGRenderer.drawText(this.title, startX, this.y + (headerH - 14.0F) / 2.0F + 1.0F, 13.5F, TEXT_TITLE, true);

      // Item count pill
      int itemCount = 0;
      if (this.type == CategoryWindow.WindowType.CATEGORY) {
         itemCount = this.moduleRows.size();
      } else if (this.type == CategoryWindow.WindowType.CONFIGS) {
         itemCount = Prestige.INSTANCE.getProfileManager().getAvailableConfigs().size();
      }

      if (this.type != CategoryWindow.WindowType.MENU) {
         String countStr = String.valueOf(itemCount);
         float countW = NanoVGRenderer.getTextWidth(countStr, 9.5F);
         float pillW = Math.max(22.0F, countW + 14.0F);
         float pillH = 18.0F;
         float pillX = this.x + this.width - 16.0F - pillW;
         float pillY = this.y + (headerH - pillH) / 2.0F;
         NanoVGRenderer.drawRoundedRect(pillX, pillY, pillW, pillH, 6.0F, COUNT_BG);
         NanoVGRenderer.drawText(countStr, pillX + (pillW - countW) / 2.0F, pillY + 4.0F, 9.5F, TEXT_MUTED, true);
      }

      // Hover outline
      boolean isHovered = mouseX >= this.x && mouseX <= this.x + this.width
         && mouseY >= this.y && mouseY <= this.y + totalH;
      NanoVGRenderer.drawRoundedRectOutline(
         this.x, this.y, this.width, totalH, CORNER_RADIUS, 1.0F,
         isHovered ? OUTLINE_HOVER : OUTLINE_IDLE
      );

      // Bottom accent stripe
      int stripeAlpha = isHovered ? 220 : 150;
      Color bottomStripe = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), stripeAlpha);
      NanoVGRenderer.drawRoundedRect(
         this.x + 14.0F, this.y + totalH - 3.0F, this.width - 28.0F, 2.0F, 1.0F, bottomStripe
      );

      // Content area with scissor
      float bodyY = this.y + headerH;
      NanoVG.nvgSave(vg);
      NanoVG.nvgIntersectScissor(vg, this.x, bodyY, this.width, bodyH);

      if (this.type == CategoryWindow.WindowType.CATEGORY) {
         this.renderCategoryContent(vg, this.x, bodyY - this.scrollOffset, this.width, mouseX, mouseY, accentColor, searchQuery);
      } else if (this.type == CategoryWindow.WindowType.MENU) {
         this.renderMenuContent(vg, this.x, bodyY, this.width, mouseX, mouseY, accentColor);
      } else if (this.type == CategoryWindow.WindowType.CONFIGS) {
         this.renderConfigsContent(vg, this.x, bodyY - this.scrollOffset, this.width, mouseX, mouseY, accentColor);
      }

      NanoVG.nvgRestore(vg);

      // Dragging
      if (this.dragging) {
         this.x = mouseX - this.dragX;
         this.y = mouseY - this.dragY;
      }
   }

   private void renderCategoryContent(long vg, float contentX, float startY, float w, int mouseX, int mouseY, Color accentColor, String searchQuery) {
      float currentY = startY + 6.0F;
      String q = searchQuery != null ? searchQuery.toLowerCase().trim() : "";

      for (ModuleRow row : this.moduleRows) {
         if (q.isEmpty() || row.module.getName().toString().toLowerCase().contains(q)) {
            float rowH = row.getHeight();
            row.render(vg, contentX + 6.0F, currentY, w - 12.0F, mouseX, mouseY, accentColor);
            currentY += rowH;
         }
      }
   }

   private void renderMenuContent(long vg, float contentX, float startY, float w, int mouseX, int mouseY, Color accentColor) {
      float x = contentX + 12.0F;
      float y = startY + 10.0F;

      NanoVGRenderer.drawText("Menu Key:", x, y + 2.0F, 11.5F, new Color(200, 200, 215), false);
      String keyName = this.listeningMenuKey ? "[PRESS KEY]" : "Right Shift";
      float keyW = NanoVGRenderer.getTextWidth(keyName, 10.5F) + 16.0F;
      float keyX = x + w - 24.0F - keyW;
      boolean keyHover = mouseX >= keyX && mouseX <= keyX + keyW && mouseY >= y && mouseY <= y + 20.0F;
      NanoVGRenderer.drawRoundedRect(
         keyX, y, keyW, 20.0F, 5.0F,
         this.listeningMenuKey ? accentColor : (keyHover ? new Color(40, 40, 52) : new Color(26, 26, 34))
      );
      NanoVGRenderer.drawText(keyName, keyX + 8.0F, y + 4.5F, 10.0F, Color.WHITE, this.listeningMenuKey);

      y += 28.0F;
      NanoVGRenderer.drawText("Descriptions:", x, y + 2.0F, 11.5F, new Color(200, 200, 215), false);
      String descMode = descriptionMode;
      float descW = NanoVGRenderer.getTextWidth(descMode, 10.5F) + 16.0F;
      float descX = x + w - 24.0F - descW;
      boolean descHover = mouseX >= descX && mouseX <= descX + descW && mouseY >= y && mouseY <= y + 20.0F;
      NanoVGRenderer.drawRoundedRect(descX, y, descW, 20.0F, 5.0F, descHover ? new Color(40, 40, 52) : new Color(26, 26, 34));
      NanoVGRenderer.drawText(descMode, descX + 8.0F, y + 4.5F, 10.0F, Color.WHITE, false);

      y += 28.0F;
      NanoVGRenderer.drawText("Menu Color", x, y, 11.5F, new Color(200, 200, 215), true);
      NanoVGRenderer.drawCircle(x + w - 32.0F, y + 6.0F, 6.0F, this.colorPicker.getColor());

      y += 18.0F;
      this.colorPicker.width = w - 24.0F;
      this.colorPicker.render(vg, x, y, mouseX, mouseY);
   }

   private void renderConfigsContent(long vg, float contentX, float startY, float w, int mouseX, int mouseY, Color accentColor) {
      float x = contentX + 12.0F;
      float y = startY + 10.0F;
      float boxW = w - 24.0F;

      // Input field
      float inputY = y;
      float inputH = 26.0F;
      boolean inputHover = mouseX >= x && mouseX <= x + boxW && mouseY >= inputY && mouseY <= inputY + inputH;
      Color inputBg = new Color(20, 20, 28);
      Color inputOutline = this.configInputFocused ? accentColor : (inputHover ? new Color(255, 255, 255, 40) : new Color(255, 255, 255, 22));
      NanoVGRenderer.drawRoundedRect(x, inputY, boxW, inputH, 5.0F, inputBg);
      NanoVGRenderer.drawRoundedRectOutline(x, inputY, boxW, inputH, 5.0F, 1.0F, inputOutline);
      String displayText = this.configInputText.isEmpty()
         ? (this.configInputFocused ? "|" : "Name...")
         : this.configInputText + (this.configInputFocused ? "|" : "");
      Color textColor = this.configInputText.isEmpty() && !this.configInputFocused ? new Color(130, 130, 150) : Color.WHITE;
      NanoVGRenderer.drawText(displayText, x + 10.0F, inputY + 7.0F, 11.0F, textColor, false);

      // Save button
      float btnY = inputY + 32.0F;
      float btnH = 26.0F;
      boolean btnHover = mouseX >= x && mouseX <= x + boxW && mouseY >= btnY && mouseY <= btnY + btnH;
      Color btnBg = btnHover ? new Color(42, 42, 54) : new Color(28, 28, 38);
      NanoVGRenderer.drawRoundedRect(x, btnY, boxW, btnH, 5.0F, btnBg);
      NanoVGRenderer.drawRoundedRectOutline(x, btnY, boxW, btnH, 5.0F, 1.0F, new Color(255, 255, 255, 22));
      String saveText = "Save New";
      float saveW = NanoVGRenderer.getTextWidth(saveText, 11.5F);
      NanoVGRenderer.drawText(saveText, x + (boxW - saveW) / 2.0F, btnY + 7.0F, 11.5F, Color.WHITE, true);

      // Config list
      float listY = btnY + 36.0F;
      for (String cfgName : Prestige.INSTANCE.getProfileManager().getAvailableConfigs()) {
         float rowY = listY;
         float rowH = 28.0F;
         NanoVGRenderer.drawText(cfgName, x + 4.0F, rowY + 8.0F, 11.0F, Color.WHITE, false);

         float actionBtnW = 20.0F;
         float actionBtnH = 20.0F;
         float gap = 4.0F;
         float delX = x + boxW - actionBtnW;
         float delY = rowY + 4.0F;
         boolean delHover = mouseX >= delX && mouseX <= delX + actionBtnW && mouseY >= delY && mouseY <= delY + actionBtnH;
         NanoVGRenderer.drawRoundedRect(delX, delY, actionBtnW, actionBtnH, 5.0F, delHover ? new Color(220, 38, 38) : new Color(16, 16, 22));
         NanoVGRenderer.drawRoundedRectOutline(delX, delY, actionBtnW, actionBtnH, 5.0F, 1.0F, new Color(255, 255, 255, 18));
         Color delIconColor = delHover ? Color.WHITE : new Color(170, 170, 185);
         NanoVGRenderer.drawLine(delX + 6.0F, delY + 6.0F, delX + 14.0F, delY + 14.0F, 1.4F, delIconColor);
         NanoVGRenderer.drawLine(delX + 14.0F, delY + 6.0F, delX + 6.0F, delY + 14.0F, 1.4F, delIconColor);

         float saveX = delX - actionBtnW - gap;
         float saveY = rowY + 4.0F;
         boolean saveHover = mouseX >= saveX && mouseX <= saveX + actionBtnW && mouseY >= saveY && mouseY <= saveY + actionBtnH;
         NanoVGRenderer.drawRoundedRect(saveX, saveY, actionBtnW, actionBtnH, 5.0F, saveHover ? accentColor : new Color(16, 16, 22));
         NanoVGRenderer.drawRoundedRectOutline(saveX, saveY, actionBtnW, actionBtnH, 5.0F, 1.0F, new Color(255, 255, 255, 18));
         Color saveIconColor = saveHover ? Color.WHITE : new Color(170, 170, 185);
         NanoVGRenderer.drawRoundedRectOutline(saveX + 7.0F, saveY + 5.0F, 6.0F, 6.0F, 3.0F, 1.2F, saveIconColor);
         NanoVGRenderer.drawRoundedRect(saveX + 6.0F, saveY + 9.0F, 8.0F, 6.0F, 2.0F, saveIconColor);

         float loadX = saveX - actionBtnW - gap;
         float loadY = rowY + 4.0F;
         boolean loadHover = mouseX >= loadX && mouseX <= loadX + actionBtnW && mouseY >= loadY && mouseY <= loadY + actionBtnH;
         NanoVGRenderer.drawRoundedRect(loadX, loadY, actionBtnW, actionBtnH, 5.0F, loadHover ? accentColor : new Color(16, 16, 22));
         NanoVGRenderer.drawRoundedRectOutline(loadX, loadY, actionBtnW, actionBtnH, 5.0F, 1.0F, new Color(255, 255, 255, 18));
         Color loadIconColor = loadHover ? Color.WHITE : new Color(170, 170, 185);
         NanoVGRenderer.drawLine(loadX + 5.0F, loadY + 10.0F, loadX + 8.0F, loadY + 13.0F, 1.5F, loadIconColor);
         NanoVGRenderer.drawLine(loadX + 8.0F, loadY + 13.0F, loadX + 14.0F, loadY + 6.0F, 1.5F, loadIconColor);

         listY += rowH + 2.0F;
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button, String searchQuery) {
      float headerH = this.getHeaderHeight();
      float contentH = this.getContentHeight();
      float bodyH = Math.min(contentH, this.maxBodyHeight);

      // Header drag
      if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + headerH && button == 0) {
         this.dragging = true;
         this.dragX = (float)mouseX - this.x;
         this.dragY = (float)mouseY - this.y;
         return true;
      }

      // Body content
      float bodyY = this.y + headerH;
      if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= bodyY && mouseY <= bodyY + bodyH) {
         if (this.type == CategoryWindow.WindowType.CATEGORY) {
            float currentY = bodyY - this.scrollOffset + 6.0F;
            String q = searchQuery != null ? searchQuery.toLowerCase().trim() : "";

            for (ModuleRow row : this.moduleRows) {
               if (q.isEmpty() || row.module.getName().toString().toLowerCase().contains(q)) {
                  float rowH = row.getHeight();
                  if (mouseY >= currentY && mouseY <= currentY + rowH) {
                     return row.mouseClicked(mouseX, mouseY, button, this.x + 6.0F, currentY, this.width - 12.0F);
                  }
                  currentY += rowH;
               }
            }
         } else {
            return this.handleSpecialMouseClick(mouseX, mouseY, button, bodyY);
         }
      }
      return false;
   }

   private boolean handleSpecialMouseClick(double mouseX, double mouseY, int button, float bodyY) {
      if (this.type == CategoryWindow.WindowType.MENU) {
         float menuX = this.x + 12.0F;
         float keyY = bodyY + 10.0F;
         float keyW = NanoVGRenderer.getTextWidth(this.listeningMenuKey ? "[PRESS KEY]" : "Right Shift", 10.5F) + 16.0F;
         if (button == 0 && mouseX >= menuX + this.width - 24.0F - keyW && mouseX <= menuX + this.width - 24.0F
            && mouseY >= keyY && mouseY <= keyY + 20.0F) {
            this.listeningMenuKey = !this.listeningMenuKey;
            return true;
         }

         float descY = keyY + 28.0F;
         float descW = NanoVGRenderer.getTextWidth(descriptionMode, 10.5F) + 16.0F;
         if (button == 0 && mouseX >= menuX + this.width - 24.0F - descW && mouseX <= menuX + this.width - 24.0F
            && mouseY >= descY && mouseY <= descY + 20.0F) {
            if (descriptionMode.equals("Mouse")) descriptionMode = "Always";
            else if (descriptionMode.equals("Always")) descriptionMode = "Off";
            else descriptionMode = "Mouse";
            return true;
         }

         float cpY = descY + 28.0F + 18.0F;
         return this.colorPicker.mouseClicked(mouseX, mouseY, button, menuX, cpY);
      }

      if (this.type == CategoryWindow.WindowType.CONFIGS) {
         float cfgX = this.x + 12.0F;
         float boxW = this.width - 24.0F;
         float inputY = bodyY - this.scrollOffset + 10.0F;
         float inputH = 26.0F;
         if (button == 0 && mouseX >= cfgX && mouseX <= cfgX + boxW && mouseY >= inputY && mouseY <= inputY + inputH) {
            this.configInputFocused = true;
            return true;
         }
         if (button == 0) this.configInputFocused = false;

         float btnY = inputY + 32.0F;
         float btnH = 26.0F;
         if (button == 0 && mouseX >= cfgX && mouseX <= cfgX + boxW && mouseY >= btnY && mouseY <= btnY + btnH) {
            String name = this.configInputText.trim();
            if (name.isEmpty()) name = "config_" + (Prestige.INSTANCE.getProfileManager().getAvailableConfigs().size() + 1);
            Prestige.INSTANCE.getProfileManager().saveProfile(name);
            this.configInputText = "";
            this.configInputFocused = false;
            return true;
         }

         float listY = btnY + 36.0F;
         List<String> configs = Prestige.INSTANCE.getProfileManager().getAvailableConfigs();
         float actionBtnW = 20.0F;
         float actionBtnH = 20.0F;
         float gap = 4.0F;

         for (String cfgName : configs) {
            float delX = cfgX + boxW - actionBtnW;
            float delY = listY + 4.0F;
            float saveX = delX - actionBtnW - gap;
            float saveY = listY + 4.0F;
            float loadX = saveX - actionBtnW - gap;
            float loadY = listY + 4.0F;
            if (button == 0) {
               if (mouseX >= delX && mouseX <= delX + actionBtnW && mouseY >= delY && mouseY <= delY + actionBtnH) {
                  Prestige.INSTANCE.getProfileManager().deleteProfile(cfgName);
                  return true;
               }
               if (mouseX >= saveX && mouseX <= saveX + actionBtnW && mouseY >= saveY && mouseY <= saveY + actionBtnH) {
                  Prestige.INSTANCE.getProfileManager().saveProfile(cfgName);
                  return true;
               }
               if (mouseX >= loadX && mouseX <= loadX + actionBtnW && mouseY >= loadY && mouseY <= loadY + actionBtnH) {
                  Prestige.INSTANCE.getProfileManager().loadProfile(cfgName);
                  return true;
               }
            }
            listY += 30.0F;
         }
      }
      return false;
   }

   public void mouseReleased(int button) {
      if (button == 0) this.dragging = false;
      for (ModuleRow row : this.moduleRows) row.mouseReleased(button);
      this.colorPicker.mouseReleased(button);
   }

   public void mouseScrolled(double mouseX, double mouseY, double amount) {
      float headerH = this.getHeaderHeight();
      float bodyH = Math.min(this.getContentHeight(), this.maxBodyHeight);
      if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + headerH && mouseY <= this.y + headerH + bodyH) {
         float maxScroll = Math.max(0.0F, this.getContentHeight() - this.maxBodyHeight);
         this.scrollOffset = Math.max(0.0F, Math.min(maxScroll, this.scrollOffset - (float)amount * 20.0F));
      }
   }

   public boolean keyPressed(int keyCode) {
      if (this.listeningMenuKey) {
         this.listeningMenuKey = false;
         return true;
      }
      if (this.configInputFocused) {
         if (keyCode == 259 && !this.configInputText.isEmpty()) {
            this.configInputText = this.configInputText.substring(0, this.configInputText.length() - 1);
            return true;
         }
         if (keyCode == 257 || keyCode == 335) {
            String name = this.configInputText.trim();
            if (name.isEmpty()) name = "config_" + (Prestige.INSTANCE.getProfileManager().getAvailableConfigs().size() + 1);
            Prestige.INSTANCE.getProfileManager().saveProfile(name);
            this.configInputText = "";
            this.configInputFocused = false;
            return true;
         }
      }
      for (ModuleRow row : this.moduleRows) {
         if (row.keyPressed(keyCode)) return true;
      }
      return false;
   }

   public boolean charTyped(char chr) {
      if (this.configInputFocused && chr >= ' ' && chr != 127 && this.configInputText.length() < 16) {
         this.configInputText = this.configInputText + chr;
         return true;
      }
      return false;
   }

   public enum WindowType {
      CATEGORY,
      MENU,
      CONFIGS
   }
}
