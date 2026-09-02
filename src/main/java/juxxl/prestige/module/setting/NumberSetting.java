package juxxl.prestige.module.setting;

public final class NumberSetting extends Setting<NumberSetting> {
   private double min;
   private double max;
   private double value;
   private double increment;
   private final double originalValue;

   public NumberSetting(CharSequence name, double min, double max, double value, double increment) {
      super(name);
      this.min = min;
      this.max = max;
      this.value = value;
      this.increment = increment;
      this.originalValue = value;
   }

   public double getValue() {
      return this.value;
   }

   public double getOriginalValue() {
      return this.originalValue;
   }

   public double getIncrement() {
      return this.increment;
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public int getValueInt() {
      return (int)this.value;
   }

   public float getValueFloat() {
      return (float)this.value;
   }

   public long getValueLong() {
      return (long)this.value;
   }

   public void setValue(double value) {
      double precision = 1.0 / this.increment;
      this.value = Math.round(Math.max(this.min, Math.min(this.max, value)) * precision) / precision;
   }
}
