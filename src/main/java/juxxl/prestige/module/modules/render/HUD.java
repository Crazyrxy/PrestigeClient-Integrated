package juxxl.prestige.module.modules.render;

import java.awt.Color;
import java.util.List;
import juxxl.prestige.Prestige;
import juxxl.prestige.event.events.HudListener;
import juxxl.prestige.gui.ClickGui;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.modules.client.ClickGUI;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.utils.RenderUtils;
import juxxl.prestige.utils.TextRenderer;
import juxxl.prestige.utils.Utils;
import net.minecraft.class_332;
import net.minecraft.class_640;

public final class HUD extends Module implements HudListener {
   private static final CharSequence prestige = "Prestige |";
   private final BooleanSetting info = new BooleanSetting("Info", true);
   private final BooleanSetting modules = new BooleanSetting("Modules", true).setDescription("Renders module array list");

   public HUD() {
      super("HUD", "Renders the client version and enabled modules on the HUD", -1, Category.RENDER);
      this.addSettings(this.info, this.modules);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(HudListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(HudListener.class, this);
      super.onDisable();
   }

   @Override
   public void onRenderHud(HudListener.HudEvent event) {
      if (this.mc.field_1755 != Prestige.INSTANCE.clickGui) {
         List<Module> enabledModules = Prestige.INSTANCE.getModuleManager().getEnabledModules().stream().sorted((module1, module2) -> {
            CharSequence name1 = module1.getName();
            CharSequence name2 = module2.getName();
            int filteredLength1 = TextRenderer.getWidth(name1);
            int filteredLength2 = TextRenderer.getWidth(name2);
            return Integer.compare(filteredLength2, filteredLength1);
         }).toList();
         class_332 context = event.context;
         boolean customFont = ClickGUI.customFont.getValue();
         if (!(this.mc.field_1755 instanceof ClickGui)) {
            if (this.info.getValue() && this.mc.field_1724 != null) {
               RenderUtils.unscaledProjection(context);
               int offset = 10;
               int offset2 = 10 + TextRenderer.getWidth(prestige);
               String ping = "Ping: ";
               String fps = "FPS: " + this.mc.method_47599() + " |";
               String server = this.mc.method_1558() == null ? "None" : this.mc.method_1558().field_3761;
               if (this.mc != null && this.mc.field_1724 != null && this.mc.method_1562() != null) {
                  class_640 entry = this.mc.method_1562().method_2871(this.mc.field_1724.method_5667());
                  if (entry != null) {
                     ping = ping + entry.method_2959() + " |";
                  } else {
                     ping = ping + "N/A |";
                  }
               } else {
                  ping = ping + "N/A |";
               }

               RenderUtils.renderRoundedQuad(
                  context,
                  new Color(35, 35, 35, 255),
                  5.0,
                  6.0,
                  offset2 + TextRenderer.getWidth(fps) + TextRenderer.getWidth(ping) + TextRenderer.getWidth(server) + 35,
                  30.0,
                  5.0,
                  15.0
               );
               TextRenderer.drawString(prestige, context, offset, 12, Utils.getMainColor(255, 4).getRGB());
               offset += TextRenderer.getWidth(prestige);
               TextRenderer.drawString(fps, context, offset + 10, 12, Utils.getMainColor(255, 3).getRGB());
               TextRenderer.drawString(ping, context, offset + 10 + TextRenderer.getWidth(fps) + 10, 12, Utils.getMainColor(255, 2).getRGB());
               TextRenderer.drawString(
                  server, context, offset + 10 + TextRenderer.getWidth(fps) + TextRenderer.getWidth(ping) + 20, 12, Utils.getMainColor(255, 1).getRGB()
               );
               RenderUtils.scaledProjection(context);
            }

            if (this.modules.getValue()) {
               int offset = 55;

               for (Module module : enabledModules) {
                  RenderUtils.unscaledProjection(context);
                  int charOffset = 6 + TextRenderer.getWidth(module.getName());
                  RenderUtils.renderRoundedQuad(context, new Color(0, 0, 0, 175), 0.0, offset - 4, charOffset + 5, offset + 9 * 2 - 1, 0.0, 0.0, 0.0, 5.0, 10.0);
                  context.method_25296(
                     0,
                     offset - 4,
                     2,
                     offset + 9 * 2,
                     Utils.getMainColor(255, enabledModules.indexOf(module)).getRGB(),
                     Utils.getMainColor(255, enabledModules.indexOf(module) + 1).getRGB()
                  );
                  int charOffset2 = customFont ? 5 : 8;
                  TextRenderer.drawString(
                     module.getName(), context, charOffset2, offset + (customFont ? 1 : 0), Utils.getMainColor(255, enabledModules.indexOf(module)).getRGB()
                  );
                  offset += 9 * 2 + 3;
                  RenderUtils.scaledProjection(context);
               }
            }
         }
      }
   }
}
