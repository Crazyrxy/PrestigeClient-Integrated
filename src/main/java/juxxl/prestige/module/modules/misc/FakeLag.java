package juxxl.prestige.module.modules.misc;

import com.google.common.collect.Queues;
import java.util.Queue;
import juxxl.prestige.event.events.PacketReceiveListener;
import juxxl.prestige.event.events.PacketSendListener;
import juxxl.prestige.event.events.PlayerTickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.MinMaxSetting;
import juxxl.prestige.utils.TimerUtils;
import net.minecraft.class_1304;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_2813;
import net.minecraft.class_2824;
import net.minecraft.class_2879;
import net.minecraft.class_2885;

public final class FakeLag extends Module implements PlayerTickListener, PacketReceiveListener, PacketSendListener {
   public final Queue<class_2596<?>> packetQueue = Queues.newConcurrentLinkedQueue();
   public boolean bool;
   public class_243 pos = class_243.field_1353;
   public TimerUtils timerUtil = new TimerUtils();
   private final MinMaxSetting lagDelay = new MinMaxSetting("Lag Delay", 0.0, 1000.0, 1.0, 100.0, 200.0);
   private final BooleanSetting cancelOnElytra = new BooleanSetting("Cancel on Elytra", false)
      .setDescription("Cancel the lagging effect when you're wearing an elytra");
   private int delay;

   public FakeLag() {
      super("Fake Lag", "Makes it impossible to aim at you by creating a lagging effect", -1, Category.MISC);
      this.addSettings(this.lagDelay, this.cancelOnElytra);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(PlayerTickListener.class, this);
      this.eventManager.add(PacketSendListener.class, this);
      this.eventManager.add(PacketReceiveListener.class, this);
      this.timerUtil.reset();
      if (this.mc.field_1724 != null) {
         this.pos = this.mc.field_1724.method_73189();
      }

      this.delay = this.lagDelay.getRandomValueInt();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(PlayerTickListener.class, this);
      this.eventManager.remove(PacketSendListener.class, this);
      this.eventManager.remove(PacketReceiveListener.class, this);
      this.reset();
      super.onDisable();
   }

   @Override
   public void onPacketReceive(PacketReceiveListener.PacketReceiveEvent event) {
      if (this.mc.field_1687 != null) {
         if (!this.mc.field_1724.method_29504()) {
            if (event.packet instanceof class_2664) {
               this.reset();
            }
         }
      }
   }

   @Override
   public void onPacketSend(PacketSendListener.PacketSendEvent event) {
      if (this.mc.field_1687 != null && !this.mc.field_1724.method_6115() && !this.mc.field_1724.method_29504()) {
         if (!(event.packet instanceof class_2824)
            && !(event.packet instanceof class_2879)
            && !(event.packet instanceof class_2885)
            && !(event.packet instanceof class_2813)) {
            if (this.cancelOnElytra.getValue() && this.mc.field_1724.method_6118(class_1304.field_6174).method_7909() == class_1802.field_8833) {
               this.reset();
            } else {
               if (!this.bool) {
                  this.packetQueue.add(event.packet);
                  event.cancel();
               }
            }
         } else {
            this.reset();
         }
      }
   }

   @Override
   public void onPlayerTick() {
      if (this.timerUtil.delay(this.delay) && this.mc.field_1724 != null && !this.mc.field_1724.method_6115()) {
         this.reset();
         this.delay = this.lagDelay.getRandomValueInt();
      }
   }

   private void reset() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.bool = true;
         synchronized (this.packetQueue) {
            while (!this.packetQueue.isEmpty()) {
               this.mc.method_1562().method_48296().method_52906(this.packetQueue.poll(), null, false);
            }
         }

         this.bool = false;
         this.timerUtil.reset();
         this.pos = this.mc.field_1724.method_73189();
      }
   }
}
