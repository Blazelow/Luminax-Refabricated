package dev.satherov.sathlib.common.properties;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

@FunctionalInterface
public interface PropertyApplicator<T, H extends PropertyHolder> {
   static <T extends Comparable<T>> PropertyApplicator<T, PropertyBlockHolder> block(Property<T> property) {
      return (block, var2, value) -> {
         BlockState updated = (BlockState)block.state().setValue(property, value);
         return new PropertyBlockHolder(updated, block.blockEntity());
      };
   }

   static <T, BE extends BlockEntity> PropertyApplicator<T, PropertyBlockHolder> blockEntity(Class<BE> type, BiConsumer<BE, T> setter) {
      return (block, var3, value) -> {
         block.doIfPresent(type, entity -> setter.accept(entity, (T)value));
         return block;
      };
   }

   static <T> PropertyApplicator<T, PropertyItemHolder> item(DataComponentType<T> component) {
      return (var1, item, value) -> {
         item.stack().set(component, value);
         return item;
      };
   }

   static <T> PropertyApplicator<T, PropertyItemHolder> item(Supplier<DataComponentType<T>> component) {
      return (var1, item, value) -> {
         item.stack().set(component.get(), value);
         return item;
      };
   }

   H apply(PropertyBlockHolder var1, PropertyItemHolder var2, T var3);
}
