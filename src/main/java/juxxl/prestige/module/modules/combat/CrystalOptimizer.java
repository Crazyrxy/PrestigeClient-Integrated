package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.PacketSendListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1511;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_3966;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2824.class_5908;

public final class CrystalOptimizer extends Module implements PacketSendListener {
   public CrystalOptimizer() {
      super("Crystal Optimizer", "Makes your crystals disappear faster client-side so you can place crystals faster", -1, Category.COMBAT);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(PacketSendListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(PacketSendListener.class, this);
      super.onDisable();
   }

   @Override
   public void onPacketSend(PacketSendListener.PacketSendEvent event) {
      if (event.packet instanceof class_2824 interactPacket) {
         interactPacket.method_34209(
            new class_5908() {
               public void method_34219(class_1268 hand) {
               }

               public void method_34220(class_1268 hand, class_243 pos) {
               }

               public void method_34218() {
                  if (CrystalOptimizer.this.mc.field_1765 != null) {
                     if (CrystalOptimizer.this.mc.field_1765.method_17783() == class_240.field_1331
                        && CrystalOptimizer.this.mc.field_1765 instanceof class_3966 hit
                        && hit.method_17782() instanceof class_1511) {
                        class_1293 weakness = CrystalOptimizer.this.mc.field_1724.method_6112(class_1294.field_5911);
                        class_1293 strength = CrystalOptimizer.this.mc.field_1724.method_6112(class_1294.field_5910);
                        if (weakness != null
                           && (strength == null || strength.method_5578() <= weakness.method_5578())
                           && !WorldUtils.isTool(CrystalOptimizer.this.mc.field_1724.method_6047())) {
                           return;
                        }

                        hit.method_17782().method_31472();
                        hit.method_17782().method_31745(class_5529.field_26998);
                        hit.method_17782().method_36209();
                     }
                  }
               }
            }
         );
      }
   }
}
