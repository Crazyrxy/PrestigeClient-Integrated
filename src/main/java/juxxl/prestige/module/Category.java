package juxxl.prestige.module;

public enum Category {
   COMBAT("Combat"),
   MISC("Misc"),
   RENDER("Render"),
   CLIENT("Client"),
   MACE("Mace"),
   CART("Cart");

   public final CharSequence name;

   Category(CharSequence name) {
      this.name = name;
   }
}
