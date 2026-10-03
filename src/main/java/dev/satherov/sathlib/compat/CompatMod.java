package dev.satherov.sathlib.compat;

import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;

public interface CompatMod {
   String getModId();

   default boolean isLoaded() {
      return FabricLoader.getInstance().isModLoaded(this.getModId());
   }

   default void run(Runnable runnable) {
      if (this.isLoaded()) {
         runnable.run();
      }
   }

   @Nullable
   default <T> T run(Supplier<T> supplier) {
      return this.isLoaded() ? supplier.get() : null;
   }

   default <T> T run(Supplier<T> supplier, T defaultValue) {
      return this.isLoaded() ? supplier.get() : defaultValue;
   }
}
