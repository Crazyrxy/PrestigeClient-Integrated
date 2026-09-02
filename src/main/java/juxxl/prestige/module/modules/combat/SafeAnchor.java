package juxxl.prestige.module.modules.combat;

import juxxl.prestige.event.events.GameRenderListener;
import juxxl.prestige.event.events.TickListener;
import juxxl.prestige.module.Category;
import juxxl.prestige.module.Module;
import juxxl.prestige.module.setting.BooleanSetting;
import juxxl.prestige.module.setting.KeybindSetting;
import juxxl.prestige.module.setting.ModeSetting;
import juxxl.prestige.module.setting.NumberSetting;
import juxxl.prestige.utils.BlockUtils;
import juxxl.prestige.utils.InventoryUtils;
import juxxl.prestige.utils.KeyUtils;
import juxxl.prestige.utils.RenderUtils;
import juxxl.prestige.utils.WorldUtils;
import juxxl.prestige.utils.rotation.RotationHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class SafeAnchor extends Module implements TickListener, GameRenderListener {
   private final KeybindSetting triggerKey = new KeybindSetting("Trigger Key", 71).setDescription("Keybind that activates safe anchor macro");
   private final ModeSetting<SafeAnchor.Mode> mode = new ModeSetting<>("Mode", SafeAnchor.Mode.SILENT, SafeAnchor.Mode.class)
      .setDescription("Rotation targeting mode");
   private final BooleanSetting blatant = new BooleanSetting("Blatant", false)
      .setDescription("Place anchor, charge, and glowstone rapidly in 1-2 ticks (SILENT mode only)")
      .setVisible(() -> this.mode.isMode(SafeAnchor.Mode.SILENT));
   private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0.0, 20.0, 2.0, 1.0).setDescription("Ticks delay between sequence steps");
   private final NumberSetting totemSlot = new NumberSetting("Totem Slot", 1.0, 9.0, 9.0, 1.0).setDescription("Hotbar slot for totem/holding item");
   private final NumberSetting range = new NumberSetting("Range", 3.0, 6.0, 4.5, 0.1).setDescription("Targeting range in blocks");
   private final NumberSetting rotationSpeed = new NumberSetting("Rotation Speed", 1.0, 9.9, 8.5, 0.1).setDescription("Rotation speed (GCD smooth-damp)");
   private final BooleanSetting protectSelf = new BooleanSetting("Self Protection", true)
      .setDescription("Place protective glowstone block between anchor and self");
   private final BooleanSetting checkLoot = new BooleanSetting("Check Loot", true).setDescription("Abort explosion if armor/sword/totem loot is on anchor");
   private SafeAnchor.Step currentStep = SafeAnchor.Step.IDLE;
   private int delayCounter = 0;
   private int finishCooldown = 0;
   private boolean isActive = false;
   private class_2338 targetAnchorPos = null;
   private class_2338 protectionBlockPos = null;
   private float targetYaw = 0.0F;
   private float targetPitch = 0.0F;
   private boolean hasRotationTarget = false;
   private float cameraYaw = 0.0F;
   private float cameraPitch = 0.0F;
   private long lastFrameTime = 0L;
   private boolean cameraInitialized = false;

   public SafeAnchor() {
      super("Safe Anchor", "GrimAC-bypassing safe anchor macro", -1, Category.COMBAT);
      this.addSettings(
         this.triggerKey, this.mode, this.blatant, this.switchDelay, this.totemSlot, this.range, this.rotationSpeed, this.protectSelf, this.checkLoot
      );
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      this.eventManager.add(GameRenderListener.class, this);
      this.fullReset();
      this.finishCooldown = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      this.eventManager.remove(GameRenderListener.class, this);
      this.fullReset();
      this.finishCooldown = 0;
      super.onDisable();
   }

   @Override
   public void onGameRender(GameRenderListener.GameRenderEvent event) {
      if (this.mc.field_1724 != null && this.isActive && this.mode.isMode(SafeAnchor.Mode.CAMERA) && this.hasRotationTarget) {
         long now = System.nanoTime();
         if (!this.cameraInitialized) {
            this.cameraYaw = this.mc.field_1724.method_36454();
            this.cameraPitch = this.mc.field_1724.method_36455();
            this.lastFrameTime = now;
            this.cameraInitialized = true;
         } else {
            float deltaTime = (float)(now - this.lastFrameTime) / 1.0E9F;
            this.lastFrameTime = now;
            deltaTime = Math.min(deltaTime, 0.1F);
            float speed = this.rotationSpeed.getValueFloat() * 3.0F;
            float factor = 1.0F - (float)Math.exp(-speed * deltaTime);
            float yawDiff = class_3532.method_15393(this.targetYaw - this.cameraYaw);
            float pitchDiff = this.targetPitch - this.cameraPitch;
            this.cameraYaw += yawDiff * factor;
            this.cameraPitch += pitchDiff * factor;
            this.cameraPitch = class_3532.method_15363(this.cameraPitch, -90.0F, 90.0F);
            if (this.mc.field_1690 != null && this.mc.field_1690.method_42495() != null) {
               float sens = ((Double)this.mc.field_1690.method_42495().method_41753()).floatValue() * 0.6F + 0.2F;
               float gcd = sens * sens * sens * 1.2F;
               float finalYawDiff = this.cameraYaw - this.mc.field_1724.method_36454();
               float finalPitchDiff = this.cameraPitch - this.mc.field_1724.method_36455();
               float yawGridDiff = Math.round(finalYawDiff / gcd) * gcd;
               float pitchGridDiff = Math.round(finalPitchDiff / gcd) * gcd;
               this.cameraYaw = this.mc.field_1724.method_36454() + yawGridDiff;
               this.cameraPitch = class_3532.method_15363(this.mc.field_1724.method_36455() + pitchGridDiff, -90.0F, 90.0F);
            }

            this.mc.field_1724.method_36456(this.cameraYaw);
            this.mc.field_1724.method_36457(this.cameraPitch);
         }
      }
   }

   @Override
   public void onTick() {
      if (this.mc.field_1755 == null && this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.finishCooldown > 0) {
            this.finishCooldown--;
         } else {
            if (!this.isActive && KeyUtils.isKeyPressed(this.triggerKey.getKey())) {
               this.isActive = true;
               this.currentStep = SafeAnchor.Step.FIND_TARGET;
               this.delayCounter = 0;
               this.hasRotationTarget = false;
               this.cameraInitialized = false;
            }

            if (this.isActive) {
               if (this.hasRotationTarget) {
                  this.applyRotation(this.targetYaw, this.targetPitch);
               }

               boolean isBlatant = this.blatant.getValue() && this.mode.isMode(SafeAnchor.Mode.SILENT);
               int maxStepsPerTick = isBlatant ? 6 : 1;
               int stepsExecuted = 0;

               while (this.isActive && stepsExecuted < maxStepsPerTick) {
                  int requiredDelay = isBlatant ? 0 : this.switchDelay.getValueInt();
                  if (this.delayCounter < requiredDelay) {
                     this.delayCounter++;
                     break;
                  }

                  SafeAnchor.Step prevStep = this.currentStep;
                  switch (this.currentStep) {
                     case FIND_TARGET:
                        this.executeFindTarget();
                        break;
                     case PLACE_ANCHOR:
                        this.executePlaceAnchor();
                        break;
                     case CHARGE_ANCHOR:
                        this.executeChargeAnchor();
                        break;
                     case PLACE_SHIELD:
                        this.executePlaceShield();
                        break;
                     case SWAP_TOTEM:
                        this.executeSwapTotem();
                        break;
                     case EXPLODE:
                        this.executeExplode();
                        break;
                     case DONE:
                        this.fullReset();
                  }

                  stepsExecuted++;
                  if (this.currentStep == prevStep || this.currentStep == SafeAnchor.Step.IDLE) {
                     break;
                  }
               }
            }
         }
      }
   }

   private void executeFindTarget() {
      class_3965 hitResult = this.getTargetBlockHitResult();
      if (hitResult == null) {
         this.fullReset();
      } else {
         class_2338 hitPos = hitResult.method_17777();
         class_2680 hitState = this.mc.field_1687.method_8320(hitPos);
         if (hitState.method_27852(class_2246.field_23152)) {
            this.targetAnchorPos = hitPos;
            if (this.mc.field_1724.method_73189().method_1022(class_243.method_24953(this.targetAnchorPos)) > this.range.getValue()) {
               this.fullReset();
            } else {
               this.computeProtectionPos();
               class_243 chargeTarget = this.calculateMinimalAngleTarget(this.targetAnchorPos, class_2350.field_11036);
               this.setRotationTarget(chargeTarget);
               if (BlockUtils.isAnchorCharged(this.targetAnchorPos)) {
                  this.advanceTo(SafeAnchor.Step.PLACE_SHIELD);
               } else {
                  this.advanceTo(SafeAnchor.Step.CHARGE_ANCHOR);
               }
            }
         } else {
            if (this.isReplaceable(hitPos)) {
               this.targetAnchorPos = hitPos;
            } else {
               this.targetAnchorPos = hitPos.method_10093(hitResult.method_17780());
            }

            if (this.mc.field_1724.method_73189().method_1022(class_243.method_24953(this.targetAnchorPos)) > this.range.getValue()) {
               this.fullReset();
            } else if (this.mc.field_1687.method_8320(this.targetAnchorPos).method_27852(class_2246.field_23152)) {
               this.computeProtectionPos();
               class_243 chargeTarget = this.calculateMinimalAngleTarget(this.targetAnchorPos, class_2350.field_11036);
               this.setRotationTarget(chargeTarget);
               if (BlockUtils.isAnchorCharged(this.targetAnchorPos)) {
                  this.advanceTo(SafeAnchor.Step.PLACE_SHIELD);
               } else {
                  this.advanceTo(SafeAnchor.Step.CHARGE_ANCHOR);
               }
            } else {
               this.computeProtectionPos();
               class_3965 placementHit = this.findPlacementSide(this.targetAnchorPos);
               if (placementHit != null) {
                  class_243 aimTarget = this.calculateMinimalAngleTarget(placementHit.method_17777(), placementHit.method_17780());
                  this.setRotationTarget(aimTarget);
               }

               this.advanceTo(SafeAnchor.Step.PLACE_ANCHOR);
            }
         }
      }
   }

   private void executePlaceAnchor() {
      if (this.targetAnchorPos == null) {
         this.fullReset();
      } else if (this.mc.field_1687.method_8320(this.targetAnchorPos).method_27852(class_2246.field_23152)) {
         class_243 chargeTarget = this.calculateMinimalAngleTarget(this.targetAnchorPos, class_2350.field_11036);
         this.setRotationTarget(chargeTarget);
         this.advanceTo(SafeAnchor.Step.CHARGE_ANCHOR);
      } else if (!this.isReplaceable(this.targetAnchorPos)) {
         this.fullReset();
      } else if (!this.hasItem(class_1802.field_23141)) {
         this.fullReset();
      } else {
         class_3965 hit = this.findPlacementSide(this.targetAnchorPos);
         if (hit == null) {
            this.fullReset();
         } else {
            class_243 aimTarget = this.calculateMinimalAngleTarget(hit.method_17777(), hit.method_17780());
            this.setRotationTarget(aimTarget);
            this.applyRotation(this.targetYaw, this.targetPitch);
            if (this.isRotationClose()) {
               if (!this.swapTo(class_1802.field_23141)) {
                  this.fullReset();
               } else {
                  class_3965 placementHit = new class_3965(aimTarget, hit.method_17780(), hit.method_17777(), false);
                  this.performBlockInteraction(placementHit);
                  class_243 chargeTarget = this.calculateMinimalAngleTarget(this.targetAnchorPos, class_2350.field_11036);
                  this.setRotationTarget(chargeTarget);
                  this.advanceTo(SafeAnchor.Step.CHARGE_ANCHOR);
               }
            }
         }
      }
   }

   private void executeChargeAnchor() {
      if (this.targetAnchorPos == null) {
         this.fullReset();
      } else if (BlockUtils.isAnchorCharged(this.targetAnchorPos)) {
         this.advanceTo(SafeAnchor.Step.PLACE_SHIELD);
      } else if (!this.hasItem(class_1802.field_8801)) {
         this.fullReset();
      } else {
         class_243 aimTarget = this.calculateMinimalAngleTarget(this.targetAnchorPos, class_2350.field_11036);
         this.setRotationTarget(aimTarget);
         this.applyRotation(this.targetYaw, this.targetPitch);
         if (this.isRotationClose()) {
            if (!this.swapTo(class_1802.field_8801)) {
               this.fullReset();
            } else {
               class_3965 hit = new class_3965(aimTarget, class_2350.field_11036, this.targetAnchorPos, false);
               this.performBlockInteraction(hit);
               this.advanceTo(SafeAnchor.Step.PLACE_SHIELD);
            }
         }
      }
   }

   private void executePlaceShield() {
      if (this.protectSelf.getValue() && this.protectionBlockPos != null && this.isReplaceable(this.protectionBlockPos)) {
         class_3965 hit = this.findPlacementSide(this.protectionBlockPos);
         if (hit == null) {
            this.skipShield();
         } else {
            class_243 aimTarget = this.calculateMinimalAngleTarget(hit.method_17777(), hit.method_17780());
            if (aimTarget == null) {
               this.skipShield();
            } else {
               this.setRotationTarget(aimTarget);
               this.applyRotation(this.targetYaw, this.targetPitch);
               if (this.isRotationClose()) {
                  if (this.hasItem(class_1802.field_8801) && this.swapTo(class_1802.field_8801)) {
                     class_3965 placementHit = new class_3965(aimTarget, hit.method_17780(), hit.method_17777(), false);
                     this.performBlockInteraction(placementHit);
                     this.skipShield();
                  } else {
                     this.skipShield();
                  }
               }
            }
         }
      } else {
         this.skipShield();
      }
   }

   private void skipShield() {
      if (this.targetAnchorPos != null) {
         class_243 explodeTarget = this.calculateMinimalAngleTarget(this.targetAnchorPos, class_2350.field_11036);
         this.setRotationTarget(explodeTarget);
      }

      this.advanceTo(SafeAnchor.Step.SWAP_TOTEM);
   }

   private void executeSwapTotem() {
      if (this.mc.field_1724 != null) {
         boolean foundInHotbar = false;

         for (int i = 0; i < 9; i++) {
            if (this.mc.field_1724.method_31548().method_5438(i).method_31574(class_1802.field_8288)) {
               InventoryUtils.setInvSlot(i);
               foundInHotbar = true;
               break;
            }
         }

         if (!foundInHotbar) {
            int safeSlot = this.findSafeSlotForExplode();
            InventoryUtils.setInvSlot(safeSlot);
         } else {
            class_1799 mainHandItem = this.mc.field_1724.method_6047();
            if (mainHandItem.method_31574(class_1802.field_8801) || mainHandItem.method_31574(class_1802.field_23141)) {
               int safeSlot = this.findSafeSlotForExplode();
               InventoryUtils.setInvSlot(safeSlot);
            }
         }
      }

      if (this.targetAnchorPos != null && this.hasRotationTarget) {
         this.applyRotation(this.targetYaw, this.targetPitch);
      }

      this.advanceTo(SafeAnchor.Step.EXPLODE);
   }

   private void executeExplode() {
      if (this.targetAnchorPos == null) {
         this.fullReset();
      } else if (this.checkLoot.getValue() && this.hasLootNearby(this.targetAnchorPos)) {
         this.fullReset();
      } else {
         class_243 aimTarget = this.calculateMinimalAngleTarget(this.targetAnchorPos, class_2350.field_11036);
         this.setRotationTarget(aimTarget);
         this.applyRotation(this.targetYaw, this.targetPitch);
         if (this.isRotationClose()) {
            class_3965 hit = new class_3965(aimTarget, class_2350.field_11036, this.targetAnchorPos, false);
            this.performBlockInteraction(hit);
            this.finishCooldown = this.switchDelay.getValueInt() + 2;
            this.advanceTo(SafeAnchor.Step.DONE);
         }
      }
   }

   private void advanceTo(SafeAnchor.Step next) {
      this.currentStep = next;
      this.delayCounter = 0;
   }

   private void fullReset() {
      this.isActive = false;
      this.currentStep = SafeAnchor.Step.IDLE;
      this.delayCounter = 0;
      this.targetAnchorPos = null;
      this.protectionBlockPos = null;
      this.hasRotationTarget = false;
      this.cameraInitialized = false;
      RotationHandler.clearSilent();
      if (this.mc.field_1724 != null) {
         this.targetYaw = this.mc.field_1724.method_36454();
         this.targetPitch = this.mc.field_1724.method_36455();
      }
   }

   private void setRotationTarget(class_243 worldPos) {
      if (this.mc.field_1724 != null) {
         class_243 eyes = this.mc.field_1724.method_5836(RenderUtils.tickProgress());
         double dx = worldPos.field_1352 - eyes.field_1352;
         double dy = worldPos.field_1351 - eyes.field_1351;
         double dz = worldPos.field_1350 - eyes.field_1350;
         double distXZ = Math.sqrt(dx * dx + dz * dz);
         this.targetYaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
         this.targetPitch = (float)(-Math.toDegrees(Math.atan2(dy, distXZ)));
         this.hasRotationTarget = true;
      }
   }

   private void applyRotation(float yaw, float pitch) {
      if (this.mc.field_1724 != null) {
         if (this.mode.isMode(SafeAnchor.Mode.SILENT)) {
            float speed = Math.min(9.5F, Math.max(1.0F, this.rotationSpeed.getValueFloat()));
            RotationHandler.setSilent(yaw, pitch, speed);
         } else if (this.mode.isMode(SafeAnchor.Mode.CAMERA)) {
            float speed = Math.min(9.5F, Math.max(1.0F, this.rotationSpeed.getValueFloat()));
            RotationHandler.setSilent(this.mc.field_1724.method_36454(), this.mc.field_1724.method_36455(), speed);
         }
      }
   }

   private boolean isRotationClose() {
      if (this.blatant.getValue() && this.mode.isMode(SafeAnchor.Mode.SILENT)) {
         return true;
      }

      if (this.mc.field_1724 == null) {
         return false;
      }

      float currentYaw;
      float currentPitch;
      if (this.mode.isMode(SafeAnchor.Mode.SILENT) && RotationHandler.isSilentActive()) {
         currentYaw = RotationHandler.getSilentYaw();
         currentPitch = RotationHandler.getSilentPitch();
      } else {
         currentYaw = this.mc.field_1724.method_36454();
         currentPitch = this.mc.field_1724.method_36455();
      }

      float yawDiff = Math.abs(class_3532.method_15393(this.targetYaw - currentYaw));
      float pitchDiff = Math.abs(this.targetPitch - currentPitch);
      return yawDiff < 5.0F && pitchDiff < 5.0F;
   }

   private class_243 calculateMinimalAngleTarget(class_2338 pos, class_2350 side) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         float currentYaw = this.mode.isMode(SafeAnchor.Mode.SILENT) && RotationHandler.isSilentActive()
            ? RotationHandler.getSilentYaw()
            : this.mc.field_1724.method_36454();
         float currentPitch = this.mode.isMode(SafeAnchor.Mode.SILENT) && RotationHandler.isSilentActive()
            ? RotationHandler.getSilentPitch()
            : this.mc.field_1724.method_36455();
         class_3965 currentHit = this.rayTraceBlocks(currentYaw, currentPitch, this.range.getValue());
         if (currentHit != null && currentHit.method_17783() == class_240.field_1332 && currentHit.method_17777().equals(pos)) {
            return currentHit.method_17784();
         }

         class_243 bestTarget = class_243.method_24953(pos);
         double minAngleDelta = Double.MAX_VALUE;
         class_238 box = new class_238(pos);
         double[] xSamples = new double[]{box.field_1323 + 0.1, box.field_1323 + 0.3, box.field_1323 + 0.5, box.field_1320 - 0.3, box.field_1320 - 0.1};
         double[] ySamples = new double[]{box.field_1322 + 0.1, box.field_1322 + 0.3, box.field_1322 + 0.5, box.field_1325 - 0.3, box.field_1325 - 0.1};
         double[] zSamples = new double[]{box.field_1321 + 0.1, box.field_1321 + 0.3, box.field_1321 + 0.5, box.field_1324 - 0.3, box.field_1324 - 0.1};
         class_243 eyes = this.mc.field_1724.method_5836(RenderUtils.tickProgress());

         for (double x : xSamples) {
            for (double y : ySamples) {
               for (double z : zSamples) {
                  class_243 sample = new class_243(x, y, z);
                  class_3959 clipContext = new class_3959(eyes, sample, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
                  class_3965 blockCheck = this.mc.field_1687.method_17742(clipContext);
                  if (blockCheck.method_17783() == class_240.field_1333 || !(blockCheck.method_17784().method_1025(eyes) < sample.method_1025(eyes) - 0.05)) {
                     double dx = sample.field_1352 - eyes.field_1352;
                     double dy = sample.field_1351 - eyes.field_1351;
                     double dz = sample.field_1350 - eyes.field_1350;
                     double distXZ = Math.sqrt(dx * dx + dz * dz);
                     float yRot = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
                     float xRot = (float)(-Math.toDegrees(Math.atan2(dy, distXZ)));
                     float yDiff = Math.abs(class_3532.method_15393(yRot - currentYaw));
                     float xDiff = Math.abs(xRot - currentPitch);
                     double delta = yDiff + xDiff;
                     if (delta < minAngleDelta) {
                        minAngleDelta = delta;
                        bestTarget = sample;
                     }
                  }
               }
            }
         }

         return bestTarget;
      } else {
         return class_243.method_24953(pos);
      }
   }

   private class_3965 rayTraceBlocks(float yaw, float pitch, double reach) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         class_243 src = this.mc.field_1724.method_5836(RenderUtils.tickProgress());
         float yawRad = (float)Math.toRadians(yaw);
         float pitchRad = (float)Math.toRadians(pitch);
         double f = -class_3532.method_15374(yawRad) * class_3532.method_15362(pitchRad);
         double g = -class_3532.method_15374(pitchRad);
         double h = class_3532.method_15362(yawRad) * class_3532.method_15362(pitchRad);
         class_243 end = src.method_1031(f * reach, g * reach, h * reach);
         return this.mc.field_1687.method_17742(new class_3959(src, end, class_3960.field_17559, class_242.field_1348, this.mc.field_1724));
      } else {
         return null;
      }
   }

   private void performBlockInteraction(class_3965 hit) {
      if (this.mc.field_1724 != null && this.mc.field_1761 != null && hit != null) {
         if (this.mode.isMode(SafeAnchor.Mode.CAMERA)) {
            if (this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, hit).method_23665()) {
               this.mc.field_1724.method_6104(class_1268.field_5808);
            } else {
               this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
            }
         } else {
            WorldUtils.placeBlock(hit, true);
         }
      }
   }

   private class_3965 getTargetBlockHitResult() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.mc.field_1765 instanceof class_3965 hitResult && hitResult.method_17783() == class_240.field_1332) {
            return hitResult;
         } else {
            class_243 src = this.mc.field_1724.method_5836(RenderUtils.tickProgress());
            class_243 look = this.mc.field_1724.method_5720();
            class_243 end = src.method_1019(look.method_1021(this.range.getValue()));
            class_3965 blockRaytrace = this.mc
               .field_1687
               .method_17742(new class_3959(src, end, class_3960.field_17559, class_242.field_1348, this.mc.field_1724));
            return blockRaytrace != null && blockRaytrace.method_17783() == class_240.field_1332 ? blockRaytrace : null;
         }
      } else {
         return null;
      }
   }

   private class_3965 findPlacementSide(class_2338 pos) {
      class_2350[] directions = new class_2350[]{
         class_2350.field_11033, class_2350.field_11036, class_2350.field_11043, class_2350.field_11035, class_2350.field_11039, class_2350.field_11034
      };

      for (class_2350 dir : directions) {
         class_2338 neighbor = pos.method_10093(dir);
         if (!this.isReplaceable(neighbor)) {
            return new class_3965(
               class_243.method_24953(neighbor)
                  .method_1031(dir.method_10153().method_10148() * 0.5, dir.method_10153().method_10164() * 0.5, dir.method_10153().method_10165() * 0.5),
               dir.method_10153(),
               neighbor,
               false
            );
         }
      }

      return null;
   }

   private void computeProtectionPos() {
      if (this.mc.field_1724 != null && this.targetAnchorPos != null) {
         class_243 playerPos = this.mc.field_1724.method_73189();
         class_243 anchorPos = class_243.method_24953(this.targetAnchorPos);
         class_243 midpoint = playerPos.method_1031(0.0, 0.5, 0.0).method_1019(anchorPos).method_1021(0.5);
         class_2338 protPos = class_2338.method_49637(midpoint.field_1352, midpoint.field_1351, midpoint.field_1350);
         if (this.mc.field_1724.method_24515().method_10264() > this.targetAnchorPos.method_10264()) {
            int playerY = this.mc.field_1724.method_24515().method_10264();
            int anchorY = this.targetAnchorPos.method_10264();
            int targetY = (playerY + anchorY) / 2;
            protPos = new class_2338((int)Math.floor(midpoint.field_1352), targetY, (int)Math.floor(midpoint.field_1350));
         }

         if (protPos.equals(this.targetAnchorPos) || protPos.equals(this.mc.field_1724.method_24515())) {
            class_2350 facing = class_2350.method_10150(this.mc.field_1724.method_36454());
            protPos = this.mc.field_1724.method_24515().method_10093(facing);
            if (this.mc.field_1724.method_24515().method_10264() > this.targetAnchorPos.method_10264()) {
               protPos = protPos.method_10074();
            }

            if (protPos.equals(this.targetAnchorPos)) {
               protPos = this.targetAnchorPos.method_10084();
            }
         }

         this.protectionBlockPos = protPos;
      }
   }

   private boolean isReplaceable(class_2338 pos) {
      if (this.mc.field_1687 != null && pos != null) {
         class_2680 blockState = this.mc.field_1687.method_8320(pos);
         return blockState.method_26215()
            ? true
            : blockState.method_27852(class_2246.field_10479)
               || blockState.method_27852(class_2246.field_10214)
               || blockState.method_27852(class_2246.field_10112)
               || blockState.method_27852(class_2246.field_10313)
               || blockState.method_27852(class_2246.field_10428)
               || blockState.method_27852(class_2246.field_10597)
               || blockState.method_27852(class_2246.field_10036)
               || blockState.method_27852(class_2246.field_22089)
               || blockState.method_27852(class_2246.field_10382)
               || blockState.method_27852(class_2246.field_10164)
               || blockState.method_27852(class_2246.field_10477)
               || blockState.method_27852(class_2246.field_10376)
               || blockState.method_27852(class_2246.field_10238)
               || blockState.method_27852(class_2246.field_9993)
               || blockState.method_45474();
      } else {
         return false;
      }
   }

   private boolean hasItem(class_1792 item) {
      if (this.mc.field_1724 == null) {
         return false;
      }

      for (int i = 0; i < 9; i++) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_31574(item)) {
            return true;
         }
      }

      return this.mc.field_1724.method_6079().method_31574(item);
   }

   private int findSafeSlotForExplode() {
      if (this.mc.field_1724 == null) {
         return this.totemSlot.getValueInt() - 1;
      }

      int preferredSlot = this.totemSlot.getValueInt() - 1;
      class_1799 preferredStack = this.mc.field_1724.method_31548().method_5438(preferredSlot);
      if (!preferredStack.method_31574(class_1802.field_8801) && !preferredStack.method_31574(class_1802.field_23141)) {
         return preferredSlot;
      }

      for (int i = 0; i < 9; i++) {
         class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
         if (!stack.method_31574(class_1802.field_8801) && !stack.method_31574(class_1802.field_23141)) {
            return i;
         }
      }

      return preferredSlot;
   }

   private boolean swapTo(class_1792 item) {
      if (this.mc.field_1724 == null) {
         return false;
      }

      if (this.mc.field_1724.method_6047().method_31574(item)) {
         return true;
      }

      for (int i = 0; i < 9; i++) {
         if (this.mc.field_1724.method_31548().method_5438(i).method_31574(item)) {
            InventoryUtils.setInvSlot(i);
            return true;
         }
      }

      return false;
   }

   private boolean hasLootNearby(class_2338 anchorPos) {
      if (this.mc.field_1687 == null) {
         return false;
      }

      class_238 searchBox = new class_238(anchorPos).method_1014(3.5);

      for (class_1297 entity : this.mc.field_1687.method_8335(null, searchBox)) {
         if (entity instanceof class_1542 itemEntity) {
            class_1799 stack = itemEntity.method_6983();
            if (this.isHighValueLoot(stack)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isHighValueLoot(class_1799 stack) {
      if (stack.method_7960()) {
         return false;
      } else {
         class_1792 item = stack.method_7909();
         if (item == class_1802.field_8288) {
            return true;
         } else {
            return item == class_1802.field_22027
                  || item == class_1802.field_22028
                  || item == class_1802.field_22029
                  || item == class_1802.field_22030
                  || item == class_1802.field_22022
               ? true
               : item == class_1802.field_8805
                  || item == class_1802.field_8058
                  || item == class_1802.field_8348
                  || item == class_1802.field_8285
                  || item == class_1802.field_8802;
         }
      }
   }

   public enum Mode {
      SILENT,
      CAMERA;
   }

   private enum Step {
      IDLE,
      FIND_TARGET,
      PLACE_ANCHOR,
      CHARGE_ANCHOR,
      PLACE_SHIELD,
      SWAP_TOTEM,
      EXPLODE,
      DONE;
   }
}
