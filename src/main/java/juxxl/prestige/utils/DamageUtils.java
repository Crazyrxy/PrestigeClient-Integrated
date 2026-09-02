package juxxl.prestige.utils;

import java.util.function.BiFunction;
import juxxl.prestige.Prestige;
import net.minecraft.class_1280;
import net.minecraft.class_1282;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_1934;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_5134;
import net.minecraft.class_640;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2902.class_2903;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class DamageUtils {
   public static final DamageUtils.RaycastFactory HIT_FACTORY = (context, blockPos) -> {
      class_2680 blockState = Prestige.mc.field_1687.method_8320(blockPos);
      return blockState.method_26204().method_9520() < 600.0F
         ? null
         : blockState.method_26220(Prestige.mc.field_1687, blockPos).method_1092(context.start(), context.end(), blockPos);
   };

   private DamageUtils() {
   }

   public static float crystalDamage(
      class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, DamageUtils.RaycastFactory raycastFactory
   ) {
      return explosionDamage(target, targetPos, targetBox, explosionPos, 12.0F, raycastFactory);
   }

   public static float bedDamage(class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, DamageUtils.RaycastFactory raycastFactory) {
      return explosionDamage(target, targetPos, targetBox, explosionPos, 10.0F, raycastFactory);
   }

   public static float anchorDamage(
      class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, DamageUtils.RaycastFactory raycastFactory
   ) {
      return explosionDamage(target, targetPos, targetBox, explosionPos, 10.0F, raycastFactory);
   }

   public static float explosionDamage(
      class_1309 target, class_243 targetPos, class_238 targetBox, class_243 explosionPos, float power, DamageUtils.RaycastFactory raycastFactory
   ) {
      double modDistance = distance(
         targetPos.field_1352, targetPos.field_1351, targetPos.field_1350, explosionPos.field_1352, explosionPos.field_1351, explosionPos.field_1350
      );
      if (modDistance > power) {
         return 0.0F;
      }

      double exposure = getExposure(explosionPos, targetBox, raycastFactory);
      double impact = (1.0 - modDistance / power) * exposure;
      float damage = (int)((impact * impact + impact) / 2.0 * 7.0 * 12.0 + 1.0);
      return calculateReductions(damage, target, Prestige.mc.field_1687.method_48963().method_48807(null));
   }

   public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
      return Math.sqrt(squaredDistance(x1, y1, z1, x2, y2, z2));
   }

   public static double distanceTo(class_1297 entity) {
      return distanceTo(entity.method_23317(), entity.method_23318(), entity.method_23321());
   }

   public static double distanceTo(class_2338 blockPos) {
      return distanceTo(blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260());
   }

   public static double distanceTo(class_243 vec3d) {
      return distanceTo(vec3d.method_10216(), vec3d.method_10214(), vec3d.method_10215());
   }

   public static double distanceTo(double x, double y, double z) {
      return Math.sqrt(squaredDistanceTo(x, y, z));
   }

   public static double squaredDistanceTo(class_1297 entity) {
      return squaredDistanceTo(entity.method_23317(), entity.method_23318(), entity.method_23321());
   }

   public static double squaredDistanceTo(class_2338 blockPos) {
      return squaredDistanceTo(blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260());
   }

   public static double squaredDistanceTo(double x, double y, double z) {
      return squaredDistance(Prestige.mc.field_1724.method_23317(), Prestige.mc.field_1724.method_23318(), Prestige.mc.field_1724.method_23321(), x, y, z);
   }

   public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
      double f = x1 - x2;
      double g = y1 - y2;
      double h = z1 - z2;
      return org.joml.Math.fma(f, f, org.joml.Math.fma(g, g, h * h));
   }

   public static float crystalDamage(class_1309 target, class_243 crystal, boolean predictMovement, class_2338 obsidianPos) {
      return overridingExplosionDamage(target, crystal, 12.0F, predictMovement, obsidianPos, class_2246.field_10540.method_9564());
   }

   public static float crystalDamage(class_1309 target, class_243 crystal) {
      return explosionDamage(target, crystal, 12.0F, false);
   }

   public static float bedDamage(class_1309 target, class_243 bed) {
      return explosionDamage(target, bed, 10.0F, false);
   }

   public static float anchorDamage(class_1309 target, class_243 anchor) {
      return overridingExplosionDamage(target, anchor, 10.0F, false, class_2338.method_49638(anchor), class_2246.field_10124.method_9564());
   }

   private static float overridingExplosionDamage(
      class_1309 target, class_243 explosionPos, float power, boolean predictMovement, class_2338 overridePos, class_2680 overrideState
   ) {
      return explosionDamage(target, explosionPos, power, predictMovement, getOverridingHitFactory(overridePos, overrideState));
   }

   private static float explosionDamage(class_1309 target, class_243 explosionPos, float power, boolean predictMovement) {
      return explosionDamage(target, explosionPos, power, predictMovement, HIT_FACTORY);
   }

   public static class_1934 getGameMode(class_1657 player) {
      class_640 playerListEntry = Prestige.mc.method_1562().method_2871(player.method_5667());
      return playerListEntry == null ? class_1934.field_9219 : playerListEntry.method_2958();
   }

   private static float explosionDamage(
      class_1309 target, class_243 explosionPos, float power, boolean predictMovement, DamageUtils.RaycastFactory raycastFactory
   ) {
      if (target == null) {
         return 0.0F;
      } else if (target instanceof class_1657 player && getGameMode(player) == class_1934.field_9220) {
         return 0.0F;
      } else {
         class_243 position = predictMovement ? target.method_73189().method_1019(target.method_18798()) : target.method_73189();
         class_238 box = target.method_5829();
         if (predictMovement) {
            box = box.method_997(target.method_18798());
         }

         return explosionDamage(target, position, box, explosionPos, power, raycastFactory);
      }
   }

   public static DamageUtils.RaycastFactory getOverridingHitFactory(class_2338 overridePos, class_2680 overrideState) {
      return (context, blockPos) -> {
         class_2680 blockState;
         if (blockPos.equals(overridePos)) {
            blockState = overrideState;
         } else {
            blockState = Prestige.mc.field_1687.method_8320(blockPos);
            if (blockState.method_26204().method_9520() < 600.0F) {
               return null;
            }
         }

         return blockState.method_26220(Prestige.mc.field_1687, blockPos).method_1092(context.start(), context.end(), blockPos);
      };
   }

   public static float getAttackDamage(class_1309 attacker, class_1309 target) {
      float itemDamage = (float)attacker.method_45325(class_5134.field_23721);
      class_1282 damageSource = attacker instanceof class_1657 player
         ? Prestige.mc.field_1687.method_48963().method_48802(player)
         : Prestige.mc.field_1687.method_48963().method_48812(attacker);
      class_1799 stack = attacker.method_59958();
      float enchantDamage = 0.0F;
      if (attacker instanceof class_1657 playerEntity) {
         float charge = playerEntity.method_7261(0.5F);
         itemDamage *= 0.2F + charge * charge * 0.8F;
         enchantDamage *= charge;
         if (charge > 0.9F
            && attacker.field_6017 > 0.0
            && !attacker.method_24828()
            && !attacker.method_6101()
            && !attacker.method_5799()
            && !attacker.method_6059(class_1294.field_5919)
            && !attacker.method_5765()) {
            itemDamage *= 1.5F;
         }
      }

      float damage = itemDamage + enchantDamage;
      return calculateReductions(damage, target, damageSource);
   }

   public static float fallDamage(class_1309 entity) {
      if (entity instanceof class_1657 player && player.method_31549().field_7479) {
         return 0.0F;
      } else if (!entity.method_6059(class_1294.field_5906) && !entity.method_6059(class_1294.field_5902)) {
         int surface = Prestige.mc
            .field_1687
            .method_8500(entity.method_24515())
            .method_12032(class_2903.field_13197)
            .method_12603(entity.method_31477() & 15, entity.method_31479() & 15);
         if (entity.method_31478() >= surface) {
            return fallDamageReductions(entity, surface);
         }

         class_3965 raycastResult = Prestige.mc
            .field_1687
            .method_17742(
               new class_3959(
                  entity.method_73189(),
                  new class_243(entity.method_23317(), Prestige.mc.field_1687.method_31607(), entity.method_23321()),
                  class_3960.field_17558,
                  class_242.field_36338,
                  entity
               )
            );
         return raycastResult.method_17783() == class_240.field_1333 ? 0.0F : fallDamageReductions(entity, raycastResult.method_17777().method_10264());
      } else {
         return 0.0F;
      }
   }

   private static float fallDamageReductions(class_1309 entity, int surface) {
      int fallHeight = (int)(entity.method_23318() - surface + entity.field_6017 - 3.0);
      class_1293 jumpBoostInstance = entity.method_6112(class_1294.field_5913);
      if (jumpBoostInstance != null) {
         fallHeight -= jumpBoostInstance.method_5578() + 1;
      }

      return calculateReductions(fallHeight, entity, Prestige.mc.field_1687.method_48963().method_48827());
   }

   public static float calculateReductions(float damage, class_1309 entity, class_1282 damageSource) {
      if (damageSource.method_5514()) {
         switch (Prestige.mc.field_1687.method_8407()) {
            case field_5805:
               damage = Math.min(damage / 2.0F + 1.0F, damage);
               break;
            case field_5807:
               damage *= 1.5F;
         }
      }

      damage = class_1280.method_5496(entity, damage, damageSource, getArmor(entity), (float)entity.method_45325(class_5134.field_23725));
      damage = resistanceReduction(entity, damage);
      damage = protectionReduction(entity, damage, damageSource);
      return Math.max(damage, 0.0F);
   }

   private static float getArmor(class_1309 entity) {
      return (float)Math.floor(entity.method_45325(class_5134.field_23724));
   }

   private static float protectionReduction(class_1309 player, float damage, class_1282 source) {
      return class_1280.method_5497(damage, 0.0F);
   }

   private static float resistanceReduction(class_1309 player, float damage) {
      class_1293 resistance = player.method_6112(class_1294.field_5907);
      if (resistance != null) {
         int lvl = resistance.method_5578() + 1;
         damage *= 1.0F - lvl * 0.2F;
      }

      return Math.max(damage, 0.0F);
   }

   private static float getExposure(class_243 source, class_238 box, DamageUtils.RaycastFactory raycastFactory) {
      double xDiff = box.field_1320 - box.field_1323;
      double yDiff = box.field_1325 - box.field_1322;
      double zDiff = box.field_1324 - box.field_1321;
      double xStep = 1.0 / (xDiff * 2.0 + 1.0);
      double yStep = 1.0 / (yDiff * 2.0 + 1.0);
      double zStep = 1.0 / (zDiff * 2.0 + 1.0);
      if (xStep > 0.0 && yStep > 0.0 && zStep > 0.0) {
         int misses = 0;
         int hits = 0;
         double xOffset = (1.0 - Math.floor(1.0 / xStep) * xStep) * 0.5;
         double zOffset = (1.0 - Math.floor(1.0 / zStep) * zStep) * 0.5;
         xStep *= xDiff;
         yStep *= yDiff;
         zStep *= zDiff;
         double startX = box.field_1323 + xOffset;
         double startY = box.field_1322;
         double startZ = box.field_1321 + zOffset;
         double endX = box.field_1320 + xOffset;
         double endY = box.field_1325;
         double endZ = box.field_1324 + zOffset;

         for (double x = startX; x <= endX; x += xStep) {
            for (double y = startY; y <= endY; y += yStep) {
               for (double z = startZ; z <= endZ; z += zStep) {
                  class_243 position = new class_243(x, y, z);
                  if (raycast(new DamageUtils.ExposureRaycastContext(position, source), raycastFactory) == null) {
                     misses++;
                  }

                  hits++;
               }
            }
         }

         return (float)misses / hits;
      } else {
         return 0.0F;
      }
   }

   private static class_3965 raycast(DamageUtils.ExposureRaycastContext context, DamageUtils.RaycastFactory raycastFactory) {
      return (class_3965)class_1922.method_17744(context.start, context.end, context, raycastFactory, ctx -> null);
   }

   public record ExposureRaycastContext(class_243 start, class_243 end) {
   }

   @FunctionalInterface
   public interface RaycastFactory extends BiFunction<DamageUtils.ExposureRaycastContext, class_2338, class_3965> {
   }
}
