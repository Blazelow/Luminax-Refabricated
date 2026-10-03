package dev.satherov.luminax.common.item;

import dev.satherov.luminax.common.block.LuminaxBlockEntity;
import dev.satherov.luminax.common.block.LuminaxHolder;
import dev.satherov.luminax.core.LXProperties;
import dev.satherov.luminax.core.LXRegistry;
import dev.satherov.sathlib.common.item.SLItem;
import net.minecraft.world.item.Item;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

@NothingNull
public class LuminaxWandItem extends SLItem {
    
    public LuminaxWandItem(Item.Properties properties) {
        super(properties
                .stacksTo(1)
                .component(LXRegistry.GLOWING, false)
                .component(LXRegistry.COLOR, 0xFFFFFF)
        );
    }
    
    public static ItemStack find(Player player) {
        if (player.getMainHandItem().getItem() instanceof LuminaxWandItem) return player.getMainHandItem();
        if (player.getOffhandItem().getItem() instanceof LuminaxWandItem) return player.getOffhandItem();
        return ItemStack.EMPTY;
    }
    
    private static boolean updateBlock(ServerLevel level, ItemStack stack, BlockPos pos, BlockState state) {
        final LuminaxBlockEntity entity = LXRegistry.BLOCK_ENTITY.getBlockEntity(level, pos);
        if (!(state.getBlock() instanceof LuminaxHolder) || entity == null) return false;
        
        BlockState updated = LXProperties.CONTAINER.applyToBlock(stack, state, entity).state();
        level.setBlockAndUpdate(pos, updated);
        return true;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        LXProperties.CONTAINER.forEach(property -> builder.accept(SLComponent.empty()
                .append(property.getName().translate(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(property.displayItemValue(stack, LXRegistry.BLOCK.defaultBlockState())))
        );
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) return InteractionResult.CONSUME;
        final ItemStack stack = context.getItemInHand();
        final BlockPos blockPos = context.getClickedPos();
        final BlockState blockState = level.getBlockState(blockPos);
        return LuminaxWandItem.updateBlock(level, stack, blockPos, blockState) ? InteractionResult.SUCCESS_SERVER : InteractionResult.FAIL;
    }
}
