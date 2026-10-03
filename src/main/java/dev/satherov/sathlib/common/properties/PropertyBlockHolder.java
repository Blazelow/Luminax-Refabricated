package dev.satherov.sathlib.common.properties;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record PropertyBlockHolder(BlockState state, @Nullable BlockEntity blockEntity) implements PropertyHolder {
   public <BE extends BlockEntity> void doIfPresent(Class<BE> type, Consumer<BE> consumer) {
      if (type.isInstance(this.blockEntity)) {
         consumer.accept(type.cast(this.blockEntity));
      }
   }

   @Nullable
   public <T, BE extends BlockEntity> T supplyIfPresent(Class<BE> type, Function<BE, T> consumer) {
      return type.isInstance(this.blockEntity) ? consumer.apply(type.cast(this.blockEntity)) : null;
   }
}
