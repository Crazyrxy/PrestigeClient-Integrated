package juxxl.prestige.module;

import java.util.ArrayList;
import java.util.List;
import juxxl.prestige.Prestige;
import juxxl.prestige.event.events.ButtonListener;
import juxxl.prestige.module.modules.cart.InstaCart;
import juxxl.prestige.module.modules.cart.XBowCart;
import juxxl.prestige.module.modules.client.ClickGUI;
import juxxl.prestige.module.modules.client.Friends;
import juxxl.prestige.module.modules.client.SelfDestruct;
import juxxl.prestige.module.modules.combat.AimAssist;
import juxxl.prestige.module.modules.combat.AnchorMacro;
import juxxl.prestige.module.modules.combat.AutoCrystal;
import juxxl.prestige.module.modules.combat.AutoDoubleHand;
import juxxl.prestige.module.modules.combat.AutoHitCrystal;
import juxxl.prestige.module.modules.combat.AutoInventoryTotem;
import juxxl.prestige.module.modules.combat.AutoJumpReset;
import juxxl.prestige.module.modules.combat.AutoPot;
import juxxl.prestige.module.modules.combat.AutoPotRefill;
import juxxl.prestige.module.modules.combat.AutoWTap;
import juxxl.prestige.module.modules.combat.CrystalOptimizer;
import juxxl.prestige.module.modules.combat.DoubleAnchor;
import juxxl.prestige.module.modules.combat.HoverTotem;
import juxxl.prestige.module.modules.combat.NoMissDelay;
import juxxl.prestige.module.modules.combat.SafeAnchor;
import juxxl.prestige.module.modules.combat.ShieldDisabler;
import juxxl.prestige.module.modules.combat.TotemOffhand;
import juxxl.prestige.module.modules.combat.TriggerBot;
import juxxl.prestige.module.modules.mace.MaceSwap;
import juxxl.prestige.module.modules.mace.AutoMace;
import juxxl.prestige.module.modules.mace.MaceAim;
import juxxl.prestige.module.modules.mace.StunSlam;
import juxxl.prestige.module.modules.mace.BreachSwap;
import juxxl.prestige.module.modules.misc.AutoClicker;
import juxxl.prestige.module.modules.misc.AutoXP;
import juxxl.prestige.module.modules.misc.FakeLag;
import juxxl.prestige.module.modules.misc.Freecam;
import juxxl.prestige.module.modules.misc.KeyPearl;
import juxxl.prestige.module.modules.misc.NoBreakDelay;
import juxxl.prestige.module.modules.misc.NoJumpDelay;
import juxxl.prestige.module.modules.misc.PackSpoof;
import juxxl.prestige.module.modules.misc.PingSpoof;
import juxxl.prestige.module.modules.misc.Prevent;
import juxxl.prestige.module.modules.misc.Sprint;
import juxxl.prestige.module.modules.render.HUD;
import juxxl.prestige.module.modules.render.NoBounce;
import juxxl.prestige.module.modules.render.PlayerESP;
import juxxl.prestige.module.modules.render.StorageEsp;
import juxxl.prestige.module.modules.render.TargetHud;
import juxxl.prestige.module.setting.KeybindSetting;

public final class ModuleManager implements ButtonListener {
   private final List<Module> modules = new ArrayList<>();

   public ModuleManager() {
      this.addModules();
      this.addKeybinds();
   }

   public void addModules() {
      // Combat
      this.add(new AimAssist());
      this.add(new AnchorMacro());
      this.add(new AutoCrystal());
      this.add(new AutoDoubleHand());
      this.add(new AutoHitCrystal());
      this.add(new AutoInventoryTotem());
      this.add(new TriggerBot());
      this.add(new AutoPot());
      this.add(new AutoPotRefill());
      this.add(new AutoWTap());
      this.add(new CrystalOptimizer());
      this.add(new DoubleAnchor());
      this.add(new HoverTotem());
      this.add(new NoMissDelay());
      this.add(new ShieldDisabler());
      this.add(new TotemOffhand());
      this.add(new AutoJumpReset());
      this.add(new SafeAnchor());

      // Mace
      this.add(new MaceSwap());
      this.add(new AutoMace());
      this.add(new MaceAim());
      this.add(new StunSlam());
      this.add(new BreachSwap());

      // Cart
      this.add(new InstaCart());
      this.add(new XBowCart());

      // Misc
      this.add(new Prevent());
      this.add(new AutoXP());
      this.add(new NoJumpDelay());
      this.add(new PingSpoof());
      this.add(new FakeLag());
      this.add(new AutoClicker());
      this.add(new KeyPearl());
      this.add(new NoBreakDelay());
      this.add(new Freecam());
      this.add(new PackSpoof());
      this.add(new Sprint());

      // Render
      this.add(new HUD());
      this.add(new NoBounce());
      this.add(new PlayerESP());
      this.add(new StorageEsp());
      this.add(new TargetHud());

      // Client
      this.add(new ClickGUI());
      this.add(new Friends());
      this.add(new SelfDestruct());
   }

   public List<Module> getEnabledModules() {
      return this.modules.stream().filter(Module::isEnabled).toList();
   }

   public List<Module> getModules() {
      return this.modules;
   }

   public void addKeybinds() {
      Prestige.INSTANCE.getEventManager().add(ButtonListener.class, this);
      System.out.println("[Prestige/ModuleManager] addKeybinds: registered ButtonListener with " + this.modules.size() + " modules");

      for (Module module : this.modules) {
         module.addSetting(new KeybindSetting("Keybind", module.getKey(), true).setDescription("Key to enabled the module"));
         if ("Prestige".equals(module.getName())) {
            System.out.println("[Prestige/ModuleManager] ClickGUI module bound to key=" + module.getKey());
         }
      }
   }

   public List<Module> getModulesInCategory(Category category) {
      return this.modules.stream().filter(module -> module.getCategory() == category).toList();
   }

   public <T extends Module> T getModule(Class<T> moduleClass) {
      return (T)this.modules.stream().filter(moduleClass::isInstance).findFirst().orElse(null);
   }

   public void add(Module module) {
      this.modules.add(module);
   }

   @Override
   public void onButtonPress(ButtonListener.ButtonEvent event) {
      System.out.println("[Prestige/ModuleManager] onButtonPress: button=" + event.button + " action=" + event.action + " destruct=" + SelfDestruct.destruct);
      if (!SelfDestruct.destruct) {
         this.modules.forEach(module -> {
            if (module.getKey() == event.button && event.action == 1) {
               System.out.println("[Prestige/ModuleManager] matched module " + module.getName() + " -> toggle");
               module.toggle();
            }
         });
      }
   }
}
