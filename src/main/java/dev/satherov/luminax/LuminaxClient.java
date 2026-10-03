package dev.satherov.luminax;

import dev.satherov.luminax.client.lang.LXLanguage;
import dev.satherov.luminax.client.screen.LXColorScreen;
import dev.satherov.luminax.common.block.LuminaxBlockEntity;
import dev.satherov.luminax.common.item.LuminaxWandItem;
import dev.satherov.luminax.core.LXRegistry;
import dev.satherov.luminax.network.ToggleGlowing;
import dev.satherov.sathlib.client.input.SLKeybindManager;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.lwjgl.glfw.GLFW;

import java.util.List;

@NothingNull
public class LuminaxClient implements ClientModInitializer {
    
    public static final SLKeybindManager KEYBINDS = SLKeybindManager.create(Luminax.MOD_ID, Luminax.id("default"));
    public static final KeyMapping TOGGLE_GLOWING = LuminaxClient.KEYBINDS.add(LXLanguage.KEY_TOGGLE_GLOWING, GLFW.GLFW_KEY_X, () -> {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        if (mc.level == null || player == null || mc.screen != null) return;
        
        final ItemStack stack = LuminaxWandItem.find(player);
        if (stack.isEmpty()) return;
        
        boolean enabled = !stack.getOrDefault(LXRegistry.GLOWING, false);
        stack.set(LXRegistry.GLOWING, enabled);
        ClientPlayNetworking.send(new ToggleGlowing(enabled));
    });
    public static final KeyMapping OPEN_COLOR_PICKER = LuminaxClient.KEYBINDS.add(LXLanguage.KEY_OPEN_COLOR_PICKER, GLFW.GLFW_KEY_V, () -> {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        if (mc.level == null || player == null || mc.screen != null) return;
        
        final ItemStack stack = LuminaxWandItem.find(player);
        if (stack.isEmpty()) return;
        
        if (mc.screen instanceof LXColorScreen) mc.setScreen(null);
        else mc.setScreen(new LXColorScreen(stack));
    });
    private static final List<BlockTintSource> LUMINAX_BLOCK_TINT = List.of(new BlockTintSource() {
        
        @Override
        public int color(BlockState state) {
            return ARGB.opaque(0xFFFFFF);
        }
        
        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            LuminaxBlockEntity entity = LXRegistry.BLOCK_ENTITY.getBlockEntity(level, pos);
            return ARGB.opaque(entity != null ? entity.getColor() : 0xFFFFFF);
        }
    });
    
    @Override
    public void onInitializeClient() {
        LuminaxClient.KEYBINDS.register();
        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
                client.getBlockColors().register(LuminaxClient.LUMINAX_BLOCK_TINT, LXRegistry.BLOCKS.toArray(Block[]::new))
        );
    }
}
