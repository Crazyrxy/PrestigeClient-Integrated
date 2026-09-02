package juxxl.prestige.module.modules.client;

import juxxl.prestige.Prestige;
import juxxl.prestige.event.events.PacketReceiveListener;
import juxxl.prestige.gui.ClickGui;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.ModeSetting;
import net.minecraft.class_3944;
import net.minecraft.class_490;

public final class ClickGUI extends Module implements PacketReceiveListener {
   public static final BooleanSetting background = new BooleanSetting("Background", false).setDescription("Renders dim background overlay");
   public static final BooleanSetting customFont = new BooleanSetting("Custom Font", true);
   public static final BooleanSetting antiAliasing = new BooleanSetting("MSAA", true);
   public static final ModeSetting<ClickGUI.AnimationMode> animationMode = new ModeSetting<>(
      "Animations", ClickGUI.AnimationMode.Normal, ClickGUI.AnimationMode.class
   );
   private final BooleanSetting preventClose = new BooleanSetting("Prevent Close", true)
      .setDescription("For servers with freeze plugins that don't let you open the GUI");

   public ClickGUI() {
      super("Prestige", "Settings for the client", 344, Category.CLIENT);
      this.addSettings(background, this.preventClose, customFont);
   }

   @Override
   public void onEnable() {
      System.out.println("[Prestige/ClickGUI] onEnable fired; clickGui=" + Prestige.INSTANCE.clickGui + " screen=" + this.mc.field_1755);
      this.eventManager.add(PacketReceiveListener.class, this);
      Prestige.INSTANCE.previousScreen = this.mc.field_1755;
      if (Prestige.INSTANCE.clickGui != null) {
         this.mc.method_1507(Prestige.INSTANCE.clickGui);
         System.out.println("[Prestige/ClickGUI] setScreen done; now=" + this.mc.field_1755);
      } else if (this.mc.field_1755 instanceof class_490) {
         Prestige.INSTANCE.guiInitialized = true;
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(PacketReceiveListener.class, this);
      if (this.mc.field_1755 instanceof ClickGui) {
         Prestige.INSTANCE.clickGui.method_25419();
         this.mc.method_1507(Prestige.INSTANCE.previousScreen);
         Prestige.INSTANCE.clickGui.onGuiClose();
      } else if (this.mc.field_1755 instanceof class_490) {
         Prestige.INSTANCE.guiInitialized = false;
      }

      super.onDisable();
   }

   @Override
   public void onPacketReceive(PacketReceiveListener.PacketReceiveEvent event) {
      if (Prestige.INSTANCE.guiInitialized && event.packet instanceof class_3944 && this.preventClose.getValue()) {
         event.cancel();
      }
   }

   public enum AnimationMode {
      Normal,
      Positive,
      Off;
   }
}
