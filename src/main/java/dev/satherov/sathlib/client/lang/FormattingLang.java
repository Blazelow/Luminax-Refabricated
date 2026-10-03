package dev.satherov.sathlib.client.lang;

import dev.satherov.sathlib.SathLib;
import java.util.function.BiConsumer;
import net.minecraft.util.Util;

public enum FormattingLang implements SLTranslatable {
   ROUND_BRACKETS("round_brackets", "(%s)"),
   SQUARE_BRACKETS("square_brackets", "[%s]"),
   CURLY_BRACKETS("curly_brackets", "{%s}");

   private final String key;
   private final String translation;

   private FormattingLang(String key, String translation) {
      this.key = Util.makeDescriptionId("formatting", SathLib.id(key));
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
