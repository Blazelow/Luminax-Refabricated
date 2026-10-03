package dev.satherov.luminax.network;

import dev.satherov.luminax.Luminax;
import dev.satherov.luminax.client.lang.LXLanguage;
import dev.satherov.luminax.common.item.LuminaxWandItem;
import dev.satherov.luminax.core.LXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@NothingNull
public record ToggleGlowing(boolean enabled) implements CustomPacketPayload {
    
    public static final Type<ToggleGlowing> TYPE = new Type<>(Luminax.id("toggle_glowing"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleGlowing> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ToggleGlowing::enabled,
            ToggleGlowing::new
    );
    
    @Override
    public Type<ToggleGlowing> type() {
        return ToggleGlowing.TYPE;
    }
    
    public static void handle(ToggleGlowing payload, ServerPlayer player) {
        final ItemStack stack = LuminaxWandItem.find(player);
        if (stack.isEmpty()) return;
        
        stack.set(LXRegistry.GLOWING, payload.enabled());
        player.sendSystemMessage(
                Component.empty()
                        .append(LXLanguage.PROPERTY_GLOWING.translate(ChatFormatting.GRAY))
                        .append(": ")
                        .append(SLComponent.enabledDisabled(payload.enabled())),
                true
        );
        
        player.getInventory().setChanged();
    }
}
