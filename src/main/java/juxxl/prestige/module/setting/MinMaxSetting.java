package juxxl.prestige.module.setting;

import java.util.Random;

public class MinMaxSetting extends Setting<MinMaxSetting> {
   private final double min;
   private final double max;
   private final double increment;
   private final double originalMinValue;
   private final double originalMaxValue;
   private double minValue;
   private double maxValue;

   public MinMaxSetting(CharSequence name, double min, double max, double increment, double defaultMin, double defaultMax) {
      super(name);
      this.min = min;
      this.max = max;
      this.increment = increment;
      this.minValue = defaultMin;
      this.maxValue = defaultMax;
      this.originalMinValue = defaultMin;
      this.originalMaxValue = defaultMax;
   }

   public int getMinInt() {
      return (int)this.minValue;
   }

   public float getMinFloat() {
      return (float)this.minValue;
   }

   public long getMinLong() {
      return (long)this.minValue;
   }

   public int getMaxInt() {
      return (int)this.maxValue;
   }

   public float getMaxFloat() {
      return (float)this.maxValue;
   }

   public long getMaxLong() {
      return (long)this.maxValue;
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public double getOriginalMinValue() {
      return this.originalMinValue;
   }

   public double getOriginalMaxValue() {
      return this.originalMaxValue;
   }

   public double getIncrement() {
      return this.increment;
   }

   public double getRandomValue() {
      return this.getMaxValue() > this.getMinValue() ? new Random().nextDouble(this.getMinValue(), this.getMaxValue()) : this.getMinValue();
   }

   public int getRandomValueInt() {
      return this.getMaxValue() > this.getMinValue() ? new Random().nextInt(this.getMinInt(), this.getMaxInt()) : this.getMinInt();
   }

   public float getRandomValueFloat() {
      return this.getMaxValue() > this.getMinValue() ? new Random().nextFloat(this.getMinFloat(), this.getMaxFloat()) : this.getMinFloat();
   }

   public long getRandomValueLong() {
      return this.getMaxValue() > this.getMinValue() ? new Random().nextLong(this.getMinLong(), this.getMaxLong()) : this.getMinLong();
   }

   public void setMinValue(double value) {
      double precision = 1.0 / this.increment;
      this.minValue = Math.round(Math.max(this.min, Math.min(this.max, value)) * precision) / precision;
   }

   public void setMaxValue(double value) {
      double precision = 1.0 / this.increment;
      this.maxValue = Math.round(Math.max(this.min, Math.min(this.max, value)) * precision) / precision;
   }
}
