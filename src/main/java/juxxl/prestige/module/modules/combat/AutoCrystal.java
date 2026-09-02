package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.ItemUseListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.mixin.MinecraftClientAccessor;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.KeybindSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.BlockUtils;
import juxxl.prestige.utils.CrystalUtils;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.KeyUtils;
import juxxl.prestige.utils.MathUtils;
import juxxl.prestige.utils.MouseSimulation;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1621;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2350;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public final class AutoCrystal extends Module implements TickListener, ItemUseListener {
   private final KeybindSetting activateKey = new KeybindSetting("Activate Key", 1, false).setDescription("Key that does the crystalling");
   private final NumberSetting placeDelay = new NumberSetting("Place Delay", 0.0, 20.0, 0.0, 1.0);
   private final NumberSetting breakDelay = new NumberSetting("Break Delay", 0.0, 20.0, 0.0, 1.0);
   private final NumberSetting placeChance = new NumberSetting("Place Chance", 0.0, 100.0, 100.0, 1.0).setDescription("Randomization");
   private final NumberSetting breakChance = new NumberSetting("Break Chance", 0.0, 100.0, 100.0, 1.0).setDescription("Randomization");
   private final BooleanSetting stopOnKill = new BooleanSetting("Stop on Kill", false).setDescription("Won't crystal if a dead player is nearby");
   private final BooleanSetting fakePunch = new BooleanSetting("Fake Punch", false).setDescription("Will hit every entity and block if you miss a hitcrystal");
   private final BooleanSetting clickSimulation = new BooleanSetting("Click Simulation", false).setDescription("Makes the CPS hud think you're legit");
   private final BooleanSetting damageTick = new BooleanSetting("Damage Tick", false).setDescription("Times your crystals for a perfect d-tap");
   private final BooleanSetting antiWeakness = new BooleanSetting("Anti-Weakness", false)
      .setDescription("Silently switches to a sword and then hits the crystal if you have weakness");
   private final NumberSetting particleChance = new NumberSetting("Particle Chance", 0.0, 100.0, 20.0, 1.0)
      .setDescription("Adds block breaking particles to make it seem more legit from your POV (Only works with fake punch)");
   private int placeClock;
   private int breakClock;
   public boolean crystalling;

   public AutoCrystal() {
      super("Auto Crystal", "Automatically crystals fast for you", -1, Category.COMBAT);
      this.addSettings(
         this.activateKey,
         this.placeDelay,
         this.breakDelay,
         this.placeChance,
         this.breakChance,
         this.stopOnKill,
         this.fakePunch,
         this.clickSimulation,
         this.damageTick,
         this.antiWeakness,
         this.particleChance
      );
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(ItemUseListener.class, this);
      this.placeClock = 0;
      this.breakClock = 0;
      this.crystalling = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (this.mc.field_1755 == null) {
         boolean dontPlace = this.placeClock != 0;
         boolean dontBreak = this.breakClock != 0;
         if (!this.stopOnKill.getValue() || !WorldUtils.isDeadBodyNearby()) {
            int randomInt = MathUtils.randomInt(1, 100);
            if (dontPlace) {
               this.placeClock--;
            }

            if (dontBreak) {
               this.breakClock--;
            }

            if (!this.mc.field_1724.method_6115()) {
               if (!this.damageTick.getValue() || !this.damageTickCheck()) {
                  if (this.activateKey.getKey() != -1 && !KeyUtils.isKeyPressed(this.activateKey.getKey())) {
                     this.placeClock = 0;
                     this.breakClock = 0;
                     this.crystalling = false;
                  } else {
                     this.crystalling = true;
                     if (this.mc.field_1724.method_6047().method_7909() == class_1802.field_8301) {
                        if (this.mc.field_1765 instanceof class_3965 hit) {
                           if (this.mc.field_1765.method_17783() == class_240.field_1332) {
                              if (!dontPlace
                                 && randomInt <= this.placeChance.getValueInt()
                                 && (
                                    BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540)
                                       || BlockUtils.isBlock(hit.method_17777(), class_2246.field_9987)
                                          && CrystalUtils.canPlaceCrystalClientAssumeObsidian(hit.method_17777())
                                 )) {
                                 if (this.clickSimulation.getValue()) {
                                    MouseSimulation.mouseClick(1);
                                 }

                                 WorldUtils.placeBlock(hit, true);
                                 if (this.fakePunch.getValue()
                                    && randomInt <= this.particleChance.getValue()
                                    && CrystalUtils.canPlaceCrystalClientAssumeObsidian(hit.method_17777())
                                    && hit.method_17780() == class_2350.field_11036) {
                                    this.mc.field_1724.method_6104(class_1268.field_5808);
                                 }

                                 this.placeClock = this.placeDelay.getValueInt();
                              }

                              if (this.fakePunch.getValue()) {
                                 if (!dontBreak && randomInt <= this.breakChance.getValueInt()) {
                                    if (BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540)
                                       || BlockUtils.isBlock(hit.method_17777(), class_2246.field_9987)) {
                                       return;
                                    }

                                    if (this.clickSimulation.getValue()) {
                                       if (!BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540)
                                          && !BlockUtils.isBlock(hit.method_17777(), class_2246.field_9987)) {
                                          MouseSimulation.mouseClick(0);
                                       } else if (CrystalUtils.canPlaceCrystalClientAssumeObsidian(hit.method_17777())) {
                                          MouseSimulation.mouseClick(0);
                                       }
                                    }

                                    this.mc.field_1761.method_2910(hit.method_17777(), hit.method_17780());
                                    this.mc.field_1724.method_6104(class_1268.field_5808);
                                    this.mc.field_1761.method_2902(hit.method_17777(), hit.method_17780());
                                    this.breakClock = this.breakDelay.getValueInt();
                                 }

                                 if (!dontPlace && randomInt <= this.placeChance.getValueInt() && dontBreak && this.clickSimulation.getValue()) {
                                    MouseSimulation.mouseClick(1);
                                 }
                              }
                           }

                           if (this.mc.field_1765.method_17783() == class_240.field_1333 && this.fakePunch.getValue()) {
                              if (!dontBreak && randomInt <= this.breakChance.getValueInt()) {
                                  if (this.mc.field_1761.method_2924()) {
                                     ((MinecraftClientAccessor) this.mc).setAttackCooldown(10);
                                  }

                                 if (this.clickSimulation.getValue()) {
                                    MouseSimulation.mouseClick(0);
                                 }

                                 this.mc.field_1724.method_6104(class_1268.field_5808);
                                 this.breakClock = this.breakDelay.getValueInt();
                              }

                              if (!dontPlace && randomInt <= this.placeChance.getValueInt() && dontBreak && this.clickSimulation.getValue()) {
                                 MouseSimulation.mouseClick(1);
                              }
                           }
                        }

                        randomInt = MathUtils.randomInt(1, 100);
                        if (this.mc.field_1765 instanceof class_3966 hit && !dontBreak && randomInt <= this.breakChance.getValueInt()) {
                           class_1297 entity = hit.method_17782();
                           if (!this.fakePunch.getValue() && !(entity instanceof class_1511) && !(entity instanceof class_1621)) {
                              return;
                           }

                           int previousSlot = this.mc.field_1724.method_31548().method_67532();
                           if ((entity instanceof class_1511 || entity instanceof class_1621) && this.antiWeakness.getValue() && this.cantBreakCrystal()) {
                              InventoryUtils.selectSword();
                           }

                           if (this.clickSimulation.getValue()) {
                              MouseSimulation.mouseClick(0);
                           }

                           WorldUtils.hitEntity(entity, true);
                           this.breakClock = this.breakDelay.getValueInt();
                           if (this.antiWeakness.getValue()) {
                              InventoryUtils.setInvSlot(previousSlot);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onItemUse(ItemUseListener.ItemUseEvent event) {
      if (this.mc.field_1724.method_6047().method_7909() == class_1802.field_8301
         && this.mc.field_1765 instanceof class_3965 h
         && this.mc.field_1765.method_17783() == class_240.field_1332
         && (BlockUtils.isBlock(h.method_17777(), class_2246.field_10540) || BlockUtils.isBlock(h.method_17777(), class_2246.field_9987))) {
         event.cancel();
      }
   }

   private boolean cantBreakCrystal() {
      assert this.mc.field_1724 != null;
      class_1293 weakness = this.mc.field_1724.method_6112(class_1294.field_5911);
      class_1293 strength = this.mc.field_1724.method_6112(class_1294.field_5910);
      return weakness != null && (strength == null || strength.method_5578() <= weakness.method_5578()) && !WorldUtils.isTool(this.mc.field_1724.method_6047());
   }

   private boolean damageTickCheck() {
      return this.mc
            .field_1687
            .method_18456()
            .parallelStream()
            .filter(e -> e != this.mc.field_1724)
            .filter(e -> e.method_5858(this.mc.field_1724) < 36.0)
            .filter(e -> e.method_49107() == null)
            .filter(e -> !e.method_24828())
            .anyMatch(e -> e.field_6235 >= 2)
         && !(this.mc.field_1724.method_6052() instanceof class_1657);
   }
}
