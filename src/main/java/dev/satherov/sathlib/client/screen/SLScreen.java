package dev.satherov.sathlib.client.screen;

import dev.satherov.sathlib.client.screen.layout.SLBounds;
import dev.satherov.sathlib.client.screen.node.UINode;
import dev.satherov.sathlib.client.screen.style.DefaultTheme;
import dev.satherov.sathlib.client.screen.style.UITheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public abstract class SLScreen extends Screen {
   private final UIRoot root = new UIRoot();

   protected SLScreen(Component title) {
      super(title);
   }

   protected final UIRoot root() {
      return this.root;
   }

   protected abstract UINode<?> create();

   protected SLBounds createViewport() {
      return new SLBounds(0, 0, this.width, this.height);
   }

   protected UITheme createSkin() {
      return DefaultTheme.INSTANCE;
   }

   protected void init() {
      this.rebuild();
   }

   public void tick() {
      this.root.tick();
   }

   public void removed() {
      this.root.setContent(null);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
      super.extractRenderState(graphics, mouseX, mouseY, partialTick);
      this.root.setViewport(this.createViewport());
      this.root.render(graphics, this.font, mouseX, mouseY, partialTick);
   }

   public void mouseMoved(double mouseX, double mouseY) {
      super.mouseMoved(mouseX, mouseY);
      this.root.mouseMoved(this.font, mouseX, mouseY);
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      return this.root.mouseClicked(this.font, event, doubleClick) ? true : super.mouseClicked(event, doubleClick);
   }

   public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
      return this.root.mouseDragged(this.font, event, deltaX, deltaY) ? true : super.mouseDragged(event, deltaX, deltaY);
   }

   public boolean mouseReleased(MouseButtonEvent event) {
      return this.root.mouseReleased(this.font, event) ? true : super.mouseReleased(event);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      return this.root.mouseScrolled(this.font, mouseX, mouseY, scrollX, scrollY) ? true : super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
   }

   public boolean keyPressed(KeyEvent event) {
      return this.root.keyPressed(event) ? true : super.keyPressed(event);
   }

   public boolean keyReleased(KeyEvent event) {
      return this.root.keyReleased(event) ? true : super.keyReleased(event);
   }

   public boolean charTyped(CharacterEvent event) {
      return this.root.charTyped(event) ? true : super.charTyped(event);
   }

   private void rebuild() {
      this.root.setSkin(this.createSkin());
      this.root.setViewport(this.createViewport());
      this.root.setContent(this.create());
   }
}
