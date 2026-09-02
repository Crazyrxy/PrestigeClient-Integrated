package juxxl.prestige.module.modules.combat;

import juxxl.prestige.Prestige;
import juxxl.prestige.event.events.AttackListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.modules.client.Friends;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.MinMaxSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.MouseSimulation;
import juxxl.prestige.utils.TimerUtils;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1642;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1819;
import net.minecraft.class_3966;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import org.lwjgl.glfw.GLFW;

public final class TriggerBot extends Module implements TickListener, AttackListener {
   private final BooleanSetting inScreen = new BooleanSetting("Work In Screen", false).setDescription("Will trigger even if youre inside a screen");
   private final BooleanSetting whileUse = new BooleanSetting("While Use", false)
      .setDescription("Will hit the player no matter if you're eating or blocking with a shield");
   private final BooleanSetting onLeftClick = new BooleanSetting("On Left Click", false).setDescription("Only gets triggered if holding down left click");
   private final BooleanSetting allItems = new BooleanSetting("All Items", false).setDescription("Works with all Items /THIS USES SWORD DELAY AS THE DELAY/");
   private final MinMaxSetting swordDelay = new MinMaxSetting("Sword Delay", 0.0, 1000.0, 1.0, 540.0, 550.0).setDescription("Delay for swords");
   private final MinMaxSetting axeDelay = new MinMaxSetting("Axe Delay", 0.0, 1000.0, 1.0, 780.0, 800.0).setDescription("Delay for axes");
   private final BooleanSetting checkShield = new BooleanSetting("Check Shield", false)
      .setDescription("Checks if the player is blocking your hits with a shield (Recommended with Shield Disabler)");
   private final BooleanSetting onlyCritSword = new BooleanSetting("Only Crit Sword", false).setDescription("Only does critical hits with a sword");
   private final BooleanSetting onlyCritAxe = new BooleanSetting("Only Crit Axe", false).setDescription("Only does critical hits with an axe");
   private final BooleanSetting swing = new BooleanSetting("Swing Hand", true).setDescription("Whether to swing the hand or not");
   private final BooleanSetting whileAscend = new BooleanSetting("While Ascending", false)
      .setDescription("Wont hit if you're ascending from a jump, only if on ground or falling");
   private final BooleanSetting clickSimulation = new BooleanSetting("Click Simulation", false).setDescription("Makes the CPS hud think you're legit");
   private final BooleanSetting strayBypass = new BooleanSetting("Stray Bypass", false).setDescription("Bypasses stray's Anti-TriggerBot");
   private final BooleanSetting allEntities = new BooleanSetting("All Entities", false).setDescription("Will attack all entities");
   private final BooleanSetting useShield = new BooleanSetting("Use Shield", false).setDescription("Uses shield if it's in your offhand");
   private final NumberSetting shieldTime = new NumberSetting("Shield Time", 100.0, 1000.0, 350.0, 1.0);
   private final BooleanSetting sticky = new BooleanSetting("Same Player", false).setDescription("Hits the player that was recently attacked, good for FFA");
   private final TimerUtils timer = new TimerUtils();
   private int currentSwordDelay;
   private int currentAxeDelay;

   public TriggerBot() {
      super("Trigger Bot", "Automatically hits players for you", -1, Category.COMBAT);
      this.addSettings(
         this.inScreen,
         this.whileUse,
         this.onLeftClick,
         this.allItems,
         this.swordDelay,
         this.axeDelay,
         this.checkShield,
         this.whileAscend,
         this.sticky,
         this.onlyCritSword,
         this.onlyCritAxe,
         this.swing,
         this.clickSimulation,
         this.strayBypass,
         this.allEntities,
         this.useShield,
         this.shieldTime
      );
   }

   @Override
   public void onEnable() {
      this.currentSwordDelay = this.swordDelay.getRandomValueInt();
      this.currentAxeDelay = this.axeDelay.getRandomValueInt();
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(AttackListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(AttackListener.class, this);
      super.onDisable();
   }

   @Override
   public void onTick() {
      try {
         if (!this.inScreen.getValue() && this.mc.field_1755 != null) {
            return;
         }

         if (Prestige.INSTANCE.getModuleManager().getModule(Friends.class).antiAttack.getValue() && Prestige.INSTANCE.getFriendManager().isAimingOverFriend()) {
            return;
         }

         class_1799 mainHandStack = this.mc.field_1724.method_6047();
         class_1792 item = mainHandStack.method_7909();
         if (this.onLeftClick.getValue() && GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 0) != 1) {
            return;
         }

         if ((
               this.mc.field_1724.method_6079().method_7909().method_57347().method_57832(class_9334.field_50075)
                  || this.mc.field_1724.method_6079().method_7909() instanceof class_1819
            )
            && GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 1) == 1
            && !this.whileUse.getValue()) {
            return;
         }

         if (!this.whileAscend.getValue()
            && (
               !this.mc.field_1724.method_24828() && this.mc.field_1724.method_18798().field_1351 > 0.0
                  || !this.mc.field_1724.method_24828() && this.mc.field_1724.field_6017 <= 0.0
            )) {
            return;
         }

         if (!this.allItems.getValue()) {
            if (WorldUtils.isSword(mainHandStack)) {
               if (this.mc.field_1765 instanceof class_3966 hit) {
                  class_1297 entity = hit.method_17782();
                  assert this.mc.field_1724.method_6052() != null;
                  if (this.sticky.getValue() && entity != this.mc.field_1724.method_6052()) {
                     return;
                  }

                  if (entity instanceof class_1657
                     || this.strayBypass.getValue() && entity instanceof class_1642
                     || this.allEntities.getValue() && entity != null) {
                     if (entity instanceof class_1657 player && this.checkShield.getValue() && player.method_6039() && !WorldUtils.isShieldFacingAway(player)) {
                        return;
                     }

                     if (this.onlyCritSword.getValue() && this.mc.field_1724.field_6017 <= 0.0) {
                        return;
                     }

                     if (this.timer.delay(this.currentSwordDelay)) {
                        if (this.useShield.getValue()
                           && this.mc.field_1724.method_6079().method_7909() == class_1802.field_8255
                           && this.mc.field_1724.method_6039()) {
                           MouseSimulation.mouseRelease(1);
                        }

                        WorldUtils.hitEntity(entity, this.swing.getValue());
                        if (this.clickSimulation.getValue()) {
                           MouseSimulation.mouseClick(0);
                        }

                        this.currentSwordDelay = this.swordDelay.getRandomValueInt();
                        this.timer.reset();
                     } else if (this.useShield.getValue() && this.mc.field_1724.method_6079().method_7909() == class_1802.field_8255) {
                        int useFor = this.shieldTime.getValueInt();
                        MouseSimulation.mouseClick(1, useFor);
                     }
                  }
               }
            } else if (WorldUtils.isAxe(mainHandStack) && this.mc.field_1765 instanceof class_3966 hit) {
               class_1297 entity = hit.method_17782();
               if (entity instanceof class_1657 || this.strayBypass.getValue() && entity instanceof class_1642 || this.allEntities.getValue() && entity != null
                  )
                {
                  if (entity instanceof class_1657 player && this.checkShield.getValue() && player.method_6039() && !WorldUtils.isShieldFacingAway(player)) {
                     return;
                  }

                  if (this.onlyCritAxe.getValue() && this.mc.field_1724.field_6017 <= 0.0) {
                     return;
                  }

                  if (this.timer.delay(this.currentAxeDelay)) {
                     WorldUtils.hitEntity(entity, this.swing.getValue());
                     if (this.clickSimulation.getValue()) {
                        MouseSimulation.mouseClick(0);
                     }

                     this.currentAxeDelay = this.axeDelay.getRandomValueInt();
                     this.timer.reset();
                  } else if (this.useShield.getValue() && this.mc.field_1724.method_6079().method_7909() == class_1802.field_8255) {
                     int useFor = this.shieldTime.getValueInt();
                     MouseSimulation.mouseClick(1, useFor);
                  }
               }
            }
         } else if (this.mc.field_1765 instanceof class_3966 entityHit && this.mc.field_1765.method_17783() == class_240.field_1331) {
            class_1297 entity = entityHit.method_17782();
            assert this.mc.field_1724.method_6052() != null;
            if (this.sticky.getValue() && entity != this.mc.field_1724.method_6052()) {
               return;
            }

            if (entity instanceof class_1657 || this.strayBypass.getValue() && entity instanceof class_1642 || this.allEntities.getValue() && entity != null) {
               if (entity instanceof class_1657 player && this.checkShield.getValue() && player.method_6039() && !WorldUtils.isShieldFacingAway(player)) {
                  return;
               }

               if (this.onlyCritSword.getValue() && this.mc.field_1724.field_6017 <= 0.0) {
                  return;
               }

               if (this.timer.delay(this.currentSwordDelay)) {
                  WorldUtils.hitEntity(entity, this.swing.getValue());
                  if (this.clickSimulation.getValue()) {
                     MouseSimulation.mouseClick(0);
                  }

                  this.currentSwordDelay = this.swordDelay.getRandomValueInt();
                  this.timer.reset();
               } else if (this.useShield.getValue() && this.mc.field_1724.method_6079().method_7909() == class_1802.field_8255) {
                  int useFor = this.shieldTime.getValueInt();
                  MouseSimulation.mouseClick(1, useFor);
               }
            }
         }
      } catch (Exception var6) {
      }
   }

   @Override
   public void onAttack(AttackListener.AttackEvent event) {
      if (GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 0) != 1) {
         event.cancel();
      }
   }
}
