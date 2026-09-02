package juxxl.prestige.font;

import java.awt.Font;
import java.util.Random;
import net.minecraft.class_332;
import org.joml.Matrix3x2fStack;

public final class GlyphPageFontRenderer {
   private static final char SECTION_SIGN = '§';
   public Random fontRandom = new Random();
   private float posX;
   private float posY;
   private final int[] colorCode = new int[32];
   private boolean boldStyle;
   private boolean italicStyle;
   private boolean underlineStyle;
   private boolean strikethroughStyle;
   private final GlyphPage regularGlyphPage;
   private final GlyphPage boldGlyphPage;
   private final GlyphPage italicGlyphPage;
   private final GlyphPage boldItalicGlyphPage;

   public GlyphPageFontRenderer(GlyphPage regularGlyphPage, GlyphPage boldGlyphPage, GlyphPage italicGlyphPage, GlyphPage boldItalicGlyphPage) {
      this.regularGlyphPage = regularGlyphPage;
      this.boldGlyphPage = boldGlyphPage;
      this.italicGlyphPage = italicGlyphPage;
      this.boldItalicGlyphPage = boldItalicGlyphPage;

      for (int i = 0; i < 32; i++) {
         int j = (i >> 3 & 1) * 85;
         int k = (i >> 2 & 1) * 170 + j;
         int l = (i >> 1 & 1) * 170 + j;
         int i1 = (i & 1) * 170 + j;
         if (i == 6) {
            k += 85;
         }

         if (i >= 16) {
            k /= 4;
            l /= 4;
            i1 /= 4;
         }

         this.colorCode[i] = (k & 0xFF) << 16 | (l & 0xFF) << 8 | i1 & 0xFF;
      }
   }

   public static GlyphPageFontRenderer create(CharSequence fontName, int size, boolean bold, boolean italic, boolean boldItalic) {
      char[] chars = new char[256];

      for (int i = 0; i < chars.length; i++) {
         chars[i] = (char)i;
      }

      GlyphPage regularPage = new GlyphPage(new Font(fontName.toString(), 0, size), true, true);
      regularPage.generateGlyphPage(chars);
      regularPage.setupTexture();
      GlyphPage boldPage = regularPage;
      GlyphPage italicPage = regularPage;
      GlyphPage boldItalicPage = regularPage;
      if (bold) {
         boldPage = new GlyphPage(new Font(fontName.toString(), 1, size), true, true);
         boldPage.generateGlyphPage(chars);
         boldPage.setupTexture();
      }

      if (italic) {
         italicPage = new GlyphPage(new Font(fontName.toString(), 2, size), true, true);
         italicPage.generateGlyphPage(chars);
         italicPage.setupTexture();
      }

      if (boldItalic) {
         boldItalicPage = new GlyphPage(new Font(fontName.toString(), 3, size), true, true);
         boldItalicPage.generateGlyphPage(chars);
         boldItalicPage.setupTexture();
      }

      return new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
   }

   public static GlyphPageFontRenderer createFromID(CharSequence id, int size, boolean bold, boolean italic, boolean boldItalic) {
      char[] chars = new char[256];

      for (int i = 0; i < chars.length; i++) {
         chars[i] = (char)i;
      }

      Font font = null;

      try {
         font = Font.createFont(0, GlyphPageFontRenderer.class.getResourceAsStream(id.toString())).deriveFont(0, size);
      } catch (Exception e) {
         e.printStackTrace();
      }

      GlyphPage regularPage = new GlyphPage(font, true, true);
      regularPage.generateGlyphPage(chars);
      regularPage.setupTexture();
      GlyphPage boldPage = regularPage;
      GlyphPage italicPage = regularPage;
      GlyphPage boldItalicPage = regularPage;

      try {
         if (bold) {
            boldPage = new GlyphPage(Font.createFont(0, GlyphPageFontRenderer.class.getResourceAsStream(id.toString())).deriveFont(1, size), true, true);
            boldPage.generateGlyphPage(chars);
            boldPage.setupTexture();
         }

         if (italic) {
            italicPage = new GlyphPage(Font.createFont(0, GlyphPageFontRenderer.class.getResourceAsStream(id.toString())).deriveFont(2, size), true, true);
            italicPage.generateGlyphPage(chars);
            italicPage.setupTexture();
         }

         if (boldItalic) {
            boldItalicPage = new GlyphPage(Font.createFont(0, GlyphPageFontRenderer.class.getResourceAsStream(id.toString())).deriveFont(3, size), true, true);
            boldItalicPage.generateGlyphPage(chars);
            boldItalicPage.setupTexture();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
   }

   public int drawStringWithShadow(class_332 context, CharSequence text, float x, float y, int color) {
      return this.drawString(context, text, x, y, color, true);
   }

   public int drawStringWithShadow(class_332 context, CharSequence text, double x, double y, int color) {
      return this.drawString(context, text, (float)x, (float)y, color, true);
   }

   public int drawString(class_332 context, CharSequence text, float x, float y, int color) {
      return this.drawString(context, text, x, y, color, false);
   }

   public int drawString(class_332 context, CharSequence text, double x, double y, int color) {
      return this.drawString(context, text, (float)x, (float)y, color, false);
   }

   public int drawCenteredString(class_332 context, CharSequence text, double x, double y, float scale, int color) {
      return this.drawString(context, text, (float)x - this.getStringWidth(text) / 2, (float)y, scale, color, false);
   }

   public int drawCenteredString(class_332 context, CharSequence text, double x, double y, int color) {
      return this.drawString(context, text, (float)x - this.getStringWidth(text) / 2, (float)y, color, false);
   }

   public int drawCenteredStringWidthShadow(class_332 context, CharSequence text, double x, double y, int color) {
      return this.drawString(context, text, (float)x - this.getStringWidth(text) / 2, (float)y, color, true);
   }

   public int drawString(class_332 context, CharSequence text, float x, float y, float scale, int color, boolean dropShadow) {
      this.resetStyles();
      int i;
      if (dropShadow) {
         i = this.renderString(context, text, x + 1.0F, y + 1.0F, scale, color, true);
         i = Math.max(i, this.renderString(context, text, x, y, scale, color, false));
      } else {
         i = this.renderString(context, text, x, y, scale, color, false);
      }

      return i;
   }

   public int drawString(class_332 context, CharSequence text, float x, float y, int color, boolean dropShadow) {
      this.resetStyles();
      int i;
      if (dropShadow) {
         i = this.renderString(context, text, x + 1.0F, y + 1.0F, color, true);
         i = Math.max(i, this.renderString(context, text, x, y, color, false));
      } else {
         i = this.renderString(context, text, x, y, color, false);
      }

      return i;
   }

   private int renderString(class_332 context, CharSequence text, float x, float y, int color, boolean dropShadow) {
      if (text == null) {
         return 0;
      }

      if ((color & -67108864) == 0) {
         color |= -16777216;
      }

      if (dropShadow) {
         color = (color & 16579836) >> 2 | color & 0xFF000000;
      }

      this.posX = x * 2.0F;
      this.posY = y * 2.0F;
      this.renderStringAtPos(context, text, dropShadow, color);
      return (int)(this.posX / 4.0F);
   }

   private int renderString(class_332 context, CharSequence text, float x, float y, float scale, int color, boolean dropShadow) {
      if (text == null) {
         return 0;
      }

      if ((color & -67108864) == 0) {
         color |= -16777216;
      }

      if (dropShadow) {
         color = (color & 16579836) >> 2 | color & 0xFF000000;
      }

      this.posX = x * 2.0F;
      this.posY = y * 2.0F;
      this.renderStringAtPos(context, text, scale, dropShadow, color);
      return (int)(this.posX / 4.0F);
   }

   private void renderStringAtPos(class_332 context, CharSequence text, boolean shadow, int color) {
      GlyphPage glyphPage = this.getCurrentGlyphPage();
      float alpha = (color >> 24 & 0xFF) / 255.0F;
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      Matrix3x2fStack matrices = context.method_51448();
      matrices.pushMatrix();
      matrices.scale(0.5F, 0.5F);

      for (int i = 0; i < text.length(); i++) {
         char c0 = text.charAt(i);
         if (c0 == 167 && i + 1 < text.length()) {
            int i1 = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(text.charAt(i + 1)));
            if (i1 < 16) {
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               if (i1 < 0) {
                  i1 = 15;
               }

               if (shadow) {
                  i1 += 16;
               }

               int j1 = this.colorCode[i1];
               red = (j1 >> 16 & 0xFF) / 255.0F;
               green = (j1 >> 8 & 0xFF) / 255.0F;
               blue = (j1 & 0xFF) / 255.0F;
            } else if (i1 != 16) {
               if (i1 == 17) {
                  this.boldStyle = true;
               } else if (i1 == 18) {
                  this.strikethroughStyle = true;
               } else if (i1 == 19) {
                  this.underlineStyle = true;
               } else if (i1 == 20) {
                  this.italicStyle = true;
               } else {
                  this.boldStyle = false;
                  this.strikethroughStyle = false;
                  this.underlineStyle = false;
                  this.italicStyle = false;
               }
            }

            i++;
         } else {
            glyphPage = this.getCurrentGlyphPage();
            int currentColor = this.toArgb(red, green, blue, alpha);
            float f = glyphPage.drawChar(context, c0, this.posX, this.posY, currentColor);
            this.doDraw(context, f, glyphPage, currentColor);
         }
      }

      matrices.popMatrix();
   }

   private void renderStringAtPos(class_332 context, CharSequence text, float scale, boolean shadow, int color) {
      GlyphPage glyphPage = this.getCurrentGlyphPage();
      float alpha = (color >> 24 & 0xFF) / 255.0F;
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      Matrix3x2fStack matrices = context.method_51448();
      matrices.pushMatrix();
      matrices.scale(scale, scale);

      for (int i = 0; i < text.length(); i++) {
         char c0 = text.charAt(i);
         if (c0 == 167 && i + 1 < text.length()) {
            int i1 = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(text.charAt(i + 1)));
            if (i1 < 16) {
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               if (i1 < 0) {
                  i1 = 15;
               }

               if (shadow) {
                  i1 += 16;
               }

               int j1 = this.colorCode[i1];
               red = (j1 >> 16 & 0xFF) / 255.0F;
               green = (j1 >> 8 & 0xFF) / 255.0F;
               blue = (j1 & 0xFF) / 255.0F;
            } else if (i1 != 16) {
               if (i1 == 17) {
                  this.boldStyle = true;
               } else if (i1 == 18) {
                  this.strikethroughStyle = true;
               } else if (i1 == 19) {
                  this.underlineStyle = true;
               } else if (i1 == 20) {
                  this.italicStyle = true;
               } else {
                  this.boldStyle = false;
                  this.strikethroughStyle = false;
                  this.underlineStyle = false;
                  this.italicStyle = false;
               }
            }

            i++;
         } else {
            glyphPage = this.getCurrentGlyphPage();
            int currentColor = this.toArgb(red, green, blue, alpha);
            float f = glyphPage.drawChar(context, c0, this.posX, this.posY, currentColor);
            this.doDraw(context, f, glyphPage, currentColor);
         }
      }

      matrices.popMatrix();
   }

   private void doDraw(class_332 context, float f, GlyphPage glyphPage, int color) {
      if (this.strikethroughStyle) {
         this.drawLine(context, this.posX, this.posX + f, this.posY + glyphPage.getMaxFontHeight() / 2.0F, color);
      }

      if (this.underlineStyle) {
         this.drawLine(context, this.posX - 1.0F, this.posX + f, this.posY + glyphPage.getMaxFontHeight(), color);
      }

      this.posX += f;
   }

   private void drawLine(class_332 context, float startX, float endX, float y, int color) {
      int left = Math.round(Math.min(startX, endX));
      int right = Math.max(left + 1, Math.round(Math.max(startX, endX)));
      int top = Math.round(y - 1.0F);
      context.method_25294(left, top, right, top + 1, color);
   }

   private int toArgb(float red, float green, float blue, float alpha) {
      int packedAlpha = Math.max(0, Math.min(255, Math.round(alpha * 255.0F)));
      int packedRed = Math.max(0, Math.min(255, Math.round(red * 255.0F)));
      int packedGreen = Math.max(0, Math.min(255, Math.round(green * 255.0F)));
      int packedBlue = Math.max(0, Math.min(255, Math.round(blue * 255.0F)));
      return packedAlpha << 24 | packedRed << 16 | packedGreen << 8 | packedBlue;
   }

   private GlyphPage getCurrentGlyphPage() {
      if (this.boldStyle && this.italicStyle) {
         return this.boldItalicGlyphPage;
      } else if (this.boldStyle) {
         return this.boldGlyphPage;
      } else {
         return this.italicStyle ? this.italicGlyphPage : this.regularGlyphPage;
      }
   }

   private void resetStyles() {
      this.boldStyle = false;
      this.italicStyle = false;
      this.underlineStyle = false;
      this.strikethroughStyle = false;
   }

   public int getFontHeight() {
      return this.regularGlyphPage.getMaxFontHeight() / 2;
   }

   public int getStringWidth(CharSequence text) {
      if (text == null) {
         return 0;
      }

      this.resetStyles();
      int width = 0;
      int size = text.length();
      boolean on = false;

      for (int i = 0; i < size; i++) {
         char character = text.charAt(i);
         if (character == 167) {
            on = true;
         } else if (on && character >= '0' && character <= 'r') {
            int colorIndex = "0123456789abcdefklmnor".indexOf(character);
            if (colorIndex < 16) {
               this.boldStyle = false;
               this.italicStyle = false;
            } else if (colorIndex == 17) {
               this.boldStyle = true;
            } else if (colorIndex == 20) {
               this.italicStyle = true;
            } else if (colorIndex == 21) {
               this.boldStyle = false;
               this.italicStyle = false;
            }

            i++;
            on = false;
         } else {
            if (on) {
               i--;
            }

            character = text.charAt(i);
            GlyphPage currentPage = this.getCurrentGlyphPage();
            width = (int)(width + (currentPage.getWidth(character) - 8.0F));
         }
      }

      return width / 2;
   }

   public CharSequence trimStringToWidth(CharSequence text, int width) {
      return this.trimStringToWidth(text, width, false);
   }

   public CharSequence trimStringToWidth(CharSequence text, int maxWidth, boolean reverse) {
      StringBuilder stringbuilder = new StringBuilder();
      this.resetStyles();
      boolean on = false;
      int j = reverse ? text.length() - 1 : 0;
      int k = reverse ? -1 : 1;
      int width = 0;

      for (int i = j; i >= 0 && i < text.length() && i < maxWidth; i += k) {
         char character = text.charAt(i);
         if (character == 167) {
            on = true;
         } else if (on && character >= '0' && character <= 'r') {
            int colorIndex = "0123456789abcdefklmnor".indexOf(character);
            if (colorIndex < 16) {
               this.boldStyle = false;
               this.italicStyle = false;
            } else if (colorIndex == 17) {
               this.boldStyle = true;
            } else if (colorIndex == 20) {
               this.italicStyle = true;
            } else if (colorIndex == 21) {
               this.boldStyle = false;
               this.italicStyle = false;
            }

            i++;
            on = false;
         } else {
            if (on) {
               i--;
            }

            character = text.charAt(i);
            GlyphPage currentPage = this.getCurrentGlyphPage();
            width = (int)(width + (currentPage.getWidth(character) - 8.0F) / 2.0F);
         }

         if (i > width) {
            break;
         }

         if (reverse) {
            stringbuilder.insert(0, character);
         } else {
            stringbuilder.append(character);
         }
      }

      return stringbuilder.toString();
   }
}
