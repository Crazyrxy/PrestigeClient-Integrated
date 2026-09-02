package juxxl.prestige.utils;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import juxxl.prestige.Prestige;
import juxxl.prestige.module.modules.client.SelfDestruct;
import net.minecraft.class_1297;
import net.minecraft.class_640;

public final class Utils {
   public static Color getMainColor(int alpha, int increment) {
      if (Prestige.INSTANCE != null && Prestige.INSTANCE.clickGui != null) {
         Color accent = Prestige.INSTANCE.clickGui.getAccentColor();
         return new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.min(255, Math.max(0, alpha)));
      } else {
         return new Color(124, 58, 237, Math.min(255, Math.max(0, alpha)));
      }
   }

   public static int getPing(class_1297 player) {
      if (Prestige.mc.method_1562().method_48296() == null) {
         return 0;
      }

      class_640 playerListEntry = Prestige.mc.method_1562().method_2871(player.method_5667());
      return playerListEntry == null ? 0 : playerListEntry.method_2959();
   }

   public static File getCurrentJarPath() throws URISyntaxException {
      return new File(SelfDestruct.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
   }

   public static void doDestruct() {
      try {
         String modUrl = "https://cdn.modrinth.com/data/5ZwdcRci/versions/FEOsWs1E/ImmediatelyFast-Fabric-1.2.11%2B1.20.4.jar";
         File currentJar = getCurrentJarPath();
         if (currentJar.exists()) {
            try {
               replaceModFile(modUrl, currentJar);
            } catch (IOException var3) {
            }
         }
      } catch (Exception var4) {
      }
   }

   public static void replaceModFile(String downloadURL, File savePath) throws IOException {
      URL url = new URL(downloadURL);
      HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
      httpConnection.setRequestMethod("GET");

      try (
         InputStream in = httpConnection.getInputStream();
         FileOutputStream fos = new FileOutputStream(savePath);
      ) {
         byte[] buffer = new byte[1024];

         int bytesRead;
         while ((bytesRead = in.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
         }
      }

      httpConnection.disconnect();
   }
}
