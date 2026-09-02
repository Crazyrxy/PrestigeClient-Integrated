package juxxl.prestige.module.setting;

import java.util.Arrays;
import java.util.List;

public final class ModeSetting<T extends Enum<T>> extends Setting<ModeSetting<T>> {
   public int index;
   private final List<T> possibleValues;
   private final int originalValue;

   public ModeSetting(CharSequence name, T defaultValue, Class<T> type) {
      super(name);
      T[] values = type != null ? type.getEnumConstants() : null;
      if (values == null && defaultValue != null) {
         values = defaultValue.getDeclaringClass().getEnumConstants();
      }

      if (values == null) {
         values = (T[])(defaultValue != null ? new Enum[]{defaultValue} : new Enum[0]);
      }

      this.possibleValues = Arrays.asList(values);
      int idx = this.possibleValues.indexOf(defaultValue);
      this.index = idx >= 0 ? idx : 0;
      this.originalValue = this.index;
   }

   public T getMode() {
      if (this.possibleValues.isEmpty()) {
         return null;
      }
      if (this.index < 0 || this.index >= this.possibleValues.size()) {
         this.index = 0;
      }
      return this.possibleValues.get(this.index);
   }

   public void setMode(T mode) {
      int idx = this.possibleValues.indexOf(mode);
      this.index = idx >= 0 ? idx : 0;
   }

   public void setModeIndex(int mode) {
      if (this.possibleValues.isEmpty()) {
         this.index = 0;
         return;
      }
      if (mode < 0) {
         this.index = 0;
      } else if (mode >= this.possibleValues.size()) {
         this.index = this.possibleValues.size() - 1;
      } else {
         this.index = mode;
      }
   }

   public int getModeIndex() {
      return this.index;
   }

   public int getOriginalValue() {
      return this.originalValue;
   }

   public void cycle() {
      if (this.index < this.possibleValues.size() - 1) {
         this.index++;
      } else {
         this.index = 0;
      }
   }

   public boolean isMode(T mode) {
      return this.index == this.possibleValues.indexOf(mode);
   }
}
