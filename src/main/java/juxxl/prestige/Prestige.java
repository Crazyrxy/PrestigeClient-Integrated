package juxxl.prestige;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import juxxl.prestige.event.EventManager;
import juxxl.prestige.gui.ClickGui;
import juxxl.prestige.managers.FriendManager;
import juxxl.prestige.managers.ProfileManager;
import juxxl.prestige.module.ModuleManager;
import juxxl.prestige.utils.rotation.RotatorManager;
import net.minecraft.class_310;
import net.minecraft.class_437;

public final class Prestige {
   public RotatorManager rotatorManager;
   public ProfileManager profileManager;
   public ModuleManager moduleManager;
   public EventManager eventManager;
   public FriendManager friendManager;
   public static class_310 mc;
   public String version = " b1.3";
   public static boolean BETA;
   public static Prestige INSTANCE;
   public boolean guiInitialized;
   public ClickGui clickGui;
   public class_437 previousScreen = null;
   public long lastModified;
   public File modJar;

   public Prestige() throws InterruptedException, IOException {
      INSTANCE = this;
      this.eventManager = new EventManager();
      this.moduleManager = new ModuleManager();
      this.clickGui = new ClickGui();
      this.rotatorManager = new RotatorManager();
      this.profileManager = new ProfileManager();
      this.friendManager = new FriendManager();
      this.getProfileManager().loadProfile();
      this.setLastModified();
      this.guiInitialized = false;
      mc = class_310.method_1551();
   }

   public ProfileManager getProfileManager() {
      return this.profileManager;
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public FriendManager getFriendManager() {
      return this.friendManager;
   }

   public EventManager getEventManager() {
      return this.eventManager;
   }

   public ClickGui getClickGui() {
      return this.clickGui;
   }

   public void resetModifiedDate() {
      this.modJar.setLastModified(this.lastModified);
   }

   public String getVersion() {
      return this.version;
   }

   public void setLastModified() {
      try {
         this.modJar = new File(Prestige.class.getProtectionDomain().getCodeSource().getLocation().toURI());
         this.lastModified = this.modJar.lastModified();
      } catch (URISyntaxException var2) {
      }
   }
}
