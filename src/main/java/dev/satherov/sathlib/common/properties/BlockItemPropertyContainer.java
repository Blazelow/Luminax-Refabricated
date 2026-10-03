package dev.satherov.sathlib.common.properties;

import dev.satherov.sathlib.core.annotations.NothingNull;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

@NothingNull
public class BlockItemPropertyContainer<B extends Block, I extends Item> implements Iterable<BlockItemProperty<?>> {
   private final Class<B> blockClass;
   private final Class<I> itemClass;
   private final Map<Identifier, BlockItemProperty<?>> properties;

   public static <B extends Block, I extends Item> BlockItemPropertyContainer.Builder<B, I> builder(Class<B> blockClass, Class<I> itemClass) {
      return new BlockItemPropertyContainer.Builder<>(blockClass, itemClass);
   }

   public PropertyItemHolder applyToItem(ItemStack stack, BlockState state) {
      return this.applyToItem(stack, state, null);
   }

   public PropertyItemHolder applyToItem(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      if (!this.supports(stack, state)) {
         return item;
      } else {
         for (BlockItemProperty<?> property : this) {
            item = property.applyToItem(item.stack(), state, entity);
         }

         return item;
      }
   }

   public PropertyBlockHolder applyToBlock(ItemStack stack, BlockState state) {
      return this.applyToBlock(stack, state, null);
   }

   public PropertyBlockHolder applyToBlock(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      if (!this.supports(stack, state)) {
         return block;
      } else {
         for (BlockItemProperty<?> property : this) {
            block = property.applyToBlock(stack, block.state(), block.blockEntity());
         }

         return block;
      }
   }

   public boolean matchItems(ItemStack stack, ItemStack other, BlockState state) {
      return this.matchItems(stack, other, state, null);
   }

   public boolean matchItems(ItemStack stack, ItemStack other, BlockState state, @Nullable BlockEntity entity) {
      if (this.supports(stack) && this.supports(other) && this.supports(state)) {
         for (BlockItemProperty<?> property : this) {
            if (!property.matchItems(stack, other, state, entity)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean matchBlocks(ItemStack stack, BlockState state, BlockState other) {
      return this.matchBlocks(stack, state, null, other, null);
   }

   public boolean matchBlocks(ItemStack stack, BlockState state, @Nullable BlockEntity entity, BlockState other, @Nullable BlockEntity otherEntity) {
      if (this.supports(stack) && this.supports(state) && this.supports(other)) {
         for (BlockItemProperty<?> property : this) {
            if (!property.matchBlocks(stack, state, entity, other, otherEntity)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean matches(ItemStack stack, BlockState state) {
      return this.matches(stack, state, null);
   }

   public boolean matches(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      if (this.supports(stack) && this.supports(state)) {
         for (BlockItemProperty<?> property : this) {
            if (!property.match(stack, state, entity)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public Iterator<BlockItemProperty<?>> iterator() {
      return this.properties.values().iterator();
   }

   public List<BlockItemProperty<?>> getProperties() {
      return List.copyOf(this.properties.values());
   }

   @Nullable
   public BlockItemProperty<?> getProperty(Identifier identifier) {
      return this.properties.get(identifier);
   }

   private boolean supports(ItemStack stack, BlockState state) {
      return this.supports(stack) && this.supports(state);
   }

   private boolean supports(ItemStack stack) {
      return this.itemClass.isInstance(stack.getItem());
   }

   private boolean supports(BlockState state) {
      return this.blockClass.isInstance(state.getBlock());
   }

   public Class<B> getBlockClass() {
      return this.blockClass;
   }

   public Class<I> getItemClass() {
      return this.itemClass;
   }

   protected BlockItemPropertyContainer(Class<B> blockClass, Class<I> itemClass, Map<Identifier, BlockItemProperty<?>> properties) {
      this.blockClass = blockClass;
      this.itemClass = itemClass;
      this.properties = properties;
   }

   public static final class Builder<B extends Block, I extends Item> {
      private final Class<B> blockClass;
      private final Class<I> itemClass;
      private final com.google.common.collect.ImmutableMap.Builder<Identifier, BlockItemProperty<?>> properties = new com.google.common.collect.ImmutableMap.Builder();

      public BlockItemPropertyContainer.Builder<B, I> property(BlockItemProperty<?> property) {
         this.properties.put(property.getIdentifier(), property);
         return this;
      }

      public BlockItemPropertyContainer<B, I> build() {
         return new BlockItemPropertyContainer<>(this.blockClass, this.itemClass, this.properties.build());
      }

      public Builder(Class<B> blockClass, Class<I> itemClass) {
         this.blockClass = blockClass;
         this.itemClass = itemClass;
      }
   }
}
