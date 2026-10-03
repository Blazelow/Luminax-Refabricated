package dev.satherov.luminax.core;

import lombok.experimental.UtilityClass;

import dev.satherov.luminax.Luminax;
import dev.satherov.luminax.client.lang.LXLanguage;
import dev.satherov.luminax.common.block.LuminaxBlock;
import dev.satherov.luminax.common.block.LuminaxBlockEntity;
import dev.satherov.luminax.common.block.LuminaxButton;
import dev.satherov.luminax.common.block.LuminaxPressurePlate;
import dev.satherov.luminax.common.block.LuminaxSlab;
import dev.satherov.luminax.common.block.LuminaxStair;
import dev.satherov.luminax.common.block.LuminaxWall;
import dev.satherov.luminax.common.item.LuminaxWandItem;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Everything is registered while this class initializes, which happens from {@link Luminax#onInitialize()} via {@link #init()}.
 * The declaration order matters: the stair needs {@link #BLOCK}, and the block entity type needs every block.
 */
@UtilityClass
public final class LXRegistry {
    
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> ITEMS = new ArrayList<>();
    
    public static final DataComponentType<Integer> COLOR = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, Luminax.id("color"),
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build()
    );
    public static final DataComponentType<Boolean> GLOWING = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, Luminax.id("glowing"),
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );
    public static final LuminaxWandItem LUMINAX_WAND = LXRegistry.registerItem("luminax_wand", properties -> new LuminaxWandItem(properties));
    public static final LuminaxBlock BLOCK = LXRegistry.register("luminax_block", LuminaxBlock::new);
    public static final CreativeModeTab TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, Luminax.id("creative_tab"),
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(LXLanguage.CREATIVE_TAB_DEFAULT.translate())
                    .icon(() -> LXRegistry.BLOCK.asItem().getDefaultInstance())
                    .displayItems((_, out) -> LXRegistry.ITEMS.stream().map(Item::getDefaultInstance).forEach(out::accept))
                    .build()
    );
    public static final LuminaxStair STAIRS = LXRegistry.register("luminax_stair", LuminaxStair::new);
    public static final LuminaxSlab SLAB = LXRegistry.register("luminax_slab", LuminaxSlab::new);
    public static final LuminaxWall WALL = LXRegistry.register("luminax_wall", LuminaxWall::new);
    public static final LuminaxPressurePlate PRESSURE_PLATE = LXRegistry.register("luminax_pressure_plate", LuminaxPressurePlate::new);
    public static final LuminaxButton BUTTON = LXRegistry.register("luminax_button", LuminaxButton::new);
    public static final BlockEntityType<LuminaxBlockEntity> BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, Luminax.id("glass_tile"),
            new BlockEntityType<>(LuminaxBlockEntity::new, LXRegistry.BLOCKS.toArray(Block[]::new))
    );
    
    private static <T extends Item> T registerItem(String name, Function<Item.Properties, T> factory) {
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Luminax.id(name));
        final T item = Registry.register(BuiltInRegistries.ITEM, key, factory.apply(new Item.Properties().setId(key)));
        LXRegistry.ITEMS.add(item);
        return item;
    }
    
    private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> factory) {
        final ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Luminax.id(name));
        final T block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).setId(blockKey)));
        LXRegistry.BLOCKS.add(block);
        
        final ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Luminax.id(name));
        LXRegistry.ITEMS.add(Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix())));
        return block;
    }
    
    /**
     * Forces the class to initialize, which registers everything.
     */
    public static void init() {
    }
}
