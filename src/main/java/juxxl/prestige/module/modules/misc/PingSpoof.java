package juxxl.prestige.module.modules.misc;

import juxxl.prestige.event.events.PacketReceiveListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.MinMaxSetting;
import net.minecraft.class_2670;
import net.minecraft.class_2827;

public final class PingSpoof extends Module implements PacketReceiveListener {
   private final MinMaxSetting ping = new MinMaxSetting("Ping", 0.0, 1000.0, 1.0, 0.0, 600.0).setDescription("The ping you want to achieve");
   private int delay;

   public PingSpoof() {
      super("Ping Spoof", "Holds back packets making the server think your internet connection is bad.", -1, Category.MISC);
      this.addSettings(this.ping);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(PacketReceiveListener.class, this);
      this.delay = this.ping.getRandomValueInt();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(PacketReceiveListener.class, this);
      super.onDisable();
   }

   @Override
   public void onPacketReceive(PacketReceiveListener.PacketReceiveEvent event) {
      if (event.packet instanceof class_2670 packet) {
         new Thread(() -> {
            try {
               Thread.sleep(this.delay);
               this.mc.method_1562().method_48296().method_10743(new class_2827(packet.method_11517()));
               this.delay = this.ping.getRandomValueInt();
            } catch (InterruptedException var3) {
            }
         }).start();
         event.cancel();
      }
   }
}
