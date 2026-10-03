package dev.satherov.sathlib.client.screen.layout;

public record SLScalar(SLScalar.Mode mode, float value) {
   public static SLScalar pixels(int pixels) {
      return new SLScalar(SLScalar.Mode.FIXED, pixels);
   }

   public static SLScalar percent(float percent) {
      return new SLScalar(SLScalar.Mode.PERCENT, percent);
   }

   public static SLScalar zero() {
      return pixels(0);
   }

   public int resolve(int reference) {
      return switch (this.mode) {
         case FIXED -> Math.round(this.value);
         case PERCENT -> Math.round(reference * this.value);
      };
   }

   public static enum Mode {
      FIXED,
      PERCENT;
   }
}
