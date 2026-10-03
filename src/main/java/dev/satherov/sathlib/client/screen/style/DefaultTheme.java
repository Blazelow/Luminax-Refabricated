package dev.satherov.sathlib.client.screen.style;

import dev.satherov.sathlib.client.screen.layout.SLBounds;
import dev.satherov.sathlib.client.screen.render.SLRenderContext;
import dev.satherov.sathlib.common.menu.slot.SLSlotVisuals;
import dev.satherov.sathlib.util.SLColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public enum DefaultTheme implements UITheme {
   INSTANCE;

   private static final int PANEL_FILL = -300080079;
   private static final int PANEL_BORDER = -11181444;
   private static final int BUTTON_FILL = -13549479;
   private static final int BUTTON_HOVER = -12758156;
   private static final int BUTTON_PRESS = -14141876;
   private static final int BUTTON_DISABLED = -14669773;
   private static final int BUTTON_BORDER = -7297852;
   private static final int TRACK_FILL = -15064528;
   private static final int TRACK_BORDER = -10389610;
   private static final int ACCENT = -8600321;
   private static final int TEXT = -722949;
   private static final int TEXT_DISABLED = -7628885;

   @Override
   public void renderPanel(SLRenderContext context, SLBounds bounds) {
      context.fill(bounds, -300080079);
      context.outline(bounds, -11181444);
   }

   @Override
   public void renderButton(SLRenderContext context, SLBounds bounds, Component text, boolean hovered, boolean pressed, boolean enabled) {
      int fillColor = -13549479;
      if (!enabled) {
         fillColor = -14669773;
      } else if (pressed) {
         fillColor = -14141876;
      } else if (hovered) {
         fillColor = -12758156;
      }

      context.fill(bounds, fillColor);
      context.outline(bounds, -7297852);
      context.centeredVisualText(text, bounds, this.labelColor(enabled), false);
   }

   @Override
   public void renderProgressBar(SLRenderContext context, SLBounds bounds, float progress, @Nullable Component overlay, boolean enabled) {
      float clampedProgress = Mth.clamp(progress, 0.0F, 1.0F);
      context.fill(bounds, -15064528);
      context.outline(bounds, -10389610);
      int innerWidth = Math.max(0, bounds.width() - 2);
      int innerHeight = Math.max(0, bounds.height() - 2);
      int fillWidth = Math.round(innerWidth * clampedProgress);
      if (fillWidth > 0 && innerHeight > 0) {
         int fillColor = enabled ? -8600321 : SLColorUtils.lerp(0.5F, -8600321, -10389610);
         context.fill(new SLBounds(bounds.x() + 1, bounds.y() + 1, fillWidth, innerHeight), fillColor);
      }

      if (overlay != null) {
         context.centeredText(overlay, bounds, this.labelColor(enabled), false);
      }
   }

   @Override
   public void renderSlotFrame(SLRenderContext context, SLBounds bounds, SLSlotVisuals visuals, boolean hovered, boolean active) {
      if (visuals.drawFrame()) {
         float inactiveBlend = active ? 0.0F : 0.45F;
         int fillColor = inactiveBlend > 0.0F ? SLColorUtils.lerp(inactiveBlend, visuals.fillColor(), -15064528) : visuals.fillColor();
         int borderColor = inactiveBlend > 0.0F ? SLColorUtils.lerp(inactiveBlend, visuals.borderColor(), -10389610) : visuals.borderColor();
         if (hovered) {
            fillColor = SLColorUtils.lerp(0.18F, fillColor, -8600321);
            borderColor = SLColorUtils.lerp(0.22F, borderColor, -8600321);
         }

         context.fill(bounds, fillColor);
         context.outline(bounds, borderColor);
      }
   }

   @Override
   public int labelColor(boolean enabled) {
      return enabled ? -722949 : -7628885;
   }

   @Override
   public int accentColor() {
      return -8600321;
   }
}
