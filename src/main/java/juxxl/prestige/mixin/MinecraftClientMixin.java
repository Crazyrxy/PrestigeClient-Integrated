package juxxl.prestige.mixin;

import juxxl.prestige.Prestige;
import juxxl.prestige.event.EventManager;
import juxxl.prestige.event.events.AttackListener;
import juxxl.prestige.event.events.BlockBreakingListener;
import juxxl.prestige.event.events.ItemUseListener;
import juxxl.prestige.event.events.ResolutionListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.utils.MouseSimulation;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_310.class)
public class MinecraftClientMixin {
   @Shadow
   @Nullable
   public class_638 field_1687;
   @Shadow
   @Final
   private class_1041 field_1704;

   @Inject(method = "method_1574", at = @At("HEAD"))
   private void onTick(CallbackInfo ci) {
      if (this.field_1687 != null) {
         TickListener.TickEvent event = new TickListener.TickEvent();
         EventManager.fire(event);
      }
   }

   @Inject(method = "method_15993", at = @At("HEAD"))
   private void onResolutionChanged(CallbackInfo ci) {
      EventManager.fire(new ResolutionListener.ResolutionEvent(this.field_1704));
   }

   @Inject(method = "method_1583", at = @At("HEAD"), cancellable = true)
   private void onItemUse(CallbackInfo ci) {
      ItemUseListener.ItemUseEvent event = new ItemUseListener.ItemUseEvent();
      EventManager.fire(event);
      if (event.isCancelled()) {
         ci.cancel();
      }

      if (MouseSimulation.isMouseButtonPressed(1)) {
         MouseSimulation.mouseButtons.put(1, false);
         ci.cancel();
      }
   }

   @Inject(method = "method_1536", at = @At("HEAD"), cancellable = true)
   private void onAttack(CallbackInfoReturnable<Boolean> cir) {
      AttackListener.AttackEvent event = new AttackListener.AttackEvent();
      EventManager.fire(event);
      if (event.isCancelled()) {
         cir.setReturnValue(false);
      }

      if (MouseSimulation.isMouseButtonPressed(0)) {
         MouseSimulation.mouseButtons.put(0, false);
         cir.setReturnValue(false);
      }
   }

   @Inject(method = "method_1590", at = @At("HEAD"), cancellable = true)
   private void onBlockBreaking(boolean breaking, CallbackInfo ci) {
      BlockBreakingListener.BlockBreakingEvent event = new BlockBreakingListener.BlockBreakingEvent();
      EventManager.fire(event);
      if (event.isCancelled()) {
         ci.cancel();
      }

      if (MouseSimulation.isMouseButtonPressed(0)) {
         MouseSimulation.mouseButtons.put(0, false);
         ci.cancel();
      }
   }

   @Inject(method = "method_1490", at = @At("HEAD"))
   private void onClose(CallbackInfo ci) {
      Prestige.INSTANCE.getProfileManager().saveProfile();
   }
}
