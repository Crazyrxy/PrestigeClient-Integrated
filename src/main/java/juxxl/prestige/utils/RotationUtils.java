package juxxl.prestige.utils;

import juxxl.prestige.Prestige;
import juxxl.prestige.utils.rotation.Rotation;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public final class RotationUtils {
   public static class_243 getEyesPos(class_1657 player) {
      return player.method_5836(RenderUtils.tickProgress());
   }

   public static class_2338 getCameraBlockPos() {
      return Prestige.mc.field_1773.method_19418().method_19328();
   }

   public static class_2338 getEyesBlockPos() {
      return new class_2338(
         (int)getEyesPos(Prestige.mc.field_1724).field_1352,
         (int)getEyesPos(Prestige.mc.field_1724).field_1351,
         (int)getEyesPos(Prestige.mc.field_1724).field_1350
      );
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

   public static Rotation getDiff(Rotation rotation1, Rotation rotation2) {
      double yaw = Math.abs(Math.max(rotation1.yaw(), rotation2.yaw()) - Math.min(rotation1.yaw(), rotation2.yaw()));
      double pitch = Math.abs(Math.max(rotation1.pitch(), rotation2.pitch()) - Math.min(rotation1.pitch(), rotation2.pitch()));
      return new Rotation(yaw, pitch);
   }

   public static Rotation getSmoothRotation(Rotation from, Rotation to, double speed) {
      return new Rotation(
         class_3532.method_17821((float)speed, (float)from.yaw(), (float)to.yaw()),
         class_3532.method_17821((float)speed, (float)from.pitch(), (float)to.pitch())
      );
   }

   public static double getTotalDiff(Rotation rotation1, Rotation rotation2) {
      Rotation diff = getDiff(rotation1, rotation2);
      return diff.yaw() + diff.pitch();
   }

   public static class_243 getClientLookVec() {
      return getPlayerLookVec(Prestige.mc.field_1724);
   }

   public static Rotation getDirection(class_1297 entity, class_243 vec) {
      double dx = vec.field_1352 - entity.method_23317();
      double dy = vec.field_1351 - entity.method_23318();
      double dz = vec.field_1350 - entity.method_23321();
      double dist = class_3532.method_15355((float)(dx * dx + dz * dz));
      return new Rotation(class_3532.method_15338(Math.toDegrees(Math.atan2(dz, dx)) - 90.0), -class_3532.method_15338(Math.toDegrees(Math.atan2(dy, dist))));
   }

   public static double getAngleToRotation(Rotation rotation) {
      double currentYaw = class_3532.method_15393(Prestige.mc.field_1724.method_36454());
      double currentPitch = class_3532.method_15393(Prestige.mc.field_1724.method_36455());
      double diffYaw = class_3532.method_15338(currentYaw - rotation.yaw());
      double diffPitch = class_3532.method_15338(currentPitch - rotation.pitch());
      return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
   }
}
