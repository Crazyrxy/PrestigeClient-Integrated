package juxxl.prestige.utils;

import java.util.Objects;
import java.util.stream.Stream;
import juxxl.prestige.Prestige;
import juxxl.prestige.module.modules.client.Friends;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1675;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2818;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_9362;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class WorldUtils {
   public static boolean isDeadBodyNearby() {
      return Prestige.mc
         .field_1687
         .method_18456()
         .parallelStream()
         .filter(e -> e != Prestige.mc.field_1724)
         .filter(e -> e.method_5858(Prestige.mc.field_1724) <= 36.0)
         .anyMatch(class_1309::method_29504);
   }

   public static class_1297 findNearestEntity(class_1657 toPlayer, float radius, boolean seeOnly) {
      float mr = Float.MAX_VALUE;
      class_1297 entity = null;
      assert Prestige.mc.field_1687 != null;

      for (class_1297 e : Prestige.mc.field_1687.method_18112()) {
         float d = e.method_5739(toPlayer);
         if (e != toPlayer && d <= radius && Prestige.mc.field_1724.method_6057(e) == seeOnly && d < mr) {
            mr = d;
            entity = e;
         }
      }

      return entity;
   }

   public static double distance(class_243 fromVec, class_243 toVec) {
      return Math.sqrt(
         Math.pow(toVec.field_1352 - fromVec.field_1352, 2.0)
            + Math.pow(toVec.field_1351 - fromVec.field_1351, 2.0)
            + Math.pow(toVec.field_1350 - fromVec.field_1350, 2.0)
      );
   }

   public static class_1657 findNearestPlayer(class_1657 toPlayer, float range, boolean seeOnly, boolean excludeFriends) {
      float minRange = Float.MAX_VALUE;
      class_1657 minPlayer = null;

      for (class_1657 player : Prestige.mc.field_1687.method_18456()) {
         float distance = (float)distance(toPlayer.method_73189(), player.method_73189());
         if ((
               !excludeFriends
                  || !Prestige.INSTANCE.getModuleManager().getModule(Friends.class).disableAimAssist.getValue()
                  || !Prestige.INSTANCE.getFriendManager().isFriend(player)
            )
            && player != toPlayer
            && distance <= range
            && player.method_6057(toPlayer) == seeOnly
            && distance < minRange) {
            minRange = distance;
            minPlayer = player;
         }
      }

      return minPlayer;
   }

   public static class_243 getPlayerLookVec(float yaw, float pitch) {
      float f = pitch * (float) (Math.PI / 180.0);
      float g = -yaw * (float) (Math.PI / 180.0);
      float h = class_3532.method_15362(g);
      float i = class_3532.method_15374(g);
      float j = class_3532.method_15362(f);
      float k = class_3532.method_15374(f);
      return new class_243(i * j, -k, h * j);
   }

   public static class_243 getPlayerLookVec(class_1657 player) {
      return getPlayerLookVec(player.method_36454(), player.method_36455());
   }

   public static class_239 getHitResult(double radius) {
      return getHitResult(Prestige.mc.field_1724, false, Prestige.mc.field_1724.method_36454(), Prestige.mc.field_1724.method_36455(), radius);
   }

   public static class_239 getHitResult(class_1657 entity, boolean ignoreInvisibles, float yaw, float pitch, double distance) {
      if (entity != null && Prestige.mc.field_1687 != null) {
         double d = distance;
         class_243 cameraPosVec = entity.method_5836(RenderUtils.tickProgress());
         class_243 rotationVec = getPlayerLookVec(yaw, pitch);
         class_243 range = cameraPosVec.method_1031(rotationVec.field_1352 * d, rotationVec.field_1351 * d, rotationVec.field_1350 * d);
         class_239 result = Prestige.mc.field_1687.method_17742(new class_3959(cameraPosVec, range, class_3960.field_17559, class_242.field_1348, entity));
         double e = d * d;
         d = distance;
         if (result != null) {
            e = result.method_17784().method_1025(cameraPosVec);
         }

         class_243 vec3d3 = cameraPosVec.method_1031(rotationVec.field_1352 * d, rotationVec.field_1351 * d, rotationVec.field_1350 * d);
         class_238 box = entity.method_5829().method_18804(rotationVec.method_1021(d)).method_1009(1.0, 1.0, 1.0);
         class_3966 entityHitResult = class_1675.method_18075(
            entity, cameraPosVec, vec3d3, box, entityx -> !entityx.method_7325() && entityx.method_5863() && (!entityx.method_5767() || !ignoreInvisibles), e
         );
         if (entityHitResult != null) {
            class_243 vec3d4 = entityHitResult.method_17784();
            double g = cameraPosVec.method_1025(vec3d4);
            if (distance > distance && g > Math.pow(distance, 2.0) || g < e || result == null) {
               result = (class_239)(g > Math.pow(distance, 2.0)
                  ? class_3965.method_17778(
                     vec3d4, class_2350.method_10142(rotationVec.field_1352, rotationVec.field_1351, rotationVec.field_1350), class_2338.method_49638(vec3d4)
                  )
                  : entityHitResult);
            }
         }

         return result;
      } else {
         return null;
      }
   }

   public static void placeBlock(class_3965 blockHit, boolean swingHand) {
      class_1269 result = Prestige.mc.field_1761.method_2896(Prestige.mc.field_1724, class_1268.field_5808, blockHit);
      if (result.method_23665() && swingHand) {
         Prestige.mc.field_1724.method_6104(class_1268.field_5808);
      }
   }

   public static Stream<class_2818> getLoadedChunks() {
      int radius = Math.max(2, Prestige.mc.field_1690.method_38521()) + 3;
      int diameter = radius * 2 + 1;
      class_1923 center = Prestige.mc.field_1724.method_31476();
      class_1923 min = new class_1923(center.field_9181 - radius, center.field_9180 - radius);
      class_1923 max = new class_1923(center.field_9181 + radius, center.field_9180 + radius);
      return Stream.<class_1923>iterate(min, pos -> {
            int x = pos.field_9181;
            int z = pos.field_9180;
            if (++x > max.field_9181) {
               x = min.field_9181;
               z++;
            }

            if (z > max.field_9180) {
               throw new IllegalStateException("Stream limit didn't work.");
            } else {
               return new class_1923(x, z);
            }
         })
         .limit((long)diameter * diameter)
         .filter(c -> Prestige.mc.field_1687.method_8393(c.field_9181, c.field_9180))
         .map(c -> Prestige.mc.field_1687.method_8497(c.field_9181, c.field_9180))
         .filter(Objects::nonNull);
   }

   public static boolean isShieldFacingAway(class_1657 player) {
      if (Prestige.mc.field_1724 != null && player != null) {
         class_243 playerPos = Prestige.mc.field_1724.method_73189();
         class_243 targetPos = player.method_73189();
         class_243 directionToPlayer = playerPos.method_1020(targetPos).method_1029();
         float yaw = player.method_36454();
         float pitch = player.method_36455();
         class_243 facingDirection = new class_243(
               -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
               -Math.sin(Math.toRadians(pitch)),
               Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
            )
            .method_1029();
         double dotProduct = facingDirection.method_1026(directionToPlayer);
         return dotProduct < 0.0;
      } else {
         return false;
      }
   }

   public static boolean isSword(class_1799 itemStack) {
      return itemStack.method_31573(class_3489.field_42611);
   }

   public static boolean isAxe(class_1799 itemStack) {
      return itemStack.method_31573(class_3489.field_42612);
   }

   public static boolean isWeapon(class_1799 itemStack) {
      return isSword(itemStack) || isAxe(itemStack);
   }

   public static boolean isTool(class_1799 itemStack) {
      return isWeapon(itemStack)
         || itemStack.method_31573(class_3489.field_42614)
         || itemStack.method_31573(class_3489.field_42615)
         || itemStack.method_31573(class_3489.field_42613);
   }

   public static boolean isMace(class_1799 itemStack) {
      return itemStack.method_7909() instanceof class_9362;
   }

   public static boolean isMaceItem(class_1792 item) {
      return item instanceof class_9362;
   }

   public static boolean isCrit(class_1657 player, class_1297 target) {
      return player.method_7261(0.5F) > 0.9F
         && player.field_6017 > 0.0
         && !player.method_24828()
         && !player.method_6101()
         && !player.method_5869()
         && !player.method_6059(class_1294.field_5919)
         && target instanceof class_1309;
   }

   public static void hitEntity(class_1297 entity, boolean swingHand) {
      Prestige.mc.field_1761.method_2918(Prestige.mc.field_1724, entity);
      if (swingHand) {
         Prestige.mc.field_1724.method_6104(class_1268.field_5808);
      }
   }
}