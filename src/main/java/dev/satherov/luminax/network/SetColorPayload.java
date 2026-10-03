package dev.satherov.luminax.network;

import dev.satherov.luminax.Luminax;
import dev.satherov.luminax.common.item.LuminaxWandItem;
import dev.satherov.luminax.core.LXProperties;
import dev.satherov.luminax.core.LXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@NothingNull
public record SetColorPayload(int color) implements CustomPacketPayload {
    
    public static final Type<SetColorPayload> TYPE = new Type<>(Luminax.id("set_color"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, SetColorPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SetColorPayload::color,
            SetColorPayload::new
    );
    
    @Override
    public Type<SetColorPayload> type() {
        return SetColorPayload.TYPE;
    }
    
    public static void handle(SetColorPayload payload, ServerPlayer player) {
        final ItemStack stack = LuminaxWandItem.find(player);
        if (stack.isEmpty()) return;
        
        LXProperties.COLOR.applyValueItem(payload.color(), stack, LXRegistry.BLOCK.defaultBlockState());
        player.getInventory().setChanged();
    }
}
