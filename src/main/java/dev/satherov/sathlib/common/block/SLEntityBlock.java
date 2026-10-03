package dev.satherov.sathlib.common.block;

import dev.satherov.sathlib.core.annotations.NothingNull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

@NothingNull
public interface SLEntityBlock<T extends BlockEntity> extends EntityBlock {
   @Nullable
   @Override
   T newBlockEntity(BlockPos pos, BlockState state);
}
