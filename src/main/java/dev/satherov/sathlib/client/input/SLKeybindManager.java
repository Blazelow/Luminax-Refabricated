package dev.satherov.sathlib.client.input;

import dev.satherov.sathlib.client.lang.SLTranslatable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.Identifier;

/**
 * Registers key mappings and runs a callback once per key press.
 */
public class SLKeybindManager {
   private final Category category;
   private final Map<KeyMapping, Runnable> mappings = new LinkedHashMap<>();

   private SLKeybindManager(Category category) {
      this.category = category;
   }

   public static SLKeybindManager create(String namespace, Identifier category) {
      return new SLKeybindManager(Category.register(category));
   }

   public KeyMapping add(SLTranslatable name, int key, Runnable onPress) {
      KeyMapping mapping = new KeyMapping(name.key(), key, this.category);
      this.mappings.put(mapping, onPress);
      return mapping;
   }

   public KeyMapping add(Function<Category, KeyMapping> factory, Runnable onPress) {
      KeyMapping mapping = factory.apply(this.category);
      this.mappings.put(mapping, onPress);
      return mapping;
   }

   public void register() {
      for (KeyMapping mapping : this.mappings.keySet()) {
         KeyMappingHelper.registerKeyMapping(mapping);
      }

      ClientTickEvents.END_CLIENT_TICK.register(client -> {
         for (Map.Entry<KeyMapping, Runnable> entry : this.mappings.entrySet()) {
            while (entry.getKey().consumeClick()) {
               entry.getValue().run();
            }
         }
      });
   }
}
