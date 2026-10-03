package dev.satherov.sathlib.client.lang;

import dev.satherov.sathlib.network.chat.SLComponent;
import net.minecraft.network.chat.Component;

public interface SLTranslatable {
   String key();

   String translation();

   default SLComponent translate() {
      return SLComponent.of(Component.translatable(this.key()));
   }

   default SLComponent translate(Object... args) {
      return SLComponent.identify(this.key(), args);
   }
}
