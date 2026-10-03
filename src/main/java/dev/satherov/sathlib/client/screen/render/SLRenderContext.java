package dev.satherov.sathlib.client.screen.render;

import dev.satherov.sathlib.client.screen.layout.SLBounds;
import dev.satherov.sathlib.client.screen.style.UITheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class SLRenderContext {
   private final GuiGraphicsExtractor graphics;
   private final Font font;
   private final UITheme skin;
   private final float partialTick;
   private final int mouseX;
   private final int mouseY;
   private final SLClipStack clipStack;

   public SLRenderContext(GuiGraphicsExtractor graphics, Font font, UITheme skin, float partialTick, int mouseX, int mouseY) {
      this.graphics = graphics;
      this.font = font;
      this.skin = skin;
      this.partialTick = partialTick;
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      this.clipStack = new SLClipStack(graphics);
   }

   public GuiGraphicsExtractor graphics() {
      return this.graphics;
   }

   public Font font() {
      return this.font;
   }

   public UITheme skin() {
      return this.skin;
   }

   public float partialTick() {
      return this.partialTick;
   }

   public int mouseX() {
      return this.mouseX;
   }

   public int mouseY() {
      return this.mouseY;
   }

   public void fill(SLBounds bounds, int color) {
      this.graphics.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), color);
   }

   public void outline(SLBounds bounds, int color) {
      this.graphics.outline(bounds.x(), bounds.y(), bounds.width(), bounds.height(), color);
   }

   public void text(Component text, int x, int y, int color, boolean shadow) {
      this.graphics.text(this.font, text, x, y, color, shadow);
   }

   public void centeredText(Component text, SLBounds bounds, int color, boolean shadow) {
      int textWidth = this.font.width(text);
      int textX = bounds.x() + (bounds.width() - textWidth) / 2;
      int textY = bounds.y() + (bounds.height() - 9) / 2;
      this.text(text, textX, textY, color, shadow);
   }

   public void centeredVisualText(Component text, SLBounds bounds, int color, boolean shadow) {
      int textWidth = this.font.width(text);
      int textX = bounds.x() + (bounds.width() - textWidth) / 2;
      int textY = this.centeredVisualTextY(bounds);
      this.text(text, textX, textY, color, shadow);
   }

   private int centeredVisualTextY(SLBounds bounds) {
      int lineHeight = 9 + 3;
      if (bounds.height() < lineHeight) {
         return bounds.y() + Math.max(0, (bounds.height() - lineHeight) / 2);
      } else {
         int slack = bounds.height() - lineHeight;
         return bounds.y() + Math.floorDiv(slack + 1, 2) + 2;
      }
   }

   public void blitSprite(Identifier sprite, int x, int y, int width, int height) {
      this.graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
   }

   public void item(ItemStack stack, int x, int y, int seed) {
      this.graphics.item(stack, x, y, seed);
   }

   public void fakeItem(ItemStack stack, int x, int y, int seed) {
      this.graphics.fakeItem(stack, x, y, seed);
   }

   public void itemDecorations(ItemStack stack, int x, int y, @Nullable String itemCount) {
      this.graphics.itemDecorations(this.font, stack, x, y, itemCount);
   }

   public void nextStratum() {
      this.graphics.nextStratum();
   }

   public void pushClip(SLBounds bounds) {
      this.clipStack.push(bounds);
   }

   public void popClip() {
      this.clipStack.pop();
   }
}
