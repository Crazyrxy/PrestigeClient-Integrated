package juxxl.prestige.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import juxxl.prestige.Prestige;
import juxxl.prestige.gui.nanovg.CategoryWindow;
import juxxl.prestige.gui.nanovg.SearchBar;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.modules.client.ClickGUI;
import juxxl.prestige.util.render.nanovg.NanoVGContext;
import juxxl.prestige.util.render.nanovg.NanoVGFrameManager;
import juxxl.prestige.util.render.nanovg.NanoVGRenderer;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

/**
 * ClickGui — Nocturne shell: slate glass panels, teal accent rail, compact cards.
 */
public final class ClickGui extends class_437 {
   public final List<CategoryWindow> windows = new ArrayList<>();
   public final SearchBar searchBar = new SearchBar();
   public Color currentColor;

   public ClickGui() {
      super(class_2561.method_43473());
      this.initWindows();
   }

   private static final float PANEL_WIDTH = 175.0F;
   private static final float PANEL_GAP = 10.0F;
   private static final float EDGE_PAD = 12.0F;

   private void initWindows() {
      this.windows.clear();
      float offsetX = EDGE_PAD;
      float startY = 20.0F;
      float spacing = PANEL_WIDTH + PANEL_GAP;

      for (Category category : Category.values()) {
         CategoryWindow w = new CategoryWindow(offsetX, startY, category);
         w.width = PANEL_WIDTH;
         this.windows.add(w);
         offsetX += spacing;
      }

      CategoryWindow menu = new CategoryWindow(offsetX, startY, "Menu", "assets/prestige/icons/brush.png", CategoryWindow.WindowType.MENU);
      menu.width = PANEL_WIDTH;
      this.windows.add(menu);
      offsetX += spacing;

      CategoryWindow configs = new CategoryWindow(offsetX, startY, "CONFIG", "assets/prestige/icons/file-braces-corner.png", CategoryWindow.WindowType.CONFIGS);
      configs.width = PANEL_WIDTH;
      this.windows.add(configs);
   }

   /** Scale so every panel fits on screen width (and leaves room for search bar). */
   public double getAutoScale() {
      class_310 mc = class_310.method_1551();
      if (mc == null || mc.method_22683() == null) {
         return 0.75;
      }
      float screenW = (float) mc.method_22683().method_4486();
      float screenH = (float) mc.method_22683().method_4502();
      int count = Math.max(1, this.windows.size());
      float totalW = EDGE_PAD * 2.0F + count * PANEL_WIDTH + (count - 1) * PANEL_GAP;
      float totalH = 20.0F + 420.0F + 56.0F; // top pad + panel body + search
      double scaleW = (screenW - 4.0) / totalW;
      double scaleH = (screenH - 4.0) / totalH;
      double scale = Math.min(scaleW, scaleH);
      if (scale > 1.0) scale = 1.0;
      if (scale < 0.35) scale = 0.35;
      return scale;
   }

   private void layoutWindows() {
      float offsetX = EDGE_PAD;
      float startY = 20.0F;
      float spacing = PANEL_WIDTH + PANEL_GAP;
      for (CategoryWindow w : this.windows) {
         if (!w.dragging) {
            w.x = offsetX;
            w.y = startY;
            w.width = PANEL_WIDTH;
         }
         offsetX += spacing;
      }
   }

   public Color getAccentColor() {
      for (CategoryWindow window : this.windows) {
         if (window.type == CategoryWindow.WindowType.MENU) {
            return window.colorPicker.getColor();
         }
      }

      return new Color(168, 85, 247);
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
   }

   public void renderNanoVG() {
      if (Prestige.mc.field_1755 == this) {
         class_310 mc = class_310.method_1551();
         if (mc != null && mc.method_22683() != null) {
            long handle = mc.method_22683().method_4490();
            int fbW = mc.method_22683().method_4480();
            int fbH = mc.method_22683().method_4507();
            if (handle != 0L && fbW > 0 && fbH > 0) {
               NanoVGFrameManager.beginFrame();
               long vg = NanoVGContext.getHandle();
               if (vg != 0L) {
                  double[] xpos = new double[1];
                  double[] ypos = new double[1];
                  GLFW.glfwGetCursorPos(handle, xpos, ypos);
                  double userScale = this.getAutoScale();

                  double scaleFactor = mc.method_22683().method_4495() * userScale;
                  int mouseX = (int)(xpos[0] / scaleFactor);
                  int mouseY = (int)(ypos[0] / scaleFactor);
                  float screenW = (float)(mc.method_22683().method_4486() / userScale);
                  float screenH = (float)(mc.method_22683().method_4502() / userScale);
                  Color accentColor = this.getAccentColor();
                  String searchQuery = this.searchBar.getQuery();
                  if (ClickGUI.background.getValue()) {
                     NanoVGRenderer.drawRect(0.0F, 0.0F, screenW, screenH, new Color(8, 6, 14, 150));
                  }

                  for (CategoryWindow window : this.windows) {
                     window.render(vg, mouseX, mouseY, accentColor, searchQuery);
                  }

                  this.searchBar.render(vg, screenW, screenH, mouseX, mouseY, accentColor);
                  NanoVGFrameManager.endFrame();
               }
            }
         }
      }
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      double userScale = this.getAutoScale();

      double mouseX = click.comp_4798() / userScale;
      double mouseY = click.comp_4799() / userScale;
      int button = click.method_74245();
      float screenW = (float)(Prestige.mc.method_22683().method_4486() / userScale);
      float screenH = (float)(Prestige.mc.method_22683().method_4502() / userScale);
      if (this.searchBar.mouseClicked(mouseX, mouseY, button, screenW, screenH)) {
         return true;
      }

      for (int i = this.windows.size() - 1; i >= 0; i--) {
         CategoryWindow window = this.windows.get(i);
         if (window.mouseClicked(mouseX, mouseY, button, this.searchBar.getQuery())) {
            this.windows.remove(i);
            this.windows.add(window);
            return true;
         }
      }

      return super.method_25402(click, doubled);
   }

   public boolean method_25406(class_11909 click) {
      int button = click.method_74245();

      for (CategoryWindow window : this.windows) {
         window.mouseReleased(button);
      }

      return super.method_25406(click);
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      for (CategoryWindow window : this.windows) {
         window.mouseScrolled(mouseX, mouseY, verticalAmount);
      }

      return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean method_25404(class_11908 keyInput) {
      int keyCode = keyInput.comp_4795();
      if (this.searchBar.keyPressed(keyCode)) {
         return true;
      }

      for (CategoryWindow window : this.windows) {
         if (window.keyPressed(keyCode)) {
            return true;
         }
      }

      return super.method_25404(keyInput);
   }

   public boolean method_25400(class_11905 charInput) {
      if (this.searchBar.charTyped((char)charInput.comp_4793())) {
         return true;
      }

      for (CategoryWindow window : this.windows) {
         if (window.charTyped((char)charInput.comp_4793())) {
            return true;
         }
      }

      return super.method_25400(charInput);
   }

   public boolean method_25421() {
      return false;
   }

   public void method_25419() {
      Prestige.INSTANCE.getModuleManager().getModule(ClickGUI.class).setEnabledStatus(false);
      this.onGuiClose();
   }

   public void onGuiClose() {
      Prestige.mc.method_1507(Prestige.INSTANCE.previousScreen);
      this.searchBar.setFocused(false);
   }
}
