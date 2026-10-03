package dev.satherov.sathlib.client.screen.layout;

public record SLLength(SLLength.Mode mode, float value) {
   public static SLLength content() {
      return new SLLength(SLLength.Mode.CONTENT, 0.0F);
   }

   public static SLLength pixels(int pixels) {
      return new SLLength(SLLength.Mode.FIXED, pixels);
   }

   public static SLLength percent(float percent) {
      return new SLLength(SLLength.Mode.PERCENT, percent);
   }

   public static SLLength fill() {
      return fill(1.0F);
   }

   public static SLLength fill(float weight) {
      return new SLLength(SLLength.Mode.FILL, Math.max(0.0F, weight));
   }

   public boolean isFill() {
      return this.mode == SLLength.Mode.FILL;
   }

   public float weight() {
      return this.mode == SLLength.Mode.FILL ? Math.max(0.0F, this.value) : 0.0F;
   }

   public int resolvePreferred(int available, int preferred) {
      return switch (this.mode) {
         case CONTENT, FILL -> preferred;
         case FIXED -> Math.round(this.value);
         case PERCENT -> Math.round(available * this.value);
      };
   }

   public int resolveFinal(int available, int preferred) {
      return switch (this.mode) {
         case CONTENT -> preferred;
         case FIXED -> Math.round(this.value);
         case PERCENT -> Math.round(available * this.value);
         case FILL -> Math.max(0, available);
      };
   }

   public static enum Mode {
      CONTENT,
      FIXED,
      PERCENT,
      FILL;
   }
}
