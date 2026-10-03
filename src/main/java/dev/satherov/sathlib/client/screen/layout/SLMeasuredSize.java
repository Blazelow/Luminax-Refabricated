package dev.satherov.sathlib.client.screen.layout;

public record SLMeasuredSize(int width, int height) {
   public static final SLMeasuredSize ZERO = new SLMeasuredSize(0, 0);
}
