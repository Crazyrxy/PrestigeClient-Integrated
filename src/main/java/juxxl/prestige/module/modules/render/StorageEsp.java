package juxxl.prestige.module.modules.render;

import java.awt.Color;
import juxxl.prestige.event.events.GameRenderListener;
import juxxl.prestige.event.events.PacketReceiveListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.RenderUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_2595;
import net.minecraft.class_2605;
import net.minecraft.class_2611;
import net.minecraft.class_2627;
import net.minecraft.class_2636;
import net.minecraft.class_2637;
import net.minecraft.class_2646;
import net.minecraft.class_2818;
import net.minecraft.class_3719;
import net.minecraft.class_3866;
import net.minecraft.class_4184;
import net.minecraft.class_7833;

public final class StorageEsp extends Module implements GameRenderListener, PacketReceiveListener {
   private final NumberSetting alpha = new NumberSetting("Alpha", 1.0, 255.0, 125.0, 1.0);
   private final BooleanSetting donutBypass = new BooleanSetting("Donut Bypass", false);
   private final BooleanSetting tracers = new BooleanSetting("Tracers", false).setDescription("Draws a line from your player to the storage block");

   public StorageEsp() {
      super("Storage ESP", "Renders storage blocks through walls", -1, Category.RENDER);
      this.addSettings(this.donutBypass, this.alpha, this.tracers);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(PacketReceiveListener.class, this);
      this.eventManager.add(GameRenderListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(PacketReceiveListener.class, this);
      this.eventManager.remove(GameRenderListener.class, this);
      super.onDisable();
   }

   @Override
   public void onGameRender(GameRenderListener.GameRenderEvent event) {
      this.renderStorages(event);
   }

   private Color getColor(class_2586 blockEntity, int a) {
      if (blockEntity instanceof class_2646) {
         return new Color(200, 91, 0, a);
      } else if (blockEntity instanceof class_2595) {
         return new Color(156, 91, 0, a);
      } else if (blockEntity instanceof class_2611) {
         return new Color(117, 0, 255, a);
      } else if (blockEntity instanceof class_2636) {
         return new Color(138, 126, 166, a);
      } else if (blockEntity instanceof class_2627) {
         return new Color(134, 0, 158, a);
      } else if (blockEntity instanceof class_3866) {
         return new Color(125, 125, 125, a);
      } else if (blockEntity instanceof class_3719) {
         return new Color(255, 140, 140, a);
      } else {
         return blockEntity instanceof class_2605 ? new Color(80, 80, 255, a) : new Color(255, 255, 255, 0);
      }
   }

   private void renderStorages(GameRenderListener.GameRenderEvent event) {
      class_4184 cam = this.mc.field_1773.method_19418();
      if (cam != null) {
         event.matrices.method_22903();
         class_243 cameraPos = cam.method_71156();
         event.matrices.method_22907(class_7833.field_40714.rotationDegrees(cam.method_19329()));
         event.matrices.method_22907(class_7833.field_40716.rotationDegrees(cam.method_19330() + 180.0F));
         event.matrices.method_22904(-cameraPos.field_1352, -cameraPos.field_1351, -cameraPos.field_1350);

         for (class_2818 chunk : WorldUtils.getLoadedChunks().toList()) {
            for (class_2338 blockPos : chunk.method_12021()) {
               class_2586 blockEntity = this.mc.field_1687.method_8321(blockPos);
               if (blockEntity != null) {
                  RenderUtils.renderFilledBox(
                     event.matrices,
                     blockPos.method_10263() + 0.1F,
                     blockPos.method_10264() + 0.05F,
                     blockPos.method_10260() + 0.1F,
                     blockPos.method_10263() + 0.9F,
                     blockPos.method_10264() + 0.85F,
                     blockPos.method_10260() + 0.9F,
                     this.getColor(blockEntity, this.alpha.getValueInt())
                  );
                  class_243 center = new class_243(blockPos.method_10263() + 0.5, blockPos.method_10264() + 0.5, blockPos.method_10260() + 0.5);
                  if (this.tracers.getValue() && this.mc.field_1765 != null) {
                     RenderUtils.renderLine(event.matrices, this.getColor(blockEntity, 255), this.mc.field_1765.method_17784(), center);
                  }
               }
            }
         }

         event.matrices.method_22909();
      }
   }

   @Override
   public void onPacketReceive(PacketReceiveListener.PacketReceiveEvent event) {
      if (this.donutBypass.getValue() && event.packet instanceof class_2637) {
         event.cancel();
      }
   }
}
