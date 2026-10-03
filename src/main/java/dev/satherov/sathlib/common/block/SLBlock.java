package dev.satherov.sathlib.common.block;

import dev.satherov.sathlib.core.annotations.NothingNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NothingNull
public class SLBlock extends Block {
   private static final Logger log = LoggerFactory.getLogger(SLBlock.class);
   @Nullable
   private SLBlock.StateBuilder pendingStateBuilder;

   public SLBlock(Properties properties) {
      super(properties);
      if (this.pendingStateBuilder != null) {
         this.registerDefaultState(this.pendingStateBuilder.applyDefaults(this.defaultBlockState()));
         this.pendingStateBuilder = null;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> definition) {
      SLBlock.StateBuilder builder = SLBlock.StateBuilder.create();
      this.registerState(builder);
      builder.createDefinition(definition);
      this.pendingStateBuilder = builder;
   }

   protected final void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
      BlockState newState = level.getBlockState(pos);
      this.onRemoved(level, pos, state, newState, movedByPiston);
   }

   protected final void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      if (!state.is(oldState.getBlock())) {
         this.onPlaced((ServerLevel)level, pos, state, oldState, movedByPiston);
      } else {
         this.onChanged((ServerLevel)level, pos, state, oldState, movedByPiston);
      }
   }

   protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      return this.useWithItem(stack, player, level, state, pos, hit);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      return this.useWithoutItem(player, level, state, pos, hit);
   }

   protected void registerState(SLBlock.StateBuilder builder) {
   }

   protected void onRemoved(ServerLevel level, BlockPos pos, BlockState state, BlockState newState, boolean movedByPiston) {
   }

   protected void onChanged(ServerLevel level, BlockPos pos, BlockState state, BlockState oldState, boolean movedByPiston) {
   }

   protected void onPlaced(ServerLevel level, BlockPos pos, BlockState state, BlockState oldState, boolean movedByPiston) {
   }

   protected InteractionResult useWithItem(ItemStack stack, Player player, Level level, BlockState state, BlockPos pos, BlockHitResult hit) {
      return InteractionResult.PASS;
   }

   protected InteractionResult useWithoutItem(Player player, Level level, BlockState state, BlockPos pos, BlockHitResult hit) {
      return InteractionResult.PASS;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
   }

   public static class StateBuilder {
      private final Map<Property<?>, Comparable<?>> properties = new HashMap<>();

      private StateBuilder() {
      }

      public static SLBlock.StateBuilder create() {
         return new SLBlock.StateBuilder();
      }

      private static <T extends Comparable<T>> BlockState setUnchecked(BlockState state, Property<?> property, @Nullable Comparable<?> value) {
         return value == null ? state : (BlockState)state.setValue(property, value);
      }

      public <T extends Comparable<T>, V extends T> SLBlock.StateBuilder addValue(Property<T> property, V defaultValue) {
         this.properties.put(property, defaultValue);
         return this;
      }

      public <T extends Comparable<T>> SLBlock.StateBuilder addValue(Property<T> property) {
         this.properties.put(property, null);
         return this;
      }

      private void createDefinition(Builder<Block, BlockState> builder) {
         for (Property<?> property : this.properties.keySet()) {
            builder.add(new Property[]{property});
         }
      }

      private BlockState applyDefaults(BlockState state) {
         for (Entry<Property<?>, Comparable<?>> entry : this.properties.entrySet()) {
            state = setUnchecked(state, entry.getKey(), entry.getValue());
         }

         return state;
      }
   }
}
