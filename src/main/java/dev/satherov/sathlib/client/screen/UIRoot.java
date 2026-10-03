package dev.satherov.sathlib.client.screen;

import dev.satherov.sathlib.client.screen.layout.SLBounds;
import dev.satherov.sathlib.client.screen.node.UINode;
import dev.satherov.sathlib.client.screen.render.SLRenderContext;
import dev.satherov.sathlib.client.screen.style.DefaultTheme;
import dev.satherov.sathlib.client.screen.style.UITheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.Nullable;

public final class UIRoot {
   @Nullable
   private UINode<?> content;
   private SLBounds viewport = SLBounds.EMPTY;
   private UITheme skin = DefaultTheme.INSTANCE;
   private boolean layoutDirty = true;
   @Nullable
   private UINode<?> hoveredNode;
   @Nullable
   private UINode<?> pressedNode;
   @Nullable
   private UINode<?> focusedNode;

   @Nullable
   public UINode<?> getContent() {
      return this.content;
   }

   public void setContent(@Nullable UINode<?> content) {
      this.hoveredNode = null;
      this.pressedNode = null;
      this.focusedNode = null;
      if (this.content != null) {
         this.content.detach();
      }

      this.content = content;
      if (this.content != null) {
         this.content.attach(this, null);
      }

      this.invalidateLayout();
   }

   public SLBounds getViewport() {
      return this.viewport;
   }

   public void setViewport(SLBounds viewport) {
      if (!this.viewport.equals(viewport)) {
         this.viewport = viewport;
         this.invalidateLayout();
      }
   }

   public UITheme getSkin() {
      return this.skin;
   }

   public void setSkin(UITheme skin) {
      this.skin = skin;
   }

   public void invalidateLayout() {
      this.layoutDirty = true;
   }

   public void requestFocus(@Nullable UINode<?> node) {
      if (this.focusedNode != node) {
         if (this.focusedNode != null) {
            this.focusedNode.setFocusedState(false);
         }

         this.focusedNode = node != null && node.isFocusable() ? node : null;
         if (this.focusedNode != null) {
            this.focusedNode.setFocusedState(true);
         }
      }
   }

   public void render(GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY, float partialTick) {
      this.resolveLayout(font);
      if (this.content != null) {
         SLRenderContext renderContext = new SLRenderContext(graphics, font, this.skin, partialTick, mouseX, mouseY);
         this.content.renderTree(renderContext);
      }
   }

   public void tick() {
      if (this.content != null) {
         this.content.tickTree();
      }
   }

   public void mouseMoved(Font font, double mouseX, double mouseY) {
      this.resolveLayout(font);
      this.updateHoveredNode(this.findHitNode(mouseX, mouseY));
      if (this.hoveredNode != null) {
         this.hoveredNode.mouseMoved(mouseX, mouseY);
      }
   }

   public boolean mouseClicked(Font font, MouseButtonEvent event, boolean doubleClick) {
      this.resolveLayout(font);
      UINode<?> target = this.findHitNode(event.x(), event.y());
      this.updateHoveredNode(target);
      if (target == null) {
         this.requestFocus(null);
         return false;
      } else if (target.mousePressed(event, doubleClick)) {
         this.pressedNode = target;
         this.requestFocus(target);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(Font font, MouseButtonEvent event, double deltaX, double deltaY) {
      this.resolveLayout(font);
      return this.pressedNode == null ? false : this.pressedNode.mouseDragged(event, deltaX, deltaY);
   }

   public boolean mouseReleased(Font font, MouseButtonEvent event) {
      this.resolveLayout(font);
      if (this.pressedNode == null) {
         return false;
      } else {
         UINode<?> releasedNode = this.pressedNode;
         this.pressedNode = null;
         boolean handled = releasedNode.mouseReleased(event);
         this.updateHoveredNode(this.findHitNode(event.x(), event.y()));
         return handled;
      }
   }

   public boolean mouseScrolled(Font font, double mouseX, double mouseY, double scrollX, double scrollY) {
      this.resolveLayout(font);
      UINode<?> target = this.findHitNode(mouseX, mouseY);
      return target == null ? false : target.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
   }

   public boolean keyPressed(KeyEvent event) {
      return this.focusedNode == null ? false : this.focusedNode.keyPressed(event);
   }

   public boolean keyReleased(KeyEvent event) {
      return this.focusedNode == null ? false : this.focusedNode.keyReleased(event);
   }

   public boolean charTyped(CharacterEvent event) {
      return this.focusedNode == null ? false : this.focusedNode.charTyped(event);
   }

   public void resolveLayout(Font font) {
      if (this.layoutDirty && this.content != null) {
         this.content.measure(font, this.viewport.width(), this.viewport.height());
         this.content.layout(this.viewport, font);
         this.layoutDirty = false;
      }
   }

   private void updateHoveredNode(@Nullable UINode<?> nextHoveredNode) {
      if (this.hoveredNode != nextHoveredNode) {
         if (this.hoveredNode != null) {
            this.hoveredNode.setHoveredState(false);
         }

         this.hoveredNode = nextHoveredNode;
         if (this.hoveredNode != null) {
            this.hoveredNode.setHoveredState(true);
         }
      }
   }

   @Nullable
   private UINode<?> findHitNode(double mouseX, double mouseY) {
      return this.content == null ? null : this.content.hitTest(mouseX, mouseY);
   }
}
