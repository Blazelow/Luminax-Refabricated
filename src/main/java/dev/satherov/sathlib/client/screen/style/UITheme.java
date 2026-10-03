package dev.satherov.sathlib.client.screen.style;

import dev.satherov.sathlib.client.screen.layout.SLBounds;
import dev.satherov.sathlib.client.screen.render.SLRenderContext;
import dev.satherov.sathlib.common.menu.slot.SLSlotVisuals;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public interface UITheme {
   void renderPanel(SLRenderContext var1, SLBounds var2);

   void renderButton(SLRenderContext var1, SLBounds var2, Component var3, boolean var4, boolean var5, boolean var6);

   void renderProgressBar(SLRenderContext var1, SLBounds var2, float var3, @Nullable Component var4, boolean var5);

   void renderSlotFrame(SLRenderContext var1, SLBounds var2, SLSlotVisuals var3, boolean var4, boolean var5);

   int labelColor(boolean var1);

   int accentColor();
}
