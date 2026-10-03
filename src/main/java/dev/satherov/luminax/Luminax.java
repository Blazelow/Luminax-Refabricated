package dev.satherov.luminax;

import dev.satherov.luminax.core.LXRegistry;
import dev.satherov.luminax.network.SetColorPayload;
import dev.satherov.luminax.network.ToggleGlowing;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.resources.Identifier;

public class Luminax implements ModInitializer {
    
    public static final String MOD_ID = "luminax";
    
    @Override
    public void onInitialize() {
        LXRegistry.init();
        
        PayloadTypeRegistry.serverboundPlay().register(ToggleGlowing.TYPE, ToggleGlowing.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetColorPayload.TYPE, SetColorPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ToggleGlowing.TYPE, (payload, context) -> ToggleGlowing.handle(payload, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetColorPayload.TYPE, (payload, context) -> SetColorPayload.handle(payload, context.player()));
    }
    
    public static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(Luminax.MOD_ID, path);
    }
}
