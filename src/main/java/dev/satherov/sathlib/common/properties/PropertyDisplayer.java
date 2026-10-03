package dev.satherov.sathlib.common.properties;

import dev.satherov.sathlib.client.lang.SLDisplayable;
import dev.satherov.sathlib.client.lang.SLTranslatable;
import dev.satherov.sathlib.network.chat.SLComponent;

@FunctionalInterface
public interface PropertyDisplayer<T> {
   static <T> PropertyDisplayer<T> direct(SLComponent component) {
      return var1 -> component;
   }

   static <T> PropertyDisplayer<T> direct(SLTranslatable translatable) {
      return var1 -> translatable.translate();
   }

   static PropertyDisplayer<Boolean> boolDisplayer(SLComponent on, SLComponent off) {
      return value -> value ? on : off;
   }

   static PropertyDisplayer<Boolean> boolDisplayer(SLTranslatable on, SLTranslatable off) {
      return value -> value ? on.translate() : off.translate();
   }

   static <T extends Enum<T> & PropertyEnum> PropertyDisplayer<T> enumValueDisplayer() {
      return rec$ -> rec$.display();
   }

   static <T extends Enum<T> & PropertyEnum> PropertyDisplayer<T> enumTooltipDisplayer() {
      return rec$ -> rec$.tooltip();
   }

   static <T> PropertyDisplayer<T> defaultValueDisplayer() {
      return val -> {
         return switch (val) {
            case null -> SLComponent.empty();
            case String string -> SLComponent.string(string);
            case Boolean bool -> SLComponent.enabledDisabled(bool);
            case SLDisplayable displayable -> displayable.display();
            default -> SLComponent.string(String.valueOf(val));
         };
      };
   }

   static <T> PropertyDisplayer<T> empty() {
      return var0 -> SLComponent.empty();
   }

   SLComponent display(T var1);
}
