package dev.satherov.sathlib.util;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;

public final class SLMathUtils {
   public static double cos(double degree) {
      return StrictMath.cos(degree * (float) (Math.PI / 180.0));
   }

   public static float cos(float degree) {
      return (float)StrictMath.cos(degree * (float) (Math.PI / 180.0));
   }

   public static double sin(double degree) {
      return StrictMath.sin(degree * (float) (Math.PI / 180.0));
   }

   public static float sin(float degree) {
      return (float)StrictMath.sin(degree * (float) (Math.PI / 180.0));
   }

   public static Vector2i getPointOnCircle(Vector2i center, float degrees, float radius) {
      return new Vector2i((int)(center.x() + cos(degrees) * radius), (int)(center.y() + sin(degrees) * radius));
   }

   public static Vector2f getPointOnCircle(Vector2f center, float degrees, float radius) {
      return new Vector2f(center.x() + cos(degrees) * radius, center.y() + sin(degrees) * radius);
   }

   public static Vector2d getPointOnCircle(Vector2d center, float degrees, float radius) {
      return new Vector2d(center.x() + cos(degrees) * radius, center.y() + sin(degrees) * radius);
   }

   public static int[] split(int value, int parts) {
      if (parts <= 0) {
         return new int[0];
      } else {
         int[] result = new int[parts];
         int base = value / parts;
         int remainder = value % parts;

         for (int index = 0; index < parts; index++) {
            result[index] = base + (index < remainder ? 1 : 0);
         }

         return result;
      }
   }

   public static String sanitizeHex(@Nullable String text) {
      if (text != null && !text.isBlank()) {
         String source = text.trim();
         StringBuilder digits = new StringBuilder(6);

         for (int index = 0; index < source.length(); index++) {
            char character = source.charAt(index);
            if (character != '#') {
               if (Character.digit(character, 16) >= 0) {
                  digits.append(Character.toUpperCase(character));
               }

               if (digits.length() == 6) {
                  break;
               }
            }
         }

         return digits.toString();
      } else {
         return "";
      }
   }

   @Nullable
   public static Integer tryPraseToHex(String text) {
      String hex = sanitizeHex(text);
      if (hex.length() != 6) {
         return null;
      } else {
         try {
            return Integer.parseInt(hex, 16);
         } catch (NumberFormatException var3) {
            return null;
         }
      }
   }

   public static String rgbToHex(int rgb) {
      return String.format("#%06X", rgb & 16777215);
   }

   public static String argbToHex(int argb) {
      return String.format("#%08X", argb);
   }

   private SLMathUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
