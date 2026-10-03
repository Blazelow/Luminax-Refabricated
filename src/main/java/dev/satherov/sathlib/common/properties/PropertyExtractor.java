package dev.satherov.sathlib.common.properties;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;

@FunctionalInterface
public interface PropertyExtractor<T> {
   static <T extends Comparable<T>> PropertyExtractor<T> block(Property<T> property, T defaultValue) {
      return (block, var3) -> (T)block.state().getValueOrElse(property, defaultValue);
   }

   static <T, BE extends BlockEntity> PropertyExtractor<T> blockEntity(Class<BE> type, Function<BE, T> getter, T defaultValue) {
      return (block, var4) -> Objects.requireNonNullElse(block.supplyIfPresent(type, getter), defaultValue);
   }

   static <T> PropertyExtractor<T> item(DataComponentType<T> component, T defaultValue) {
      return (var2, item) -> (T)item.stack().getOrDefault(component, defaultValue);
   }

   static <T> PropertyExtractor<T> item(Supplier<DataComponentType<T>> component, T defaultValue) {
      return (var2, item) -> (T)item.stack().getOrDefault(component.get(), defaultValue);
   }

   T extract(PropertyBlockHolder var1, PropertyItemHolder var2);
}
