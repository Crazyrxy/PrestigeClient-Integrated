package juxxl.prestige.module.modules.combat;

import juxxl.prestige.Prestige;
import juxxl.prestige.event.events.AttackListener;
import juxxl.prestige.event.events.ItemUseListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.KeybindSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.BlockUtils;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.KeyUtils;
import juxxl.prestige.utils.MathUtils;
import juxxl.prestige.utils.MouseSimulation;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import org.lwjgl.glfw.GLFW;

public final class AutoHitCrystal extends Module implements TickListener, ItemUseListener, AttackListener {
   private final KeybindSetting activateKey = new KeybindSetting("Activate Key", 1, false).setDescription("Key that does hit crystalling");
   private final BooleanSetting checkPlace = new BooleanSetting("Check Place", false).setDescription("Checks if you can place the obsidian on that block");
   private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0.0, 20.0, 0.0, 1.0);
   private final NumberSetting switchChance = new NumberSetting("Switch Chance", 0.0, 100.0, 100.0, 1.0);
   private final NumberSetting placeDelay = new NumberSetting("Place Delay", 0.0, 20.0, 0.0, 1.0);
   private final NumberSetting placeChance = new NumberSetting("Place Chance", 0.0, 100.0, 100.0, 1.0).setDescription("Randomization");
   private final BooleanSetting workWithTotem = new BooleanSetting("Work With Totem", false);
   private final BooleanSetting workWithCrystal = new BooleanSetting("Work With Crystal", false);
   private final BooleanSetting clickSimulation = new BooleanSetting("Click Simulation", false).setDescription("Makes the CPS hud think you're legit");
   private final BooleanSetting swordSwap = new BooleanSetting("Sword Swap", true);
   private int placeClock = 0;
   private int switchClock = 0;
   private boolean active;
   private boolean crystalling;
   private boolean crystalSelected;

   public AutoHitCrystal() {
      super("Auto Hit Crystal", "Automatically hit-crystals for you", -1, Category.COMBAT);
      this.addSettings(
         this.activateKey,
         this.checkPlace,
         this.switchDelay,
         this.switchChance,
         this.placeDelay,
         this.placeChance,
         this.workWithTotem,
         this.workWithCrystal,
         this.clickSimulation,
         this.swordSwap
      );
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(ItemUseListener.class, this);
      this.eventManager.add(AttackListener.class, this);
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      this.eventManager.remove(AttackListener.class, this);
      super.onDisable();
   }

   @Override
   public void onTick() {
      int randomNum = MathUtils.randomInt(1, 100);
      if (this.mc.field_1755 == null) {
         if (KeyUtils.isKeyPressed(this.activateKey.getKey())) {
            if (this.mc.field_1765 instanceof class_3965 hitResult
               && this.mc.field_1765.method_17783() == class_240.field_1332
               && !this.active
               && !BlockUtils.canPlaceBlockClient(hitResult.method_17777())
               && this.checkPlace.getValue()) {
               return;
            }

            class_1799 mainHandStack = this.mc.field_1724.method_6047();
            if (!WorldUtils.isSword(mainHandStack)
               && (!this.workWithTotem.getValue() || !mainHandStack.method_31574(class_1802.field_8288))
               && (!this.workWithCrystal.getValue() || !mainHandStack.method_31574(class_1802.field_8301))
               && !this.active) {
               return;
            }

            if (this.mc.field_1765 instanceof class_3965 hitResult
               && !this.active
               && this.swordSwap.getValue()
               && this.mc.field_1765.method_17783() == class_240.field_1332) {
               class_2248 block = this.mc.field_1687.method_8320(hitResult.method_17777()).method_26204();
               this.crystalling = block == class_2246.field_10540 || block == class_2246.field_9987;
            }

            this.active = true;
            if (!this.crystalling && this.mc.field_1765 instanceof class_3965 hit) {
               if (hit.method_17783() == class_240.field_1333) {
                  return;
               }

               if (!BlockUtils.isBlock(hit.method_17777(), class_2246.field_10540)) {
                  if (BlockUtils.isBlock(hit.method_17777(), class_2246.field_23152) && BlockUtils.isAnchorCharged(hit.method_17777())) {
                     return;
                  }

                  this.mc.field_1690.field_1904.method_23481(false);
                  if (!this.mc.field_1724.method_24518(class_1802.field_8281)) {
                     if (this.switchClock > 0) {
                        this.switchClock--;
                        return;
                     }

                     if (randomNum <= this.switchChance.getValueInt()) {
                        this.switchClock = this.switchDelay.getValueInt();
                        InventoryUtils.selectItemFromHotbar(class_1802.field_8281);
                     }
                  }

                  if (this.mc.field_1724.method_24518(class_1802.field_8281)) {
                     if (this.placeClock > 0) {
                        this.placeClock--;
                        return;
                     }

                     if (this.clickSimulation.getValue()) {
                        MouseSimulation.mouseClick(1);
                     }

                     randomNum = MathUtils.randomInt(1, 100);
                     if (randomNum <= this.placeChance.getValueInt()) {
                        WorldUtils.placeBlock(hit, true);
                        this.placeClock = this.placeDelay.getValueInt();
                        this.crystalling = true;
                     }
                  }
               }
            }

            if (this.crystalling) {
               if (!this.mc.field_1724.method_24518(class_1802.field_8301) && !this.crystalSelected) {
                  if (this.switchClock > 0) {
                     this.switchClock--;
                     return;
                  }

                  randomNum = MathUtils.randomInt(1, 100);
                  if (randomNum <= this.switchChance.getValueInt()) {
                     this.crystalSelected = InventoryUtils.selectItemFromHotbar(class_1802.field_8301);
                     this.switchClock = this.switchDelay.getValueInt();
                  }
               }

               if (this.mc.field_1724.method_24518(class_1802.field_8301)) {
                  AutoCrystal autoCrystal = Prestige.INSTANCE.getModuleManager().getModule(AutoCrystal.class);
                  if (!autoCrystal.isEnabled()) {
                     autoCrystal.onTick();
                  }
               }
            }
         } else {
            this.reset();
         }
      }
   }

   @Override
   public void onItemUse(ItemUseListener.ItemUseEvent event) {
      class_1799 mainHandStack = this.mc.field_1724.method_6047();
      if ((mainHandStack.method_31574(class_1802.field_8301) || mainHandStack.method_31574(class_1802.field_8281))
         && GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 1) != 1) {
         event.cancel();
      }
   }

   public void reset() {
      this.placeClock = this.placeDelay.getValueInt();
      this.switchClock = this.switchDelay.getValueInt();
      this.active = false;
      this.crystalling = false;
      this.crystalSelected = false;
   }

   @Override
   public void onAttack(AttackListener.AttackEvent event) {
      if (this.mc.field_1724.method_6047().method_31574(class_1802.field_8301) && GLFW.glfwGetMouseButton(this.mc.method_22683().method_4490(), 0) != 1) {
         event.cancel();
      }
   }
}
