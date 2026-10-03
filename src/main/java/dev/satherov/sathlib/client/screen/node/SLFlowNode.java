package dev.satherov.sathlib.client.screen.node;

import dev.satherov.sathlib.client.screen.layout.SLAlignment;
import dev.satherov.sathlib.client.screen.layout.SLAxis;
import dev.satherov.sathlib.client.screen.layout.SLBounds;
import dev.satherov.sathlib.client.screen.layout.SLInsets;
import dev.satherov.sathlib.client.screen.layout.SLLength;
import dev.satherov.sathlib.client.screen.layout.SLMeasuredSize;
import dev.satherov.sathlib.client.screen.layout.SLScalar;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;

public abstract class SLFlowNode<S extends SLFlowNode<S>> extends UIContainerNode<S> {
   private final SLAxis axis;
   private SLScalar gap = SLScalar.zero();
   private SLAlignment mainAxisAlignment = SLAlignment.START;

   protected SLFlowNode(SLAxis axis) {
      this.axis = axis;
   }

   public final S gap(SLScalar gap) {
      this.gap = gap;
      this.invalidateLayout();
      return this.self();
   }

   public final S mainAxisAlignment(SLAlignment alignment) {
      this.mainAxisAlignment = alignment;
      this.invalidateLayout();
      return this.self();
   }

   @Override
   protected SLMeasuredSize measureContent(Font font, int availableWidth, int availableHeight) {
      int totalMain = 0;
      int maxCross = 0;
      boolean firstVisibleChild = true;
      int gapPixels = this.gap.resolve(this.axis == SLAxis.HORIZONTAL ? availableWidth : availableHeight);

      for (UINode<?> child : this.getChildren()) {
         if (child.isVisible()) {
            SLInsets margin = child.getLayoutSpec().margin();
            int childAvailableWidth = Math.max(0, availableWidth - margin.horizontal(availableWidth));
            int childAvailableHeight = Math.max(0, availableHeight - margin.vertical(availableHeight));
            SLMeasuredSize childSize = child.measure(font, childAvailableWidth, childAvailableHeight);
            int outerMain = this.axis == SLAxis.HORIZONTAL
               ? childSize.width() + margin.horizontal(availableWidth)
               : childSize.height() + margin.vertical(availableHeight);
            int outerCross = this.axis == SLAxis.HORIZONTAL
               ? childSize.height() + margin.vertical(availableHeight)
               : childSize.width() + margin.horizontal(availableWidth);
            if (!firstVisibleChild) {
               totalMain += gapPixels;
            }

            totalMain += outerMain;
            maxCross = Math.max(maxCross, outerCross);
            firstVisibleChild = false;
         }
      }

      return this.axis == SLAxis.HORIZONTAL ? new SLMeasuredSize(totalMain, maxCross) : new SLMeasuredSize(maxCross, totalMain);
   }

   @Override
   protected void onLayout(Font font, SLBounds contentBounds) {
      List<UINode<?>> visibleChildren = new ArrayList<>();

      for (UINode<?> child : this.getChildren()) {
         if (child.isVisible()) {
            visibleChildren.add(child);
         }
      }

      if (!visibleChildren.isEmpty()) {
         int availableMain = this.axis == SLAxis.HORIZONTAL ? contentBounds.width() : contentBounds.height();
         int availableCross = this.axis == SLAxis.HORIZONTAL ? contentBounds.height() : contentBounds.width();
         int gapPixels = this.gap.resolve(availableMain);
         int preferredMain = 0;
         float totalFillWeight = 0.0F;

         for (int childIndex = 0; childIndex < visibleChildren.size(); childIndex++) {
            UINode<?> childx = visibleChildren.get(childIndex);
            SLInsets margin = childx.getLayoutSpec().margin();
            SLMeasuredSize childSize = childx.getMeasuredSize();
            if (childIndex > 0) {
               preferredMain += gapPixels;
            }

            preferredMain += this.axis == SLAxis.HORIZONTAL
               ? childSize.width() + margin.horizontal(contentBounds.width())
               : childSize.height() + margin.vertical(contentBounds.height());
            SLLength mainLength = this.axis == SLAxis.HORIZONTAL ? childx.getLayoutSpec().width() : childx.getLayoutSpec().height();
            totalFillWeight += mainLength.weight();
         }

         int extraMain = Math.max(0, availableMain - preferredMain);

         int flowStart = switch (this.mainAxisAlignment) {
            case CENTER -> extraMain / 2;
            case END -> extraMain;
            case START, FILL -> 0;
         };
         int cursor = (this.axis == SLAxis.HORIZONTAL ? contentBounds.x() : contentBounds.y()) + flowStart;
         int distributedMain = 0;

         for (int childIndex = 0; childIndex < visibleChildren.size(); childIndex++) {
            UINode<?> childx = visibleChildren.get(childIndex);
            SLInsets margin = childx.getLayoutSpec().margin();
            SLMeasuredSize childSize = childx.getMeasuredSize();
            int marginStart = this.axis == SLAxis.HORIZONTAL ? margin.left(contentBounds.width()) : margin.top(contentBounds.height());
            int marginEnd = this.axis == SLAxis.HORIZONTAL ? margin.right(contentBounds.width()) : margin.bottom(contentBounds.height());
            int marginCrossStart = this.axis == SLAxis.HORIZONTAL ? margin.top(contentBounds.height()) : margin.left(contentBounds.width());
            int marginCrossEnd = this.axis == SLAxis.HORIZONTAL ? margin.bottom(contentBounds.height()) : margin.right(contentBounds.width());
            SLLength mainLength = this.axis == SLAxis.HORIZONTAL ? childx.getLayoutSpec().width() : childx.getLayoutSpec().height();
            SLLength crossLength = this.axis == SLAxis.HORIZONTAL ? childx.getLayoutSpec().height() : childx.getLayoutSpec().width();
            int additionalMain = 0;
            if (mainLength.isFill() && totalFillWeight > 0.0F) {
               if (childIndex == visibleChildren.size() - 1) {
                  additionalMain = Math.max(0, extraMain - distributedMain);
               } else {
                  additionalMain = Math.round(extraMain * (mainLength.weight() / totalFillWeight));
                  distributedMain += additionalMain;
               }
            }

            int preferredChildMain = this.axis == SLAxis.HORIZONTAL ? childSize.width() : childSize.height();
            int resolvedChildMain = mainLength.resolveFinal(preferredChildMain + additionalMain, preferredChildMain);
            int crossAvailable = Math.max(0, availableCross - marginCrossStart - marginCrossEnd);
            SLAlignment crossAlignment = this.axis == SLAxis.HORIZONTAL
               ? childx.getLayoutSpec().verticalAlignment()
               : childx.getLayoutSpec().horizontalAlignment();
            int preferredChildCross = this.axis == SLAxis.HORIZONTAL ? childSize.height() : childSize.width();
            int resolvedChildCross = crossLength.isFill()
               ? crossAvailable
               : crossAlignment.resolveSize(crossAvailable, crossLength.resolveFinal(crossAvailable, preferredChildCross));
            int childMainStart = cursor + marginStart;
            int crossRegionStart = (this.axis == SLAxis.HORIZONTAL ? contentBounds.y() : contentBounds.x()) + marginCrossStart;
            int crossRegionAvailable = Math.max(0, availableCross - marginCrossStart - marginCrossEnd);
            int childCrossStart = crossAlignment.resolvePosition(crossRegionStart, crossRegionAvailable, resolvedChildCross);
            int offsetX = childx.getLayoutSpec().offsetX().resolve(contentBounds.width());
            int offsetY = childx.getLayoutSpec().offsetY().resolve(contentBounds.height());
            SLBounds childBounds = this.axis == SLAxis.HORIZONTAL
               ? new SLBounds(childMainStart + offsetX, childCrossStart + offsetY, resolvedChildMain, resolvedChildCross)
               : new SLBounds(childCrossStart + offsetX, childMainStart + offsetY, resolvedChildCross, resolvedChildMain);
            childx.layout(childBounds, font);
            cursor += marginStart + resolvedChildMain + marginEnd;
            if (childIndex < visibleChildren.size() - 1) {
               cursor += gapPixels;
            }
         }
      }
   }
}
