package juxxl.prestige.module.modules.misc;

import juxxl.prestige.event.events.PacketReceiveListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import net.minecraft.class_2596;
import net.minecraft.class_2720;
import net.minecraft.class_2856;
import net.minecraft.class_2856.class_2857;

public class PackSpoof extends Module implements PacketReceiveListener {
   public PackSpoof() {
      super("Pack Spoof", "Ignores custom resource packs", -1, Category.MISC);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(PacketReceiveListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(PacketReceiveListener.class, this);
      super.onDisable();
   }

   @Override
   public void onPacketReceive(PacketReceiveListener.PacketReceiveEvent event) {
      if (this.mc.method_1562() != null) {
         class_2596<?> packet = event.packet;
         if (packet instanceof class_2720) {
            event.cancel();
            this.mc.method_1562().method_52787(new class_2856(this.mc.field_1724.method_5667(), class_2857.field_13016));
            this.mc.method_1562().method_52787(new class_2856(this.mc.field_1724.method_5667(), class_2857.field_13017));
         }
      }
   }
}
