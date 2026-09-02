package juxxl.prestige;

import java.io.IOException;
import net.fabricmc.api.ModInitializer;

public final class Main implements ModInitializer {
   public void onInitialize() {
      System.out.println("[Prestige] Main.onInitialize entered");
      try {
         new Prestige();
         System.out.println("[Prestige] Constructed successfully; ClickGUI key = "
                 + (Prestige.INSTANCE != null && Prestige.INSTANCE.getModuleManager() != null
                     ? Prestige.INSTANCE.getModuleManager()
                         .getModule(juxxl.prestige.module.modules.client.ClickGUI.class).getKey()
                     : "<null>"));
      } catch (Throwable t) {
         System.err.println("[Prestige] FATAL during init:");
         t.printStackTrace();
      }
   }
}
