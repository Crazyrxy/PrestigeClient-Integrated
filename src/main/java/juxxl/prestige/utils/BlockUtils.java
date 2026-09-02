package juxxl.prestige.utils;

import java.util.List;
import java.util.stream.Stream;
import juxxl.prestige.Prestige;
import juxxl.prestige.utils.rotation.Rotation;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_4969;

public final class BlockUtils {
   public static boolean isBlock(class_2338 pos, class_2248 block) {
      return Prestige.mc.field_1687.method_8320(pos).method_26204() == block;
   }

   public static void rotateToBlock(class_2338 pos) {
      assert Prestige.mc.field_1724 != null;
      Rotation rotation = RotationUtils.getDirection(Prestige.mc.field_1724, pos.method_46558());
      Prestige.mc.field_1724.method_36457((float)rotation.pitch());
      Prestige.mc.field_1724.method_36456((float)rotation.yaw());
   }

   public static boolean isAnchorCharged(class_2338 pos) {
      return isBlock(pos, class_2246.field_23152) ? (Integer)Prestige.mc.field_1687.method_8320(pos).method_11654(class_4969.field_23153) != 0 : false;
   }

   public static boolean isAnchorNotCharged(class_2338 pos) {
      return isBlock(pos, class_2246.field_23152) ? (Integer)Prestige.mc.field_1687.method_8320(pos).method_11654(class_4969.field_23153) == 0 : false;
   }

   public static boolean canPlaceBlockClient(class_2338 block) {
      class_2338 up = block.method_10084();
      if (!Prestige.mc.field_1687.method_22347(up)) {
         return false;
      }

      double x = up.method_10263();
      double y = up.method_10264();
      double z = up.method_10260();
      List<class_1297> list = Prestige.mc.field_1687.method_8335(null, new class_238(x, y, z, x + 1.0, y + 1.0, z + 1.0));
      list.removeIf(entity -> entity instanceof class_1542);
      return list.isEmpty();
   }

   public static Stream<class_2338> getAllInBoxStream(class_2338 from, class_2338 to) {
      class_2338 min = new class_2338(
         Math.min(from.method_10263(), to.method_10263()), Math.min(from.method_10264(), to.method_10264()), Math.min(from.method_10260(), to.method_10260())
      );
      class_2338 max = new class_2338(
         Math.max(from.method_10263(), to.method_10263()), Math.max(from.method_10264(), to.method_10264()), Math.max(from.method_10260(), to.method_10260())
      );
      Stream<class_2338> stream = Stream.iterate(min, pos -> {
         int x = pos.method_10263();
         int y = pos.method_10264();
         int z = pos.method_10260();
         if (++x > max.method_10263()) {
            x = min.method_10263();
            y++;
         }

         if (y > max.method_10264()) {
            y = min.method_10264();
            z++;
         }

         if (z > max.method_10260()) {
            throw new IllegalStateException("Stream limit didn't work.");
         } else {
            return new class_2338(x, y, z);
         }
      });
      int limit = (max.method_10263() - min.method_10263() + 1) * (max.method_10264() - min.method_10264() + 1) * (max.method_10260() - min.method_10260() + 1);
      return stream.limit(limit);
   }
}
