package dev.satherov.sathlib.client.screen.node;

import dev.satherov.sathlib.client.screen.UIRoot;
import dev.satherov.sathlib.client.screen.layout.SLInsets;
import dev.satherov.sathlib.client.screen.render.SLRenderContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.Nullable;

public abstract class UIContainerNode<S extends UIContainerNode<S>> extends UINode<S> {
   private final List<UINode<?>> children = new ArrayList<>();

   protected UIContainerNode() {
   }

   public List<UINode<?>> getChildren() {
      return Collections.unmodifiableList(this.children);
   }

   public final S addChild(UINode<?> child) {
      this.children.add(child);
      if (this.getRoot() != null) {
         child.attach(this.getRoot(), this);
      }

      this.invalidateLayout();
      return this.self();
   }

   public final void removeChild(UINode<?> child) {
      if (this.children.remove(child)) {
         child.detach();
         this.invalidateLayout();
      }
   }

   public final void clearChildren() {
      List<UINode<?>> detachedChildren = List.copyOf(this.children);
      this.children.clear();

      for (UINode<?> child : detachedChildren) {
         child.detach();
      }

      this.invalidateLayout();
   }

   protected final SLInsets childMargin(UINode<?> child) {
      return child.getLayoutSpec().margin();
   }

   @Override
   protected void onAttached(UIRoot root) {
      for (UINode<?> child : this.children) {
         child.attach(root, this);
      }
   }

   @Override
   protected void onDetached() {
      for (UINode<?> child : List.copyOf(this.children)) {
         child.detach();
      }
   }

   @Override
   protected void renderChildren(SLRenderContext context) {
      for (UINode<?> child : this.children) {
         child.renderTree(context);
      }
   }

   @Override
   protected void tickChildren() {
      for (UINode<?> child : this.children) {
         child.tickTree();
      }
   }

   @Nullable
   @Override
   public UINode<?> hitTest(double mouseX, double mouseY) {
      if (this.isVisible() && this.isEnabled() && this.getBounds().contains(mouseX, mouseY)) {
         for (int childIndex = this.children.size() - 1; childIndex >= 0; childIndex--) {
            UINode<?> child = this.children.get(childIndex);
            UINode<?> hitNode = child.hitTest(mouseX, mouseY);
            if (hitNode != null) {
               return hitNode;
            }
         }

         return super.hitTest(mouseX, mouseY);
      } else {
         return null;
      }
   }
}
