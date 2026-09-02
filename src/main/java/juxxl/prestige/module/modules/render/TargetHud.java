package juxxl.prestige.module.modules.render;

import java.awt.Color;
import juxxl.prestige.event.events.HudListener;
import juxxl.prestige.event.events.PacketSendListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.MathUtils;
import juxxl.prestige.utils.RenderUtils;
import juxxl.prestige.utils.TextRenderer;
import juxxl.prestige.utils.Utils;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_332;
import net.minecraft.class_640;
import net.minecraft.class_7532;
import net.minecraft.class_2824.class_5908;
import org.joml.Matrix3x2fStack;

public final class TargetHud extends Module implements HudListener, PacketSendListener {
   private final NumberSetting xCoord = new NumberSetting("X", 0.0, 1920.0, 500.0, 1.0);
   private final NumberSetting yCoord = new NumberSetting("Y", 0.0, 1080.0, 500.0, 1.0);
   private final BooleanSetting hudTimeout = new BooleanSetting("Timeout", true).setDescription("Target hud will disappear after 10 seconds");
   private long lastAttackTime = 0L;
   public static float animation;
   private static final long timeout = 10000L;

   public TargetHud() {
      super("Target HUD", "Gives you information about the enemy player", -1, Category.RENDER);
      this.addSettings(this.xCoord, this.yCoord, this.hudTimeout);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(HudListener.class, this);
      this.eventManager.add(PacketSendListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(HudListener.class, this);
      this.eventManager.remove(PacketSendListener.class, this);
      super.onDisable();
   }

   @Override
   public void onRenderHud(HudListener.HudEvent event) {
      class_332 context = event.context;
      int x = this.xCoord.getValueInt();
      int y = this.yCoord.getValueInt();
      RenderUtils.unscaledProjection(context);
      if ((!this.hudTimeout.getValue() || System.currentTimeMillis() - this.lastAttackTime <= 10000L)
         && this.mc.field_1724.method_6052() != null
         && this.mc.field_1724.method_6052() instanceof class_1657 player
         && player.method_5805()) {
         animation = RenderUtils.fast(animation, this.mc.field_1724.method_6052() instanceof class_1657 player1 && player1.method_5805() ? 0 : 1, 15.0F);
         class_640 entry = this.mc.method_1562().method_2871(player.method_5667());
         Matrix3x2fStack matrices = context.method_51448();
         matrices.pushMatrix();
         matrices.scaleAround(Math.max(0.0F, 1.0F - animation), 1.0F, x + 170.0F, y + 100.0F);
         RenderUtils.renderRoundedQuad(context, new Color(0, 0, 0, 175), x, y, x + 340, y + 200, 5.0, 5.0, 5.0, 5.0, 10.0);
         RenderUtils.renderRoundedQuad(context, Utils.getMainColor(255, 1), x, y + 27, x + 340, y + 29, 0.0, 0.0, 0.0, 0.0, 10.0);
         TextRenderer.drawString(
            player.method_5477().getString() + " - " + MathUtils.roundToDecimal(player.method_5739(this.mc.field_1724), 0.5) + " blocks",
            context,
            x + 23,
            y + 5,
            Color.WHITE.getRGB()
         );
         if (entry == null) {
            int charOff1 = x + 5;
            CharSequence chars = "Type: Bot";
            TextRenderer.drawString(chars, context, charOff1, y + 35, new Color(255, 80, 80, 255).getRGB());
            matrices.popMatrix();
            RenderUtils.scaledProjection(context);
            return;
         }

         int charOff1 = x + 5;
         CharSequence chars = "Type: Player";
         TextRenderer.drawString(chars, context, charOff1, y + 35, Color.white.getRGB());
         TextRenderer.drawString("Health: " + Math.round(player.method_6032() + player.method_6067()), context, x + 5, y + 65, Color.GREEN.getRGB());
         context.method_25294(
            x, y + 200, x + 4, y + 200 - Math.min(Math.round((player.method_6032() + player.method_6067()) * 5.0F), 171), Color.GREEN.darker().getRGB()
         );
         TextRenderer.drawString("Invisible: " + (player.method_5767() ? "Yes" : "No"), context, x + 5, y + 95, Color.WHITE.getRGB());
         TextRenderer.drawString("Ping: " + entry.method_2959(), context, x + 5, y + 125, Color.WHITE.getRGB());
         class_7532.method_52722(context, entry.method_52810(), x + 3, y + 3, 20);
         if (player.field_6235 != 0) {
            charOff1 = x + 125;
            chars = "Damage Tick: " + player.field_6235;
            TextRenderer.drawString(chars, context, charOff1, y + 65, Color.WHITE.getRGB());
            context.method_25294(x + 125, y + 80, x + 125 + player.field_6235 * 15, y + 83, this.getDamageTickColor(player.field_6235).getRGB());
         }

         matrices.popMatrix();
      } else {
         animation = RenderUtils.fast(animation, 1.0F, 15.0F);
      }

      RenderUtils.scaledProjection(context);
   }

   private Color getDamageTickColor(int hurtTime) {
      return switch (hurtTime) {
         case 0 -> null;
         case 1 -> new Color(0, 255, 0, 255);
         case 2 -> new Color(50, 255, 0, 255);
         case 3 -> new Color(100, 255, 0, 255);
         case 4 -> new Color(175, 255, 0, 255);
         case 5 -> new Color(200, 255, 0, 255);
         case 6 -> new Color(255, 255, 0, 255);
         case 7 -> new Color(255, 150, 0, 255);
         case 8 -> new Color(255, 100, 0, 255);
         case 9 -> new Color(255, 50, 0, 255);
         case 10 -> new Color(255, 0, 0, 255);
         default -> throw new IllegalStateException("uv" + hurtTime);
      };
   }

   @Override
   public void onPacketSend(PacketSendListener.PacketSendEvent event) {
      if (event.packet instanceof class_2824 packet) {
         packet.method_34209(new class_5908() {
            public void method_34219(class_1268 hand) {
            }

            public void method_34220(class_1268 hand, class_243 pos) {
            }

            public void method_34218() {
               if (TargetHud.this.mc.field_1692 instanceof class_1657) {
                  TargetHud.this.lastAttackTime = System.currentTimeMillis();
               }
            }
         });
      }
   }
}
