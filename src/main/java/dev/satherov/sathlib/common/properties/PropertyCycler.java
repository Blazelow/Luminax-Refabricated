package dev.satherov.sathlib.common.properties;

@FunctionalInterface
public interface PropertyCycler<T> {
   PropertyCycler<Boolean> BOOLEAN = (var0, val) -> !val;

   static <E extends Enum<E> & PropertyEnum> PropertyCycler<E> enumCycler(Class<E> type) {
      return (forward, value) -> {
         E[] values = type.getEnumConstants();
         int next = value.ordinal() + (forward ? 1 : -1);
         if (next < 0) {
            next += values.length;
         }

         return values[next % values.length];
      };
   }

   static PropertyCycler<Short> numberCycler(short min, short max) {
      return (forward, val) -> {
         short next = (short)(val + (forward ? 1 : -1));
         if (next > max) {
            return min;
         } else {
            return next < min ? max : next;
         }
      };
   }

   static PropertyCycler<Integer> numberCycler(int min, int max) {
      return (forward, val) -> {
         int next = val + (forward ? 1 : -1);
         if (next > max) {
            return min;
         } else {
            return next < min ? max : next;
         }
      };
   }

   static PropertyCycler<Long> numberCycler(long min, long max) {
      return (forward, val) -> {
         long next = val + (forward ? 1 : -1);
         if (next > max) {
            return min;
         } else {
            return next < min ? max : next;
         }
      };
   }

   static PropertyCycler<Float> numberCycler(float min, float max) {
      return (forward, val) -> {
         float next = val + (forward ? 1 : -1);
         if (next > max) {
            return min;
         } else {
            return next < min ? max : next;
         }
      };
   }

   static PropertyCycler<Double> numberCycler(double min, double max) {
      return (forward, val) -> {
         double next = val + (forward ? 1 : -1);
         if (next > max) {
            return min;
         } else {
            return next < min ? max : next;
         }
      };
   }

   T cycle(boolean var1, T var2);
}
