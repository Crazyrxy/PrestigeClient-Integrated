package juxxl.prestige.module.setting;

import java.util.function.Supplier;

public abstract class Setting<T extends Setting<T>> {
   private CharSequence name;
   public CharSequence description;
   private Supplier<Boolean> visibility = () -> true;

   public Setting(CharSequence name) {
      this.name = name;
   }

   public void setName(CharSequence name) {
      this.name = name;
   }

   public CharSequence getName() {
      return this.name;
   }

   public boolean isVisible() {
      return this.visibility == null || this.visibility.get();
   }

   public T setVisible(Supplier<Boolean> visibility) {
      this.visibility = visibility;
      return (T)this;
   }

   public CharSequence getDescription() {
      return this.description;
   }

   public T setDescription(CharSequence desc) {
      this.description = desc;
      return (T)this;
   }
}
