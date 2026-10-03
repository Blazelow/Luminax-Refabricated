package dev.satherov.luminax.common.block;

import dev.satherov.luminax.core.LXRegistry;
import dev.satherov.sathlib.common.block.SLEntityBlock;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.jetbrains.annotations.Nullable;

@NothingNull
public class LuminaxStair extends StairBlock implements SLEntityBlock<LuminaxBlockEntity>, LuminaxHolder {
    
    public LuminaxStair(Properties properties) {
        super(LXRegistry.BLOCK.defaultBlockState(), properties.lightLevel((state) -> state.getValue(BlockStateProperties.LIT) ? 15 : 0));
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.LIT, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(BlockStateProperties.LIT));
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return LuminaxHolder.super.getStateForPlacement(context, super.getStateForPlacement(context));
    }
    
    @Override
    public int getLightBlock(BlockState state, BlockGetter level) {
        return LuminaxHolder.super.getLightBlock(state, level);
    }
    
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer instanceof ServerPlayer player) LuminaxHolder.super.setPlacedBy(level, pos, player);
    }
    
    @Override
    public LuminaxBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LuminaxBlockEntity(pos, state);
    }
}
