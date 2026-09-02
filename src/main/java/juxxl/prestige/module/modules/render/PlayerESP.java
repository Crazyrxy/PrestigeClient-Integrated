package juxxl.prestige.module.modules.render;

import java.awt.Color;
import juxxl.prestige.event.events.GameRenderListener;
import juxxl.prestige.event.events.HudListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.ModeSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.ProjectionUtils;
import juxxl.prestige.utils.RenderUtils;
import juxxl.prestige.utils.Utils;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_7833;

public final class PlayerESP extends Module implements GameRenderListener, HudListener {
   public final ModeSetting<PlayerESP.Mode> mode = new ModeSetting<>("Mode", PlayerESP.Mode.ThreeD, PlayerESP.Mode.class);
   private final NumberSetting alpha = new NumberSetting("Alpha", 0.0, 255.0, 100.0, 1.0);
   private final NumberSetting width = new NumberSetting("Line width", 1.0, 10.0, 1.0, 1.0);
   private final BooleanSetting tracers = new BooleanSetting("Tracers", false).setDescription("Draws a line from your player to the other");
   private final BooleanSetting threeDOutline = new BooleanSetting("3D box outline", false);
   private final BooleanSetting twoDOutline = new BooleanSetting("2D Outline", false);

   public PlayerESP() {
      super("Player ESP", "Renders players through walls", -1, Category.RENDER);
      this.addSettings(this.alpha, this.mode, this.threeDOutline, this.twoDOutline, this.width, this.tracers);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(GameRenderListener.class, this);
      this.eventManager.add(HudListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(GameRenderListener.class, this);
      this.eventManager.remove(HudListener.class, this);
      super.onDisable();
   }

   @Override
   public void onGameRender(GameRenderListener.GameRenderEvent event) {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         class_4184 camera = this.mc.field_1773.method_19418();
         if (camera != null) {
            event.matrices.method_22903();
            class_243 cameraPos = camera.method_71156();
            event.matrices.method_22907(class_7833.field_40714.rotationDegrees(camera.method_19329()));
            event.matrices.method_22907(class_7833.field_40716.rotationDegrees(camera.method_19330() + 180.0F));
            event.matrices.method_22904(-cameraPos.field_1352, -cameraPos.field_1351, -cameraPos.field_1350);

            for (class_1657 player : this.mc.field_1687.method_18456()) {
               if (this.shouldRender(player)) {
                  class_238 box = this.getRenderBox(player, event.delta).method_1014(0.02);
                  if (this.mode.isMode(PlayerESP.Mode.ThreeD)) {
                     RenderUtils.renderFilledBox(
                        event.matrices,
                        (float)box.field_1323,
                        (float)box.field_1322,
                        (float)box.field_1321,
                        (float)box.field_1320,
                        (float)box.field_1325,
                        (float)box.field_1324,
                        this.getColor(this.alpha.getValueInt()).brighter()
                     );
                     if (this.threeDOutline.getValue()) {
                        RenderUtils.renderBoxOutline(event.matrices, box, this.getColor(255), this.width.getValueInt());
                     }
                  }

                  if (this.tracers.getValue() && this.mc.field_1765 != null) {
                     RenderUtils.renderLine(
                        event.matrices, Utils.getMainColor(255, 1), this.mc.field_1765.method_17784(), player.method_30950(RenderUtils.tickProgress())
                     );
                  }
               }
            }

            event.matrices.method_22909();
         }
      }
   }

   @Override
   public void onRenderHud(HudListener.HudEvent event) {
      if (this.mode.isMode(PlayerESP.Mode.TwoD) && this.mc.field_1687 != null && this.mc.field_1724 != null) {
         for (class_1657 player : this.mc.field_1687.method_18456()) {
            if (this.shouldRender(player)) {
               PlayerESP.ScreenBounds bounds = this.projectBounds(this.getRenderBox(player, event.delta));
               if (bounds != null && bounds.isValid()) {
                  Color outlineColor = this.getColor(255);
                  int thickness = Math.max(1, this.width.getValueInt());
                  if (this.twoDOutline.getValue()) {
                     Color borderColor = new Color(0, 0, 0, Math.min(255, outlineColor.getAlpha()));
                     this.drawOutline(event.context, bounds, thickness + 1, borderColor);
                  }

                  this.drawOutline(event.context, bounds, thickness, outlineColor);
               }
            }
         }
      }
   }

   private boolean shouldRender(class_1297 entity) {
      return entity instanceof class_1657 player && player != this.mc.field_1724 && player.method_5805();
   }

   private class_238 getRenderBox(class_1657 player, float tickDelta) {
      double x = class_3532.method_16436(tickDelta, player.field_6014, player.method_23317());
      double y = class_3532.method_16436(tickDelta, player.field_6036, player.method_23318());
      double z = class_3532.method_16436(tickDelta, player.field_5969, player.method_23321());
      return player.method_5829().method_989(x - player.method_23317(), y - player.method_23318(), z - player.method_23321());
   }

   private PlayerESP.ScreenBounds projectBounds(class_238 box) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      int visibleCorners = 0;

      for (double x : new double[]{box.field_1323, box.field_1320}) {
         for (double y : new double[]{box.field_1322, box.field_1325}) {
            for (double z : new double[]{box.field_1321, box.field_1324}) {
               ProjectionUtils.ProjectedPoint projected = ProjectionUtils.project(new class_243(x, y, z));
               if (projected != null) {
                  visibleCorners++;
                  minX = Math.min(minX, projected.x());
                  minY = Math.min(minY, projected.y());
                  maxX = Math.max(maxX, projected.x());
                  maxY = Math.max(maxY, projected.y());
               }
            }
         }
      }

      return visibleCorners == 0 ? null : new PlayerESP.ScreenBounds((int)Math.floor(minX), (int)Math.floor(minY), (int)Math.ceil(maxX), (int)Math.ceil(maxY));
   }

   private void drawOutline(class_332 context, PlayerESP.ScreenBounds bounds, int thickness, Color color) {
      int packedColor = color.getRGB();
      context.method_25294(bounds.minX, bounds.minY, bounds.maxX, bounds.minY + thickness, packedColor);
      context.method_25294(bounds.minX, bounds.maxY - thickness, bounds.maxX, bounds.maxY, packedColor);
      context.method_25294(bounds.minX, bounds.minY + thickness, bounds.minX + thickness, bounds.maxY - thickness, packedColor);
      context.method_25294(bounds.maxX - thickness, bounds.minY + thickness, bounds.maxX, bounds.maxY - thickness, packedColor);
   }

   private Color getColor(int alpha) {
      return Utils.getMainColor(alpha, 1);
   }

   public enum Mode {
      TwoD,
      ThreeD;
   }

   private record ScreenBounds(int minX, int minY, int maxX, int maxY) {
      private boolean isValid() {
         return this.maxX > this.minX && this.maxY > this.minY;
      }
   }
}
