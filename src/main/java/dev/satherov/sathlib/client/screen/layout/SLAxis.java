package dev.satherov.sathlib.client.screen.layout;

public enum SLAxis {
   HORIZONTAL,
   VERTICAL;

   public SLAxis opposite() {
      return this == HORIZONTAL ? VERTICAL : HORIZONTAL;
   }
}
