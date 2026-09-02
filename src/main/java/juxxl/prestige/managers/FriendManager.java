package juxxl.prestige.managers;

import java.util.HashSet;
import java.util.Set;
import juxxl.prestige.Prestige;
import net.minecraft.class_1657;
import net.minecraft.class_3966;

public final class FriendManager {
   private final Set<String> friends = new HashSet<>();

   public void addFriend(class_1657 player) {
      this.friends.add(player.method_5477().getString());
   }

   public void removeFriend(class_1657 player) {
      this.friends.remove(player.method_5477().getString());
   }

   public boolean isFriend(class_1657 player) {
      return this.friends.contains(player.method_5477().getString());
   }

   public boolean isAimingOverFriend() {
      return Prestige.mc.field_1765 instanceof class_3966 hitResult && hitResult.method_17782() instanceof class_1657 player ? this.isFriend(player) : false;
   }
}
