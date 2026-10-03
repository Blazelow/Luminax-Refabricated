package dev.satherov.sathlib.network.chat;

import com.mojang.blaze3d.platform.InputConstants.Key;
import dev.satherov.sathlib.client.lang.FormattingLang;
import dev.satherov.sathlib.client.lang.GenericLang;
import dev.satherov.sathlib.client.lang.SLTranslatable;
import dev.satherov.sathlib.core.annotations.NothingNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

@NothingNull
public class SLComponent implements Component {
   private final MutableComponent component;

   private SLComponent(MutableComponent component) {
      this.component = component;
   }

   public static SLComponent empty() {
      return new SLComponent(Component.empty());
   }

   public static SLComponent of(MutableComponent component) {
      return new SLComponent(component);
   }

   public static SLComponent string(String string) {
      return new SLComponent(Component.literal(string));
   }

   public static SLComponent identify(String key, Object... args) {
      List<ChatFormatting> formattings = new ArrayList<>();
      List<Object> arguments = new ArrayList<>();

      for (Object arg : args) {
         if (arg instanceof ChatFormatting formatting) {
            formattings.add(formatting);
         } else if (arg instanceof Component || arg instanceof Number || arg instanceof Boolean || arg instanceof String) {
            arguments.add(arg);
         } else if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            throw new IllegalArgumentException("Translation Argument must either ChatFormatting or a Component, Number, Boolean or String, given for " + key + " was " + arg);
         }
      }

      MutableComponent component = arguments.isEmpty() ? Component.translatable(key) : Component.translatable(key, arguments.toArray());
      if (!formattings.isEmpty()) {
         component.withStyle(formattings.toArray(ChatFormatting[]::new));
      }

      return of(component);
   }

   public static SLComponent key(Key key) {
      return of(key.getDisplayName().copy());
   }

   public static SLComponent pos(BlockPos pos) {
      return of(Component.translatable("chat.coordinates", new Object[]{pos.getX(), pos.getY(), pos.getZ()}));
   }

   public static SLComponent enabledDisabled(boolean enabled) {
      return enabled
         ? GenericLang.ENABLED.translate(new Object[]{ChatFormatting.DARK_GREEN})
         : GenericLang.DISABLED.translate(new Object[]{ChatFormatting.DARK_RED});
   }

   public static SLComponent onOff(boolean on) {
      return on ? GenericLang.ON.translate(new Object[]{ChatFormatting.DARK_GREEN}) : GenericLang.OFF.translate(new Object[]{ChatFormatting.DARK_RED});
   }

   public static SLComponent allowedDenied(boolean allowed) {
      return allowed ? GenericLang.ALLOW.translate(new Object[]{ChatFormatting.DARK_GREEN}) : GenericLang.DENY.translate(new Object[]{ChatFormatting.DARK_RED});
   }

   public static SLComponent roundBrackets(Component component) {
      return FormattingLang.ROUND_BRACKETS.translate(new Object[]{component});
   }

   public static SLComponent squareBrackets(Component component) {
      return FormattingLang.SQUARE_BRACKETS.translate(new Object[]{component});
   }

   public static SLComponent curlyBrackets(Component component) {
      return FormattingLang.CURLY_BRACKETS.translate(new Object[]{component});
   }

   public SLComponent append(Component component) {
      this.component.append(component);
      return this;
   }

   public SLComponent append(SLTranslatable translatable) {
      return this.append(translatable.translate());
   }

   public SLComponent literal(String text) {
      this.component.append(Component.literal(text));
      return this;
   }

   public SLComponent translateable(String translationKey) {
      this.component.append(Component.translatable(translationKey));
      return this;
   }

   public SLComponent translateable(String translationKey, Object... args) {
      this.component.append(identify(translationKey, args));
      return this;
   }

   public SLComponent style(ChatFormatting format) {
      this.component.withStyle(format);
      return this;
   }

   public SLComponent style(ChatFormatting... formats) {
      this.component.withStyle(formats);
      return this;
   }

   public SLComponent style(UnaryOperator<SLStyle> operator) {
      this.component.withStyle(operator.apply(new SLStyle()).create());
      return this;
   }

   public Style getStyle() {
      return this.component.getStyle();
   }

   public ComponentContents getContents() {
      return this.component.getContents();
   }

   public List<Component> getSiblings() {
      return this.component.getSiblings();
   }

   public FormattedCharSequence getVisualOrderText() {
      return this.component.getVisualOrderText();
   }
}
