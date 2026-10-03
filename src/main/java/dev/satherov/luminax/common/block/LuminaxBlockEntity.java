package dev.satherov.luminax.common.block;

import lombok.Getter;

import dev.satherov.luminax.core.LXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jetbrains.annotations.Nullable;

@NothingNull
public class LuminaxBlockEntity extends BlockEntity {
    
    private @Getter int color = 0xFFFFFF;
    
    public LuminaxBlockEntity(BlockPos pos, BlockState blockState) {
        super(LXRegistry.BLOCK_ENTITY, pos, blockState);
    }
    
    public void setColor(int color) {
        if (this.color == color) return;
        
        this.color = color;
        this.setChanged();
        this.update();
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("color", this.color);
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.color = input.getInt("color").orElse(0xFFFFFF);
        this.update();
    }
    
    private void update() {
        if (this.level == null) return;
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
    
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
