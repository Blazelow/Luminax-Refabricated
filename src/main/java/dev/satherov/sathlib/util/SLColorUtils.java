package dev.satherov.sathlib.util;

import java.util.function.UnaryOperator;
import net.minecraft.util.Mth;

public final class SLColorUtils {
   public static int alpha(int color) {
      return color >>> 24;
   }

   public static int red(int color) {
      return color >> 16 & 0xFF;
   }

   public static int green(int color) {
      return color >> 8 & 0xFF;
   }

   public static int blue(int color) {
      return color & 0xFF;
   }

   public static int argb(int alpha, int red, int green, int blue) {
      return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
   }

   public static int argb(int red, int green, int blue) {
      return 0xFF000000 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
   }

   public static int rgb(int red, int green, int blue) {
      return red << 16 | (green & 0xFF) << 8 | blue & 0xFF;
   }

   public static int lerp(float alpha, int from, int to) {
      int a = Mth.lerpInt(alpha, alpha(from), alpha(to));
      int r = Mth.lerpInt(alpha, red(from), red(to));
      int g = Mth.lerpInt(alpha, green(from), green(to));
      int b = Mth.lerpInt(alpha, blue(from), blue(to));
      return argb(a, r, g, b);
   }

   public static int hsvToRgb(float hue, float saturation, float value) {
      return hsvToArgb(hue, saturation, value, 255);
   }

   public static int hsvToArgb(float hue, float saturation, float value, int alpha) {
      float wrappedHue = wrapHue(hue);
      float clampedSat = Mth.clamp(saturation, 0.0F, 1.0F);
      float clampedVal = Mth.clamp(value, 0.0F, 1.0F);
      int clampedAlpha = Mth.clamp(alpha, 0, 255);
      if (clampedSat <= 0.0F) {
         int gray = Mth.clamp((int)(clampedVal * 255.0F), 0, 255);
         return argb(clampedAlpha, gray, gray, gray);
      } else {
         float scaledHue = wrappedHue * 6.0F;
         int sector = Mth.clamp((int)scaledHue, 0, 5);
         float fraction = scaledHue - sector;
         float base = clampedVal * (1.0F - clampedSat);
         float down = clampedVal * (1.0F - clampedSat * fraction);
         float up = clampedVal * (1.0F - clampedSat * (1.0F - fraction));
         float red;
         float green;
         float blue;
         switch (sector) {
            case 0:
               red = clampedVal;
               green = up;
               blue = base;
               break;
            case 1:
               red = down;
               green = clampedVal;
               blue = base;
               break;
            case 2:
               red = base;
               green = clampedVal;
               blue = up;
               break;
            case 3:
               red = base;
               green = down;
               blue = clampedVal;
               break;
            case 4:
               red = up;
               green = base;
               blue = clampedVal;
               break;
            case 5:
               red = clampedVal;
               green = base;
               blue = down;
               break;
            default:
               throw new IllegalStateException("Unreachable sector: " + sector);
         }

         int redInt = Mth.clamp((int)(red * 255.0F), 0, 255);
         int greenInt = Mth.clamp((int)(green * 255.0F), 0, 255);
         int blueInt = Mth.clamp((int)(blue * 255.0F), 0, 255);
         return argb(clampedAlpha, redInt, greenInt, blueInt);
      }
   }

   public static float wrapHue(float hue) {
      float result = hue % 1.0F;
      if (result < 0.0F) {
         result++;
      }

      if (result >= 1.0F) {
         result = 0.0F;
      }

      return result;
   }

   public static float[] rgbToHsv(int rgb) {
      float red = red(rgb) / 255.0F;
      float green = green(rgb) / 255.0F;
      float blue = blue(rgb) / 255.0F;
      float value = Math.max(red, Math.max(green, blue));
      float min = Math.min(red, Math.min(green, blue));
      float delta = value - min;
      float hue;
      if (delta == 0.0F) {
         hue = 0.0F;
      } else if (value == red) {
         hue = (green - blue) / delta % 6.0F;
      } else if (value == green) {
         hue = (blue - red) / delta + 2.0F;
      } else {
         hue = (red - green) / delta + 4.0F;
      }

      hue /= 6.0F;
      if (hue < 0.0F) {
         hue++;
      }

      float saturation = value == 0.0F ? 0.0F : delta / value;
      return new float[]{wrapHue(hue), Mth.clamp(saturation, 0.0F, 1.0F), Mth.clamp(value, 0.0F, 1.0F)};
   }

   private SLColorUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static enum Channel {
      ALPHA(SLColorUtils::alpha),
      RED(SLColorUtils::red),
      GREEN(SLColorUtils::green),
      BLUE(SLColorUtils::blue);

      private final UnaryOperator<Integer> constructor;

      public int of(int packed) {
         return this.constructor.apply(packed);
      }

      private Channel(final UnaryOperator<Integer> constructor) {
         this.constructor = constructor;
      }
   }
}
