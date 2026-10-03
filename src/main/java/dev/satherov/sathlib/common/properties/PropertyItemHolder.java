package dev.satherov.sathlib.common.properties;

import net.minecraft.world.item.ItemStack;

public record PropertyItemHolder(ItemStack stack) implements PropertyHolder {
}
