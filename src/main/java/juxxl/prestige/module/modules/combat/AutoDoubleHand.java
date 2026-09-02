package juxxl.prestige.module.modules.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import juxxl.prestige.Prestige;
import juxxl.prestige.event.events.HudListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.BlockUtils;
import juxxl.prestige.utils.CrystalUtils;
import juxxl.prestige.utils.DamageUtils;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.RotationUtils;
import net.minecraft.class_1511;
import net.minecraft.class_1661;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class AutoDoubleHand extends Module implements HudListener {
   private final BooleanSetting stopOnCrystal = new BooleanSetting("Stop On Crystal", false).setDescription("Stops while Auto Crystal is running");
   private final BooleanSetting checkShield = new BooleanSetting("Check Shield", false).setDescription("Checks if you're blocking with a shield");
   private final BooleanSetting onPop = new BooleanSetting("On Pop", false).setDescription("Switches to a totem if you pop");
   private final BooleanSetting onHealth = new BooleanSetting("On Health", false).setDescription("Switches to totem if low on health");
   private final BooleanSetting predict = new BooleanSetting("Predict Damage", true);
   private final NumberSetting health = new NumberSetting("Health", 1.0, 20.0, 2.0, 1.0).setDescription("Health to trigger at");
   private final BooleanSetting onGround = new BooleanSetting("On Ground", true).setDescription("Whether crystal damage is checked on ground or not");
   private final BooleanSetting checkPlayers = new BooleanSetting("Check Players", true).setDescription("Checks for nearby players");
   private final NumberSetting distance = new NumberSetting("Distance", 1.0, 10.0, 5.0, 0.1).setDescription("Player distance");
   private final BooleanSetting predictCrystals = new BooleanSetting("Predict Crystals", false);
   private final BooleanSetting checkAim = new BooleanSetting("Check Aim", false).setDescription("Checks if the opponent is aiming at obsidian");
   private final BooleanSetting checkItems = new BooleanSetting("Check Items", false).setDescription("Checks if the opponent is holding crystals");
   private final NumberSetting activatesAbove = new NumberSetting("Activates Above", 0.0, 4.0, 0.2, 0.1).setDescription("Height to trigger at");
   private boolean belowHealth;
   private boolean offhandHasNoTotem;

   public AutoDoubleHand() {
      super("Auto Double Hand", "Automatically switches to your totem when you're about to pop", -1, Category.COMBAT);
      this.addSettings(
         this.stopOnCrystal,
         this.checkShield,
         this.onPop,
         this.onHealth,
         this.predict,
         this.health,
         this.onGround,
         this.checkPlayers,
         this.distance,
         this.predictCrystals,
         this.checkAim,
         this.checkItems,
         this.activatesAbove
      );
      this.belowHealth = false;
      this.offhandHasNoTotem = false;
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
      if (this.mc.field_1724 != null) {
         if (!Prestige.INSTANCE.getModuleManager().getModule(AutoCrystal.class).crystalling || !this.stopOnCrystal.getValue()) {
            double squaredDistance = this.distance.getValue() * this.distance.getValue();
            class_1661 inventory = this.mc.field_1724.method_31548();
            if (!this.checkShield.getValue() || !this.mc.field_1724.method_6039()) {
               if (this.mc.field_1724.method_6079().method_7909() != class_1802.field_8288 && this.onPop.getValue() && !this.offhandHasNoTotem) {
                  this.offhandHasNoTotem = true;
                  InventoryUtils.selectItemFromHotbar(class_1802.field_8288);
               }

               if (this.mc.field_1724.method_6079().method_7909() == class_1802.field_8288) {
                  this.offhandHasNoTotem = false;
               }

               if (this.mc.field_1724.method_6032() <= this.health.getValue() && this.onHealth.getValue() && !this.belowHealth) {
                  this.belowHealth = true;
                  InventoryUtils.selectItemFromHotbar(class_1802.field_8288);
               }

               if (this.mc.field_1724.method_6032() > this.health.getValue()) {
                  this.belowHealth = false;
               }

               if (this.predict.getValue()) {
                  if (!(this.mc.field_1724.method_6032() > 19.0F)) {
                     if (this.onGround.getValue() || !this.mc.field_1724.method_24828()) {
                        if (!this.checkPlayers.getValue()
                           || !this.mc
                              .field_1687
                              .method_18456()
                              .parallelStream()
                              .filter(e -> e != this.mc.field_1724)
                              .noneMatch(p -> this.mc.field_1724.method_5858(p) <= squaredDistance)) {
                           double above = this.activatesAbove.getValue();
                           int floor = (int)Math.floor(above);

                           for (int i = 1; i <= floor; i++) {
                              if (!this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10069(0, -i, 0)).method_26215()) {
                                 return;
                              }
                           }

                           class_243 playerPos = this.mc.field_1724.method_73189();
                           class_2338 playerBlockPos = new class_2338(
                              (int)playerPos.field_1352, (int)playerPos.field_1351 - (int)above, (int)playerPos.field_1350
                           );
                           if (this.mc.field_1687.method_8320(new class_2338(playerBlockPos)).method_26215()) {
                              List<class_1511> crystals = this.nearbyCrystals();
                              ArrayList<class_243> pos = new ArrayList<>();
                              crystals.forEach(e -> pos.add(e.method_73189()));
                              if (this.predictCrystals.getValue()) {
                                 Stream<class_2338> s = BlockUtils.getAllInBoxStream(
                                       this.mc.field_1724.method_24515().method_10069(-6, -8, -6), this.mc.field_1724.method_24515().method_10069(6, 2, 6)
                                    )
                                    .filter(
                                       e -> this.mc.field_1687.method_8320(e).method_26204() == class_2246.field_10540
                                          || this.mc.field_1687.method_8320(e).method_26204() == class_2246.field_9987
                                    )
                                    .filter(CrystalUtils::canPlaceCrystalClient);
                                 if (this.checkAim.getValue()) {
                                    if (this.checkItems.getValue()) {
                                       s = s.filter(this::arePeopleAimingAtBlockAndHoldingCrystals);
                                    } else {
                                       s = s.filter(this::arePeopleAimingAtBlock);
                                    }
                                 }

                                 s.forEachOrdered(e -> pos.add(class_243.method_24955(e).method_1031(0.0, 1.0, 0.0)));
                              }

                              for (class_243 crys : pos) {
                                 double damage = DamageUtils.crystalDamage(this.mc.field_1724, crys);
                                 if (damage >= this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067()) {
                                    InventoryUtils.selectItemFromHotbar(class_1802.field_8288);
                                    break;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private List<class_1511> nearbyCrystals() {
      class_243 pos = this.mc.field_1724.method_73189();
      return this.mc.field_1687.method_8390(class_1511.class, new class_238(pos.method_1031(-6.0, -6.0, -6.0), pos.method_1031(6.0, 6.0, 6.0)), e -> true);
   }

   private boolean arePeopleAimingAtBlock(class_2338 block) {
      class_243[] eyesPos = new class_243[1];
      class_3965[] hitResult = new class_3965[1];
      return this.mc
         .field_1687
         .method_18456()
         .parallelStream()
         .filter(e -> e != this.mc.field_1724)
         .anyMatch(
            e -> {
               eyesPos[0] = RotationUtils.getEyesPos(e);
               hitResult[0] = this.mc
                  .field_1687
                  .method_17742(
                     new class_3959(
                        eyesPos[0], eyesPos[0].method_1019(RotationUtils.getPlayerLookVec(e).method_1021(4.5)), class_3960.field_17558, class_242.field_1348, e
                     )
                  );
               return hitResult[0] != null && hitResult[0].method_17777().equals(block);
            }
         );
   }

   private boolean arePeopleAimingAtBlockAndHoldingCrystals(class_2338 block) {
      class_243[] eyesPos = new class_243[1];
      class_3965[] hitResult = new class_3965[1];
      return this.mc
         .field_1687
         .method_18456()
         .parallelStream()
         .filter(e -> e != this.mc.field_1724)
         .filter(e -> e.method_24518(class_1802.field_8301))
         .anyMatch(
            e -> {
               eyesPos[0] = RotationUtils.getEyesPos(e);
               hitResult[0] = this.mc
                  .field_1687
                  .method_17742(
                     new class_3959(
                        eyesPos[0], eyesPos[0].method_1019(RotationUtils.getPlayerLookVec(e).method_1021(4.5)), class_3960.field_17558, class_242.field_1348, e
                     )
                  );
               return hitResult[0] != null && hitResult[0].method_17777().equals(block);
            }
         );
   }
}
