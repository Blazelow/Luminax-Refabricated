package dev.satherov.sathlib.client.screen;

import dev.satherov.sathlib.client.screen.layout.SLAlignment;
import dev.satherov.sathlib.client.screen.layout.SLBounds;
import dev.satherov.sathlib.client.screen.layout.SLInsets;
import dev.satherov.sathlib.client.screen.layout.SLLength;
import dev.satherov.sathlib.client.screen.layout.SLMeasuredSize;
import dev.satherov.sathlib.client.screen.layout.SLScalar;
import dev.satherov.sathlib.client.screen.node.SLColumnNode;
import dev.satherov.sathlib.client.screen.node.SLRowNode;
import dev.satherov.sathlib.client.screen.node.SLStackNode;
import dev.satherov.sathlib.client.screen.node.UILeafNode;
import dev.satherov.sathlib.client.screen.node.UINode;
import dev.satherov.sathlib.client.screen.render.SLRenderContext;
import dev.satherov.sathlib.util.SLColorUtils;
import dev.satherov.sathlib.util.SLMathUtils;
import java.util.Objects;
import java.util.function.IntConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class ColorPickerScreen extends SLScreen {
   private static final Component TITLE = Component.literal("Color Picker");
   @Nullable
   private final IntConsumer onChanged;
   private ColorPickerScreen.HexFieldNode hexField;
   private int rgb;
   private float hue;
   private float saturation;
   private float value;

   public ColorPickerScreen() {
      this(16711680, null);
   }

   public ColorPickerScreen(int initialRgb) {
      this(initialRgb, null);
   }

   public ColorPickerScreen(int initialRgb, @Nullable IntConsumer onChanged) {
      super(TITLE);
      this.onChanged = onChanged;
      this.applyRgb(initialRgb & 16777215, true);
   }

   private static String sanitizeHexValue(@Nullable String value) {
      String digits = SLMathUtils.sanitizeHex(value);
      return digits.isEmpty() ? "" : "#" + digits;
   }

   private static void drawInsetBox(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
      graphics.fill(x, y, x + width, y + height, -871888113);
      graphics.fillGradient(x + 1, y + 1, x + width - 1, y + height - 1, 570425344, 0);
   }

   private static void drawOutline(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
      drawOutline(graphics, x, y, width, height, color, 1);
   }

   private static void drawOutline(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color, int thickness) {
      graphics.fill(x, y, x + width, y + thickness, color);
      graphics.fill(x, y + height - thickness, x + width, y + height, color);
      graphics.fill(x, y, x + thickness, y + height, color);
      graphics.fill(x + width - thickness, y, x + width, y + height, color);
   }

   @Override
   protected UINode<?> create() {
      SLStackNode rootNode = new SLStackNode();
      rootNode.size(SLLength.fill(), SLLength.fill());
      ColorPickerScreen.PickerPanelNode panelNode = new ColorPickerScreen.PickerPanelNode();
      panelNode.align(SLAlignment.CENTER, SLAlignment.CENTER);
      panelNode.size(SLLength.pixels(ColorPickerScreen.Units.panelWidth()), SLLength.pixels(ColorPickerScreen.Units.panelHeight()));
      panelNode.padding(SLInsets.of(16, 32, 16, 16));
      SLRowNode mainRow = new SLRowNode();
      mainRow.gap(SLScalar.pixels(14));
      mainRow.addChild(new ColorPickerScreen.SaturationValueNode().size(SLLength.pixels(184), SLLength.pixels(184)));
      mainRow.addChild(new ColorPickerScreen.HueSliderNode().size(SLLength.pixels(18), SLLength.pixels(184)));
      SLColumnNode rightColumn = new SLColumnNode();
      rightColumn.size(SLLength.pixels(116), SLLength.pixels(ColorPickerScreen.Units.rightColumnHeight()));
      rightColumn.gap(SLScalar.pixels(16));
      rightColumn.addChild(new ColorPickerScreen.PreviewNode().size(SLLength.pixels(82), SLLength.pixels(82)));
      SLColumnNode sliderColumn = new SLColumnNode();
      sliderColumn.gap(SLScalar.pixels(14));
      sliderColumn.addChild(new ColorPickerScreen.RgbSliderNode(ColorPickerScreen.ColorChannel.RED).size(SLLength.pixels(116), SLLength.pixels(10)));
      sliderColumn.addChild(new ColorPickerScreen.RgbSliderNode(ColorPickerScreen.ColorChannel.GREEN).size(SLLength.pixels(116), SLLength.pixels(10)));
      sliderColumn.addChild(new ColorPickerScreen.RgbSliderNode(ColorPickerScreen.ColorChannel.BLUE).size(SLLength.pixels(116), SLLength.pixels(10)));
      rightColumn.addChild(sliderColumn);
      ColorPickerScreen.HexFieldNode hexFieldNode = new ColorPickerScreen.HexFieldNode();
      hexFieldNode.size(SLLength.pixels(116), SLLength.pixels(18));
      hexFieldNode.setValueSilently(SLMathUtils.rgbToHex(this.getRgb()));
      this.hexField = hexFieldNode;
      rightColumn.addChild(hexFieldNode);
      mainRow.addChild(rightColumn);
      panelNode.addChild(mainRow);
      rootNode.addChild(panelNode);
      return rootNode;
   }

   public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
      graphics.fill(0, 0, this.width, this.height, 2047281420);
   }

   public int getRgb() {
      return this.rgb & 16777215;
   }

   private int getHueColor() {
      return SLColorUtils.argb(
         SLColorUtils.red(SLColorUtils.hsvToRgb(this.hue, 1.0F, 1.0F)),
         SLColorUtils.green(SLColorUtils.hsvToRgb(this.hue, 1.0F, 1.0F)),
         SLColorUtils.blue(SLColorUtils.hsvToRgb(this.hue, 1.0F, 1.0F))
      );
   }

   private int getPreviewColor() {
      return SLColorUtils.argb(SLColorUtils.red(this.getRgb()), SLColorUtils.green(this.getRgb()), SLColorUtils.blue(this.getRgb()));
   }

   private void updateRgbChannel(ColorPickerScreen.ColorChannel channel, float value) {
      int channelValue = Math.round(value * 255.0F);
      int rgb = this.getRgb();
      int red = SLColorUtils.red(rgb);
      int green = SLColorUtils.green(rgb);
      int blue = SLColorUtils.blue(rgb);
      switch (channel) {
         case RED:
            red = channelValue;
            break;
         case GREEN:
            green = channelValue;
            break;
         case BLUE:
            blue = channelValue;
      }

      this.applyRgb(SLColorUtils.rgb(red, green, blue), true);
   }

   private void setHsv(float hue, float saturation, float value) {
      this.hue = Mth.clamp(hue, 0.0F, 1.0F);
      this.saturation = Mth.clamp(saturation, 0.0F, 1.0F);
      this.value = Mth.clamp(value, 0.0F, 1.0F);
      this.rgb = SLColorUtils.hsvToRgb(this.hue, this.saturation, this.value) & 16777215;
      this.syncHexFieldFromColor();
   }

   private void applyRgb(int rgb, boolean syncHue) {
      this.rgb = rgb & 16777215;
      this.syncHsvFromRgb(this.rgb, syncHue);
      this.syncHexFieldFromColor();
   }

   private void syncHsvFromRgb(int rgb, boolean syncHue) {
      float[] hsv = SLColorUtils.rgbToHsv(rgb);
      this.saturation = hsv[1];
      this.value = hsv[2];
      if (syncHue) {
         if (!(hsv[1] <= 0.0F)) {
            if (hsv[0] != 0.0F || !(this.hue >= 1.0F)) {
               this.hue = hsv[0];
            }
         }
      }
   }

   private void syncHexFieldFromColor() {
      if (this.hexField != null && !this.hexField.isFocused()) {
         this.hexField.setValueSilently(SLMathUtils.rgbToHex(this.getRgb()));
      }
   }

   private void commitHexValue(String value) {
      Integer rgb = SLMathUtils.tryPraseToHex(value);
      if (rgb == null) {
         String paddedValue = value + "0".repeat(Math.max(0, 7 - value.length()));
         rgb = SLMathUtils.tryPraseToHex(paddedValue);
      }

      if (rgb == null) {
         this.syncHexFieldFromColor();
      } else {
         this.applyRgb(rgb, true);
         if (this.hexField != null) {
            this.hexField.setValueSilently(SLMathUtils.rgbToHex(this.getRgb()));
         }

         this.fireChanged();
      }
   }

   private void fireChanged() {
      if (this.onChanged != null) {
         this.onChanged.accept(this.getRgb());
      }
   }

   private static enum ColorChannel {
      RED,
      GREEN,
      BLUE;
   }

   private final class HexFieldNode extends UILeafNode<ColorPickerScreen.HexFieldNode> {
      private String value;
      private int cursor;
      private int selectionAnchor;
      private boolean wasFocused;

      private HexFieldNode() {
         Objects.requireNonNull(ColorPickerScreen.this);
         super();
         this.value = "";
      }

      @Override
      protected boolean isInputTarget() {
         return true;
      }

      @Override
      protected SLMeasuredSize measureContent(Font font, int availableWidth, int availableHeight) {
         return new SLMeasuredSize(116, 18);
      }

      @Override
      protected void tick() {
         if (this.wasFocused && !this.isFocused()) {
            this.commitValue();
         }

         this.wasFocused = this.isFocused();
      }

      @Override
      protected void renderSelf(SLRenderContext context) {
         SLBounds bounds = this.getBounds();
         int outlineColor = this.isFocused() ? -2695449 : -12168865;
         ColorPickerScreen.drawInsetBox(context.graphics(), bounds.x(), bounds.y(), bounds.width(), bounds.height());
         ColorPickerScreen.drawOutline(context.graphics(), bounds.x(), bounds.y(), bounds.width(), bounds.height(), outlineColor);
         int textX = bounds.x() + 4;
         int textY = bounds.y() + (bounds.height() - 9) / 2;
         String renderText = this.value.isEmpty() ? "#RRGGBB" : this.value;
         int textColor = this.value.isEmpty() ? -8418666 : -1511949;
         if (this.hasSelection() && !this.value.isEmpty()) {
            int selectionStart = this.selectionStart();
            int selectionEnd = this.selectionEnd();
            int highlightX = textX + context.font().width(this.value.substring(0, selectionStart));
            int highlightRight = textX + context.font().width(this.value.substring(0, selectionEnd));
            int highlightColor = 1718257311;
            context.graphics().fill(highlightX, textY - 1, highlightRight, textY + 9 + 1, highlightColor);
         }

         context.graphics().text(context.font(), renderText, textX, textY, textColor, false);
         if (this.isFocused()) {
            int caretX = textX + context.font().width(this.value.substring(0, this.cursor));
            context.graphics().fill(caretX, textY, caretX + 1, textY + 9, -1);
         }
      }

      @Override
      public boolean mousePressed(MouseButtonEvent event, boolean doubleClick) {
         if (event.button() != 0) {
            return false;
         } else {
            if (doubleClick) {
               this.selectAll();
            } else {
               this.moveCursorTo(this.cursorAt(event.x()), event.hasShiftDown());
            }

            this.setPressedState(true);
            return true;
         }
      }

      @Override
      public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
         if (this.isPressed() && event.button() == 0) {
            this.moveCursorTo(this.cursorAt(event.x()), true);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean mouseReleased(MouseButtonEvent event) {
         boolean wasPressed = this.isPressed();
         this.setPressedState(false);
         return wasPressed && event.button() == 0;
      }

      @Override
      public boolean keyPressed(KeyEvent event) {
         if (!this.isFocused()) {
            return false;
         } else {
            return switch (event.key()) {
               case 256 -> {
                  if (this.getRoot() != null) {
                     this.getRoot().requestFocus(null);
                  }

                  yield true;
               }
               case 257, 335 -> {
                  this.commitValue();
                  yield true;
               }
               case 259 -> {
                  this.deleteText(-1);
                  yield true;
               }
               case 261 -> {
                  this.deleteText(1);
                  yield true;
               }
               case 262 -> {
                  this.moveCursorBy(1, event.hasShiftDown());
                  yield true;
               }
               case 263 -> {
                  this.moveCursorBy(-1, event.hasShiftDown());
                  yield true;
               }
               case 268 -> {
                  this.moveCursorTo(0, event.hasShiftDown());
                  yield true;
               }
               case 269 -> {
                  this.moveCursorTo(this.value.length(), event.hasShiftDown());
                  yield true;
               }
               default -> {
                  if (event.isSelectAll()) {
                     this.selectAll();
                     yield true;
                  } else if (event.isCopy()) {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.highlightedText());
                     yield true;
                  } else if (event.isCut()) {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.highlightedText());
                     if (this.hasSelection()) {
                        this.insertText("");
                     }

                     yield true;
                  } else if (event.isPaste()) {
                     this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                     yield true;
                  } else {
                     yield false;
                  }
               }
            };
         }
      }

      @Override
      public boolean charTyped(CharacterEvent event) {
         if (!this.isFocused()) {
            return false;
         } else {
            int codepoint = event.codepoint();
            if (codepoint != 35 && Character.digit(codepoint, 16) < 0) {
               return false;
            } else {
               this.insertText(new String(Character.toChars(codepoint)));
               return true;
            }
         }
      }

      private void commitValue() {
         ColorPickerScreen.this.commitHexValue(this.value);
      }

      private void setValueSilently(String value) {
         this.value = ColorPickerScreen.sanitizeHexValue(value);
         this.cursor = this.value.length();
         this.selectionAnchor = this.cursor;
      }

      private void insertText(String insertedText) {
         int selectionStart = this.selectionStart();
         int selectionEnd = this.selectionEnd();
         String combinedValue = this.value.substring(0, selectionStart) + insertedText + this.value.substring(selectionEnd);
         this.value = ColorPickerScreen.sanitizeHexValue(combinedValue);
         this.cursor = this.value.length();
         this.selectionAnchor = this.cursor;
      }

      private void deleteText(int direction) {
         if (this.hasSelection()) {
            this.insertText("");
         } else if (!this.value.isEmpty()) {
            int selectionStart = this.cursor;
            int selectionEnd = this.cursor;
            if (direction < 0 && this.cursor > 0) {
               selectionStart = this.cursor - 1;
            } else {
               if (direction <= 0 || this.cursor >= this.value.length()) {
                  return;
               }

               selectionEnd = this.cursor + 1;
            }

            this.value = ColorPickerScreen.sanitizeHexValue(this.value.substring(0, selectionStart) + this.value.substring(selectionEnd));
            this.cursor = Math.min(selectionStart, this.value.length());
            this.selectionAnchor = this.cursor;
         }
      }

      private void moveCursorBy(int delta, boolean keepSelection) {
         this.moveCursorTo(this.cursor + delta, keepSelection);
      }

      private void moveCursorTo(int position, boolean keepSelection) {
         this.cursor = Mth.clamp(position, 0, this.value.length());
         if (!keepSelection) {
            this.selectionAnchor = this.cursor;
         }
      }

      private void selectAll() {
         this.cursor = this.value.length();
         this.selectionAnchor = 0;
      }

      private int cursorAt(double mouseX) {
         Font font = Minecraft.getInstance().font;
         int textX = this.getBounds().x() + 4;
         int relativeX = (int)Math.round(mouseX) - textX;
         if (relativeX <= 0) {
            return 0;
         } else {
            int closestCursor = this.value.length();
            int closestDistance = Integer.MAX_VALUE;

            for (int cursorIndex = 0; cursorIndex <= this.value.length(); cursorIndex++) {
               int cursorX = font.width(this.value.substring(0, cursorIndex));
               int distance = Math.abs(cursorX - relativeX);
               if (distance < closestDistance) {
                  closestDistance = distance;
                  closestCursor = cursorIndex;
               }
            }

            return closestCursor;
         }
      }

      private boolean hasSelection() {
         return this.cursor != this.selectionAnchor;
      }

      private int selectionStart() {
         return Math.min(this.cursor, this.selectionAnchor);
      }

      private int selectionEnd() {
         return Math.max(this.cursor, this.selectionAnchor);
      }

      private String highlightedText() {
         return !this.hasSelection() ? this.value : this.value.substring(this.selectionStart(), this.selectionEnd());
      }
   }

   private final class HueSliderNode extends UILeafNode<ColorPickerScreen.HueSliderNode> {
      private HueSliderNode() {
         Objects.requireNonNull(ColorPickerScreen.this);
         super();
      }

      @Override
      protected boolean isInputTarget() {
         return true;
      }

      @Override
      protected SLMeasuredSize measureContent(Font font, int availableWidth, int availableHeight) {
         return new SLMeasuredSize(18, 184);
      }

      @Override
      protected void renderSelf(SLRenderContext context) {
         SLBounds bounds = this.getBounds();
         ColorPickerScreen.drawInsetBox(context.graphics(), bounds.x(), bounds.y(), bounds.width(), bounds.height());
         int trackX = bounds.x() + 1;
         int trackY = bounds.y() + 1;
         int trackWidth = Math.max(0, bounds.width() - 2);
         int trackHeight = Math.max(0, bounds.height() - 2);

         for (int row = 0; row < trackHeight; row++) {
            float hue = (float)row / Math.max(1, trackHeight - 1);
            int color = SLColorUtils.hsvToArgb(hue, 1.0F, 1.0F, 255);
            context.fill(new SLBounds(trackX, trackY + row, trackWidth, 1), color);
         }

         ColorPickerScreen.drawOutline(context.graphics(), bounds.x(), bounds.y(), bounds.width(), bounds.height(), -16052976);
         int handleY = trackY + Math.round(Math.max(0, trackHeight - 1) * ColorPickerScreen.this.hue);
         context.fill(new SLBounds(bounds.x() - 1, handleY - 1, bounds.width() + 2, 3), -2695449);
         context.fill(new SLBounds(bounds.x(), handleY, bounds.width(), 1), -1);
      }

      @Override
      public boolean mousePressed(MouseButtonEvent event, boolean doubleClick) {
         if (event.button() != 0) {
            return false;
         } else {
            this.updateHue(event.y());
            this.setPressedState(true);
            return true;
         }
      }

      @Override
      public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
         if (this.isPressed() && event.button() == 0) {
            this.updateHue(event.y());
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean mouseReleased(MouseButtonEvent event) {
         boolean wasPressed = this.isPressed();
         this.setPressedState(false);
         if (wasPressed && event.button() == 0) {
            this.updateHue(event.y());
            ColorPickerScreen.this.fireChanged();
            return true;
         } else {
            return false;
         }
      }

      private void updateHue(double mouseY) {
         SLBounds bounds = this.getBounds();
         float hue = (float)((mouseY - (bounds.y() + 1)) / Math.max(1.0, bounds.height() - 3.0));
         ColorPickerScreen.this.setHsv(hue, ColorPickerScreen.this.saturation, ColorPickerScreen.this.value);
      }
   }

   private static final class Palette {
      private static final int SHADOW = 603979776;
      private static final int SCRIM = 2047281420;
      private static final int PITCH = -16052976;
      private static final int SOOT = -871888113;
      private static final int CHARCOAL = -787672810;
      private static final int ONYX = -149941223;
      private static final int GRAPHITE = -149283034;
      private static final int SLATE = -14472653;
      private static final int STEEL = -12168865;
      private static final int STORM = -9795937;
      private static final int ASH = -8418666;
      private static final int VEIL = 570425344;
      private static final int CLEAR = 0;
      private static final int PEARL = -2695449;
      private static final int CLOUD = -1511949;
      private static final int SNOW = -1;
   }

   private final class PickerPanelNode extends SLColumnNode {
      private PickerPanelNode() {
         Objects.requireNonNull(ColorPickerScreen.this);
         super();
      }

      @Override
      protected void renderSelf(SLRenderContext context) {
         SLBounds bounds = this.getBounds();
         int trackX = bounds.x() + 16;
         int trackWidth = Math.max(0, bounds.width() - 32);
         int fillWidth = Math.max(0, Math.min(trackWidth, Math.round(trackWidth * ColorPickerScreen.this.hue)));
         context.fill(new SLBounds(bounds.x() - 8, bounds.y() - 8, bounds.width() + 16, bounds.height() + 16), 603979776);
         context.graphics().fillGradient(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(), -149283034, -149941223);
         context.fill(new SLBounds(bounds.x() + 1, bounds.y() + 1, bounds.width() - 2, bounds.height() - 2), -787672810);
         ColorPickerScreen.drawOutline(context.graphics(), bounds.x(), bounds.y(), bounds.width(), bounds.height(), -12168865);
         context.fill(new SLBounds(trackX, bounds.y() + 8, trackWidth, 1), -14472653);
         context.fill(new SLBounds(trackX, bounds.y() + 8, fillWidth, 2), ColorPickerScreen.this.getHueColor());
         context.text(ColorPickerScreen.TITLE, bounds.x() + 16, bounds.y() + 16, -1511949, false);
      }
   }

   private final class PreviewNode extends UILeafNode<ColorPickerScreen.PreviewNode> {
      private PreviewNode() {
         Objects.requireNonNull(ColorPickerScreen.this);
         super();
      }

      @Override
      protected SLMeasuredSize measureContent(Font font, int availableWidth, int availableHeight) {
         return new SLMeasuredSize(82, 82);
      }

      @Override
      protected void renderSelf(SLRenderContext context) {
         SLBounds bounds = this.getBounds();
         ColorPickerScreen.drawInsetBox(context.graphics(), bounds.x() - 2, bounds.y() - 2, bounds.width() + 4, bounds.height() + 4);
         ColorPickerScreen.drawOutline(context.graphics(), bounds.x() - 1, bounds.y() - 1, bounds.width() + 2, bounds.height() + 2, -16052976);
         int innerX = bounds.x() + (bounds.width() - 72) / 2;
         int innerY = bounds.y() + (bounds.height() - 72) / 2;
         context.fill(new SLBounds(innerX, innerY, 72, 72), ColorPickerScreen.this.getPreviewColor());
      }
   }

   private final class RgbSliderNode extends UILeafNode<ColorPickerScreen.RgbSliderNode> {
      private final ColorPickerScreen.ColorChannel channel;

      private RgbSliderNode(ColorPickerScreen.ColorChannel channel) {
         Objects.requireNonNull(ColorPickerScreen.this);
         super();
         this.channel = Objects.requireNonNull(channel);
      }

      @Override
      protected boolean isInputTarget() {
         return true;
      }

      @Override
      protected SLMeasuredSize measureContent(Font font, int availableWidth, int availableHeight) {
         return new SLMeasuredSize(116, 10);
      }

      @Override
      protected void renderSelf(SLRenderContext context) {
         SLBounds bounds = this.getBounds();
         ColorPickerScreen.drawInsetBox(context.graphics(), bounds.x(), bounds.y(), bounds.width(), bounds.height());
         int trackX = bounds.x() + 1;
         int trackY = bounds.y() + 1;
         int trackWidth = Math.max(0, bounds.width() - 2);
         int trackHeight = Math.max(0, bounds.height() - 2);
         int startColor = this.startColor();
         int endColor = this.endColor();

         for (int column = 0; column < trackWidth; column++) {
            float alpha = (float)column / Math.max(1, trackWidth - 1);
            int color = SLColorUtils.lerp(alpha, startColor, endColor);
            context.fill(new SLBounds(trackX + column, trackY, 1, trackHeight), color);
         }

         ColorPickerScreen.drawOutline(context.graphics(), bounds.x(), bounds.y(), bounds.width(), bounds.height(), -16052976);
         int handleX = trackX + Math.round(Math.max(0, trackWidth - 1) * this.currentValue());
         context.fill(new SLBounds(handleX - 1, bounds.y() - 1, 3, bounds.height() + 2), -2695449);
         context.fill(new SLBounds(handleX, bounds.y(), 1, bounds.height()), -1);
      }

      @Override
      public boolean mousePressed(MouseButtonEvent event, boolean doubleClick) {
         if (event.button() != 0) {
            return false;
         } else {
            this.updateValue(event.x());
            this.setPressedState(true);
            return true;
         }
      }

      @Override
      public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
         if (this.isPressed() && event.button() == 0) {
            this.updateValue(event.x());
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean mouseReleased(MouseButtonEvent event) {
         boolean wasPressed = this.isPressed();
         this.setPressedState(false);
         if (wasPressed && event.button() == 0) {
            this.updateValue(event.x());
            ColorPickerScreen.this.fireChanged();
            return true;
         } else {
            return false;
         }
      }

      private float currentValue() {
         int rgb = ColorPickerScreen.this.getRgb();

         return switch (this.channel) {
            case RED -> SLColorUtils.red(rgb) / 255.0F;
            case GREEN -> SLColorUtils.green(rgb) / 255.0F;
            case BLUE -> SLColorUtils.blue(rgb) / 255.0F;
         };
      }

      private int startColor() {
         int rgb = ColorPickerScreen.this.getRgb();

         return switch (this.channel) {
            case RED -> SLColorUtils.argb(0, SLColorUtils.green(rgb), SLColorUtils.blue(rgb));
            case GREEN -> SLColorUtils.argb(SLColorUtils.red(rgb), 0, SLColorUtils.blue(rgb));
            case BLUE -> SLColorUtils.argb(SLColorUtils.red(rgb), SLColorUtils.green(rgb), 0);
         };
      }

      private int endColor() {
         int rgb = ColorPickerScreen.this.getRgb();

         return switch (this.channel) {
            case RED -> SLColorUtils.argb(255, SLColorUtils.green(rgb), SLColorUtils.blue(rgb));
            case GREEN -> SLColorUtils.argb(SLColorUtils.red(rgb), 255, SLColorUtils.blue(rgb));
            case BLUE -> SLColorUtils.argb(SLColorUtils.red(rgb), SLColorUtils.green(rgb), 255);
         };
      }

      private void updateValue(double mouseX) {
         SLBounds bounds = this.getBounds();
         float value = (float)((mouseX - (bounds.x() + 1)) / Math.max(1.0, bounds.width() - 3.0));
         ColorPickerScreen.this.updateRgbChannel(this.channel, Mth.clamp(value, 0.0F, 1.0F));
      }
   }

   private final class SaturationValueNode extends UILeafNode<ColorPickerScreen.SaturationValueNode> {
      private SaturationValueNode() {
         Objects.requireNonNull(ColorPickerScreen.this);
         super();
      }

      @Override
      protected boolean isInputTarget() {
         return true;
      }

      @Override
      protected SLMeasuredSize measureContent(Font font, int availableWidth, int availableHeight) {
         return new SLMeasuredSize(184, 184);
      }

      @Override
      protected void renderSelf(SLRenderContext context) {
         SLBounds bounds = this.getBounds();
         ColorPickerScreen.drawInsetBox(context.graphics(), bounds.x() - 2, bounds.y() - 2, bounds.width() + 4, bounds.height() + 4);

         for (int column = 0; column < bounds.width(); column++) {
            float alpha = (float)column / Math.max(1, bounds.width() - 1);
            int columnColor = SLColorUtils.lerp(alpha, -1, ColorPickerScreen.this.getHueColor());
            context.graphics().fillGradient(bounds.x() + column, bounds.y(), bounds.x() + column + 1, bounds.bottom(), columnColor, -16777216);
         }

         ColorPickerScreen.drawOutline(context.graphics(), bounds.x() - 1, bounds.y() - 1, bounds.width() + 2, bounds.height() + 2, -16052976);
         int cursorX = Math.round(ColorPickerScreen.this.saturation * Math.max(0, bounds.width() - 1));
         int cursorY = Math.round((1.0F - ColorPickerScreen.this.value) * Math.max(0, bounds.height() - 1));
         int innerColor = ColorPickerScreen.this.value > 0.65F && ColorPickerScreen.this.saturation < 0.35F ? -2695449 : -1;
         ColorPickerScreen.drawOutline(context.graphics(), bounds.x() + cursorX - 4, bounds.y() + cursorY - 4, 9, 9, -2695449);
         ColorPickerScreen.drawOutline(context.graphics(), bounds.x() + cursorX - 3, bounds.y() + cursorY - 3, 7, 7, innerColor);
      }

      @Override
      public boolean mousePressed(MouseButtonEvent event, boolean doubleClick) {
         if (event.button() != 0) {
            return false;
         } else {
            this.updateSelection(event.x(), event.y());
            this.setPressedState(true);
            return true;
         }
      }

      @Override
      public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
         if (this.isPressed() && event.button() == 0) {
            this.updateSelection(event.x(), event.y());
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean mouseReleased(MouseButtonEvent event) {
         boolean wasPressed = this.isPressed();
         this.setPressedState(false);
         if (wasPressed && event.button() == 0) {
            this.updateSelection(event.x(), event.y());
            ColorPickerScreen.this.fireChanged();
            return true;
         } else {
            return false;
         }
      }

      private void updateSelection(double mouseX, double mouseY) {
         SLBounds bounds = this.getBounds();
         float saturation = (float)((mouseX - bounds.x()) / Math.max(1.0, bounds.width() - 1.0));
         float value = 1.0F - (float)((mouseY - bounds.y()) / Math.max(1.0, bounds.height() - 1.0));
         ColorPickerScreen.this.setHsv(ColorPickerScreen.this.hue, saturation, value);
      }
   }

   private static final class Units {
      private static final int PANEL_PADDING = 16;
      private static final int TITLE_HEIGHT = 16;
      private static final int ACCENT_Y = 8;
      private static final int SV_SIZE = 184;
      private static final int HUE_WIDTH = 18;
      private static final int COLUMN_GAP = 14;
      private static final int INFO_WIDTH = 116;
      private static final int PREVIEW_SIZE = 82;
      private static final int PREVIEW_FILL_SIZE = 72;
      private static final int FIELD_HEIGHT = 18;
      private static final int SLIDER_HEIGHT = 10;
      private static final int SLIDER_GAP = 14;
      private static final int SECTION_GAP = 16;
      private static final int TEXT_PADDING = 4;

      private static int panelWidth() {
         return 378;
      }

      private static int rightColumnHeight() {
         return 190;
      }

      private static int panelHeight() {
         return 48 + Math.max(184, rightColumnHeight());
      }
   }
}
