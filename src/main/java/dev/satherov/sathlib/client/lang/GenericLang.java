package dev.satherov.sathlib.client.lang;

import dev.satherov.sathlib.SathLib;
import java.util.function.BiConsumer;
import net.minecraft.util.Util;

public enum GenericLang implements SLTranslatable {
   ON("on", "On"),
   OFF("off", "Off"),
   ENABLED("enabled", "Enabled"),
   DISABLED("disabled", "Disabled"),
   ALLOW("allow", "Allow"),
   DENY("deny", "Deny"),
   ALLOWED("allowed", "Allowed"),
   DENIED("denied", "Denied"),
   NONE("none", "None"),
   ALL("all", "All"),
   ANY("any", "Any"),
   SUPPORTED("supported", "Supported"),
   UNSUPPORTED("unsupported", "Unsupported");

   private final String key;
   private final String translation;

   private GenericLang(String key, String translation) {
      this.key = Util.makeDescriptionId("generic", SathLib.id(key));
      this.translation = translation;
   }

   public static void translate(BiConsumer<String, String> consumer) {
      for (SLTranslatable lang : values()) {
         consumer.accept(lang.key(), lang.translation());
      }
   }

   @Override
   public String key() {
      return this.key;
   }

   @Override
   public String translation() {
      return this.translation;
   }
}
