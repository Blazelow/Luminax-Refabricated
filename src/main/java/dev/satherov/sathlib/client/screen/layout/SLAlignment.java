package dev.satherov.sathlib.client.screen.layout;

public enum SLAlignment {
   START,
   CENTER,
   END,
   FILL;

   public int resolveSize(int available, int preferred) {
      return this == FILL ? Math.max(0, available) : Math.min(Math.max(0, preferred), Math.max(0, available));
   }

   public int resolvePosition(int start, int available, int size) {
      return switch (this) {
         case START, FILL -> start;
         case CENTER -> start + (available - size) / 2;
         case END -> start + (available - size);
      };
   }
}
