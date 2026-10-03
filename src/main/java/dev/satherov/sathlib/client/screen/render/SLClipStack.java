package dev.satherov.sathlib.client.screen.render;

import dev.satherov.sathlib.client.screen.layout.SLBounds;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class SLClipStack {
   private final GuiGraphicsExtractor graphics;
   private final Deque<SLBounds> clips = new ArrayDeque<>();

   public SLClipStack(GuiGraphicsExtractor graphics) {
      this.graphics = graphics;
   }

   public void push(SLBounds bounds) {
      this.graphics.enableScissor(bounds.x(), bounds.y(), bounds.right(), bounds.bottom());
      this.clips.push(bounds);
   }

   public void pop() {
      if (!this.clips.isEmpty()) {
         this.graphics.disableScissor();
         this.clips.pop();
      }
   }
}
