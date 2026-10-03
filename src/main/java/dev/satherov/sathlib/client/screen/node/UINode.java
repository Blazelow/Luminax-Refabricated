package dev.satherov.sathlib.client.screen.node;

import dev.satherov.sathlib.client.screen.UIRoot;
import dev.satherov.sathlib.client.screen.layout.SLAlignment;
import dev.satherov.sathlib.client.screen.layout.SLBounds;
import dev.satherov.sathlib.client.screen.layout.SLInsets;
import dev.satherov.sathlib.client.screen.layout.SLLayoutSpec;
import dev.satherov.sathlib.client.screen.layout.SLLength;
import dev.satherov.sathlib.client.screen.layout.SLMeasuredSize;
import dev.satherov.sathlib.client.screen.layout.SLScalar;
import dev.satherov.sathlib.client.screen.render.SLRenderContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.Nullable;

public abstract class UINode<S extends UINode<S>> {
   @Nullable
   private UIContainerNode<?> parent;
   @Nullable
   private UIRoot root;
   private SLLayoutSpec layoutSpec = SLLayoutSpec.defaultSpec();
   private SLMeasuredSize measuredSize = SLMeasuredSize.ZERO;
   private SLBounds bounds = SLBounds.EMPTY;
   private boolean layoutDirty = true;
   private boolean visible = true;
   private boolean enabled = true;
   private boolean hovered;
   private boolean pressed;
   private boolean focused;

   protected UINode() {
   }

   protected final S self() {
      return (S)this;
   }

   public SLBounds getContentBounds() {
      return this.bounds.inset(this.layoutSpec.padding());
   }

   public void setVisible(boolean visible) {
      if (this.visible != visible) {
         this.visible = visible;
         this.invalidateLayout();
      }
   }

   public final S width(SLLength width) {
      this.layoutSpec = this.layoutSpec.withWidth(width);
      this.invalidateLayout();
      return this.self();
   }

   public final S height(SLLength height) {
      this.layoutSpec = this.layoutSpec.withHeight(height);
      this.invalidateLayout();
      return this.self();
   }

   public final S size(SLLength width, SLLength height) {
      this.layoutSpec = this.layoutSpec.withWidth(width).withHeight(height);
      this.invalidateLayout();
      return this.self();
   }

   public final S margin(SLInsets margin) {
      this.layoutSpec = this.layoutSpec.withMargin(margin);
      this.invalidateLayout();
      return this.self();
   }

   public final S padding(SLInsets padding) {
      this.layoutSpec = this.layoutSpec.withPadding(padding);
      this.invalidateLayout();
      return this.self();
   }

   public final S align(SLAlignment horizontalAlignment, SLAlignment verticalAlignment) {
      this.layoutSpec = this.layoutSpec.withAlignment(horizontalAlignment, verticalAlignment);
      this.invalidateLayout();
      return this.self();
   }

   public final S offset(SLScalar offsetX, SLScalar offsetY) {
      this.layoutSpec = this.layoutSpec.withOffset(offsetX, offsetY);
      this.invalidateLayout();
      return this.self();
   }

   public final void invalidateLayout() {
      this.layoutDirty = true;
      if (this.root != null) {
         this.root.invalidateLayout();
      }
   }

   public final void attach(UIRoot root, @Nullable UIContainerNode<?> parent) {
      this.root = root;
      this.parent = parent;
      this.onAttached(root);
   }

   public final void detach() {
      this.onDetached();
      this.parent = null;
      this.root = null;
   }

   public final SLMeasuredSize measure(Font font, int availableWidth, int availableHeight) {
      if (!this.visible) {
         this.measuredSize = SLMeasuredSize.ZERO;
         this.layoutDirty = false;
         return this.measuredSize;
      } else {
         int clampedWidth = Math.max(0, availableWidth);
         int clampedHeight = Math.max(0, availableHeight);
         int paddingWidth = this.layoutSpec.padding().horizontal(clampedWidth);
         int paddingHeight = this.layoutSpec.padding().vertical(clampedHeight);
         int contentAvailableWidth = Math.max(0, clampedWidth - paddingWidth);
         int contentAvailableHeight = Math.max(0, clampedHeight - paddingHeight);
         SLMeasuredSize measuredContent = this.measureContent(font, contentAvailableWidth, contentAvailableHeight);
         int preferredWidth = measuredContent.width() + paddingWidth;
         int preferredHeight = measuredContent.height() + paddingHeight;
         this.measuredSize = new SLMeasuredSize(
            Math.max(0, this.layoutSpec.width().resolvePreferred(clampedWidth, preferredWidth)),
            Math.max(0, this.layoutSpec.height().resolvePreferred(clampedHeight, preferredHeight))
         );
         return this.measuredSize;
      }
   }

   public final void layout(SLBounds bounds, Font font) {
      this.bounds = bounds;
      this.layoutDirty = false;
      this.onLayout(font, this.getContentBounds());
   }

   public final void renderTree(SLRenderContext context) {
      if (this.visible) {
         this.renderSelf(context);
         this.renderChildren(context);
      }
   }

   public final void tickTree() {
      this.tick();
      this.tickChildren();
   }

   @Nullable
   public UINode<?> hitTest(double mouseX, double mouseY) {
      if (this.visible && this.enabled && this.bounds.contains(mouseX, mouseY)) {
         return this.isInputTarget() ? this : null;
      } else {
         return null;
      }
   }

   public final void setHoveredState(boolean hovered) {
      this.hovered = hovered;
   }

   public final void setPressedState(boolean pressed) {
      this.pressed = pressed;
   }

   public final void setFocusedState(boolean focused) {
      this.focused = focused;
   }

   public boolean isFocusable() {
      return this.isInputTarget();
   }

   protected boolean isInputTarget() {
      return false;
   }

   protected abstract SLMeasuredSize measureContent(Font var1, int var2, int var3);

   protected void onLayout(Font font, SLBounds contentBounds) {
   }

   protected void renderSelf(SLRenderContext context) {
   }

   protected void renderChildren(SLRenderContext context) {
   }

   protected void tickChildren() {
   }

   protected void onAttached(UIRoot root) {
   }

   protected void onDetached() {
   }

   protected void tick() {
   }

   public boolean mouseMoved(double mouseX, double mouseY) {
      return false;
   }

   public boolean mousePressed(MouseButtonEvent event, boolean doubleClick) {
      this.setPressedState(true);
      return false;
   }

   public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
      return false;
   }

   public boolean mouseReleased(MouseButtonEvent event) {
      this.setPressedState(false);
      return false;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      return false;
   }

   public boolean keyPressed(KeyEvent event) {
      return false;
   }

   public boolean keyReleased(KeyEvent event) {
      return false;
   }

   public boolean charTyped(CharacterEvent event) {
      return false;
   }

   @Nullable
   public UIContainerNode<?> getParent() {
      return this.parent;
   }

   @Nullable
   public UIRoot getRoot() {
      return this.root;
   }

   public SLLayoutSpec getLayoutSpec() {
      return this.layoutSpec;
   }

   public SLMeasuredSize getMeasuredSize() {
      return this.measuredSize;
   }

   public SLBounds getBounds() {
      return this.bounds;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isHovered() {
      return this.hovered;
   }

   public boolean isPressed() {
      return this.pressed;
   }

   public boolean isFocused() {
      return this.focused;
   }
}
