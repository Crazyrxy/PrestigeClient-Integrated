package juxxl.prestige.gui.nanovg;

import java.awt.Color;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.KeybindSetting;
import juxxl.prestige.module.setting.MinMaxSetting;
import juxxl.prestige.module.setting.ModeSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.module.setting.Setting;
import juxxl.prestige.module.setting.StringSetting;
import juxxl.prestige.nanovg.NanoVGWidgets;
import juxxl.prestige.util.render.nanovg.NanoVGRenderer;
import juxxl.prestige.utils.KeyUtils;

public class ModuleRow {
   public final Module module;
   public boolean expanded = false;
   private Setting<?> activeDraggingSlider = null;
   private float hoverAlpha = 0.0F;
   private float enabledGlowAlpha = 0.0F;
   private static final float ROW_HEIGHT = 24.0F;
   private static final float SETTING_ROW_BOOL = 20.0F;
   private static final float SETTING_ROW_NUM = 28.0F;
   private static final float SETTING_ROW_MODE = 22.0F;
   private static final float SETTING_ROW_KEY = 22.0F;
   private static final float SETTING_ROW_STR = 22.0F;
   private static final float SETTING_ROW_DEFAULT = 20.0F;
   private static final Color TEXT_DIM = new Color(150, 150, 165);
   private static final Color TEXT_SETTING = new Color(175, 175, 190);
   private static final Color CHEVRON_COLOR = new Color(130, 130, 150);
   private static final Color SETTING_BG = new Color(14, 14, 20);
   private static final Color SETTING_BG_HOVER = new Color(28, 24, 40);
   private static final Color SETTING_OUTLINE = new Color(168, 85, 247, 40);

   public ModuleRow(Module module) {
      this.module = module;
   }

   public float getHeight() {
      float h = 24.0F;
      if (this.expanded && !this.module.getSettings().isEmpty()) {
         for (Setting<?> setting : this.module.getSettings()) {
            if (setting.isVisible()) {
               if (setting instanceof BooleanSetting) {
                  h += 20.0F;
               } else if (setting instanceof NumberSetting || setting instanceof MinMaxSetting) {
                  h += 28.0F;
               } else if (setting instanceof ModeSetting) {
                  h += 22.0F;
               } else if (setting instanceof KeybindSetting) {
                  h += 22.0F;
               } else if (setting instanceof StringSetting) {
                  h += 22.0F;
               } else {
                  h += 20.0F;
               }
            }
         }

         h += 4.0F;
      }

      return h;
   }

   public void render(long vg, float x, float y, float w, int mouseX, int mouseY, Color accentColor) {
      boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 24.0F;
      boolean enabled = this.module.isEnabled();
      float targetHover = hover ? 1.0F : 0.0F;
      this.hoverAlpha = this.hoverAlpha + (targetHover - this.hoverAlpha) * 0.25F;
      float targetGlow = enabled ? 1.0F : 0.0F;
      this.enabledGlowAlpha = this.enabledGlowAlpha + (targetGlow - this.enabledGlowAlpha) * 0.2F;

      // Hover wash
      if (this.hoverAlpha > 0.01F) {
         NanoVGRenderer.drawRoundedRect(x + 4.0F, y + 1.0F, w - 8.0F, 22.0F, 6.0F, new Color(255, 255, 255, (int)(14.0F * this.hoverAlpha)));
      }
      // Enabled purple wash
      if (this.enabledGlowAlpha > 0.01F) {
         int bgAlpha = (int)(40.0F * this.enabledGlowAlpha);
         NanoVGRenderer.drawRoundedRect(x + 4.0F, y + 1.0F, w - 8.0F, 22.0F, 6.0F, new Color(168, 85, 247, bgAlpha));
      }

      // Status dot (green = on, muted = off) like reference UI
      Color dot = enabled ? new Color(74, 222, 128) : new Color(80, 80, 95);
      NanoVGRenderer.drawCircle(x + 12.0F, y + 12.0F, 3.0F, dot);

      Color nameColor = enabled ? new Color(210, 160, 255) : new Color(230, 230, 238);
      String name = this.module.getName().toString();
      float nameX = x + 20.0F;
      NanoVGRenderer.drawText(name, nameX, y + 6.5F, 11.0F, nameColor, enabled);
      if (this.module.getKey() > 0 && this.module.getKey() != -1) {
         String keyTag = KeyUtils.getKey(this.module.getKey()).toString();
         float tagW = NanoVGRenderer.getTextWidth(keyTag, 9.5F);
         float tagX = x + w - (this.module.getSettings().isEmpty() ? 8.0F : 18.0F) - tagW;
         NanoVGRenderer.drawText(keyTag, tagX, y + 7.25F + 0.5F, 9.5F, new Color(113, 113, 122), false);
      }

      if (!this.module.getSettings().isEmpty()) {
         float cx = x + w - 10.0F;
         float cy = y + 12.0F;
         Color chevronColor = this.expanded ? (accentColor != null ? accentColor : new Color(192, 132, 252)) : CHEVRON_COLOR;
         if (this.expanded) {
            NanoVGRenderer.drawLine(cx - 3.0F, cy + 1.5F, cx, cy - 1.5F, 1.2F, chevronColor);
            NanoVGRenderer.drawLine(cx, cy - 1.5F, cx + 3.0F, cy + 1.5F, 1.2F, chevronColor);
         } else {
            NanoVGRenderer.drawLine(cx - 3.0F, cy - 1.5F, cx, cy + 1.5F, 1.2F, chevronColor);
            NanoVGRenderer.drawLine(cx, cy + 1.5F, cx + 3.0F, cy - 1.5F, 1.2F, chevronColor);
         }
      }

      if (this.expanded && !this.module.getSettings().isEmpty()) {
         NanoVGRenderer.drawLine(x + 6.0F, y + 24.0F, x + w - 6.0F, y + 24.0F, 1.0F, new Color(255, 255, 255, 8));
         float currentY = y + 24.0F + 3.0F;
         float indentX = x + 8.0F;
         float settingW = w - 16.0F;

         for (Setting<?> setting : this.module.getSettings()) {
            if (setting.isVisible()) {
               if (setting instanceof BooleanSetting boolSetting) {
                  this.renderBooleanSetting(vg, boolSetting, indentX, currentY, settingW, mouseX, mouseY, accentColor);
                  currentY += 20.0F;
               } else if (setting instanceof NumberSetting numSetting) {
                  this.renderNumberSetting(vg, numSetting, indentX, currentY, settingW, mouseX, mouseY, accentColor);
                  currentY += 28.0F;
               } else if (setting instanceof ModeSetting<?> modeSetting) {
                  this.renderModeSetting(vg, modeSetting, indentX, currentY, settingW, mouseX, mouseY, accentColor);
                  currentY += 22.0F;
               } else if (setting instanceof KeybindSetting keySetting) {
                  this.renderKeybindSetting(vg, keySetting, indentX, currentY, settingW, mouseX, mouseY, accentColor);
                  currentY += 22.0F;
               } else if (setting instanceof StringSetting strSetting) {
                  this.renderStringSetting(vg, strSetting, indentX, currentY, settingW, mouseX, mouseY, accentColor);
                  currentY += 22.0F;
               }
            }
         }
      }
   }

   private void renderBooleanSetting(long vg, BooleanSetting setting, float x, float y, float w, int mouseX, int mouseY, Color accentColor) {
      boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 20.0F;
      NanoVGRenderer.drawText(setting.getName().toString(), x, y + 4.5F, 10.5F, new Color(220, 240, 242), false);
      float toggleX = x + w - 26.0F;
      float toggleY = y + 4.0F;
      float[] accentRgb = new float[]{accentColor.getRed() / 255.0F, accentColor.getGreen() / 255.0F, accentColor.getBlue() / 255.0F};
      NanoVGWidgets.toggle(vg, NanoVGWidgets.getColor1(), toggleX, toggleY, setting.getValue(), hover, 1.0F, accentRgb);
   }

   private void renderNumberSetting(long vg, NumberSetting setting, float x, float y, float w, int mouseX, int mouseY, Color accentColor) {
      boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 28.0F;
      NanoVGRenderer.drawText(setting.getName().toString(), x, y + 3.0F, 10.5F, new Color(130, 155, 160), false);
      String valStr = String.format("%.1f", setting.getValue());
      float valW = NanoVGRenderer.getTextWidth(valStr, 10.0F);
      NanoVGRenderer.drawText(valStr, x + w - valW, y + 3.0F, 10.0F, new Color(228, 228, 231), false);
      float sliderY = y + 18.0F;
      float sliderH = 4.0F;
      float progress = (float)((setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin()));
      float[] accentRgb = new float[]{accentColor.getRed() / 255.0F, accentColor.getGreen() / 255.0F, accentColor.getBlue() / 255.0F};
      NanoVGWidgets.slider(vg, NanoVGWidgets.getColor1(), x, sliderY, w, sliderH, progress, hover, 1.0F, accentRgb);
      if (this.activeDraggingSlider == setting) {
         double newPct = Math.max(0.0, Math.min(1.0, (double)(mouseX - x) / w));
         double val = setting.getMin() + newPct * (setting.getMax() - setting.getMin());
         setting.setValue(val);
      }
   }

   private void renderModeSetting(long vg, ModeSetting<?> setting, float x, float y, float w, int mouseX, int mouseY, Color accentColor) {
      NanoVGRenderer.drawText(setting.getName().toString(), x, y + 5.5F, 10.5F, new Color(130, 155, 160), false);
      Object modeObj = setting.getMode();
      String modeName = modeObj != null ? modeObj.toString() : "?";
      float btnW = Math.max(50.0F, NanoVGRenderer.getTextWidth(modeName, 10.0F) + 16.0F);
      float btnX = x + w - btnW;
      float btnY = y + 2.0F;
      float btnH = 19.0F;
      boolean hover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
      NanoVGRenderer.drawRoundedRect(btnX, btnY, btnW, btnH, 4.0F, hover ? SETTING_BG_HOVER : SETTING_BG);
      NanoVGRenderer.drawRoundedRectOutline(btnX, btnY, btnW, btnH, 4.0F, 1.0F, SETTING_OUTLINE);
      float textX = btnX + (btnW - NanoVGRenderer.getTextWidth(modeName, 10.0F)) / 2.0F;
      NanoVGRenderer.drawText(modeName, textX, btnY + 4.0F, 10.0F, Color.WHITE, false);
   }

   private void renderKeybindSetting(long vg, KeybindSetting setting, float x, float y, float w, int mouseX, int mouseY, Color accentColor) {
      NanoVGRenderer.drawText(setting.getName().toString(), x, y + 6.0F, 10.5F, TEXT_SETTING, false);
      String keyName = setting.isListening() ? "[PRESS KEY]" : KeyUtils.getKey(setting.getKey()).toString();
      float btnW = Math.max(48.0F, NanoVGRenderer.getTextWidth(keyName, 10.0F) + 16.0F);
      float btnX = x + w - btnW;
      float btnY = y + 2.0F;
      float btnH = 20.0F;
      boolean hover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
      Color bg = setting.isListening() ? accentColor : (hover ? SETTING_BG_HOVER : SETTING_BG);
      NanoVGRenderer.drawRoundedRect(btnX, btnY, btnW, btnH, 4.0F, bg);
      NanoVGRenderer.drawRoundedRectOutline(btnX, btnY, btnW, btnH, 4.0F, 1.0F, SETTING_OUTLINE);
      float textX = btnX + btnW / 2.0F - NanoVGRenderer.getTextWidth(keyName, 10.0F) / 2.0F;
      NanoVGRenderer.drawText(keyName, textX, btnY + 4.5F, 10.0F, Color.WHITE, setting.isListening());
   }

   private void renderStringSetting(long vg, StringSetting setting, float x, float y, float w, int mouseX, int mouseY, Color accentColor) {
      NanoVGRenderer.drawText(setting.getName().toString(), x, y + 6.0F, 10.5F, TEXT_SETTING, false);
      float inputW = 80.0F;
      float inputX = x + w - inputW;
      float inputY = y + 3.0F;
      float inputH = 21.0F;
      NanoVGWidgets.inputField(vg, NanoVGWidgets.getColor1(), inputX, inputY, inputW, inputH, setting.getValue(), "Text...", false, 1.0F);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y, float w) {
      if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 24.0F) {
         if (button == 0) {
            this.module.toggle();
            return true;
         }

         if (button == 1) {
            this.expanded = !this.expanded;
            return true;
         }
      }

      if (this.expanded && !this.module.getSettings().isEmpty()) {
         float currentY = y + 24.0F + 3.0F;
         float indentX = x + 10.0F;
         float settingW = w - 20.0F;

         for (Setting<?> setting : this.module.getSettings()) {
            if (setting.isVisible()) {
               if (setting instanceof BooleanSetting boolSetting) {
                  if (mouseX >= indentX && mouseX <= indentX + settingW && mouseY >= currentY && mouseY <= currentY + 20.0F) {
                     boolSetting.setValue(!boolSetting.getValue());
                     return true;
                  }

                  currentY += 20.0F;
               } else if (setting instanceof NumberSetting numSetting) {
                  if (mouseX >= indentX && mouseX <= indentX + settingW && mouseY >= currentY + 16.0F && mouseY <= currentY + 30.0F) {
                     this.activeDraggingSlider = numSetting;
                     double pct = Math.max(0.0, Math.min(1.0, (mouseX - indentX) / settingW));
                     numSetting.setValue(numSetting.getMin() + pct * (numSetting.getMax() - numSetting.getMin()));
                     return true;
                  }

                  currentY += 28.0F;
               } else if (setting instanceof ModeSetting<?> modeSetting) {
                  if (mouseX >= indentX && mouseX <= indentX + settingW && mouseY >= currentY && mouseY <= currentY + 22.0F) {
                     modeSetting.cycle();
                     return true;
                  }

                  currentY += 22.0F;
               } else if (setting instanceof KeybindSetting keySetting) {
                  float btnW = 50.0F;
                  float btnX = indentX + settingW - btnW;
                  if (mouseX >= btnX && mouseX <= indentX + settingW && mouseY >= currentY && mouseY <= currentY + 22.0F) {
                     keySetting.toggleListening();
                     return true;
                  }

                  currentY += 22.0F;
               } else if (setting instanceof StringSetting) {
                  currentY += 22.0F;
               }
            }
         }
      }

      return false;
   }

   public void mouseReleased(int button) {
      if (button == 0) {
         this.activeDraggingSlider = null;
      }
   }

   public boolean keyPressed(int keyCode) {
      if (this.expanded && !this.module.getSettings().isEmpty()) {
         for (Setting<?> setting : this.module.getSettings()) {
            if (setting.isVisible() && setting instanceof KeybindSetting keySetting && keySetting.isListening()) {
               if (keyCode != 256 && keyCode != 261) {
                  keySetting.setKey(keyCode);
               } else {
                  keySetting.setKey(-1);
               }

               keySetting.setListening(false);
               return true;
            }
         }
      }

      return false;
   }
}
