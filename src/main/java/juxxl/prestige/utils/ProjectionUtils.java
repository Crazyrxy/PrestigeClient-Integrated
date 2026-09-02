package juxxl.prestige.utils;

import juxxl.prestige.Prestige;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class ProjectionUtils {
   private ProjectionUtils() {
   }

   @Nullable
   public static ProjectionUtils.ProjectedPoint project(class_243 worldPos) {
      if (Prestige.mc.field_1687 != null && Prestige.mc.field_1773 != null) {
         class_4184 camera = Prestige.mc.field_1773.method_19418();
         if (camera == null) {
            return null;
         }

         int width = Prestige.mc.method_22683().method_4486();
         int height = Prestige.mc.method_22683().method_4502();
         if (width > 0 && height > 0) {
            class_243 cameraPos = camera.method_71156();
            Vector4f clip = new Vector4f(
               (float)(worldPos.field_1352 - cameraPos.field_1352),
               (float)(worldPos.field_1351 - cameraPos.field_1351),
               (float)(worldPos.field_1350 - cameraPos.field_1350),
               1.0F
            );
            Matrix4f view = new Matrix4f().rotateX((float)Math.toRadians(camera.method_19329())).rotateY((float)Math.toRadians(camera.method_19330() + 180.0F));
            Matrix4f projection = new Matrix4f()
               .setPerspective(
                  (float)Math.toRadians(((Integer)Prestige.mc.field_1690.method_41808().method_41753()).intValue()),
                  (float)width / height,
                  0.05F,
                  ((Integer)Prestige.mc.field_1690.method_42503().method_41753()).intValue() * 16.0F
               );
            view.transform(clip);
            projection.transform(clip);
            if (clip.w <= 0.0F) {
               return null;
            } else {
               float ndcX = clip.x / clip.w;
               float ndcY = clip.y / clip.w;
               float ndcZ = clip.z / clip.w;
               if (!(ndcZ < -1.0F) && !(ndcZ > 1.0F)) {
                  float screenX = (ndcX * 0.5F + 0.5F) * width;
                  float screenY = (1.0F - (ndcY * 0.5F + 0.5F)) * height;
                  return new ProjectionUtils.ProjectedPoint(screenX, screenY, ndcZ);
               } else {
                  return null;
               }
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public record ProjectedPoint(float x, float y, float depth) {
   }
}
