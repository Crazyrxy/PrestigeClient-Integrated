package juxxl.prestige.module.modules.client;

import java.awt.Color;
import juxxl.prestige.Prestige;
import juxxl.prestige.event.events.AttackListener;
import juxxl.prestige.event.events.ButtonListener;
import juxxl.prestige.event.events.HudListener;
import juxxl.prestige.managers.FriendManager;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.KeybindSetting;
import juxxl.prestige.utils.RenderUtils;
import juxxl.prestige.utils.TextRenderer;
import juxxl.prestige.utils.WorldUtils;
import net.minecraft.class_1657;
import net.minecraft.class_332;
import net.minecraft.class_3966;

public final class Friends extends Module implements ButtonListener, AttackListener, HudListener {
   private final KeybindSetting addFriendKey = new KeybindSetting("Friend Key", 2, false).setDescription("Key to add/remove friends");
   public final BooleanSetting antiAttack = new BooleanSetting("Anti-Attack", false).setDescription("Doesn't let you hit friends");
   public final BooleanSetting disableAimAssist = new BooleanSetting("Anti-Aim", false).setDescription("Disables aim assist for friends");
   public final BooleanSetting friendStatus = new BooleanSetting("Friend Status", false).setDescription("Tells you if you're aiming at a friend or not");
   private FriendManager manager;

   public Friends() {
      super("Friends", "This module makes it so you can't do certain stuff if you have a player friended!", -1, Category.CLIENT);
      this.addSettings(this.addFriendKey, this.antiAttack, this.disableAimAssist, this.friendStatus);
      this.setKey(-1);
   }

   @Override
   public void onEnable() {
      this.manager = Prestige.INSTANCE.getFriendManager();
      this.eventManager.add(ButtonListener.class, this);
      this.eventManager.add(AttackListener.class, this);
      this.eventManager.add(HudListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(ButtonListener.class, this);
      this.eventManager.remove(AttackListener.class, this);
      this.eventManager.remove(HudListener.class, this);
      super.onDisable();
   }

   @Override
   public void onButtonPress(ButtonListener.ButtonEvent event) {
      if (this.mc.field_1724 != null) {
         if (this.mc.field_1755 == null) {
            if (this.mc.field_1765 instanceof class_3966 hitResult
               && hitResult.method_17782() instanceof class_1657 player
               && event.button == this.addFriendKey.getKey()
               && event.action == 1) {
               if (!this.manager.isFriend(player)) {
                  this.manager.addFriend(player);
               } else {
                  this.manager.removeFriend(player);
               }
            }
         }
      }
   }

   @Override
   public void onAttack(AttackListener.AttackEvent event) {
      if (this.antiAttack.getValue()) {
         if (this.manager.isAimingOverFriend()) {
            event.cancel();
         }
      }
   }

   @Override
   public void onRenderHud(HudListener.HudEvent event) {
      if (this.friendStatus.getValue()) {
         class_332 context = event.context;
         RenderUtils.unscaledProjection(context);
         if (WorldUtils.getHitResult(100.0) instanceof class_3966 hitResult
            && hitResult.method_17782() instanceof class_1657 player
            && this.manager.isFriend(player)) {
            TextRenderer.drawCenteredString(
               "Player is friend", context, this.mc.method_22683().method_4480() / 2, this.mc.method_22683().method_4507() / 2 + 25, Color.GREEN.getRGB()
            );
         }

         RenderUtils.scaledProjection(context);
      }
   }
}
