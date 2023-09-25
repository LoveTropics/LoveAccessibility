package com.lovetropics.accessibility.client.narrator.description;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public record ItemDescription(Component component) {
    @Nullable
    public static ItemDescription describe(final ItemStack item) {
        if (!item.isEmpty()) {
            return new ItemDescription(describeComponent(item));
        }
        return null;
    }

    private static Component describeComponent(final ItemStack item) {
        final MutableComponent prefix = Component.literal(item.getCount() + " ");
        final Component typeName = item.getItem().getName(item);
        if (item.hasCustomHoverName()) {
            final Component displayName = item.getHoverName();
            return prefix.append(CommonComponents.joinForNarration(displayName, typeName));
        } else {
            return prefix.append(typeName);
        }
    }
}
