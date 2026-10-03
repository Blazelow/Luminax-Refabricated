package dev.satherov.sathlib.common.menu.slot;

public record SLSlotVisuals(boolean drawFrame, int fillColor, int borderColor) {
   public static final SLSlotVisuals DEFAULT = new SLSlotVisuals(true, -15723236, -12036757);
   public static final SLSlotVisuals PLAYER = new SLSlotVisuals(true, -15854822, -12300186);
   public static final SLSlotVisuals HOTBAR = new SLSlotVisuals(true, -15854822, -10851964);
   public static final SLSlotVisuals MACHINE = new SLSlotVisuals(true, -15656668, -9598032);
   public static final SLSlotVisuals FRAMELESS = new SLSlotVisuals(false, 0, 0);
}
