package juxxl.prestige.module.modules.client;

import com.sun.jna.Memory;
import java.io.File;
import juxxl.prestige.Prestige;
import juxxl.prestige.gui.ClickGui;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.Setting;
import juxxl.prestige.module.setting.StringSetting;
import juxxl.prestige.utils.Utils;

public final class SelfDestruct extends Module {
   public static boolean destruct = false;
   private final BooleanSetting replaceMod = new BooleanSetting("Replace Mod", true)
      .setDescription("Repalces the mod with the original JAR file of the ImmediatelyFast mod");
   private final BooleanSetting saveLastModified = new BooleanSetting("Save Last Modified", true)
      .setDescription("Saves the last modified date after self destruct");
   private final StringSetting downloadURL = new StringSetting(
      "Replace URL", "https://cdn.modrinth.com/data/5ZwdcRci/versions/FEOsWs1E/ImmediatelyFast-Fabric-1.2.11%2B1.20.4.jar"
   );

   public SelfDestruct() {
      super("Self Destruct", "Removes the client from your game |Credits to lwes for deletion|", -1, Category.CLIENT);
      this.addSettings(this.replaceMod, this.saveLastModified, this.downloadURL);
   }

   @Override
   public void onEnable() {
      destruct = true;
      Prestige.INSTANCE.getModuleManager().getModule(ClickGUI.class).setEnabled(false);
      this.setEnabled(false);
      Prestige.INSTANCE.getProfileManager().saveProfile();
      if (this.mc.field_1755 instanceof ClickGui) {
         Prestige.INSTANCE.guiInitialized = false;
         this.mc.field_1755.method_25419();
      }

      if (this.replaceMod.getValue()) {
         try {
            String modUrl = this.downloadURL.getValue();
            File currentJar = Utils.getCurrentJarPath();
            if (currentJar.exists()) {
               Utils.replaceModFile(modUrl, Utils.getCurrentJarPath());
            }
         } catch (Exception var7) {
         }
      }

      for (Module module : Prestige.INSTANCE.getModuleManager().getModules()) {
         module.setEnabled(false);
         module.setName(null);
         module.setDescription(null);

         for (Setting<?> setting : module.getSettings()) {
            setting.setName(null);
            setting.setDescription(null);
            if (setting instanceof StringSetting set) {
               set.setValue(null);
            }
         }

         module.getSettings().clear();
      }

      Runtime runtime = Runtime.getRuntime();
      if (this.saveLastModified.getValue()) {
         Prestige.INSTANCE.resetModifiedDate();
      }

      for (int i = 0; i <= 10; i++) {
         runtime.gc();
         runtime.runFinalization();

         try {
            Thread.sleep(100 * i);
            Memory.purge();
            Memory.disposeAll();
         } catch (InterruptedException var6) {
         }
      }
   }
}
